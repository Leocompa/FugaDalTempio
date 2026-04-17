package it.unicam.cs.mpgc.rpg118708.model.exception;

import it.unicam.cs.mpgc.rpg118708.model.Stats;

/**
 * Eccezione lanciata quando i valori delle statistiche di combattimento
 * non rispettano i vincoli del dominio.
 *
 * <p>Viene sollevata nel costruttore di {@link Stats} quando i valori
 * forniti violano le regole di base: gli HP massimi devono essere positivi,
 * attacco e difesa non possono essere negativi.</p>
 */
public class InvalidStatsException extends RuntimeException {

    /**
     * Crea l'eccezione con un messaggio descrittivo del vincolo violato.
     *
     * @param message descrizione del problema (es. "maxHp deve essere positivo")
     */
    public InvalidStatsException(String message) {
        super(message);
    }
}
