package it.unicam.cs.mpgc.rpg118708.model;

/**
 * Amuleto equipaggiabile: applica un bonus permanente a difesa e HP massimi
 * finché rimane equipaggiato.
 */
public class Amulet extends Item {

    /**
     * Crea un amuleto.
     *
     * @param id    identificatore univoco
     * @param name  nome visualizzato
     * @param value non utilizzato per questo tipo (convenzione: 0)
     */
    public Amulet(String id, String name, int value) {
        super(id, name, value);
    }

    @Override
    public ItemType getType() { return ItemType.AMULET; }
}
