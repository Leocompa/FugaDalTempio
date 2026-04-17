package it.unicam.cs.mpgc.rpg118708.model;

/**
 * Pergamena di fuoco: aumenta l'attacco del giocatore per un singolo turno
 * di combattimento.
 */
public class Scroll extends Item {

    /**
     * Crea una pergamena.
     *
     * @param id    identificatore univoco
     * @param name  nome visualizzato
     * @param value bonus di attacco temporaneo
     */
    public Scroll(String id, String name, int value) {
        super(id, name, value);
    }

    @Override
    public ItemType getType() { return ItemType.SCROLL; }
}
