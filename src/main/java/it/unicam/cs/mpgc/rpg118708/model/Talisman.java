package it.unicam.cs.mpgc.rpg118708.model;

/**
 * Talismano della luna: dimezza il danno del prossimo attacco nemico
 * nel combattimento corrente.
 */
public class Talisman extends Item {

    /**
     * Crea un talismano.
     *
     * @param id    identificatore univoco
     * @param name  nome visualizzato
     * @param value non utilizzato per questo tipo (convenzione: 0)
     */
    public Talisman(String id, String name, int value) {
        super(id, name, value);
    }

    @Override
    public ItemType getType() { return ItemType.TALISMAN; }
}
