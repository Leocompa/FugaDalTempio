package it.unicam.cs.mpgc.rpg118708.model;

/**
 * Pozione di cura: ripristina HP al giocatore quando utilizzata in combattimento.
 */
public class Potion extends Item {

    /**
     * Crea una pozione.
     *
     * @param id    identificatore univoco
     * @param name  nome visualizzato
     * @param value quantità di HP ripristinati
     */
    public Potion(String id, String name, int value) {
        super(id, name, value);
    }

    @Override
    public ItemType getType() { return ItemType.POTION; }
}
