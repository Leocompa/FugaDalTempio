package it.unicam.cs.mpgc.rpg118708.model;

/**
 * Rappresenta un oggetto raccoglibile o utilizzabile nel gioco.
 *
 * <p>Un oggetto è immutabile dopo la creazione: id, nome, tipo e valore non
 * cambiano. Il comportamento associato all'uso di un oggetto è determinato
 * dal suo {@link ItemType} e gestito da
 * {@link it.unicam.cs.mpgc.rpg118708.engine.CombatManager}.</p>
 */
public class Item {

    private final String id;
    private final String name;
    private final ItemType type;
    private final int value;

    /**
     * Crea un oggetto di gioco.
     *
     * @param id    identificatore univoco
     * @param name  nome visualizzato
     * @param type  categoria dell'oggetto
     * @param value valore numerico (es. HP curati, bonus attacco)
     */
    public Item(String id, String name, ItemType type, int value) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public ItemType getType() { return type; }
    public int getValue() { return value; }
}
