package it.unicam.cs.mpgc.rpg118708.model;

/**
 * Amuleto equipaggiabile: applica un bonus permanente a difesa e HP massimi
 * finché rimane equipaggiato.
 */
public class Amulet extends Item {

    /** Bonus di difesa applicato all'equip. */
    public static final int DEF_BONUS = 4;

    /** Bonus di HP massimi applicato all'equip. */
    public static final int HP_BONUS  = 10;

    /**
     * Crea un amuleto.
     *
     * @param id    identificatore univoco
     * @param name  nome visualizzato
     * @param value non utilizzato per questo tipo
     */
    public Amulet(String id, String name, int value) {
        super(id, name, value);
    }

    @Override
    public ItemType getType() { return ItemType.AMULET; }

    /**
     * Gli amuleti si equipaggiano tramite flusso separato: questo metodo
     * non produce effetti e restituisce una stringa vuota.
     *
     * @param player  il giocatore
     * @param context il contesto di combattimento
     * @return stringa vuota
     */
    @Override
    public String applyInCombat(Player player, CombatItemContext context) {
        return "";
    }
}
