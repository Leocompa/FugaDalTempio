package it.unicam.cs.mpgc.rpg118708.view.exploration;

import it.unicam.cs.mpgc.rpg118708.view.GameScene;
import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.engine.GameState;
import it.unicam.cs.mpgc.rpg118708.model.Player;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.Set;

/**
 * Scena di esplorazione del tempio con visuale a piattaforme 2D.
 *
 * <p>Gestisce il game loop (tramite {@link AnimationTimer}) e l'input da
 * tastiera, delegando la fisica del personaggio a {@link PlayerPhysics},
 * le interazioni e le collisioni a {@link ExplorationInteractionHandler} e
 * il rendering a {@link ExplorationRenderer}.</p>
 *
 * <p>Le transizioni di scena vengono notificate al
 * {@link it.unicam.cs.mpgc.rpg118708.controller.GameController} tramite
 * callback ({@code onEnterCombat}, {@code onZoneComplete}, {@code onSave},
 * {@code onExit}).</p>
 */
public class ExplorationScene implements GameScene {

    private final int W, H, GROUND_Y;
    private static final int PLAYER_W = 24;

    private final GameManager                  gameManager;
    private final SceneRenderer                renderer;
    private final PlayerPhysics                physics;
    private final ExplorationInteractionHandler handler;
    private       Scene                        scene;
    private       AnimationTimer               gameLoop;

    private final Set<KeyCode> keysPressed = new HashSet<>();
    private long frame = 0;

    private Runnable onSave;
    private Runnable onExit;

    /**
     * Costruisce la scena di esplorazione per la partita corrente.
     *
     * @param gameManager il gestore dello stato di gioco
     */
    public ExplorationScene(GameManager gameManager) {
        javafx.geometry.Rectangle2D screen =
                javafx.stage.Screen.getPrimary().getVisualBounds();
        this.W        = (int) screen.getWidth();
        this.H        = (int) (screen.getHeight() * 0.80);
        this.GROUND_Y = this.H - 100;
        this.gameManager = gameManager;

        Canvas canvas = new Canvas(W, H);
        this.renderer = new ExplorationRenderer(
                canvas.getGraphicsContext2D(), gameManager, W, H, GROUND_Y);
        this.physics  = new PlayerPhysics();
        this.handler  = new ExplorationInteractionHandler(gameManager, renderer, W, GROUND_Y);

        buildScene(canvas);
    }

    private void buildScene(Canvas canvas) {
        VBox root = new VBox();
        root.getStyleClass().add("combat-root");
        root.getChildren().add(canvas);

        javafx.geometry.Rectangle2D screen =
                javafx.stage.Screen.getPrimary().getVisualBounds();
        scene = new Scene(root, screen.getWidth(), screen.getHeight());
        scene.getStylesheets().add(getClass().getResource("/game.css").toExternalForm());

        scene.setOnKeyPressed(e -> {
            keysPressed.add(e.getCode());
            if (e.getCode() == KeyCode.S && (e.isMetaDown() || e.isControlDown())) {
                if (onSave != null) onSave.run();
            }
            if (e.getCode() == KeyCode.ESCAPE) showExitConfirm();
        });
        scene.setOnKeyReleased(e -> keysPressed.remove(e.getCode()));

        gameLoop = new AnimationTimer() {
            private long lastNow = 0;

            @Override
            public void handle(long now) {
                if (lastNow == 0) { lastNow = now; return; }
                double dt = Math.min((now - lastNow) / 1_000_000_000.0, 0.05);
                lastNow = now;
                frame++;
                update(dt);
                renderer.render(frame,
                        handler.isNearExit(), handler.isNearEntrance(),
                        physics.isOnGround(), keysPressed);
            }
        };
    }

    private void showExitConfirm() {
        gameLoop.stop();
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Fuga dal Tempio");
        alert.setHeaderText("Vuoi tornare al menu principale?");
        alert.setContentText("I progressi non salvati andranno persi.");

        javafx.scene.control.ButtonType btnSi =
                new javafx.scene.control.ButtonType("Sì, esci");
        javafx.scene.control.ButtonType btnNo =
                new javafx.scene.control.ButtonType("No, continua",
                        javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnSi, btnNo);
        alert.showAndWait().ifPresent(response -> {
            if (response == btnSi) { if (onExit != null) onExit.run(); }
            else gameLoop.start();
        });
    }

    private void update(double dt) {
        if (handleGameOverInput()) return;
        if (gameManager.getState() != GameState.EXPLORING) return;

        Player player = gameManager.getPlayer();
        physics.handleMovement(player, keysPressed, W - PLAYER_W, dt);
        physics.applyPhysics(player, keysPressed, GROUND_Y, dt);
        if (handler.checkTrapCollision(player)) return;
        if (handler.checkEnemyCollision(player)) return;
        handler.updateNavigationHints(player);
        handler.handleInteractions(player, keysPressed);
    }

    private boolean handleGameOverInput() {
        if (gameManager.getState() == GameState.GAME_OVER) {
            if (keysPressed.contains(KeyCode.R)) {
                gameManager.respawn();
                keysPressed.clear();
            }
            return true;
        }
        return false;
    }

    /** Avvia il game loop. */
    public void start() { gameLoop.start(); }

    /** Ferma il game loop. */
    public void stop()  { gameLoop.stop(); }

    /** Mostra il messaggio di salvataggio avvenuto. */
    public void showSaveMessage() { renderer.showSaveMessage("Partita salvata!"); }

    /** @return la scena JavaFX pronta per essere impostata sullo stage */
    public Scene getScene() { return scene; }

    /**
     * Registra la callback invocata quando il giocatore entra in combattimento.
     *
     * @param cb la callback da invocare
     */
    public void setOnEnterCombat(Runnable cb) { handler.setOnEnterCombat(cb); }

    /**
     * Registra la callback invocata quando tutte le stanze della zona sono completate.
     *
     * @param cb la callback da invocare
     */
    public void setOnZoneComplete(Runnable cb){ handler.setOnZoneComplete(cb); }

    /**
     * Registra la callback invocata quando il giocatore richiede il salvataggio.
     *
     * @param cb la callback da invocare
     */
    public void setOnSave(Runnable cb)        { this.onSave = cb; }

    /**
     * Registra la callback invocata quando il giocatore torna al menu principale.
     *
     * @param cb la callback da invocare
     */
    public void setOnExit(Runnable cb)        { this.onExit = cb; }
}
