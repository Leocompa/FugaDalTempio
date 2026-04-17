package it.unicam.cs.mpgc.rpg118708.model;

import it.unicam.cs.mpgc.rpg118708.model.exception.InvalidNameException;

/**
 * Rappresenta il personaggio controllato dal giocatore.
 *
 * <p>Aggrega le statistiche di combattimento ({@link Stats}), l'inventario
 * degli oggetti ({@link Inventory}) e la posizione nella scena di esplorazione.
 * Delega le operazioni di danno, cura e guadagno XP alla classe {@link Stats},
 * mantenendo la propria responsabilità sulla gestione degli oggetti equipaggiati
 * e sul movimento.</p>
 */
public class Player implements Combatant {

    private String name;
    private Stats stats;
    private Inventory inventory;
    private int x;
    private int y;
    private Direction direction;
    private Item equippedItem;

    /**
     * Crea un nuovo giocatore con le statistiche base e inventario vuoto.
     *
     * @param name il nome del personaggio (non null né vuoto)
     * @throws InvalidNameException se {@code name} è null o vuoto
     */
    public Player(String name) {
        if (name == null || name.isBlank()) throw new InvalidNameException("il nome del giocatore non può essere null o vuoto");
        this.name = name;
        this.stats = new Stats(40, 8, 4, 1);
        this.inventory = new Inventory();
        this.x = 0;
        this.y = 0;
        this.direction = Direction.RIGHT;
    }

    /** @return {@code true} se il giocatore ha HP maggiori di zero */
    public boolean isAlive() {
        return !stats.isDead();
    }

    /**
     * Aggiunge XP al giocatore; può innescare un level-up.
     *
     * @param amount quantità di XP da aggiungere
     * @return {@code true} se si è verificato almeno un level-up
     */
    public boolean gainXp(int amount) {
        return stats.gainXp(amount);
    }

    /**
     * Subisce danni ridotti dalla difesa.
     *
     * @param amount danno lordo in ingresso
     */
    public void takeDamage(int amount) {
        stats.takeDamage(amount);
    }

    /**
     * Ripristina HP fino al massimo consentito.
     *
     * @param amount quantità di HP da ripristinare
     */
    public void heal(int amount) {
        stats.heal(amount);
    }

    /**
     * Aggiorna la posizione del personaggio nella scena.
     *
     * @param x coordinata orizzontale
     * @param y coordinata verticale
     */
    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Equipaggia un amuleto, sostituendo l'eventuale oggetto già equipaggiato.
     *
     * @param item l'oggetto da equipaggiare
     * @return {@code true} se l'oggetto è stato equipaggiato con successo
     */
    public boolean equip(Item item) {
        if (item instanceof Amulet) {
            this.equippedItem = item;
            return true;
        }
        return false;
    }

    /** Rimuove l'oggetto attualmente equipaggiato. */
    public void unequip() { this.equippedItem = null; }

    public Item getEquippedItem() { return equippedItem; }
    public boolean hasEquipped() { return equippedItem != null; }
    public String getName() { return name; }
    public Stats getStats() { return stats; }
    public Inventory getInventory() { return inventory; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Direction getDirection() { return direction; }
    public void setX(int x) { this.x = x; }
    public void setDirection(Direction direction) { this.direction = direction; }
}
