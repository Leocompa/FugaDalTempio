package it.unicam.cs.mpgc.rpg118708.engine;

import it.unicam.cs.mpgc.rpg118708.model.Enemy;
import it.unicam.cs.mpgc.rpg118708.model.Item;
import it.unicam.cs.mpgc.rpg118708.model.Player;
import it.unicam.cs.mpgc.rpg118708.model.Room;
import it.unicam.cs.mpgc.rpg118708.model.Zone;

import java.util.List;

/**
 * Gestisce lo stato globale della partita in corso.
 *
 * <p>È il punto centrale di coordinamento tra il modello di dati (giocatore,
 * zone, stanze) e le operazioni di gioco: transizioni di stato, navigazione
 * tra le stanze, raccolta oggetti e verifica trappole. Non contiene logica
 * di rendering o di input — quelle responsabilità appartengono alle classi
 * nel package {@code view}.</p>
 *
 * <p>Il contatore {@code totalEnemiesDefeated} viene usato in
 * {@link #enterCombat(Enemy)} per determinare il numero di usi speciali disponibili
 * nel combattimento corrente, scalando la difficoltà con la progressione.</p>
 */
public class GameManager {

    private final Player player;
    private final List<Zone> zones;
    private int currentZoneIndex;
    private GameState state;
    private final CombatManager combatManager;
    private int totalEnemiesDefeated = 0;

    /**
     * Crea un nuovo gestore di partita.
     *
     * @param player il personaggio del giocatore
     * @param zones  la lista ordinata delle zone che compongono il mondo
     */
    public GameManager(Player player, List<Zone> zones) {
        this.player = player;
        this.zones = zones;
        this.currentZoneIndex = 0;
        this.state = GameState.EXPLORING;
        this.combatManager = new CombatManager(player);
    }

    /**
     * Restituisce la zona attualmente attiva.
     *
     * @return la zona corrente
     */
    public Zone getCurrentZone() {
        return zones.get(currentZoneIndex);
    }

    /**
     * Restituisce la stanza attualmente attiva nella zona corrente.
     *
     * @return la stanza corrente
     */
    public Room getCurrentRoom() {
        return getCurrentZone().getCurrentRoom();
    }

    /**
     * Avvia un combattimento contro il nemico specificato.
     * Il numero di mosse speciali disponibili scala con i nemici già sconfitti.
     *
     * @param enemy il nemico da affrontare
     */
    public void enterCombat(Enemy enemy) {
        int specialUses = Math.min(3, 1 + totalEnemiesDefeated / 2);
        combatManager.startCombat(enemy, specialUses);
        state = GameState.COMBAT;
    }

    public void endCombat() {
        state = GameState.EXPLORING;
    }

    /**
     * Avanza alla stanza successiva nella zona corrente, se disponibile.
     *
     * @return {@code true} se l'avanzamento è avvenuto
     */
    public boolean advanceRoom() {
        Zone zone = getCurrentZone();
        if (zone.isLastRoom()) return false;
        zone.advanceRoom();
        return true;
    }

    /**
     * Avanza alla zona successiva, se disponibile. Se non ci sono altre zone,
     * imposta lo stato a {@link GameState#VICTORY}.
     *
     * @return {@code true} se si è passati a una nuova zona
     */
    public boolean advanceZone() {
        if (currentZoneIndex < zones.size() - 1) {
            currentZoneIndex++;
            getCurrentZone().setCurrentRoomIndex(0);
            state = GameState.EXPLORING;
            return true;
        }
        state = GameState.VICTORY;
        return false;
    }

    /**
     * Tenta di raccogliere un oggetto dalla stanza corrente aggiungendolo all'inventario.
     *
     * @param item l'oggetto da raccogliere
     * @return {@code true} se l'oggetto era nella stanza ed è stato aggiunto all'inventario
     */
    public boolean collectItem(Item item) {
        Room room = getCurrentRoom();
        if (room.getItems().contains(item)) {
            boolean added = player.getInventory().addItem(item);
            if (added) room.removeItem(item);
            return added;
        }
        return false;
    }

    /**
     * Riporta il giocatore alla prima stanza della zona corrente con HP pieni,
     * senza perdere l'inventario.
     */
    public void respawn() {
        getCurrentZone().setCurrentRoomIndex(0);
        player.getStats().setCurrentHp(player.getStats().getMaxHp());
        state = GameState.EXPLORING;
    }

    public boolean isGameOver() { return state == GameState.GAME_OVER; }

    public boolean isVictory() { return state == GameState.VICTORY; }

    /**
     * Torna alla stanza precedente nella zona corrente, se possibile.
     *
     * @return {@code true} se il ritorno è avvenuto
     */
    public boolean goBackRoom() {
        return getCurrentZone().goBack();
    }

    public void registerEnemyDefeated() { totalEnemiesDefeated++; }

    public Player getPlayer() { return player; }

    public List<Zone> getZones() { return zones; }

    public int getCurrentZoneIndex() { return currentZoneIndex; }

    /**
     * Imposta l'indice della zona corrente (usato dal caricamento da slot).
     *
     * @param index il nuovo indice di zona
     */
    public void setCurrentZoneIndex(int index) { this.currentZoneIndex = index; }

    public GameState getState() { return state; }

    /**
     * Imposta lo stato della partita.
     *
     * @param state il nuovo stato
     */
    public void setState(GameState state) { this.state = state; }

    public CombatManager getCombatManager() { return combatManager; }

    public int getTotalEnemiesDefeated() { return totalEnemiesDefeated; }

    /**
     * Imposta il contatore dei nemici sconfitti (usato dal caricamento da slot).
     *
     * @param value il nuovo valore del contatore
     */
    public void setTotalEnemiesDefeated(int value) { this.totalEnemiesDefeated = value; }
}
