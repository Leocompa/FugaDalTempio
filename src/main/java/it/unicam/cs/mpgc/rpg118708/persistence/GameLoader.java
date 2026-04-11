package it.unicam.cs.mpgc.rpg118708.persistence;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class GameLoader {

    private static final String SAVE_PATH = "savegame.xml";

    public boolean saveExists() {
        return new File(SAVE_PATH).exists();
    }

    public void load(GameManager gameManager) {
        try {
            File file = new File(SAVE_PATH);
            if (!file.exists()) return;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            Element playerEl = (Element) doc.getElementsByTagName("player").item(0);
            Stats stats = gameManager.getPlayer().getStats();
            stats.setMaxHp(Integer.parseInt(playerEl.getAttribute("maxHp")));
            stats.setCurrentHp(Integer.parseInt(playerEl.getAttribute("hp")));
            stats.setAttack(Integer.parseInt(playerEl.getAttribute("attack")));
            stats.setDefense(Integer.parseInt(playerEl.getAttribute("defense")));
            stats.setLevel(Integer.parseInt(playerEl.getAttribute("level")));
            stats.setCurrentXp(Integer.parseInt(playerEl.getAttribute("xp")));
            stats.setXpToNextLevel(Integer.parseInt(playerEl.getAttribute("xpToNextLevel")));

            Element progressEl = (Element) doc.getElementsByTagName("progress").item(0);
            int zoneIndex = Integer.parseInt(progressEl.getAttribute("zoneIndex"));
            int roomIndex = Integer.parseInt(progressEl.getAttribute("roomIndex"));
            gameManager.setCurrentZoneIndex(zoneIndex);
            gameManager.getCurrentZone().setCurrentRoomIndex(roomIndex);

            org.w3c.dom.NodeList zoneNodes = doc.getElementsByTagName("zone");
            for (int i = 0; i < zoneNodes.getLength(); i++) {
                Element zoneEl = (Element) zoneNodes.item(i);
                String zoneId = zoneEl.getAttribute("id");
                Zone zone = gameManager.getZones().stream()
                        .filter(z -> z.getId().equals(zoneId))
                        .findFirst().orElse(null);
                if (zone == null) continue;

                zone.setCompleted(Boolean.parseBoolean(zoneEl.getAttribute("completed")));

                org.w3c.dom.NodeList roomNodes = zoneEl.getElementsByTagName("room");
                for (int j = 0; j < roomNodes.getLength(); j++) {
                    Element roomEl = (Element) roomNodes.item(j);
                    String roomId = roomEl.getAttribute("id");
                    Room room = zone.getRooms().stream()
                            .filter(r -> r.getId().equals(roomId))
                            .findFirst().orElse(null);
                    if (room == null) continue;

                    room.setPuzzleSolved(Boolean.parseBoolean(roomEl.getAttribute("puzzleSolved")));

                    org.w3c.dom.NodeList enemyNodes = roomEl.getElementsByTagName("enemy");
                    for (int k = 0; k < enemyNodes.getLength(); k++) {
                        Element enemyEl = (Element) enemyNodes.item(k);
                        String enemyId = enemyEl.getAttribute("id");
                        boolean alive = Boolean.parseBoolean(enemyEl.getAttribute("alive"));
                        int hp = Integer.parseInt(enemyEl.getAttribute("hp"));
                        room.getEnemies().stream()
                                .filter(e -> e.getId().equals(enemyId))
                                .findFirst().ifPresent(e -> {
                                    e.getStats().setCurrentHp(alive ? hp : 0);
                                });
                    }

                    org.w3c.dom.NodeList itemNodes = roomEl.getElementsByTagName("item");
                    room.getItems().clear();
                    for (int k = 0; k < itemNodes.getLength(); k++) {
                        Element itemEl = (Element) itemNodes.item(k);
                        String itemId = itemEl.getAttribute("id");
                        gameManager.getZones().stream()
                                .flatMap(z -> z.getRooms().stream())
                                .flatMap(r -> r.getItems().stream())
                                .filter(it -> it.getId().equals(itemId))
                                .findFirst().ifPresent(room.getItems()::add);
                    }
                }
            }

            Element inventoryEl = (Element) doc.getElementsByTagName("inventory").item(0);
            if (inventoryEl != null) {
                org.w3c.dom.NodeList invItems = inventoryEl.getElementsByTagName("item");
                for (int i = 0; i < invItems.getLength(); i++) {
                    Element itemEl = (Element) invItems.item(i);
                    String name = itemEl.getAttribute("name");
                    String type = itemEl.getAttribute("type");
                    int value = Integer.parseInt(itemEl.getAttribute("value"));
                    String id = itemEl.getAttribute("id");
                    Item item = new Item(id, name, ItemType.valueOf(type), value);
                    gameManager.getPlayer().getInventory().addItem(item);
                }
            }

        } catch (Exception e) {
            System.err.println("Errore durante il caricamento: " + e.getMessage());
        }
    }

    public String loadPlayerName() {
        try {
            File file = new File(SAVE_PATH);
            if (!file.exists()) return "";
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();
            Element playerEl = (Element) doc.getElementsByTagName("player").item(0);
            return playerEl.getAttribute("name");
        } catch (Exception e) {
            return "";
        }
    }
}