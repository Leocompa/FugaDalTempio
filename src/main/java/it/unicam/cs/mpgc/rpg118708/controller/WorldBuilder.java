package it.unicam.cs.mpgc.rpg118708.controller;

import it.unicam.cs.mpgc.rpg118708.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione di {@link WorldFactory} che costruisce il mondo di gioco
 * in modo hardcoded.
 *
 * <p>Questa classe è responsabile della sola creazione del mondo: definisce
 * zone, stanze, nemici, NPC, trappole e oggetti. Lo scaling progressivo dei
 * nemici avviene centralmente in {@link #buildWorld()}, evitando che le
 * singole stanze conoscano la loro posizione globale nella progressione.</p>
 *
 * <p>Per generare il mondo da un file di configurazione o con algoritmi
 * procedurali, è sufficiente fornire un'altra implementazione di
 * {@link WorldFactory} senza modificare il {@link GameController}.</p>
 */
public class WorldBuilder implements WorldFactory {

    /**
     * {@inheritDoc}
     *
     * <p>Costruisce le zone, poi applica uno scaling progressivo ai nemici
     * in base all'indice globale della stanza (0 = prima stanza in assoluto).
     * Lo scaling viene applicato una sola volta in questo metodo.</p>
     */
    @Override
    public List<Zone> buildWorld() {
        List<Zone> zones = new ArrayList<>();
        zones.add(buildZoneOne());

        int roomIndex = 0;
        for (Zone zone : zones) {
            for (Room room : zone.getRooms()) {
                for (Enemy enemy : room.getEnemies()) {
                    applyScaling(enemy, roomIndex);
                }
                roomIndex++;
            }
        }
        return zones;
    }

    private Zone buildZoneOne() {
        Zone zone = new Zone("zone1", "Tempio di Persepoli");

        zone.addRoom(buildRoom("r1", "Corridoio d'Ingresso", 1));
        zone.addRoom(buildRoom("r2", "Sala delle Lame",      2));
        zone.addRoom(buildRoom("r3", "Cripta dei Guerrieri", 3));
        zone.addRoom(buildRoom("r4", "Sala del Trono",       4));
        zone.addRoom(buildRoom("r5", "Sancta Sanctorum",     5));

        return zone;
    }

    private Room buildRoom(String id, String name, int roomNumber) {
        return switch (roomNumber) {
            case 1 -> buildTutorialRoom(id, name);
            case 2 -> buildSoldierRoom(id, name);
            case 3 -> buildArcherRoom(id, name);
            case 4 -> buildCaptainRoom(id, name);
            case 5 -> buildBossRoom(id, name);
            default -> new Room(id, name);
        };
    }

    private Room buildTutorialRoom(String id, String name) {
        Room room = new Room(id, name);
        room.addNpc(new NPC("npc1", "Ombra del Sacerdote",
                "Straniero... questo tempio custodisce segreti antichi. Prendi questa pozione, ne avrai bisogno.",
                new Potion("potion_npc1", "Pozione del Sacerdote", 20)));
        room.addItem(new Potion("potion1", "Pozione", 15));
        return room;
    }

    private Room buildSoldierRoom(String id, String name) {
        Room room = new Room(id, name);
        room.addTrap(new Trap("trap1", 6));
        room.addEnemy(new Enemy("e_" + id, "Soldato Persiano",
                new Stats(25, 7, 2, 1), 60,
                List.of(
                        new CombatAction("attack",  "Attacca",  CombatActionType.ATTACK,  0),
                        new CombatAction("special", "Fendente", CombatActionType.SPECIAL, 3)
                )));
        return room;
    }

    private Room buildArcherRoom(String id, String name) {
        Room room = new Room(id, name);
        room.addTrap(new Trap("trap2", 8));
        room.addItem(new Potion("potion2", "Pozione", 15));
        room.addEnemy(new Enemy("e_" + id, "Arciere Persiano",
                new Stats(32, 9, 3, 2), 90,
                List.of(
                        new CombatAction("attack",  "Freccia",       CombatActionType.ATTACK,  0),
                        new CombatAction("special", "Salva Freccia", CombatActionType.SPECIAL, 4),
                        new CombatAction("heal",    "Benda",         CombatActionType.HEAL,    8)
                )));
        return room;
    }

    private Room buildCaptainRoom(String id, String name) {
        Room room = new Room(id, name);
        room.addTrap(new Trap("trap3", 10));
        room.addNpc(new NPC("npc2", "Ombra del Guerriero",
                "Il boss ti aspetta. Prendi questa Pergamena di Fuoco — aumenterà il tuo attacco per un turno.",
                new Scroll("scroll_npc2", "Pergamena di Fuoco", 8)));
        room.addEnemy(new Enemy("e_" + id, "Capitano della Guardia",
                new Stats(45, 11, 4, 3), 130,
                List.of(
                        new CombatAction("attack",  "Colpo Pesante", CombatActionType.ATTACK,  0),
                        new CombatAction("special", "Tempesta",      CombatActionType.SPECIAL, 6),
                        new CombatAction("heal",    "Recupera",      CombatActionType.HEAL,    12)
                )));
        return room;
    }

    private Room buildBossRoom(String id, String name) {
        Room room = new Room(id, name);
        room.addItem(new Amulet("amulet1", "Amuleto del Tempio", 0));
        room.addNpc(new NPC("npc3", "Voce del Tempio",
                "Hai raggiunto il cuore del tempio... il Dio Serpente non ti lascerà passare. Prendi questo Talismano.",
                new Talisman("talisman_npc3", "Talismano della Luna", 5)));
        room.addEnemy(new Boss(
                "boss_finale", "Dio Serpente Apep", "Signore del Caos",
                new Stats(80, 14, 6, 6), 300,
                List.of(
                        new CombatAction("attack",  "Morso del Caos",  CombatActionType.ATTACK,  0),
                        new CombatAction("special", "Maledizione",     CombatActionType.SPECIAL, 6),
                        new CombatAction("heal",    "Rigenerazione",   CombatActionType.HEAL,    15)
                )
        ));
        return room;
    }

    /**
     * Applica uno scaling progressivo alle statistiche del nemico in base
     * all'indice globale della stanza (0-based).
     * Ogni stanza aggiunge {@code roomIndex} punti ad attacco e difesa,
     * e {@code roomIndex * 3} punti agli HP massimi.
     *
     * @param enemy     il nemico da scalare
     * @param roomIndex l'indice 0-based della stanza nella progressione globale
     */
    private void applyScaling(Enemy enemy, int roomIndex) {
        Stats s = enemy.getStats();
        s.setAttack(s.getAttack() + roomIndex);
        s.setDefense(s.getDefense() + roomIndex);
        s.setMaxHp(s.getMaxHp() + roomIndex * 3);
        s.setCurrentHp(s.getMaxHp());
    }
}
