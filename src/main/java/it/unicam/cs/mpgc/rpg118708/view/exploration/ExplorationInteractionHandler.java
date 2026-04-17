package it.unicam.cs.mpgc.rpg118708.view.exploration;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.engine.GameState;
import it.unicam.cs.mpgc.rpg118708.model.*;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.Set;

/**
 * Gestisce le collisioni e le interazioni della scena di esplorazione.
 *
 * <p>Responsabilità unica: rilevare collisioni con trappole e nemici,
 * aggiornare i flag di prossimità a uscite/ingressi e rispondere all'input
 * [E] per raccolta oggetti, dialoghi NPC e navigazione tra stanze.
 * Non gestisce fisica né rendering.</p>
 */
class ExplorationInteractionHandler {

    private static final int PLAYER_W       = 24;
    private static final int PLAYER_H       = 32;
    private static final int INTERACT_RANGE = 60;

    private final GameManager         gameManager;
    private final SceneRenderer renderer;
    private final int                 W;
    private final int                 GROUND_Y;

    private boolean  nearExit     = false;
    private boolean  nearEntrance = false;
    private Runnable onEnterCombat;
    private Runnable onZoneComplete;

    /**
     * Costruisce il gestore delle interazioni di esplorazione.
     *
     * @param gameManager il gestore dello stato di gioco
     * @param renderer    il renderer (per triggerare warning e dialoghi)
     * @param w           larghezza del canvas
     * @param groundY     coordinata Y del suolo
     */
    ExplorationInteractionHandler(GameManager gameManager, SceneRenderer renderer,
                                  int w, int groundY) {
        this.gameManager = gameManager;
        this.renderer    = renderer;
        this.W           = w;
        this.GROUND_Y    = groundY;
    }

    /** Registra la callback invocata quando scatta un combattimento. */
    void setOnEnterCombat(Runnable cb)  { this.onEnterCombat  = cb; }

    /** Registra la callback invocata quando la zona è completata. */
    void setOnZoneComplete(Runnable cb) { this.onZoneComplete = cb; }

    /** @return {@code true} se il giocatore è vicino all'uscita e la stanza è libera */
    boolean isNearExit()     { return nearExit; }

    /** @return {@code true} se il giocatore è vicino all'ingresso e non è la prima stanza */
    boolean isNearEntrance() { return nearEntrance; }

    /**
     * Verifica le collisioni con le trappole della stanza corrente.
     *
     * @param player il giocatore
     * @return {@code true} se il giocatore è morto per una trappola
     */
    boolean checkTrapCollision(Player player) {
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
     * Verifica le collisioni con i nemici vivi della stanza corrente.
     * Se trovata, avvia il combattimento dopo un breve ritardo visivo.
     *
     * @param player il giocatore
     * @return {@code true} se è scattato un combattimento
     */
    boolean checkEnemyCollision(Player player) {
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

    /**
     * Aggiorna i flag di prossimità all'uscita e all'ingresso della stanza.
     *
     * @param player il giocatore
     */
    void updateNavigationHints(Player player) {
        nearExit     = player.getX() >= W - PLAYER_W - 80
                    && gameManager.getCurrentRoom().isCleared();
        nearEntrance = player.getX() <= 50
                    && gameManager.getCurrentZone().getCurrentRoomIndex() > 0;
    }

    /**
     * Gestisce la pressione di [E] per interagire con uscita, ingresso,
     * oggetti nella stanza e NPC.
     *
     * @param player      il giocatore
     * @param keysPressed insieme dei tasti premuti
     */
    void handleInteractions(Player player, Set<KeyCode> keysPressed) {
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

    private void collectNearbyItems(Player player) {
        int itemX = W / 2;
        for (Item item : new ArrayList<>(gameManager.getCurrentRoom().getItems())) {
            if (Math.abs(player.getX() - itemX) < INTERACT_RANGE) {
                gameManager.collectItem(item);
            }
        }
    }

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
}
