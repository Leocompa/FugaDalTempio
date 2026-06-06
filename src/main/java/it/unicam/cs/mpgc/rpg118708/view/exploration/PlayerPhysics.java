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
 *
 * <p>Tutte le costanti sono espresse in px/s o px/s² per garantire un
 * comportamento indipendente dal frame-rate del display.</p>
 */
class PlayerPhysics {

    // Equivalenti esatti dei valori originali a 60 fps (4 px/frame, 1 px/frame², -14 px/frame)
    private static final double SPEED     = 240.0;   // px/s
    private static final int    PLAYER_W  = 24;
    private static final double GRAVITY   = 3600.0;  // px/s²
    private static final double JUMP_VY   = -840.0;  // px/s

    private double  posX     = Double.NaN;
    private double  posY     = Double.NaN;
    private double  vy       = 0.0;
    private boolean onGround = true;

    /** @return {@code true} se il giocatore è attualmente a terra */
    boolean isOnGround() { return onGround; }

    /**
     * Aggiorna la posizione orizzontale e la direzione in base ai tasti premuti.
     *
     * @param player      il giocatore da aggiornare
     * @param keysPressed insieme dei tasti premuti
     * @param maxX        limite destro dello schermo (larghezza canvas - PLAYER_W)
     * @param dt          tempo trascorso dall'ultimo frame in secondi
     */
    void handleMovement(Player player, Set<KeyCode> keysPressed, int maxX, double dt) {
        syncIfExternal(player.getX(), player.getY());
        if (keysPressed.contains(KeyCode.LEFT) || keysPressed.contains(KeyCode.A)) {
            posX -= SPEED * dt;
            player.setDirection(Direction.LEFT);
        }
        if (keysPressed.contains(KeyCode.RIGHT) || keysPressed.contains(KeyCode.D)) {
            posX += SPEED * dt;
            player.setDirection(Direction.RIGHT);
        }
        posX = Math.max(0, Math.min(maxX, posX));
        player.setX((int) Math.round(posX));
    }

    /**
     * Applica gravità, gestisce il salto e aggiorna la posizione verticale.
     *
     * @param player      il giocatore da aggiornare
     * @param keysPressed insieme dei tasti premuti
     * @param groundY     coordinata Y del suolo
     * @param dt          tempo trascorso dall'ultimo frame in secondi
     */
    void applyPhysics(Player player, Set<KeyCode> keysPressed, int groundY, double dt) {
        if ((keysPressed.contains(KeyCode.UP)    || keysPressed.contains(KeyCode.W)
          || keysPressed.contains(KeyCode.SPACE)) && onGround) {
            vy = JUMP_VY;
            onGround = false;
        }
        vy   += GRAVITY * dt;
        posY += vy * dt;
        if (posY >= groundY) {
            posY     = groundY;
            vy       = 0;
            onGround = true;
        }
        player.moveTo((int) Math.round(posX), (int) Math.round(posY));
    }

    // Riallinea le posizioni float se il modello è stato spostato esternamente
    // (es. respawn, cambio stanza), rilevato da una differenza > 2 px.
    private void syncIfExternal(int modelX, int modelY) {
        if (Double.isNaN(posX) || Math.abs(posX - modelX) > 2) posX = modelX;
        if (Double.isNaN(posY) || Math.abs(posY - modelY) > 2) { posY = modelY; vy = 0; }
    }
}
