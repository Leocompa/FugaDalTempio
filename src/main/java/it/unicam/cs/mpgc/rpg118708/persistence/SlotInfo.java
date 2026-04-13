package it.unicam.cs.mpgc.rpg118708.persistence;

public class SlotInfo {

    private final int slot;
    private final String playerName;
    private final int level;
    private final int roomNumber;
    private final String timestamp;

    public SlotInfo(int slot, String playerName, int level, int roomNumber, String timestamp) {
        this.slot = slot;
        this.playerName = playerName;
        this.level = level;
        this.roomNumber = roomNumber;
        this.timestamp = timestamp;
    }

    public int getSlot() { return slot; }
    public String getPlayerName() { return playerName; }
    public int getLevel() { return level; }
    public int getRoomNumber() { return roomNumber; }
    public String getTimestamp() { return timestamp; }
}