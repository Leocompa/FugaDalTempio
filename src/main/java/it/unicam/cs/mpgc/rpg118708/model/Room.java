package it.unicam.cs.mpgc.rpg118708.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rappresenta una singola stanza all'interno di una zona del tempio.
 *
 * <p>Una stanza è un contenitore passivo di entità di gioco: nemici, trappole,
 * oggetti raccoglibili e NPC. Non contiene logica di gioco — le interazioni
 * con queste entità sono gestite da {@link it.unicam.cs.mpgc.rpg118708.engine.GameManager}
 * e da {@link it.unicam.cs.mpgc.rpg118708.view.ExplorationScene}.</p>
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
     * @param id   identificatore univoco
     * @param name nome visualizzato
     */
    public Room(String id, String name) {
        this.id = id;
        this.name = name;
        this.enemies = new ArrayList<>();
        this.traps = new ArrayList<>();
        this.items = new ArrayList<>();
        this.npcs = new ArrayList<>();
    }

    /** Aggiunge un nemico alla stanza. */
    public void addEnemy(Enemy enemy) { enemies.add(enemy); }

    /** Aggiunge una trappola alla stanza. */
    public void addTrap(Trap trap) { traps.add(trap); }

    /** Aggiunge un oggetto raccoglibile alla stanza. */
    public void addItem(Item item) { items.add(item); }

    /** Aggiunge un NPC alla stanza. */
    public void addNpc(NPC npc) { npcs.add(npc); }

    /**
     * Indica se la stanza è stata ripulita da tutti i nemici.
     *
     * @return {@code true} se nessun nemico è ancora vivo
     */
    public boolean isCleared() {
        return enemies.stream().allMatch(e -> !e.isAlive());
    }

    /** Rimuove un oggetto dalla stanza (es. dopo che il giocatore lo raccoglie). */
    public void removeItem(Item item) { items.remove(item); }

    /** Rimuove tutti gli oggetti dalla stanza (usato dalla persistenza al caricamento). */
    public void clearItems() { items.clear(); }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<Enemy> getEnemies() { return Collections.unmodifiableList(enemies); }
    public List<Trap> getTraps() { return Collections.unmodifiableList(traps); }
    public List<Item> getItems() { return Collections.unmodifiableList(items); }
    public List<NPC> getNpcs() { return Collections.unmodifiableList(npcs); }
}
