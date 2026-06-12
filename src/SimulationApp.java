package com.example.projetglcellule;

import com.example.projetglcellule.model.Grid;
import com.example.projetglcellule.model.Topology;
import com.example.projetglcellule.model.cell.Cell;
import com.example.projetglcellule.model.cell.AutotrophCell;
import com.example.projetglcellule.model.cell.HerbivoreCell;
import com.example.projetglcellule.model.cell.CarnivoreCell;
import com.example.projetglcellule.controller.SaveManager;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;

public class SimulationApp extends Application {

    private static final int CELL_SIZE = 15; // pixels par cellule
    private Grid grid;
    private Canvas canvas;
    private AnimationTimer simulationLoop;

    private boolean isRunning = false;
    private int currentStep = 0;
    private long tickDurationNs = 300_000_000L; // 300ms par défaut (en nanosecondes)

    // Éléments de l'interface requis
    private Label stepLabel;
    private ToggleGroup toolGroup;
    private ToggleGroup speciesGroup;
    private Slider speedSlider;

    @Override
    public void start(Stage primaryStage) {
        int gridWidth = 50;
        int gridHeight = 40;

        // Exigence : Au lancement, la grille est VIDE
        grid = new Grid(gridWidth, gridHeight, Topology.BOUNDED);

        primaryStage.setTitle("Ecosystem Simulation - Advanced Editor Mode");

        // 1. Zone de dessin (Canvas)
        canvas = new Canvas(gridWidth * CELL_SIZE, gridHeight * CELL_SIZE);
        drawGrid();

        // 2. Gestion de la SOURIS (Exigence: Poser/Modifier case par case ou en rectangle, même en cours de route)
        canvas.setOnMousePressed(this::handleMouseClickOrDrag);
        canvas.setOnMouseDragged(this::handleMouseClickOrDrag);

        // 3. Panneau Latéral Gauche (Contrôles généraux)
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: #1e293b; -fx-pref-width: 220px;");

        stepLabel = new Label("Step: 0");
        stepLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        Button playPauseBtn = new Button("▶ Play");
        playPauseBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-pref-width: 190px;");

        Button stepBtn = new Button("⏭ Next Step");
        stepBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-pref-width: 190px;");

        // Exigence : Choix de la durée des ticks (Vitesse de simulation)
        Label speedLabel = new Label("Tick Duration (ms): 300");
        speedLabel.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 12px;");

        speedSlider = new Slider(50, 1000, 300); // de 50ms à 1000ms, valeur initiale 300ms
        speedSlider.setShowTickLabels(true);
        speedSlider.setOnMouseDragged(e -> {
            int value = (int) speedSlider.getValue();
            speedLabel.setText("Tick Duration (ms): " + value);
            tickDurationNs = value * 1_000_000L; // Conversion en nanosecondes
        });

        Button randomFillBtn = new Button("🎲 Random Fill");
        randomFillBtn.setStyle("-fx-background-color: #6b7280; -fx-text-fill: white; -fx-pref-width: 190px;");

        Button clearBtn = new Button("🗑 Clear Grid");
        clearBtn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-pref-width: 190px;");

        Button saveBtn = new Button("💾 Save State");
        saveBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-pref-width: 190px;");

        Button loadBtn = new Button("📂 Load State");
        loadBtn.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-pref-width: 190px;");

        sidebar.getChildren().addAll(stepLabel, playPauseBtn, stepBtn, new Separator(), speedLabel, speedSlider, new Separator(), randomFillBtn, clearBtn, saveBtn, loadBtn);

        // 4. Panneau Supérieur (Sélecteur d'Outils et d'Espèces)
        HBox topBar = new HBox(25);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #0f172a; -fx-alignment: center-left;");

        // Groupe d'outils de dessin
        toolGroup = new ToggleGroup();
        RadioButton rbPen = new RadioButton("Single Cell (1x1)");
        rbPen.setToggleGroup(toolGroup);
        rbPen.setSelected(true);
        rbPen.setStyle("-fx-text-fill: white;");

        RadioButton rbBrush = new RadioButton("Block Brush (5x5)");
        rbBrush.setToggleGroup(toolGroup);
        rbBrush.setStyle("-fx-text-fill: white;");

        RadioButton rbEraser = new RadioButton("Eraser 🧽");
        rbEraser.setToggleGroup(toolGroup);
        rbEraser.setStyle("-fx-text-fill: white;");

        // Groupe d'espèces
        speciesGroup = new ToggleGroup();
        RadioButton rbPlant = new RadioButton("🌱 Plant");
        rbPlant.setToggleGroup(speciesGroup);
        rbPlant.setSelected(true);
        rbPlant.setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");

        RadioButton rbHerbivore = new RadioButton("💙 Herbivore");
        rbHerbivore.setToggleGroup(speciesGroup);
        rbHerbivore.setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");

        RadioButton rbCarnivore = new RadioButton("❤️ Carnivore");
        rbCarnivore.setToggleGroup(speciesGroup);
        rbCarnivore.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");

        topBar.getChildren().addAll(new Label("🛠 TOOLS:"), rbPen, rbBrush, rbEraser, new Separator(), new Label("🧬 BIOLOGY:"), rbPlant, rbHerbivore, rbCarnivore);

        // 5. Boucle de Chronométrage Adaptative (AnimationTimer prenant en compte le Slider)
        simulationLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= tickDurationNs) {
                    executeSingleStep();
                    lastUpdate = now;
                }
            }
        };

        // 6. Événements des boutons
        playPauseBtn.setOnAction(e -> {
            if (isRunning) {
                simulationLoop.stop();
                playPauseBtn.setText("▶ Play");
                playPauseBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-pref-width: 190px;");
            } else {
                simulationLoop.start();
                playPauseBtn.setText("⏸ Pause");
                playPauseBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-pref-width: 190px;");
            }
            isRunning = !isRunning;
        });

        stepBtn.setOnAction(e -> { if (!isRunning) executeSingleStep(); });
        randomFillBtn.setOnAction(e -> { triggerRandomFill(); drawGrid(); });
        clearBtn.setOnAction(e -> { grid.clearAll(); currentStep = 0; stepLabel.setText("Step: 0"); drawGrid(); });
        saveBtn.setOnAction(e -> SaveManager.saveGame(grid));
        loadBtn.setOnAction(e -> {
            Grid loadedGrid = SaveManager.loadGame();
            if (loadedGrid != null) { grid = loadedGrid; canvas.setWidth(grid.getWidth() * CELL_SIZE); canvas.setHeight(grid.getHeight() * CELL_SIZE); drawGrid(); }
        });

        // 7. Agencement Final
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setLeft(sidebar);
        root.setCenter(canvas);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Intercepte les actions de souris pour dessiner ou effacer sur la grille à la volée.
     */
    private void handleMouseClickOrDrag(MouseEvent event) {
        // Conversion de la coordonnée pixel écran en index de case dans le tableau
        int gridX = (int) (event.getX() / CELL_SIZE);
        int gridY = (int) (event.getY() / CELL_SIZE);

        // Sécurité anti-débordement de tableau
        if (gridX < 0 || gridX >= grid.getWidth() || gridY < 0 || gridY >= grid.getHeight()) {
            return;
        }

        RadioButton selectedTool = (RadioButton) toolGroup.getSelectedToggle();
        RadioButton selectedSpecies = (RadioButton) speciesGroup.getSelectedToggle();

        if (selectedTool.getText().contains("Eraser")) {
            // Mode Gomme
            grid.clearCell(gridX, gridY);
        } else if (selectedTool.getText().contains("Single")) {
            // Mode Crayon 1x1
            grid.setCell(gridX, gridY, createSelectedCell(gridX, gridY, selectedSpecies.getText()));
        } else if (selectedTool.getText().contains("Block")) {
            // Mode Pinceau Rectangle 5x5 (centré sur le curseur)
            for (int dy = -2; dy <= 2; dy++) {
                for (int dx = -2; dx <= 2; dx++) {
                    int targetX = gridX + dx;
                    int targetY = gridY + dy;
                    if (targetX >= 0 && targetX < grid.getWidth() && targetY >= 0 && targetY < grid.getHeight()) {
                        grid.setCell(targetX, targetY, createSelectedCell(targetX, targetY, selectedSpecies.getText()));
                    }
                }
            }
        }
        drawGrid(); // Redessine instantanément la modification faite par l'utilisateur
    }

    private Cell createSelectedCell(int x, int y, String speciesText) {
        if (speciesText.contains("Plant")) return new AutotrophCell(x, y, 15, 1);
        if (speciesText.contains("Herbivore")) return new HerbivoreCell(x, y, 25, 1);
        return new CarnivoreCell(x, y, 40, 1);
    }

    private void executeSingleStep() {
        currentStep++;
        stepLabel.setText("Step: " + currentStep);

        // Étape A: Cycle biologique
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell cell = grid.getCell(x, y);
                if (cell != null && cell.isActive() && !cell.hasPlayed()) {
                    cell.update(grid);
                }
            }
        }

        // Étape B: Nettoyage
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell cell = grid.getCell(x, y);
                if (cell != null && !cell.isActive()) grid.clearCell(x, y);
            }
        }

        // Étape C: Reset flags
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell cell = grid.getCell(x, y);
                if (cell != null) cell.setHasPlayed(false);
            }
        }
        drawGrid();
    }

    private void drawGrid() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web("#020617"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Grille de fond subtile pour matérialiser les cases vides (Mode éditeur exige)
        gc.setStroke(Color.web("#1e293b"));
        gc.setLineWidth(0.5);
        for (int i = 0; i <= grid.getWidth(); i++) {
            gc.strokeLine(i * CELL_SIZE, 0, i * CELL_SIZE, canvas.getHeight());
        }
        for (int j = 0; j <= grid.getHeight(); j++) {
            gc.strokeLine(0, j * CELL_SIZE, canvas.getWidth(), j * CELL_SIZE);
        }

        // Rendu des cellules vivantes
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell cell = grid.getCell(x, y);
                if (cell != null) {
                    if (cell instanceof AutotrophCell) gc.setFill(Color.web("#22c55e"));
                    else if (cell instanceof HerbivoreCell) gc.setFill(Color.web("#3b82f6"));
                    else if (cell instanceof CarnivoreCell) gc.setFill(Color.web("#ef4444"));

                    gc.fillRoundRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2, 4, 4);
                }
            }
        }
    }

    private void triggerRandomFill() {
        Random rand = new Random();
        grid.clearAll();
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                double roll = rand.nextDouble();
                if (roll < 0.12) grid.setCell(x, y, new AutotrophCell(x, y, 15, 1));
                else if (roll < 0.16) grid.setCell(x, y, new HerbivoreCell(x, y, 25, 1));
                else if (roll < 0.18) grid.setCell(x, y, new CarnivoreCell(x, y, 40, 1));
            }
        }
    }

    public static void main(String[] args) { launch(args); }
}