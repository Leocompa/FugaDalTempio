package it.unicam.cs.mpgc.rpg118708.model;

import java.util.List;

/**
 * Nemico speciale che funge da boss di zona.
 *
 * <p>Estende {@link Enemy} aggiungendo il meccanismo di <em>enrage</em>:
 * quando gli HP scendono sotto il 50% del massimo, il boss entra in stato
 * di furia e aumenta il proprio attacco. L'enrage avviene una sola volta
 * per combattimento.</p>
 */
public class Boss extends Enemy {

    private static final int ENRAGE_ATTACK_BONUS = 5;

    private final String title;
    private boolean enraged;

    /**
     * Crea un nuovo boss.
     *
     * @param id               identificatore univoco
     * @param name             nome visualizzato
     * @param title            titolo (es. "Signore del Caos")
     * @param stats            statistiche di combattimento
     * @param xpReward         XP assegnati alla sconfitta
     * @param availableActions azioni disponibili durante il combattimento
     */
    public Boss(String id, String name, String title, Stats stats, int xpReward,
                List<CombatAction> availableActions) {
        super(id, name, stats, xpReward, availableActions);
        this.title = title;
        this.enraged = false;
    }

    /**
     * Verifica se il boss deve entrare in stato di furia e, se sì, lo attiva.
     * L'attivazione avviene una sola volta: quando gli HP correnti scendono
     * al di sotto del 50% del massimo.
     *
     * @return {@code true} se l'enrage è appena stato attivato in questa chiamata
     */
    public boolean checkEnrage() {
        if (!enraged && getStats().getCurrentHp() <= getStats().getMaxHp() / 2) {
            enraged = true;
            getStats().setAttack(getStats().getAttack() + ENRAGE_ATTACK_BONUS);
            return true;
        }
        return false;
    }

    /**
     * Invocato dopo ogni danno subito: verifica e attiva l'enrage se necessario.
     */
    @Override
    public void onDamageTaken() { checkEnrage(); }

    public String getTitle() { return title; }
    public boolean isEnraged() { return enraged; }
}
