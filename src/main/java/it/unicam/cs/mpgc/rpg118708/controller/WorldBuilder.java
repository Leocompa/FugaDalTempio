package it.unicam.cs.mpgc.rpg118708.controller;

import it.unicam.cs.mpgc.rpg118708.model.*;

import java.util.ArrayList;
import java.util.List;

public class WorldBuilder {

    public static List<Zone> buildWorld() {
        List<Zone> zones = new ArrayList<>();
        zones.add(buildZoneOne());
        return zones;
    }

    private static Zone buildZoneOne() {
        Boss boss = new Boss(
                "boss_finale", "Dio Serpente Apep", "Signore del Caos",
                new Stats(120, 22, 10, 8),
                500,
                List.of(
                        new CombatAction("attack",  "Morso del Caos",    CombatActionType.ATTACK,  0),
                        new CombatAction("special", "Maledizione",       CombatActionType.SPECIAL, 8),
                        new CombatAction("heal",    "Rigenerazione",     CombatActionType.HEAL,    20)
                )
        );

        Zone zone = new Zone("zone1", "Tempio di Persepoli", boss);

        zone.addRoom(buildRoom("r1", "Corridoio d'Ingresso", 1));
        zone.addRoom(buildRoom("r2", "Sala delle Lame",      2));
        zone.addRoom(buildRoom("r3", "Cripta dei Guerrieri", 3));
        zone.addRoom(buildRoom("r4", "Sala del Trono",       4));
        zone.addRoom(buildRoom("r5", "Sancta Sanctorum",     5));

        return zone;
    }

    private static Room buildRoom(String id, String name, int roomNumber) {
        Room room = new Room(id, name);

        if (roomNumber == 1) {
            room.addNpc(new NPC("npc1", "Ombra del Sacerdote",
                    "Straniero... questo tempio custodisce segreti antichi. Prendi questa pozione, ne avrai bisogno.",
                    new Item("potion_npc1", "Pozione del Sacerdote", ItemType.POTION, 20)));
            room.addItem(new Item("potion1", "Pozione", ItemType.POTION, 15));
        }

        if (roomNumber == 2) {
            room.addTrap(new Trap("trap1", 6));
            room.addEnemy(scaleEnemy(new Enemy("e_" + id, "Soldato Persiano",
                    new Stats(25, 7, 2, 1), 60,
                    List.of(
                            new CombatAction("attack",  "Attacca",  CombatActionType.ATTACK,  0),
                            new CombatAction("special", "Fendente", CombatActionType.SPECIAL, 3)
                    )), roomNumber));
        }

        if (roomNumber == 3) {
            room.addTrap(new Trap("trap2", 8));
            room.addItem(new Item("potion2", "Pozione", ItemType.POTION, 15));
            room.addEnemy(scaleEnemy(new Enemy("e_" + id, "Arciere Persiano",
                    new Stats(32, 9, 3, 2), 90,
                    List.of(
                            new CombatAction("attack",  "Freccia",       CombatActionType.ATTACK,  0),
                            new CombatAction("special", "Salva Freccia", CombatActionType.SPECIAL, 4),
                            new CombatAction("heal",    "Benda",         CombatActionType.HEAL,    8)
                    )), roomNumber));
        }

        if (roomNumber == 4) {
            room.addTrap(new Trap("trap3", 10));
            room.addNpc(new NPC("npc2", "Ombra del Guerriero",
                    "Il boss ti aspetta. Prendi questa Pergamena di Fuoco — aumenterà il tuo attacco per un turno.",
                    new Item("scroll_npc2", "Pergamena di Fuoco", ItemType.SCROLL, 8)));
            room.addEnemy(scaleEnemy(new Enemy("e_" + id, "Capitano della Guardia",
                    new Stats(45, 11, 4, 3), 130,
                    List.of(
                            new CombatAction("attack",  "Colpo Pesante", CombatActionType.ATTACK,  0),
                            new CombatAction("special", "Tempesta",      CombatActionType.SPECIAL, 6),
                            new CombatAction("heal",    "Recupera",      CombatActionType.HEAL,    12)
                    )), roomNumber));
        }

        if (roomNumber == 5) {
            room.addItem(new Item("amulet1", "Amuleto del Tempio", ItemType.AMULET, 0));
            room.addNpc(new NPC("npc3", "Voce del Tempio",
                    "Hai raggiunto il cuore del tempio... il Dio Serpente non ti lascerà passare. Prendi questo Talismano.",
                    new Item("talisman_npc3", "Talismano della Luna", ItemType.TALISMAN, 5)));

            Boss boss = new Boss(
                    "boss_finale", "Dio Serpente Apep", "Signore del Caos",
                    new Stats(80, 14, 6, 6),
                    300,
                    List.of(
                            new CombatAction("attack",  "Morso del Caos",  CombatActionType.ATTACK,  0),
                            new CombatAction("special", "Maledizione",     CombatActionType.SPECIAL, 6),
                            new CombatAction("heal",    "Rigenerazione",   CombatActionType.HEAL,    15)
                    )
            );
            room.addEnemy(boss);
        }

        return room;
    }

    public static Enemy scaleEnemy(Enemy enemy, int roomNumber) {
        Stats s = enemy.getStats();
        int bonus = (roomNumber - 1);
        s.setAttack(s.getAttack() + bonus);
        s.setDefense(s.getDefense() + bonus);
        s.setMaxHp(s.getMaxHp() + bonus * 3);
        s.setCurrentHp(s.getMaxHp());
        return enemy;
    }
}