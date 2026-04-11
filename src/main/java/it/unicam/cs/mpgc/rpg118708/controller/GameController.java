package it.unicam.cs.mpgc.rpg118708.controller;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.model.*;
import it.unicam.cs.mpgc.rpg118708.persistence.GameLoader;
import it.unicam.cs.mpgc.rpg118708.persistence.GameSaver;
import it.unicam.cs.mpgc.rpg118708.view.CombatScene;
import it.unicam.cs.mpgc.rpg118708.view.ExplorationScene;
import it.unicam.cs.mpgc.rpg118708.view.StartScene;
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
                String name = startScene.getPlayerName();
                if (name.isEmpty()) name = "Ladro";
                startNewGame(name);
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
        saver.save(gameManager);

        if (explorationScene != null) explorationScene.stop();

        explorationScene = new ExplorationScene(gameManager);

        explorationScene.setOnEnterCombat(() -> {
            explorationScene.stop();
            CombatController combatController = new CombatController(gameManager.getCombatManager());
            combatScene = new CombatScene(combatController);
            combatScene.refresh();

            combatController.setOnVictory(() -> {
                gameManager.endCombat();
                startExploration();
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
            System.out.println("Partita salvata.");
        });

        stage.setScene(explorationScene.getScene());
        explorationScene.start();
    }
}