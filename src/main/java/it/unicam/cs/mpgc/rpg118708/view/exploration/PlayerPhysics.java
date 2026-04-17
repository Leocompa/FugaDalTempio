package it.unicam.cs.mpgc.rpg118708.view.exploration;

import it.unicam.cs.mpgc.rpg118708.model.Direction;
import it.unicam.cs.mpgc.rpg118708.model.Player;
import javafx.scene.input.KeyCode;

import java.util.Set;

/**
 * Gestisce la fisica del giocatore durante l'esplorazione.
 *
 * <p>Responsabilità unica: aggiornare posizione e velocità verticale del
 * giocatore in base all'input e alla gravità. Non conosce la scena né il
 * renderer.</p>
 */
class PlayerPhysics {

    private static final int PLAYER_SPEED = 4;
    private static final int PLAYER_W     = 24;
    private static final int GRAVITY      = 1;
    private static final int JUMP_FORCE   = -14;

    private int     playerVY = 0;
    private boolean onGround = true;

    /** @return {@code true} se il giocatore è attualmente a terra */
    boolean isOnGround() { return onGround; }

    /**
     * Aggiorna la posizione orizzontale e la direzione in base ai tasti premuti.
     *
     * @param player      il giocatore da aggiornare
     * @param keysPressed insieme dei tasti premuti
     * @param maxX        limite destro dello schermo (larghezza canvas - PLAYER_W)
     */
    void handleMovement(Player player, Set<KeyCode> keysPressed, int maxX) {
        int px = player.getX();
        if (keysPressed.contains(KeyCode.LEFT) || keysPressed.contains(KeyCode.A)) {
            px -= PLAYER_SPEED;
            player.setDirection(Direction.LEFT);
        }
        if (keysPressed.contains(KeyCode.RIGHT) || keysPressed.contains(KeyCode.D)) {
            px += PLAYER_SPEED;
            player.setDirection(Direction.RIGHT);
        }
        player.setX(Math.max(0, Math.min(maxX, px)));
    }

    /**
     * Applica gravità, gestisce il salto e aggiorna la posizione verticale.
     *
     * @param player      il giocatore da aggiornare
     * @param keysPressed insieme dei tasti premuti
     * @param groundY     coordinata Y del suolo
     */
    void applyPhysics(Player player, Set<KeyCode> keysPressed, int groundY) {
        if ((keysPressed.contains(KeyCode.UP)    || keysPressed.contains(KeyCode.W)
          || keysPressed.contains(KeyCode.SPACE)) && onGround) {
            playerVY = JUMP_FORCE;
            onGround = false;
        }
        playerVY += GRAVITY;
        int py = player.getY() + playerVY;
        if (py >= groundY) {
            py       = groundY;
            playerVY = 0;
            onGround = true;
        }
        player.moveTo(player.getX(), py);
    }
}
