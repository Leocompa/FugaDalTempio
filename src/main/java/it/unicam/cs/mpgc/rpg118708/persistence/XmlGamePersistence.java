package it.unicam.cs.mpgc.rpg118708.persistence;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;

import java.io.File;

/**
 * Implementazione della persistenza di gioco tramite file XML su tre slot.
 *
 * <p>Questa classe funge da facade: coordina {@link XmlSaveWriter} (serializzazione)
 * e {@link XmlSaveReader} (deserializzazione), mantenendo la sola responsabilità
 * di gestire gli slot e di esporre l'interfaccia {@link GamePersistence}.</p>
 */
public class XmlGamePersistence implements GamePersistence {

    private static final String SAVE_DIR  = "saves/";
    private static final int    MAX_SLOTS = 3;

    private final XmlSaveWriter writer = new XmlSaveWriter();
    private final XmlSaveReader reader = new XmlSaveReader();

    /** Crea la directory di salvataggio se non esiste. */
    public XmlGamePersistence() {
        new File(SAVE_DIR).mkdirs();
    }

    /**
     * Restituisce il percorso del file XML per lo slot indicato.
     *
     * @param slot il numero dello slot
     * @return il percorso del file
     */
    static String getSavePath(int slot) {
        return SAVE_DIR + "save_" + slot + ".xml";
    }

    @Override
    public int getMaxSlots() { return MAX_SLOTS; }

    @Override
    public void save(GameManager gameManager, int slot) {
        try {
            writer.write(gameManager, slot);
        } catch (Exception e) {
            System.err.println("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    @Override
    public void load(GameManager gameManager, int slot) {
        try {
            reader.read(gameManager, slot);
        } catch (Exception e) {
            System.err.println("Errore durante il caricamento dello slot " + slot + ": " + e.getMessage());
        }
    }

    @Override
    public boolean saveExists(int slot) {
        return new File(getSavePath(slot)).exists();
    }

    @Override
    public boolean saveExists() {
        for (int i = 1; i <= MAX_SLOTS; i++) {
            if (saveExists(i)) return true;
        }
        return false;
    }

    @Override
    public String loadPlayerName(int slot) {
        try {
            return reader.readPlayerName(slot);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public SlotInfo getSlotInfo(int slot) {
        try {
            return reader.readSlotInfo(slot);
        } catch (Exception e) {
            return null;
        }
    }
}
