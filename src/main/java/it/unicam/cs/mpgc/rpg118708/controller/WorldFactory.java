package it.unicam.cs.mpgc.rpg118708.controller;

import it.unicam.cs.mpgc.rpg118708.model.Zone;

import java.util.List;

/**
 * Definisce il contratto per la costruzione del mondo di gioco.
 *
 * <p>Separare la creazione del mondo dalla logica del controller permette di
 * sostituire o estendere la generazione delle zone senza modificare
 * {@link GameController}: ad esempio, si potrebbe caricare il mondo da un
 * file di configurazione XML o generarlo proceduralmente implementando
 * questa interfaccia.</p>
 */
public interface WorldFactory {

    /**
     * Costruisce e restituisce la lista completa delle zone di gioco.
     * Le zone restituite sono pronte per essere usate dal {@link GameController}:
     * nemici già creati e scalati in base alla loro posizione nella progressione.
     *
     * @return lista ordinata delle zone che compongono il mondo di gioco
     */
    List<Zone> buildWorld();
}
