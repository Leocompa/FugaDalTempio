package it.unicam.cs.mpgc.rpg118708.model;

import java.util.List;

public class Boss extends Enemy {

    private final String title;
    private boolean enraged;

    public Boss(String id, String name, String title, Stats stats, int xpReward, List<CombatAction> availableActions) {
        super(id, name, stats, xpReward, availableActions);
        this.title = title;
        this.enraged = false;
    }

    public boolean checkEnrage() {
        if (!enraged && getStats().getCurrentHp() <= getStats().getMaxHp() / 2) {
            enraged = true;
            getStats().setAttack(getStats().getAttack() + 5);
            return true;
        }
        return false;
    }

    public String getTitle() { return title; }
    public boolean isEnraged() { return enraged; }
}