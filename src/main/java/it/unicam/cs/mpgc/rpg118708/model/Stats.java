package it.unicam.cs.mpgc.rpg118708.model;

import it.unicam.cs.mpgc.rpg118708.model.exception.InvalidStatsException;

/**
 * Contiene e gestisce le statistiche di combattimento di un'entità di gioco.
 *
 * <p>Responsabilità di questa classe: calcolare il danno effettivo tenendo
 * conto della difesa, gestire la progressione di livello tramite XP e applicare
 * o rimuovere bonus temporanei (es. amuleto). Ogni logica numerica di combattimento
 * che riguarda HP, attacco, difesa e livello risiede qui.</p>
 */
public class Stats {

    private int maxHp;
    private int currentHp;
    private int attack;
    private int defense;
    private int level;
    private int currentXp;
    private int xpToNextLevel;

    /**
     * Crea un set di statistiche con HP pieni e XP azzerati.
     *
     * @param maxHp    punti vita massimi (deve essere &gt; 0)
     * @param attack   valore di attacco base (deve essere &ge; 0)
     * @param defense  valore di difesa base (deve essere &ge; 0)
     * @param level    livello iniziale (deve essere &gt; 0)
     * @throws InvalidStatsException se uno dei vincoli numerici non è rispettato
     */
    public Stats(int maxHp, int attack, int defense, int level) {
        if (maxHp <= 0)   throw new InvalidStatsException("maxHp deve essere positivo, ricevuto: " + maxHp);
        if (attack < 0)   throw new InvalidStatsException("attack non può essere negativo, ricevuto: " + attack);
        if (defense < 0)  throw new InvalidStatsException("defense non può essere negativo, ricevuto: " + defense);
        if (level <= 0)   throw new InvalidStatsException("level deve essere positivo, ricevuto: " + level);
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.level = level;
        this.currentXp = 0;
        this.xpToNextLevel = computeXpThreshold(level);
    }

    private int computeXpThreshold(int level) {
        return 20 * level;
    }

    /**
     * Applica danno in ingresso ridotto dalla difesa; gli HP non scendono sotto zero.
     *
     * @param amount danno lordo
     */
    public void takeDamage(int amount) {
        int effective = Math.max(0, amount - defense);
        currentHp = Math.max(0, currentHp - effective);
    }

    /**
     * Ripristina HP senza superare il massimo.
     *
     * @param amount quantità di HP da ripristinare
     */
    public void heal(int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    /** @return {@code true} se gli HP correnti sono zero o meno */
    public boolean isDead() {
        return currentHp <= 0;
    }

    /**
     * Aggiunge XP e gestisce eventuali level-up multipli consecutivi.
     *
     * @param amount quantità di XP da aggiungere
     * @return {@code true} se si è verificato almeno un level-up
     */
    public boolean gainXp(int amount) {
        currentXp += amount;
        boolean leveled = false;
        while (currentXp >= xpToNextLevel) {
            levelUp();
            leveled = true;
        }
        return leveled;
    }

    /**
     * Applica un bonus da oggetto equipaggiato: aumenta la difesa e gli HP massimi.
     *
     * @param defBonus  punti difesa aggiuntivi
     * @param hpBonus   punti HP massimi aggiuntivi (curano anche gli HP correnti)
     */
    public void applyEquipBonus(int defBonus, int hpBonus) {
        this.defense += defBonus;
        this.maxHp += hpBonus;
        this.currentHp = Math.min(currentHp + hpBonus, maxHp);
    }

    /**
     * Rimuove un bonus da oggetto equipaggiato, ripristinando i valori precedenti.
     *
     * @param defBonus  punti difesa da rimuovere
     * @param hpBonus   punti HP massimi da rimuovere
     */
    public void removeEquipBonus(int defBonus, int hpBonus) {
        this.defense = Math.max(0, this.defense - defBonus);
        this.maxHp = Math.max(1, this.maxHp - hpBonus);
        this.currentHp = Math.min(currentHp, maxHp);
    }

    private void levelUp() {
        level++;
        currentXp = currentXp - xpToNextLevel;
        xpToNextLevel = computeXpThreshold(level);
        maxHp += 5;
        currentHp = maxHp;
        attack += 2;
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
    /**
     * Imposta gli HP massimi. Non aggiorna {@code currentHp}: invocare
     * {@link #setCurrentHp} separatamente per mantenere la coerenza dei valori.
     *
     * @param maxHp i nuovi HP massimi
     */
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public void setAttack(int attack) { this.attack = attack; }
    public void setDefense(int defense) { this.defense = defense; }
    public void setLevel(int level) { this.level = level; }
    public void setCurrentXp(int currentXp) { this.currentXp = currentXp; }
    public void setXpToNextLevel(int xpToNextLevel) { this.xpToNextLevel = xpToNextLevel; }
}
