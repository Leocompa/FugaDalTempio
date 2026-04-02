package it.unicam.cs.mpgc.rpg118708.model;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private final String id;
    private final String name;
    private final List<Enemy> enemies;
    private final List<Trap> traps;
    private final List<Item> items;
    private final List<NPC> npcs;
    private boolean puzzleSolved;
    private boolean locked;

    public Room(String id, String name) {
        this.id = id;
        this.name = name;
        this.enemies = new ArrayList<>();
        this.traps = new ArrayList<>();
        this.items = new ArrayList<>();
        this.npcs = new ArrayList<>();
        this.puzzleSolved = false;
        this.locked = false;
    }

    public void addEnemy(Enemy enemy) { enemies.add(enemy); }
    public void addTrap(Trap trap) { traps.add(trap); }
    public void addItem(Item item) { items.add(item); }
    public void addNpc(NPC npc) { npcs.add(npc); }

    public boolean isCleared() {
        return enemies.stream().allMatch(e -> !e.isAlive());
    }

    public void removeItem(Item item) { items.remove(item); }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<Trap> getTraps() { return traps; }
    public List<Item> getItems() { return items; }
    public List<NPC> getNpcs() { return npcs; }
    public boolean isPuzzleSolved() { return puzzleSolved; }
    public void setPuzzleSolved(boolean puzzleSolved) { this.puzzleSolved = puzzleSolved; }
    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
}