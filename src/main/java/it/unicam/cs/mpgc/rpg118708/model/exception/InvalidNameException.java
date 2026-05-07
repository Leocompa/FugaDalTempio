package it.unicam.cs.mpgc.rpg118708.model.exception;

import it.unicam.cs.mpgc.rpg118708.model.*;

/**
 * Eccezione lanciata quando un identificatore o un nome obbligatorio
 * risulta {@code null} o vuoto.
 *
 * <p>Viene sollevata nei costruttori delle entità del dominio
 * ({@link Player}, {@link Enemy}, {@link Item}, {@link Room}, {@link Zone}, {@link NPC})
 * per segnalare un valore non valido al boundary di creazione dell'oggetto.</p>
 */
public class InvalidNameException extends RuntimeException {

    /**
     * Crea l'eccezione con un messaggio descrittivo del campo non valido.
     *
     * @param message descrizione del problema
     */
    public InvalidNameException(String message) {
        super(message);
    }
}
