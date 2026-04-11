package it.unicam.cs.mpgc.rpg118708.model;

public class Player {

    private String name;
    private Stats stats;
    private Inventory inventory;
    private int x;
    private int y;
    private Direction direction;

    public Player(String name) {
        this.name = name;
        this.stats = new Stats(40, 8, 4, 1);
        this.inventory = new Inventory();
        this.x = 0;
        this.y = 0;
        this.direction = Direction.RIGHT;
    }

    public boolean isAlive() {
        return !stats.isDead();
    }

    public boolean gainXp(int amount) {
        return stats.gainXp(amount);
    }

    public void takeDamage(int amount) {
        stats.takeDamage(amount);
    }

    public void heal(int amount) {
        stats.heal(amount);
    }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String getName() { return name; }
    public Stats getStats() { return stats; }
    public Inventory getInventory() { return inventory; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Direction getDirection() { return direction; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setDirection(Direction direction) { this.direction = direction; }
    public void setStats(Stats stats) { this.stats = stats; }
}