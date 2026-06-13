package com.example.projetglcellule.app;

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

/**
 * Main JavaFX application for the ecosystem simulation.
 */
public class SimulationApp extends Application {

    /**
     * Creates the JavaFX application.
     */
    public SimulationApp() {
    }

    private static int cellSize = 15; 
    private Grid grid;
    private Canvas canvas;
    private AnimationTimer simulationLoop;

    private boolean isRunning = false;
    private int currentStep = 0;
    private long tickDurationNs = 300_000_000L;

    
    private Label stepLabel;
    private ToggleGroup toolGroup;
    private ToggleGroup speciesGroup;
    private Slider speedSlider;
    private BorderPane mainLayout;

    
    private TextField widthField;
    private TextField heightField;
    private CheckBox toroidalCheckBox;

    /**
     * Builds the JavaFX interface and starts the simulation window.
     *
     * @param primaryStage the main application stage
     */
    @Override
    public void start(Stage primaryStage) {
        
        int initialWidth = 40;
        int initialHeight = 30;

        
        grid = new Grid(initialWidth, initialHeight, Topology.BOUNDED);

        primaryStage.setTitle("Ecosystem Simulation - Ultimate Editor");

        
        canvas = new Canvas(initialWidth * cellSize, initialHeight * cellSize);
        drawGrid();

        
        canvas.setOnMousePressed(this::handleMouseClickOrDrag);
        canvas.setOnMouseDragged(this::handleMouseClickOrDrag);

        
        VBox topControlsContainer = new VBox(5);
        topControlsContainer.setStyle("-fx-background-color: #0f172a;");

        
        HBox gridConfigBar = new HBox(15);
        gridConfigBar.setPadding(new Insets(10, 10, 5, 10));
        gridConfigBar.setStyle("-fx-alignment: center-left;");

        Label widthLabel = new Label("Width:");
        widthLabel.setStyle("-fx-text-fill: white;");
        widthField = new TextField(String.valueOf(initialWidth));
        widthField.setPrefWidth(50);

        Label heightLabel = new Label("Height:");
        heightLabel.setStyle("-fx-text-fill: white;");
        heightField = new TextField(String.valueOf(initialHeight));
        heightField.setPrefWidth(50);

        Button resizeBtn = new Button("🔄 Apply Size");
        resizeBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold;");
        resizeBtn.setOnAction(e -> handleResize());

        
        toroidalCheckBox = new CheckBox("Toroidal Universe");
        toroidalCheckBox.setStyle("-fx-text-fill: #7dd3fc; -fx-font-weight: bold;");
        toroidalCheckBox.setOnAction(e -> handleTopologyChange());

        gridConfigBar.getChildren().addAll(widthLabel, widthField, heightLabel, heightField, resizeBtn, new Separator(), toroidalCheckBox);

        
        HBox toolBar = new HBox(20);
        toolBar.setPadding(new Insets(5, 10, 10, 10));
        toolBar.setStyle("-fx-alignment: center-left;");

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

        speciesGroup = new ToggleGroup();
        RadioButton rbPlant = new RadioButton("🌱 Plant");
        rbPlant.setToggleGroup(speciesGroup);
        rbPlant.setSelected(true);
        rbPlant.setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");

        RadioButton rbHerbivore = new RadioButton("💙 Herbivore");
        rbHerbivore.setToggleGroup(speciesGroup);
        rbHerbivore.setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");

        RadioButton rbCarnivore = new RadioButton("❤️   Carnivore");
        rbCarnivore.setToggleGroup(speciesGroup);
        rbCarnivore.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");

        toolBar.getChildren().addAll(new Label("🛠 TOOLS:"), rbPen, rbBrush, rbEraser, new Separator(), new Label("🧬 BIOLOGY:"), rbPlant, rbHerbivore, rbCarnivore);

        topControlsContainer.getChildren().addAll(gridConfigBar, toolBar);

        
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: #1e293b; -fx-pref-width: 220px;");

        stepLabel = new Label("Step: 0");
        stepLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        Button playPauseBtn = new Button("▶ Play");
        playPauseBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-pref-width: 190px;");

        Button stepBtn = new Button("⏭ Next Step");
        stepBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-pref-width: 190px;");

        Label speedLabel = new Label("Tick Duration (ms): 300");
        speedLabel.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 12px;");

        speedSlider = new Slider(50, 1000, 300);
        speedSlider.setShowTickLabels(true);
        speedSlider.setOnMouseDragged(e -> {
            int value = (int) speedSlider.getValue();
            speedLabel.setText("Tick Duration (ms): " + value);
            tickDurationNs = value * 1_000_000L;
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
            if (loadedGrid != null) {
                grid = loadedGrid;
                
                widthField.setText(String.valueOf(grid.getWidth()));
                heightField.setText(String.valueOf(grid.getHeight()));
                
                
                updateCanvasSize();
                drawGrid();
            }
        });

        
        mainLayout = new BorderPane();
        mainLayout.setTop(topControlsContainer);
        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(canvas);
        mainLayout.setStyle("-fx-background-color: #0f172a;");

        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true); 
        primaryStage.show();
    }

    
    /**
     * Rebuilds the grid using the new width and height values.
     */
    private void handleResize() {
        try {
            int newWidth = Integer.parseInt(widthField.getText().trim());
            int newHeight = Integer.parseInt(heightField.getText().trim());

            if (newWidth < 3 || newHeight < 3 || newWidth > 200 || newHeight > 200) {
                showErrorAlert("Dimensions invalides", "La largeur et la hauteur doivent être comprises entre 3 et 200.");
                return;
            }

            
            Topology currentTopo = toroidalCheckBox.isSelected() ? Topology.TOROIDAL : Topology.BOUNDED;

            
            if (newWidth > 80 || newHeight > 80) cellSize = 8;
            else if (newWidth > 50 || newHeight > 50) cellSize = 12;
            else cellSize = 15;

            
            grid = new Grid(newWidth, newHeight, currentTopo);
            currentStep = 0;
            stepLabel.setText("Step: 0");

            
            updateCanvasSize();
            drawGrid();

        } catch (NumberFormatException e) {
            showErrorAlert("Erreur de format", "Veuillez entrer des nombres entiers valides.");
        }
    }

    
    /**
     * Switches the grid topology between bounded and toroidal mode.
     */
    private void handleTopologyChange() {
        Topology newTopo = toroidalCheckBox.isSelected() ? Topology.TOROIDAL : Topology.BOUNDED;

        
        
        
        Grid newGrid = new Grid(grid.getWidth(), grid.getHeight(), newTopo);
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell c = grid.getCell(x, y);
                if (c != null) newGrid.setCell(x, y, c);
            }
        }
        grid = newGrid;
        drawGrid();
    }

    /**
     * Updates the canvas size to match the current grid dimensions.
     */
    private void updateCanvasSize() {
        canvas.setWidth(grid.getWidth() * cellSize);
        canvas.setHeight(grid.getHeight() * cellSize);
    }

    /**
     * Adds or removes cells when the user clicks or drags on the canvas.
     *
     * @param event the mouse event from the user
     */
    private void handleMouseClickOrDrag(MouseEvent event) {

    int gridX = (int) (event.getX() / cellSize);
    int gridY = (int) (event.getY() / cellSize);

    if (gridX < 0 || gridX >= grid.getWidth()
            || gridY < 0 || gridY >= grid.getHeight()) return;

    RadioButton selectedTool = (RadioButton) toolGroup.getSelectedToggle();
    RadioButton selectedSpecies = (RadioButton) speciesGroup.getSelectedToggle();

    if (selectedTool.getText().contains("Eraser")) {
        grid.clearCell(gridX, gridY);
    } else if (selectedTool.getText().contains("Single")) {
        grid.setCell(gridX, gridY, createSelectedCell(gridX, gridY, selectedSpecies.getText()));
    } else if (selectedTool.getText().contains("Block")) {
        for (int dy = -2; dy <= 2; dy++) {
            for (int dx = -2; dx <= 2; dx++) {
                int targetX = gridX + dx;
                int targetY = gridY + dy;

                if (targetX >= 0 && targetX < grid.getWidth()
                        && targetY >= 0 && targetY < grid.getHeight()) {

                    grid.setCell(targetX, targetY,
                            createSelectedCell(targetX, targetY, selectedSpecies.getText()));
                }
            }
        }
    }

    drawGrid();
}

    /**
     * Creates the cell type selected by the user.
     *
     * @param x the x position
     * @param y the y position
     * @param speciesText the selected species label
     * @return the created cell object
     */
    private Cell createSelectedCell(int x, int y, String speciesText) {
        if (speciesText.contains("Plant")) return new AutotrophCell(x, y, 15, 1);
        if (speciesText.contains("Herbivore")) return new HerbivoreCell(x, y, 25, 1);
        return new CarnivoreCell(x, y, 40, 1);
    }

    /**
     * Runs one simulation step and refreshes the view.
     */
    private void executeSingleStep() {
        currentStep++;
        stepLabel.setText("Step: " + currentStep);

        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell cell = grid.getCell(x, y);
                if (cell != null && cell.isActive() && !cell.hasPlayed()) cell.update(grid);
            }
        }
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell cell = grid.getCell(x, y);
                if (cell != null && !cell.isActive()) grid.clearCell(x, y);
            }
        }
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell cell = grid.getCell(x, y);
                if (cell != null) cell.setHasPlayed(false);
            }
        }
        drawGrid();
    }

    /**
     * Draws the current state of the grid on the canvas.
     */
    private void drawGrid() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web("#020617"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.web("#1e293b"));
        gc.setLineWidth(0.5);
        for (int i = 0; i <= grid.getWidth(); i++) {
            gc.strokeLine(i * cellSize, 0, i * cellSize, canvas.getHeight());
        }
        for (int j = 0; j <= grid.getHeight(); j++) {
            gc.strokeLine(0, j * cellSize, canvas.getWidth(), j * cellSize);
        }

        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell cell = grid.getCell(x, y);
                if (cell != null) {
                    if (cell instanceof AutotrophCell) gc.setFill(Color.web("#22c55e"));
                    else if (cell instanceof HerbivoreCell) gc.setFill(Color.web("#3b82f6"));
                    else if (cell instanceof CarnivoreCell) gc.setFill(Color.web("#ef4444"));

                    gc.fillRoundRect(x * cellSize + 1, y * cellSize + 1, cellSize - 2, cellSize - 2, 4, 4);
                }
            }
        }
    }

    /**
     * Fills the grid with random cells to start a new simulation state.
     */
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

    /**
     * Shows an error message to the user.
     *
     * @param title the alert title
     * @param content the message to display
     */
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}