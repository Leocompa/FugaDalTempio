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

    @Override
    public boolean isHealing() { return true; }

    /**
     * Ripristina HP al giocatore e rimuove la pozione dall'inventario.
     * Il contesto non viene utilizzato: l'effetto agisce direttamente sul giocatore.
     *
     * @param player  il giocatore che usa la pozione
     * @param context non utilizzato
     * @return messaggio descrittivo dell'effetto
     */
    @Override
    public String applyInCombat(Player player, CombatItemContext context) {
        player.heal(getValue());
        player.getInventory().removeItem(this);
        return "Pozione usata — +" + getValue() + " HP!";
    }
}
