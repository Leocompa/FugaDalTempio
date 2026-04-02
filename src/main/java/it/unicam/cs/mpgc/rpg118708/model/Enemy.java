package it.unicam.cs.mpgc.rpg118708.model;

import java.util.List;

public class Enemy {

    private final String id;
    private final String name;
    private Stats stats;
    private final int xpReward;
    private final List<CombatAction> availableActions;

    public Enemy(String id, String name, Stats stats, int xpReward, List<CombatAction> availableActions) {
        this.id = id;
        this.name = name;
        this.stats = stats;
        this.xpReward = xpReward;
        this.availableActions = availableActions;
    }

    public boolean isAlive() {
        return !stats.isDead();
    }

    public void takeDamage(int amount) {
        stats.takeDamage(amount);
    }

    public void heal(int amount) {
        stats.heal(amount);
    }

    public CombatAction chooseAction() {
        int index = (int) (Math.random() * availableActions.size());
        return availableActions.get(index);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Stats getStats() { return stats; }
    public int getXpReward() { return xpReward; }
    public List<CombatAction> getAvailableActions() { return availableActions; }
    public void setStats(Stats stats) { this.stats = stats; }
}