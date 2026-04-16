package it.unicam.cs.mpgc.rpg118708.persistence;

/**
 * Contiene le informazioni sintetiche di uno slot di salvataggio,
 * da mostrare nella schermata di selezione.
 *
 * <p>Oggetto immutabile: tutti i campi vengono impostati nel costruttore
 * e non sono modificabili.</p>
 */
public class SlotInfo {

    private final int slot;
    private final String playerName;
    private final int level;
    private final int roomNumber;
    private final String timestamp;

    /**
     * Crea un riepilogo dello slot di salvataggio.
     *
     * @param slot       numero dello slot (1-based)
     * @param playerName nome del personaggio salvato
     * @param level      livello del personaggio al momento del salvataggio
     * @param roomNumber numero della stanza (1-based) al momento del salvataggio
     * @param timestamp  data e ora del salvataggio (es. "16/04/2026 14:30")
     */
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
