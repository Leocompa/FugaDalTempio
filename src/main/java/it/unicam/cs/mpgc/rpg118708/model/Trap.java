package it.unicam.cs.mpgc.rpg118708.model;

public class Trap {

    private final String id;
    private final int damage;
    private boolean active;

    public Trap(String id, int damage) {
        this.id = id;
        this.damage = damage;
        this.active = true;
    }

    public void trigger(Player player) {
        if (active) {
            player.takeDamage(damage);
        }
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() { return active; }
    public String getId() { return id; }
    public int getDamage() { return damage; }
}