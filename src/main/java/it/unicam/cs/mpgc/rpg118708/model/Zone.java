package it.unicam.cs.mpgc.rpg118708.model;

import it.unicam.cs.mpgc.rpg118708.model.exception.InvalidNameException;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una zona del tempio, composta da una sequenza ordinata di stanze.
 *
 * <p>Gestisce la progressione interna tra le stanze tramite un indice corrente.
 * La zona tiene traccia del proprio stato di completamento.
 * La navigazione avanti/indietro tra le stanze avviene tramite {@link #advanceRoom()}
 * e {@link #goBack()}.</p>
 */
public class Zone {

    private final String id;
    private final String name;
    private final List<Room> rooms;
    private boolean completed;
    private int currentRoomIndex;

    /**
     * Crea una zona.
     *
     * @param id   identificatore univoco (non null né vuoto)
     * @param name nome visualizzato (non null né vuoto)
     * @throws InvalidNameException se {@code id} o {@code name} sono null o vuoti
     */
    public Zone(String id, String name) {
        if (id == null || id.isBlank())     throw new InvalidNameException("l'id della zona non può essere null o vuoto");
        if (name == null || name.isBlank()) throw new InvalidNameException("il nome della zona non può essere null o vuoto");
        this.id = id;
        this.name = name;
        this.rooms = new ArrayList<>();
        this.completed = false;
        this.currentRoomIndex = 0;
    }

    public void addRoom(Room room) { rooms.add(room); }

    /**
     * Restituisce la stanza attualmente attiva nella zona.
     *
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
     * Verifica se la stanza corrente è l'ultima della zona.
     *
     * @return {@code true} se la stanza corrente è l'ultima della zona
     */
    public boolean isLastRoom() {
        return currentRoomIndex == rooms.size() - 1;
    }

    /**
     * Verifica se tutte le stanze della zona sono state ripulite dai nemici.
     *
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

    public boolean isCompleted() { return completed; }

    /**
     * Imposta lo stato di completamento della zona.
     *
     * @param completed {@code true} se la zona è da considerarsi completata
     */
    public void setCompleted(boolean completed) { this.completed = completed; }

    public int getCurrentRoomIndex() { return currentRoomIndex; }

    /**
     * Imposta l'indice della stanza corrente (usato dal caricamento da slot).
     *
     * @param index il nuovo indice di stanza
     */
    public void setCurrentRoomIndex(int index) { this.currentRoomIndex = index; }
}
