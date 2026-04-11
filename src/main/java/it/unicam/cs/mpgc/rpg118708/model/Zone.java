package it.unicam.cs.mpgc.rpg118708.model;

import java.util.ArrayList;
import java.util.List;

public class Zone {

    private final String id;
    private final String name;
    private final List<Room> rooms;
    private final Boss boss;
    private boolean completed;
    private int currentRoomIndex;

    public Zone(String id, String name, Boss boss) {
        this.id = id;
        this.name = name;
        this.boss = boss;
        this.rooms = new ArrayList<>();
        this.completed = false;
        this.currentRoomIndex = 0;
    }

    public void addRoom(Room room) { rooms.add(room); }

    public Room getCurrentRoom() {
        if (currentRoomIndex < rooms.size()) {
            return rooms.get(currentRoomIndex);
        }
        return null;
    }

    public boolean advanceRoom() {
        if (currentRoomIndex < rooms.size() - 1) {
            currentRoomIndex++;
            return true;
        }
        return false;
    }

    public boolean isLastRoom() {
        return currentRoomIndex == rooms.size() - 1;
    }

    public boolean allRoomsCleared() {
        return rooms.stream().allMatch(Room::isCleared);
    }

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