package it.unicam.cs.mpgc.rpg118708.model;

import it.unicam.cs.mpgc.rpg118708.model.exception.InvalidNameException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rappresenta una singola stanza all'interno di una zona del tempio.
 *
 * <p>Una stanza è un contenitore passivo di entità di gioco: nemici, trappole,
 * oggetti raccoglibili e NPC. Non contiene logica di gioco — le interazioni
 * con queste entità sono gestite da {@link it.unicam.cs.mpgc.rpg118708.engine.GameManager}
 * e da {@link it.unicam.cs.mpgc.rpg118708.view.exploration.ExplorationScene}.</p>
 */
public class Room {

    private final String id;
    private final String name;
    private final List<Enemy> enemies;
    private final List<Trap> traps;
    private final List<Item> items;
    private final List<NPC> npcs;

    /**
     * Crea una stanza vuota.
     *
     * @param id   identificatore univoco (non null né vuoto)
     * @param name nome visualizzato (non null né vuoto)
     * @throws InvalidNameException se {@code id} o {@code name} sono null o vuoti
     */
    public Room(String id, String name) {
        if (id == null || id.isBlank())     throw new InvalidNameException("l'id della stanza non può essere null o vuoto");
        if (name == null || name.isBlank()) throw new InvalidNameException("il nome della stanza non può essere null o vuoto");
        this.id = id;
        this.name = name;
        this.enemies = new ArrayList<>();
        this.traps = new ArrayList<>();
        this.items = new ArrayList<>();
        this.npcs = new ArrayList<>();
    }

    public void addEnemy(Enemy enemy) { enemies.add(enemy); }

    public void addTrap(Trap trap) { traps.add(trap); }

    public void addItem(Item item) { items.add(item); }

    public void addNpc(NPC npc) { npcs.add(npc); }

    /**
     * Indica se la stanza è stata ripulita da tutti i nemici.
     *
     * @return {@code true} se nessun nemico è ancora vivo
     */
    public boolean isCleared() {
        return enemies.stream().allMatch(e -> !e.isAlive());
    }

    public void removeItem(Item item) { items.remove(item); }

    /** Rimuove tutti gli oggetti dalla stanza (usato dalla persistenza al caricamento). */
    public void clearItems() { items.clear(); }

    public String getId() { return id; }

    public String getName() { return name; }

    /** @return lista non modificabile dei nemici presenti */
    public List<Enemy> getEnemies() { return Collections.unmodifiableList(enemies); }

    /** @return lista non modificabile delle trappole presenti */
    public List<Trap> getTraps() { return Collections.unmodifiableList(traps); }

    /** @return lista non modificabile degli oggetti raccoglibili presenti */
    public List<Item> getItems() { return Collections.unmodifiableList(items); }

    /** @return lista non modificabile degli NPC presenti */
    public List<NPC> getNpcs() { return Collections.unmodifiableList(npcs); }
}
