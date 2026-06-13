# Projet Génie Logiciel – Simulation de cellules (JavaFX)

## Description

Ce projet est une simulation de monde cellulaire développée en Java avec JavaFX.  
Il modélise un environnement 2D dans lequel différentes cellules (autotrophes, herbivores et carnivores) interagissent et évoluent selon des règles biologiques.

L’architecture du projet suit le modèle MVC (Model – View – Controller) afin de séparer clairement les responsabilités :
- Model : représentation des données (grille, cellules, position)
- Controller : logique de simulation et évolution
- App : interface graphique JavaFX

L’utilisateur peut interagir avec la grille, placer des cellules, lancer/pause la simulation et contrôler la vitesse d’exécution.

---

## Fonctionnalités

- Simulation de cellules sur une grille 2D
- Différents types de cellules :
    - Autotrophes (plantes)
    - Herbivores
    - Carnivores
- Interface graphique JavaFX
- Contrôle de la simulation :
    - Play / Pause
    - Step par step
    - Ajustement de la vitesse
- Ajout / suppression de cellules via clic souris
- Sauvegarde et chargement de l’état de la simulation

---

## Technologies utilisées

- Java 21
- JavaFX 21.0.11
- Architecture MVC
- IntelliJ IDEA (recommandé) ou VS Code

---

## Structure du projet

src/
└── com.example.projetglcellule
├── app
│   └── SimulationApp.java
├── controller
│   ├── SimulationEngine.java
│   └── SaveManager.java
└── model
├── Grid.java
├── Position.java
├── Directions.java
├── Topology.java
└── cell
├── Cell.java
├── AutotrophCell.java
├── HerbivoreCell.java
├── CarnivoreCell.java
├── Movable.java
└── Consumable.java

---

## Prérequis

Avant de lancer le projet, installer :
- Java JDK 21
- JavaFX SDK 21.0.11 (décompressé)
- Vérifier que le dossier `lib` de JavaFX contient les `.jar`

---

## Compilation (ligne de commande)

Se placer à la racine du projet :

```bash 
javac --module-path "C:\javafx-sdk-21.0.11\lib" ^
      --add-modules javafx.controls,javafx.graphics ^
      -d out ^
      (Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })
```

## Execution

```bash 
java --module-path "C:\javafx-sdk-21.0.11\lib" ^
--add-modules javafx.controls,javafx.graphics ^
-cp out ^
com.example.projetglcellule.app.SimulationApp
```

## Génération de la JavaDoc

```bash 
javadoc --module-path "C:\javafx-sdk-21.0.11\lib" ^
        --add-modules javafx.controls,javafx.graphics ^
        -d javadoc ^
        -sourcepath src ^
        -subpackages com.example.projetglcellule
```

Groupe GX-B 
Roman BOULLIER
Jules HENNION