package it.unicam.cs.mpgc.rpg118708.view.exploration;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.engine.GameState;
import it.unicam.cs.mpgc.rpg118708.model.*;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Scena di esplorazione del tempio con visuale a piattaforme 2D.
 *
 * <p>Gestisce il game loop (tramite {@link AnimationTimer}), l'input da tastiera,
 * la fisica del personaggio (gravità, salto, movimento), il rilevamento delle
 * collisioni e le interazioni con gli elementi della stanza corrente.</p>
 *
 * <p>Il rendering grafico è delegato a {@link ExplorationRenderer}, che riceve
 * lo stato necessario ad ogni frame. Le transizioni di scena vengono notificate
 * al {@link it.unicam.cs.mpgc.rpg118708.controller.GameController} tramite
 * callback ({@code onEnterCombat}, {@code onZoneComplete}, {@code onSave},
 * {@code onExit}).</p>
 */
public class ExplorationScene {

    private final int W;
    private final int H;
    private final int GROUND_Y;
    private static final int PLAYER_SPEED   = 4;
    private static final int PLAYER_W       = 24;
    private static final int PLAYER_H       = 32;
    private static final int GRAVITY        = 1;
    private static final int JUMP_FORCE     = -14;
    private static final int INTERACT_RANGE = 60;

    private final GameManager       gameManager;
    private final ExplorationRenderer renderer;
    private       Scene              scene;
    private       AnimationTimer     gameLoop;

    private final Set<KeyCode> keysPressed = new HashSet<>();
    private long    frame        = 0;
    private int     playerVY     = 0;
    private boolean onGround     = true;
    private boolean nearExit     = false;
    private boolean nearEntrance = false;

    private Runnable onEnterCombat;
    private Runnable onZoneComplete;
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

        buildScene(canvas);
    }

    private void buildScene(Canvas canvas) {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #0d0d14;");
        root.getChildren().add(canvas);

        javafx.geometry.Rectangle2D screen =
                javafx.stage.Screen.getPrimary().getVisualBounds();
        scene = new Scene(root, screen.getWidth(), screen.getHeight());

        scene.setOnKeyPressed(e -> {
            keysPressed.add(e.getCode());
            if (e.getCode() == KeyCode.S && (e.isMetaDown() || e.isControlDown())) {
                if (onSave != null) onSave.run();
            }
            if (e.getCode() == KeyCode.ESCAPE) showExitConfirm();
        });
        scene.setOnKeyReleased(e -> keysPressed.remove(e.getCode()));

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                frame++;
                update();
                renderer.render(frame, nearExit, nearEntrance, onGround, keysPressed);
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


    private void update() {
        if (handleGameOverInput()) return;
        if (gameManager.getState() != GameState.EXPLORING) return;

        Player player = gameManager.getPlayer();
        handleMovement(player);
        applyPhysics(player);
        if (checkTrapCollision(player)) return;
        if (checkEnemyCollision(player)) return;
        updateNavigationHints(player);
        handleInteractions(player);
    }

    /** Gestisce il respawn quando il giocatore è in GAME_OVER e preme R. */
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

    /** Aggiorna posizione orizzontale e direzione in base ai tasti premuti. */
    private void handleMovement(Player player) {
        int px = player.getX();
        if (keysPressed.contains(KeyCode.LEFT) || keysPressed.contains(KeyCode.A)) {
            px -= PLAYER_SPEED;
            player.setDirection(Direction.LEFT);
        }
        if (keysPressed.contains(KeyCode.RIGHT) || keysPressed.contains(KeyCode.D)) {
            px += PLAYER_SPEED;
            player.setDirection(Direction.RIGHT);
        }
        player.setX(Math.max(0, Math.min(W - PLAYER_W, px)));
    }

    /** Applica gravità, salto e vincolo al suolo. */
    private void applyPhysics(Player player) {
        if ((keysPressed.contains(KeyCode.UP)    || keysPressed.contains(KeyCode.W)
          || keysPressed.contains(KeyCode.SPACE)) && onGround) {
            playerVY = JUMP_FORCE;
            onGround = false;
        }
        playerVY += GRAVITY;
        int py = player.getY() + playerVY;
        if (py >= GROUND_Y) {
            py       = GROUND_Y;
            playerVY = 0;
            onGround = true;
        }
        player.moveTo(player.getX(), py);
    }

    /**
     * Verifica la collisione con le trappole della stanza.
     *
     * @return {@code true} se il giocatore è morto per una trappola
     */
    private boolean checkTrapCollision(Player player) {
        int trapX = W / 4;
        for (Trap trap : gameManager.getCurrentRoom().getTraps()) {
            trap.setTrapX(trapX);
            trap.setTrapY(GROUND_Y + PLAYER_H - 14);
            trap.trigger(player);
        }
        if (!player.isAlive()) {
            gameManager.setState(GameState.GAME_OVER);
            return true;
        }
        return false;
    }

    /**
     * Verifica la collisione con i nemici vivi della stanza.
     * Se trovata, avvia il combattimento dopo un breve ritardo.
     *
     * @return {@code true} se è scattato un combattimento
     */
    private boolean checkEnemyCollision(Player player) {
        int enemyX = (int)(W * 0.55);
        for (Enemy enemy : gameManager.getCurrentRoom().getEnemies()) {
            if (enemy.isAlive() && collides(
                    player.getX(), player.getY(), PLAYER_W, PLAYER_H,
                    enemyX, GROUND_Y, 32, 40)) {
                renderer.triggerEnemyWarning();
                javafx.animation.PauseTransition pause =
                        new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
                pause.setOnFinished(e -> {
                    gameManager.enterCombat(enemy);
                    if (onEnterCombat != null) onEnterCombat.run();
                });
                pause.play();
                gameManager.setState(GameState.COMBAT);
                return true;
            }
        }
        return false;
    }

    /** Aggiorna i flag di prossimità a uscita e ingresso. */
    private void updateNavigationHints(Player player) {
        nearExit     = player.getX() >= W - PLAYER_W - 80
                    && gameManager.getCurrentRoom().isCleared();
        nearEntrance = player.getX() <= 50
                    && gameManager.getCurrentZone().getCurrentRoomIndex() > 0;
    }

    /** Gestisce la pressione di E per interagire con uscita, ingresso, oggetti e NPC. */
    private void handleInteractions(Player player) {
        if (nearExit && keysPressed.contains(KeyCode.E)) {
            keysPressed.clear();
            nearExit = false;
            if (!gameManager.advanceRoom()) {
                if (onZoneComplete != null) onZoneComplete.run();
            } else {
                player.moveTo(40, GROUND_Y);
                renderer.clearDialogue();
            }
            return;
        }

        if (nearEntrance && keysPressed.contains(KeyCode.E)) {
            keysPressed.clear();
            nearEntrance = false;
            gameManager.goBackRoom();
            player.moveTo(W - 80, GROUND_Y);
            return;
        }

        if (keysPressed.contains(KeyCode.E)) {
            collectNearbyItems(player);
            interactWithNearbyNPCs(player);
            keysPressed.remove(KeyCode.E);
        }
    }

    /** Raccoglie gli oggetti nella stanza se il giocatore è abbastanza vicino. */
    private void collectNearbyItems(Player player) {
        int itemX = W / 2;
        for (Item item : new ArrayList<>(gameManager.getCurrentRoom().getItems())) {
            if (Math.abs(player.getX() - itemX) < INTERACT_RANGE) {
                gameManager.collectItem(item);
            }
        }
    }

    /** Avvia il dialogo con gli NPC vicini e consegna la ricompensa se disponibile. */
    private void interactWithNearbyNPCs(Player player) {
        int npcX = (int)(W * 0.65);
        for (NPC npc : gameManager.getCurrentRoom().getNpcs()) {
            if (Math.abs(player.getX() - npcX) < INTERACT_RANGE) {
                Item reward = npc.collectReward();
                String text;
                if (reward != null && player.getInventory().addItem(reward)) {
                    text = npc.getName() + ": \"" + npc.getDialogue() + "\"\n"
                         + "[Hai ricevuto: " + reward.getName() + "!]";
                } else {
                    text = npc.getName() + ": \"" + npc.getDialogue() + "\"";
                }
                showDialogue(text);
            }
        }
    }

    private void showDialogue(String text) {
        gameManager.setState(GameState.DIALOGUE);
        renderer.showDialogue(text);
        javafx.animation.PauseTransition pause =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        pause.setOnFinished(e -> {
            gameManager.setState(GameState.EXPLORING);
            renderer.clearDialogue();
        });
        pause.play();
    }

    private boolean collides(int ax, int ay, int aw, int ah,
                             int bx, int by, int bw, int bh) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }

    /** Avvia il game loop. */
    public void start() { gameLoop.start(); }

    /** Ferma il game loop. */
    public void stop()  { gameLoop.stop(); }

    /** Mostra il messaggio di salvataggio avvenuto. */
    public void showSaveMessage() { renderer.showSaveMessage("Partita salvata!"); }

    /** @return la scena JavaFX pronta per essere impostata sullo stage */
    public Scene getScene() { return scene; }

    /** Registra la callback invocata quando il giocatore entra in combattimento. */
    public void setOnEnterCombat(Runnable cb) { this.onEnterCombat  = cb; }

    /** Registra la callback invocata quando tutte le stanze della zona sono completate. */
    public void setOnZoneComplete(Runnable cb){ this.onZoneComplete = cb; }

    /** Registra la callback invocata quando il giocatore richiede il salvataggio. */
    public void setOnSave(Runnable cb)        { this.onSave         = cb; }

    /** Registra la callback invocata quando il giocatore torna al menu principale. */
    public void setOnExit(Runnable cb)        { this.onExit         = cb; }
}
