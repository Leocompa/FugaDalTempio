package it.unicam.cs.mpgc.rpg118708.model;

/**
 * Contesto di combattimento esposto agli oggetti durante il loro utilizzo.
 *
 * <p>Consente agli {@link Item} di modificare lo stato del combattimento
 * senza dipendere direttamente dal motore di gioco ({@code CombatManager}).
 * Il motore implementa questa interfaccia e la passa agli oggetti al momento
 * dell'uso, rispettando il principio DIP.</p>
 */
public interface CombatItemContext {

    /**
     * Aggiunge un bonus temporaneo all'attacco del giocatore per il turno corrente.
     *
     * @param bonus il valore del bonus da aggiungere
     */
    void addTemporaryAttackBonus(int bonus);

    /** Attiva la riduzione del danno per il prossimo attacco nemico. */
    void activateDamageReduction();
}
