package it.unicam.cs.mpgc.rpg118708.model;

import java.util.List;
import java.util.Random;

/**
 * Rappresenta un nemico che il giocatore può incontrare in combattimento.
 *
 * <p>Un nemico possiede un set fisso di {@link CombatAction} tra cui sceglie
 * casualmente ad ogni turno. La ricompensa in XP viene assegnata al giocatore
 * alla sconfitta del nemico. {@link Boss} estende questa classe aggiungendo
 * comportamento speciale (enrage).</p>
 */
public class Enemy {

    private static final Random RANDOM = new Random();

    private final String id;
    private final String name;
    private Stats stats;
    private final int xpReward;
    private final List<CombatAction> availableActions;

    /**
     * Crea un nuovo nemico.
     *
     * @param id               identificatore univoco
     * @param name             nome visualizzato
     * @param stats            statistiche di combattimento
     * @param xpReward         XP assegnati al giocatore alla sconfitta
     * @param availableActions azioni disponibili durante il combattimento
     */
    public Enemy(String id, String name, Stats stats, int xpReward, List<CombatAction> availableActions) {
        this.id = id;
        this.name = name;
        this.stats = stats;
        this.xpReward = xpReward;
        this.availableActions = availableActions;
    }

    /** @return {@code true} se il nemico ha ancora HP positivi */
    public boolean isAlive() {
        return !stats.isDead();
    }

    /**
     * Subisce danno ridotto dalla propria difesa.
     *
     * @param amount danno lordo
     */
    public void takeDamage(int amount) {
        stats.takeDamage(amount);
    }

    /**
     * Ripristina HP senza superare il massimo.
     *
     * @param amount HP da ripristinare
     */
    public void heal(int amount) {
        stats.heal(amount);
    }

    /**
     * Sceglie casualmente una delle azioni disponibili per il turno nemico.
     *
     * @return l'azione selezionata
     */
    public CombatAction chooseAction() {
        return availableActions.get(RANDOM.nextInt(availableActions.size()));
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Stats getStats() { return stats; }
    public int getXpReward() { return xpReward; }
    public List<CombatAction> getAvailableActions() { return availableActions; }
}
