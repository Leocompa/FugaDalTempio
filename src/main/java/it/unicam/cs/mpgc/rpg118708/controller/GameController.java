package it.unicam.cs.mpgc.rpg118708.controller;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.engine.GameState;
import it.unicam.cs.mpgc.rpg118708.model.*;
import it.unicam.cs.mpgc.rpg118708.persistence.GameLoader;
import it.unicam.cs.mpgc.rpg118708.persistence.GameSaver;
import it.unicam.cs.mpgc.rpg118708.view.CombatScene;
import it.unicam.cs.mpgc.rpg118708.view.ExplorationScene;
import it.unicam.cs.mpgc.rpg118708.view.StartScene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class GameController {

    private final Stage stage;
    private GameManager gameManager;
    private final GameSaver saver;
    private final GameLoader loader;

    private StartScene startScene;
    private ExplorationScene explorationScene;
    private CombatScene combatScene;

    public GameController(Stage stage) {
        this.stage = stage;
        this.saver = new GameSaver();
        this.loader = new GameLoader();
    }

    public void start() {
        startScene = new StartScene();

        startScene.getNewGameButton().setOnAction(e -> {
            String name = startScene.getPlayerName();
            if (name.isEmpty()) name = "Ladro";
            startNewGame(name);
        });

        startScene.getLoadGameButton().setOnAction(e -> {
            if (loader.saveExists()) {
                String savedName = loader.loadPlayerName();
                if (savedName.isEmpty()) savedName = "Ladro";
                Player player = new Player(savedName);
                List<Zone> zones = WorldBuilder.buildWorld();
                int roomIndex = 0;
                for (Zone zone : zones) {
                    for (Room room : zone.getRooms()) {
                        for (Enemy enemy : room.getEnemies()) {
                            WorldBuilder.scaleEnemy(enemy, roomIndex);
                        }
                        roomIndex++;
                    }
                }
                gameManager = new GameManager(player, zones);
                loader.load(gameManager);
                startExploration();
            } else {
                startScene.getLoadGameButton().setText("Nessun salvataggio trovato");
            }
        });

        stage.setScene(startScene.getScene());
    }

    private void startNewGame(String playerName) {
        Player player = new Player(playerName);
        List<Zone> zones = WorldBuilder.buildWorld();
        int roomIndex = 0;
        for (Zone zone : zones) {
            for (Room room : zone.getRooms()) {
                for (Enemy enemy : room.getEnemies()) {
                    WorldBuilder.scaleEnemy(enemy, roomIndex);
                }
                roomIndex++;
            }
        }
        gameManager = new GameManager(player, zones);
        startExploration();
    }

    private void startExploration() {

        if (explorationScene != null) explorationScene.stop();

        explorationScene = new ExplorationScene(gameManager);

        explorationScene.setOnEnterCombat(() -> {
            explorationScene.stop();
            CombatController combatController = new CombatController(gameManager.getCombatManager());
            combatScene = new CombatScene(combatController);
            combatScene.setStage(stage);
            combatScene.refresh();

            combatController.setOnVictory(() -> {
                gameManager.endCombat();
                if (gameManager.getCurrentZone().getCurrentRoomIndex() == 4
                        && gameManager.getCurrentRoom().getEnemies().stream().noneMatch(e -> e.isAlive())) {
                    gameManager.setState(GameState.VICTORY);
                    showVictoryScreen();
                } else {
                    startExploration();
                }
            });

            combatController.setOnDefeat(() -> {
                gameManager.respawn();
                startExploration();
            });
            combatController.setOnFlee(() -> {
                gameManager.endCombat();
                startExploration();
            });

            stage.setScene(combatScene.getScene());
        });

        explorationScene.setOnZoneComplete(() -> {
            explorationScene.stop();
            gameManager.advanceZone();
            saver.save(gameManager);
            startExploration();
        });

        explorationScene.setOnSave(() -> {
            saver.save(gameManager);
            explorationScene.showSaveMessage();
        });

        stage.setScene(explorationScene.getScene());
        explorationScene.start();
    }

    private void showVictoryScreen() {
        Player player = gameManager.getPlayer();
        Stats stats = player.getStats();

        VBox overlay = new VBox(20);
        overlay.setAlignment(javafx.geometry.Pos.CENTER);
        overlay.setPadding(new javafx.geometry.Insets(60));
        overlay.setStyle("-fx-background-color: #0a1a0a;");

        javafx.scene.control.Label title = new javafx.scene.control.Label("Hai completato il tempio!");
        title.setFont(new javafx.scene.text.Font("Monospaced", 26));
        title.setStyle("-fx-text-fill: #EF9F27;");

        javafx.scene.control.Label subtitle = new javafx.scene.control.Label(
                "Il ladro " + player.getName() + " è fuggito con il tesoro.");
        subtitle.setFont(new javafx.scene.text.Font("Monospaced", 14));
        subtitle.setStyle("-fx-text-fill: #888;");

        javafx.scene.control.Label statsLabel = new javafx.scene.control.Label(
                "Livello finale:  " + stats.getLevel() + "\n" +
                        "HP:              " + stats.getCurrentHp() + " / " + stats.getMaxHp() + "\n" +
                        "ATK:             " + stats.getAttack() + "\n" +
                        "DEF:             " + stats.getDefense()
        );
        statsLabel.setFont(new javafx.scene.text.Font("Monospaced", 14));
        statsLabel.setStyle("-fx-text-fill: #ccc;");

        javafx.scene.control.Button menuBtn = new javafx.scene.control.Button("Torna al menu  ▶");
        menuBtn.setStyle("""
            -fx-background-color: #854F0B;
            -fx-text-fill: #FAEEDA;
            -fx-font-family: Monospaced;
            -fx-font-size: 14px;
            -fx-background-radius: 4;
            -fx-padding: 12px 28px;
            -fx-cursor: hand;
            """);
        menuBtn.setOnAction(e -> start());

        overlay.getChildren().addAll(title, subtitle, statsLabel, menuBtn);

        javafx.scene.Scene victoryScene = new javafx.scene.Scene(overlay, 800, 600);
        stage.setScene(victoryScene);
    }
}