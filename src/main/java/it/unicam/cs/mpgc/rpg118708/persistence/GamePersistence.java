package it.unicam.cs.mpgc.rpg118708.persistence;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;

/**
 * Definisce il contratto per il salvataggio e caricamento dello stato di gioco.
 * Permette di sostituire l'implementazione (XML, JSON, database) senza modificare
 * la logica di gioco.
 */
public interface GamePersistence {

    /**
     * Salva lo stato corrente della partita nello slot specificato.
     *
     * @param gameManager il gestore della partita corrente
     * @param slot        il numero dello slot di salvataggio
     */
    void save(GameManager gameManager, int slot);

    /**
     * Carica lo stato di una partita dallo slot specificato.
     *
     * @param gameManager il gestore della partita da popolare
     * @param slot        il numero dello slot da caricare
     */
    void load(GameManager gameManager, int slot);

    /**
     * Verifica se esiste un salvataggio nello slot specificato.
     *
     * @param slot il numero dello slot
     * @return true se il salvataggio esiste
     */
    boolean saveExists(int slot);

    /**
     * Verifica se esiste almeno un salvataggio tra tutti gli slot.
     *
     * @return true se esiste almeno un salvataggio
     */
    boolean saveExists();

    /**
     * Restituisce il nome del giocatore salvato nello slot specificato.
     *
     * @param slot il numero dello slot
     * @return il nome del giocatore, o stringa vuota se non esiste
     */
    String loadPlayerName(int slot);

    /**
     * Restituisce le informazioni sintetiche di uno slot di salvataggio.
     *
     * @param slot il numero dello slot
     * @return le informazioni dello slot, o null se vuoto
     */
    SlotInfo getSlotInfo(int slot);

    /**
     * Restituisce il numero massimo di slot di salvataggio supportati
     * da questa implementazione.
     *
     * @return il numero di slot disponibili
     */
    int getMaxSlots();
}