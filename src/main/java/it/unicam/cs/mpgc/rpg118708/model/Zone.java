package it.unicam.cs.mpgc.rpg118708.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una zona del tempio, composta da una sequenza ordinata di stanze.
 *
 * <p>Gestisce la progressione interna tra le stanze tramite un indice corrente.
 * La zona tiene traccia del proprio stato di completamento e del boss associato.
 * La navigazione avanti/indietro tra le stanze avviene tramite {@link #advanceRoom()}
 * e {@link #goBack()}.</p>
 */
public class Zone {

    private final String id;
    private final String name;
    private final List<Room> rooms;
    private final Boss boss;
    private boolean completed;
    private int currentRoomIndex;

    /**
     * Crea una zona.
     *
     * @param id   identificatore univoco
     * @param name nome visualizzato
     * @param boss il boss finale della zona (può essere {@code null} se non previsto)
     */
    public Zone(String id, String name, Boss boss) {
        this.id = id;
        this.name = name;
        this.boss = boss;
        this.rooms = new ArrayList<>();
        this.completed = false;
        this.currentRoomIndex = 0;
    }

    /** Aggiunge una stanza in coda alla sequenza della zona. */
    public void addRoom(Room room) { rooms.add(room); }

    /**
     * @return la stanza corrente, o {@code null} se l'indice è fuori range
     */
    public Room getCurrentRoom() {
        if (currentRoomIndex < rooms.size()) {
            return rooms.get(currentRoomIndex);
        }
        return null;
    }

    /**
     * Avanza alla stanza successiva se non è già l'ultima.
     *
     * @return {@code true} se l'avanzamento è avvenuto
     */
    public boolean advanceRoom() {
        if (currentRoomIndex < rooms.size() - 1) {
            currentRoomIndex++;
            return true;
        }
        return false;
    }

    /**
     * @return {@code true} se la stanza corrente è l'ultima della zona
     */
    public boolean isLastRoom() {
        return currentRoomIndex == rooms.size() - 1;
    }

    /**
     * @return {@code true} se tutte le stanze della zona sono state ripulite
     */
    public boolean allRoomsCleared() {
        return rooms.stream().allMatch(Room::isCleared);
    }

    /**
     * Torna alla stanza precedente se non si è già alla prima.
     *
     * @return {@code true} se il ritorno è avvenuto
     */
    public boolean goBack() {
        if (currentRoomIndex > 0) {
            currentRoomIndex--;
            return true;
        }
        return false;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<Room> getRooms() { return rooms; }
    public Boss getBoss() { return boss; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public int getCurrentRoomIndex() { return currentRoomIndex; }
    public void setCurrentRoomIndex(int index) { this.currentRoomIndex = index; }
}
