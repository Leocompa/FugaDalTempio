package it.unicam.cs.mpgc.rpg118708.engine;

import it.unicam.cs.mpgc.rpg118708.model.Enemy;
import it.unicam.cs.mpgc.rpg118708.model.Item;
import it.unicam.cs.mpgc.rpg118708.model.Player;
import it.unicam.cs.mpgc.rpg118708.model.Room;
import it.unicam.cs.mpgc.rpg118708.model.Trap;
import it.unicam.cs.mpgc.rpg118708.model.Zone;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private final Player player;
    private final List<Zone> zones;
    private int currentZoneIndex;
    private GameState state;
    private final CombatManager combatManager;

    public GameManager(Player player, List<Zone> zones) {
        this.player = player;
        this.zones = zones;
        this.currentZoneIndex = 0;
        this.state = GameState.EXPLORING;
        this.combatManager = new CombatManager(player);
    }

    public Zone getCurrentZone() {
        return zones.get(currentZoneIndex);
    }

    public Room getCurrentRoom() {
        return getCurrentZone().getCurrentRoom();
    }

    public void enterCombat(Enemy enemy) {
        combatManager.startCombat(enemy);
        state = GameState.COMBAT;
    }

    public void endCombat() {
        state = GameState.EXPLORING;
    }

    public boolean advanceRoom() {
        Zone zone = getCurrentZone();
        if (zone.isLastRoom()) return false;
        zone.advanceRoom();
        return true;
    }

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

    public void checkTraps() {
        Room room = getCurrentRoom();
        for (Trap trap : room.getTraps()) {
            trap.trigger(player);
        }
        if (!player.isAlive()) {
            state = GameState.GAME_OVER;
        }
    }

    public boolean collectItem(Item item) {
        Room room = getCurrentRoom();
        if (room.getItems().contains(item)) {
            boolean added = player.getInventory().addItem(item);
            if (added) room.removeItem(item);
            return added;
        }
        return false;
    }

    public void respawn() {
        getCurrentZone().setCurrentRoomIndex(0);
        player.getStats().setCurrentHp(player.getStats().getMaxHp());
        state = GameState.EXPLORING;
    }

    public boolean isGameOver() { return state == GameState.GAME_OVER; }
    public boolean isVictory() { return state == GameState.VICTORY; }

    public Player getPlayer() { return player; }
    public List<Zone> getZones() { return zones; }
    public int getCurrentZoneIndex() { return currentZoneIndex; }
    public void setCurrentZoneIndex(int index) { this.currentZoneIndex = index; }
    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public CombatManager getCombatManager() { return combatManager; }
}