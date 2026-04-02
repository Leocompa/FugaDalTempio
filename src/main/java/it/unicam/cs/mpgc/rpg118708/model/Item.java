package it.unicam.cs.mpgc.rpg118708.model;

public class Item {

    private final String id;
    private final String name;
    private final ItemType type;
    private final int value;

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