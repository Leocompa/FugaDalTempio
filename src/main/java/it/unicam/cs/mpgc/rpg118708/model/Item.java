package it.unicam.cs.mpgc.rpg118708.model;

import it.unicam.cs.mpgc.rpg118708.model.exception.InvalidNameException;

/**
 * Rappresenta un oggetto raccoglibile o utilizzabile nel gioco.
 *
 * <p>Classe astratta: ogni sottoclasse ({@link Potion}, {@link Amulet},
 * {@link Scroll}, {@link Talisman}) incapsula le caratteristiche specifiche
 * del proprio tipo. Aggiungere un nuovo tipo di oggetto richiede solo la
 * creazione di una nuova sottoclasse, senza modificare il codice esistente.</p>
 *
 * <p>Il metodo factory {@link #create(String, String, ItemType, int)} consente
 * di ricostruire un oggetto dal tipo persistito in formato {@link ItemType},
 * usato dalla persistenza XML.</p>
 */
public abstract class Item {

    private final String id;
    private final String name;
    private final int value;

    /**
     * Crea un oggetto con i campi comuni a tutte le sottoclassi.
     *
     * @param id    identificatore univoco (non null né vuoto)
     * @param name  nome visualizzato (non null né vuoto)
     * @param value valore numerico (es. HP curati, bonus attacco)
     * @throws InvalidNameException se {@code id} o {@code name} sono null o vuoti
     */
    protected Item(String id, String name, int value) {
        if (id == null || id.isBlank())     throw new InvalidNameException("l'id dell'oggetto non può essere null o vuoto");
        if (name == null || name.isBlank()) throw new InvalidNameException("il nome dell'oggetto non può essere null o vuoto");
        this.id   = id;
        this.name = name;
        this.value = value;
    }

    /**
     * Restituisce il tipo dell'oggetto, usato dalla persistenza XML.
     *
     * @return il {@link ItemType} corrispondente a questa sottoclasse
     */
    public abstract ItemType getType();

    /**
     * Crea l'istanza concreta corretta a partire da un {@link ItemType}.
     * Utilizzato dalla persistenza per ricostruire gli oggetti dal file XML.
     *
     * @param id    identificatore univoco
     * @param name  nome visualizzato
     * @param type  tipo dell'oggetto
     * @param value valore numerico
     * @return l'istanza concreta corrispondente al tipo
     */
    public static Item create(String id, String name, ItemType type, int value) {
        return switch (type) {
            case POTION   -> new Potion(id, name, value);
            case AMULET   -> new Amulet(id, name, value);
            case SCROLL   -> new Scroll(id, name, value);
            case TALISMAN -> new Talisman(id, name, value);
        };
    }

    public String getId()   { return id; }
    public String getName() { return name; }
    public int getValue()   { return value; }
}
