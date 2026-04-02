package it.unicam.cs.mpgc.rpg118708.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory {

    private static final int MAX_SIZE = 10;
    private final List<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public boolean addItem(Item item) {
        if (items.size() >= MAX_SIZE) return false;
        items.add(item);
        return true;
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public boolean hasItem(Item item) {
        return items.contains(item);
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean isFull() {
        return items.size() >= MAX_SIZE;
    }

    public int size() {
        return items.size();
    }
}