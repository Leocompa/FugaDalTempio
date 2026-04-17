package it.unicam.cs.mpgc.rpg118708.model;

import it.unicam.cs.mpgc.rpg118708.model.exception.InvalidNameException;
import it.unicam.cs.mpgc.rpg118708.model.exception.InvalidStatsException;
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
public class Enemy implements Combatant {

    private static final Random RANDOM = new Random();

    private final String id;
    private final String name;
    private Stats stats;
    private final int xpReward;
    private final List<CombatAction> availableActions;

    /**
     * Crea un nuovo nemico.
     *
     * @param id               identificatore univoco (non null né vuoto)
     * @param name             nome visualizzato (non null né vuoto)
     * @param stats            statistiche di combattimento (non null)
     * @param xpReward         XP assegnati al giocatore alla sconfitta
     * @param availableActions azioni disponibili durante il combattimento (non null)
     * @throws InvalidNameException  se {@code id} o {@code name} sono null o vuoti
     * @throws InvalidStatsException se {@code stats} è null
     */
    public Enemy(String id, String name, Stats stats, int xpReward, List<CombatAction> availableActions) {
        if (id == null || id.isBlank())     throw new InvalidNameException("l'id del nemico non può essere null o vuoto");
        if (name == null || name.isBlank()) throw new InvalidNameException("il nome del nemico non può essere null o vuoto");
        if (stats == null)                  throw new InvalidStatsException("le statistiche del nemico non possono essere null");
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
     * Invocato dal motore di combattimento dopo ogni danno subito.
     * Le sottoclassi possono sovrascriverlo per reagire (es. enrage del boss).
     */
    public void onDamageTaken() {}

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
