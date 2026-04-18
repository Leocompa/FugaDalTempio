package it.unicam.cs.mpgc.rpg118708.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gestisce la collezione di oggetti trasportati dal giocatore.
 *
 * <p>L'inventario ha una capienza massima fissa. La lista restituita da
 * {@link #getItems()} è non modificabile: le modifiche passano obbligatoriamente
 * attraverso {@link #addItem(Item)} e {@link #removeItem(Item)}, garantendo
 * che il limite di capienza sia sempre rispettato.</p>
 */
public class Inventory {

    private static final int MAX_SIZE = 10;
    private final List<Item> items;

    /** Crea un inventario vuoto. */
    public Inventory() {
        this.items = new ArrayList<>();
    }

    /**
     * Aggiunge un oggetto all'inventario se c'è spazio disponibile.
     *
     * @param item l'oggetto da aggiungere
     * @return {@code true} se l'oggetto è stato aggiunto, {@code false} se l'inventario è pieno
     */
    public boolean addItem(Item item) {
        if (items.size() >= MAX_SIZE) return false;
        items.add(item);
        return true;
    }

    /**
     * Rimuove un oggetto dall'inventario.
     *
     * @param item l'oggetto da rimuovere
     * @return {@code true} se l'oggetto era presente ed è stato rimosso
     */
    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    /**
     * Verifica se un oggetto è presente nell'inventario.
     *
     * @param item l'oggetto da cercare
     * @return {@code true} se l'oggetto è presente
     */
    public boolean hasItem(Item item) {
        return items.contains(item);
    }

    /**
     * Restituisce una vista non modificabile degli oggetti correnti.
     *
     * @return lista non modificabile degli oggetti nell'inventario
     */
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Verifica se l'inventario ha raggiunto la capienza massima.
     *
     * @return {@code true} se non è possibile aggiungere altri oggetti
     */
    public boolean isFull() {
        return items.size() >= MAX_SIZE;
    }

    /**
     * Restituisce il numero di oggetti attualmente nell'inventario.
     *
     * @return numero di oggetti presenti
     */
    public int size() {
        return items.size();
    }
}
