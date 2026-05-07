package it.unicam.cs.mpgc.rpg118708.model;

/**
 * Rappresenta una trappola posizionata in una stanza del tempio.
 *
 * <p>La trappola infligge danno al giocatore quando le loro hitbox si sovrappongono.
 * Dopo ogni attivazione entra in cooldown per evitare danni ripetuti nello stesso
 * fotogramma. Può essere disattivata permanentemente tramite {@link #deactivate()}.</p>
 */
public class Trap {

    private static final int TRAP_WIDTH  = 16;
    private static final int COOLDOWN    = 90;
    private static final int PLAYER_W   = 24;
    private static final int PLAYER_H   = 32;

    private final String id;
    private final int damage;
    private boolean active;
    private int cooldownTimer = 0;
    private int trapX = 200;
    private int trapY = 488;

    /**
     * Crea una trappola attiva con la posizione di default.
     *
     * @param id     identificatore univoco
     * @param damage danno inflitto al giocatore ad ogni attivazione
     */
    public Trap(String id, int damage) {
        this.id = id;
        this.damage = damage;
        this.active = true;
    }

    /**
     * Verifica la collisione con il giocatore e, se presente, infligge danno
     * e avvia il cooldown.
     *
     * @param player il giocatore da controllare
     */
    public void trigger(Player player) {
        if (!active) return;
        if (cooldownTimer > 0) {
            cooldownTimer--;
            return;
        }

        int px    = player.getX();
        int py    = player.getY();
        int trapH = 14;

        boolean overlapsX = px + PLAYER_W > trapX + 4 && px < trapX + TRAP_WIDTH - 4;
        boolean overlapsY = py + PLAYER_H > trapY && py < trapY + trapH;

        if (overlapsX && overlapsY) {
            player.takeDamage(damage);
            cooldownTimer = COOLDOWN;
        }
    }

    /** Disattiva permanentemente la trappola. */
    public void deactivate() { this.active = false; }

    public boolean isActive() { return active; }
    public String getId() { return id; }
    public int getDamage() { return damage; }
    public void setTrapX(int x) { this.trapX = x; }
    public void setTrapY(int y) { this.trapY = y; }
}
