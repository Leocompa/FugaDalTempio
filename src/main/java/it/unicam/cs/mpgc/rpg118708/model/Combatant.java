package it.unicam.cs.mpgc.rpg118708.model;

/**
 * Contratto comune per le entità che partecipano al combattimento.
 *
 * <p>Unifica {@link Player} e {@link Enemy} sotto un'interfaccia condivisa,
 * consentendo di trattare entrambi in modo uniforme nelle operazioni di
 * danno, cura e verifica della vitalità senza conoscerne il tipo concreto.</p>
 */
public interface Combatant {

    /** @return il nome visualizzato dell'entità */
    String getName();

    /** @return le statistiche di combattimento dell'entità */
    Stats getStats();

    /** @return {@code true} se l'entità è ancora in vita */
    boolean isAlive();

    /**
     * Applica danno lordo all'entità, ridotto dalla propria difesa.
     *
     * @param amount danno lordo
     */
    void takeDamage(int amount);

    /**
     * Ripristina HP senza superare il massimo.
     *
     * @param amount HP da ripristinare
     */
    void heal(int amount);
}
