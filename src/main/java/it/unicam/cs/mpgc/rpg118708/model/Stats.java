package it.unicam.cs.mpgc.rpg118708.model;

public class Stats {

    private int maxHp;
    private int currentHp;
    private int attack;
    private int defense;
    private int level;
    private int currentXp;
    private int xpToNextLevel;

    public Stats(int maxHp, int attack, int defense, int level) {
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.level = level;
        this.currentXp = 0;
        this.xpToNextLevel = computeXpThreshold(level);
    }

    private int computeXpThreshold(int level) {
        return 100 * level;
    }

    public void takeDamage(int amount) {
        int effective = Math.max(0, amount - defense);
        currentHp = Math.max(0, currentHp - effective);
    }

    public void heal(int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    public boolean isDead() {
        return currentHp <= 0;
    }

    public boolean gainXp(int amount) {
        currentXp += amount;
        if (currentXp >= xpToNextLevel) {
            levelUp();
            return true;
        }
        return false;
    }

    private void levelUp() {
        level++;
        currentXp = currentXp - xpToNextLevel;
        xpToNextLevel = computeXpThreshold(level);
        maxHp += 10;
        currentHp = maxHp;
        attack += 3;
        defense += 1;
    }

    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getLevel() { return level; }
    public int getCurrentXp() { return currentXp; }
    public int getXpToNextLevel() { return xpToNextLevel; }

    public void setCurrentHp(int currentHp) { this.currentHp = Math.max(0, Math.min(maxHp, currentHp)); }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public void setAttack(int attack) { this.attack = attack; }
    public void setDefense(int defense) { this.defense = defense; }
    public void setLevel(int level) { this.level = level; }
    public void setCurrentXp(int currentXp) { this.currentXp = currentXp; }
    public void setXpToNextLevel(int xpToNextLevel) { this.xpToNextLevel = xpToNextLevel; }
}