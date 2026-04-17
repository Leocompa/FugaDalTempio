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

    /**
     * Attiva la riduzione del danno per il prossimo attacco nemico
     * e rimuove il talismano dall'inventario.
     *
     * @param player  il giocatore che usa il talismano
     * @param context il contesto di combattimento su cui attivare la riduzione
     * @return messaggio descrittivo dell'effetto
     */
    @Override
    public String applyInCombat(Player player, CombatItemContext context) {
        context.activateDamageReduction();
        player.getInventory().removeItem(this);
        return "Talismano attivato — il prossimo attacco nemico sarà dimezzato!";
    }
}
