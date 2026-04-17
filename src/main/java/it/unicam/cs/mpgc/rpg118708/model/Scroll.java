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

    /**
     * Aggiunge un bonus temporaneo all'attacco del giocatore per il turno corrente
     * e rimuove la pergamena dall'inventario.
     *
     * @param player  il giocatore che usa la pergamena
     * @param context il contesto di combattimento su cui applicare il bonus
     * @return messaggio descrittivo dell'effetto
     */
    @Override
    public String applyInCombat(Player player, CombatItemContext context) {
        context.addTemporaryAttackBonus(getValue());
        player.getInventory().removeItem(this);
        return "Pergamena usata — ATK +" + getValue() + " per questo turno!";
    }
}
