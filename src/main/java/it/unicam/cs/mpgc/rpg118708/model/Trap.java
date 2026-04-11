package it.unicam.cs.mpgc.rpg118708.model;

public class Trap {

    private final String id;
    private final int damage;
    private boolean active;
    private static final int TRAP_X = 200;
    private static final int TRAP_WIDTH = 16;
    private static final int COOLDOWN = 90;
    private int cooldownTimer = 0;
    private int trapX = 200;

    public Trap(String id, int damage) {
        this.id = id;
        this.damage = damage;
        this.active = true;
    }

    public void setTrapX(int x) { this.trapX = x; }

    public void trigger(Player player) {
        if (!active) return;
        if (cooldownTimer > 0) {
            cooldownTimer--;
            return;
        }

        int px = player.getX();
        int py = player.getY();
        int pw = 24;
        int ph = 32;

        int trapY = 488;
        int trapH = 14;

        boolean overlapsX = px + pw > trapX + 4 && px < trapX + TRAP_WIDTH - 4;
        boolean overlapsY = py + ph > trapY && py < trapY + trapH;

        if (overlapsX && overlapsY) {
            player.takeDamage(damage);
            cooldownTimer = COOLDOWN;
        }
    }

    public void deactivate() { this.active = false; }
    public boolean isActive() { return active; }
    public String getId() { return id; }
    public int getDamage() { return damage; }
}