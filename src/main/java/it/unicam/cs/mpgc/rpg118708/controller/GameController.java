package it.unicam.cs.mpgc.rpg118708.controller;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.engine.GameState;
import it.unicam.cs.mpgc.rpg118708.model.*;
import it.unicam.cs.mpgc.rpg118708.persistence.GamePersistence;
import it.unicam.cs.mpgc.rpg118708.view.CombatScene;
import it.unicam.cs.mpgc.rpg118708.view.ExplorationScene;
import it.unicam.cs.mpgc.rpg118708.view.SaveSlotScene;
import it.unicam.cs.mpgc.rpg118708.view.StartScene;
import it.unicam.cs.mpgc.rpg118708.view.VictoryScene;
import javafx.stage.Stage;

/**
 * Controller principale dell'applicazione.
 *
 * <p>Coordina il flusso di gioco: gestisce le transizioni tra le scene
 * (menu, esplorazione, combattimento, salvataggio) e delega le operazioni
 * specifiche a collaboratori iniettati tramite costruttore.</p>
 *
 * <p>Dipende dalle interfacce {@link GamePersistence} e {@link WorldFactory}
 * anziché da implementazioni concrete, garantendo che la logica di controllo
 * non sia accoppiata alla tecnologia di persistenza o alla strategia di
 * costruzione del mondo (DIP).</p>
 */
public class GameController {

    private final Stage stage;
    private final GamePersistence persistence;
    private final WorldFactory worldFactory;

    private GameManager gameManager;
    private int currentSaveSlot = 1;

    private StartScene startScene;
    private ExplorationScene explorationScene;
    private CombatScene combatScene;

    /**
     * Crea il controller iniettando le dipendenze necessarie.
     *
     * @param stage        la finestra JavaFX principale
     * @param persistence  la strategia di persistenza (salvataggio/caricamento)
     * @param worldFactory la factory che costruisce il mondo di gioco
     */
    public GameController(Stage stage, GamePersistence persistence, WorldFactory worldFactory) {
        this.stage = stage;
        this.persistence = persistence;
        this.worldFactory = worldFactory;
    }

    /**
     * Mostra la schermata iniziale e registra i listener sui pulsanti.
     */
    public void start() {
        startScene = new StartScene();

        startScene.getNewGameButton().setOnAction(e -> {
            String name = startScene.getPlayerName();
            if (name.isEmpty()) name = "Ladro";
            startNewGame(name);
        });

        startScene.getLoadGameButton().setOnAction(e -> {
            if (persistence.saveExists()) {
                showLoadSlotScreen(() -> stage.setScene(startScene.getScene()));
            } else {
                startScene.getLoadGameButton().setText("Nessun salvataggio trovato");
            }
        });

        stage.setScene(startScene.getScene());
    }

    // -------------------------------------------------------------------------
    // Flusso di gioco
    // -------------------------------------------------------------------------

    private void startNewGame(String playerName) {
        gameManager = new GameManager(new Player(playerName), worldFactory.buildWorld());
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
                gameManager.registerEnemyDefeated();
                boolean lastRoom = gameManager.getCurrentZone().getCurrentRoomIndex() == 4;
                boolean allEnemiesDead = gameManager.getCurrentRoom().getEnemies()
                        .stream().noneMatch(Enemy::isAlive);
                if (lastRoom && allEnemiesDead) {
                    gameManager.setState(GameState.VICTORY);
                    showVictoryScreen();
                } else {
                    startExploration();
                }
            });

            combatController.setOnDefeat(() -> {
                gameManager = new GameManager(
                        new Player(gameManager.getPlayer().getName()),
                        worldFactory.buildWorld());
                startExploration();
            });

            combatController.setOnLoad(() -> {
                if (persistence.saveExists()) {
                    showLoadSlotScreen(() -> stage.setScene(combatScene.getScene()));
                } else {
                    gameManager.respawn();
                    startExploration();
                }
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
            persistence.save(gameManager, currentSaveSlot);
            startExploration();
        });

        explorationScene.setOnSave(this::showSaveSlotScreen);

        explorationScene.setOnExit(() -> {
            explorationScene.stop();
            start();
        });

        stage.setScene(explorationScene.getScene());
        explorationScene.start();
    }

    // -------------------------------------------------------------------------
    // Schermate secondarie
    // -------------------------------------------------------------------------

    private void showVictoryScreen() {
        stage.setScene(new VictoryScene(gameManager.getPlayer(), this::start).getScene());
    }

    private void showSaveSlotScreen() {
        SaveSlotScene saveSlotScene = new SaveSlotScene(persistence, true);
        saveSlotScene.setOnSlotSelected(slot -> {
            if (slot == -1) {
                stage.setScene(explorationScene.getScene());
                explorationScene.start();
                return;
            }
            currentSaveSlot = slot;
            persistence.save(gameManager, slot);
            explorationScene.showSaveMessage();
            stage.setScene(explorationScene.getScene());
            explorationScene.start();
        });
        if (explorationScene != null) explorationScene.stop();
        stage.setScene(saveSlotScene.getScene());
    }

    private void showLoadSlotScreen(Runnable onBack) {
        SaveSlotScene loadSlotScene = new SaveSlotScene(persistence, false);
        loadSlotScene.setOnSlotSelected(slot -> {
            if (slot == -1) {
                if (onBack != null) onBack.run();
                return;
            }
            currentSaveSlot = slot;
            String name = persistence.loadPlayerName(slot);
            if (name.isEmpty()) name = "Ladro";
            gameManager = new GameManager(new Player(name), worldFactory.buildWorld());
            persistence.load(gameManager, slot);
            startExploration();
        });
        stage.setScene(loadSlotScene.getScene());
    }
}
