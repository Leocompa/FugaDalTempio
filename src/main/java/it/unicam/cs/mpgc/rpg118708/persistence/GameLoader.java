package it.unicam.cs.mpgc.rpg118708.persistence;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Implementazione legacy del caricamento da file XML.
 *
 * @deprecated Usare {@link XmlGamePersistence} che implementa {@link GamePersistence}.
 *             Questa classe è mantenuta per compatibilità temporanea.
 */
@Deprecated
public class GameLoader {

    public boolean saveExists(int slot) {
        return new File(GameSaver.getSavePath(slot)).exists();
    }

    public boolean saveExists() {
        for (int i = 1; i <= GameSaver.getMaxSlots(); i++) {
            if (saveExists(i)) return true;
        }
        return false;
    }

    public String loadPlayerName(int slot) {
        try {
            File file = new File(GameSaver.getSavePath(slot));
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

    public String loadPlayerName() { return loadPlayerName(1); }

    public SlotInfo getSlotInfo(int slot) {
        try {
            File file = new File(GameSaver.getSavePath(slot));
            if (!file.exists()) return null;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            String timestamp = doc.getDocumentElement().getAttribute("timestamp");
            Element playerEl = (Element) doc.getElementsByTagName("player").item(0);
            Element progressEl = (Element) doc.getElementsByTagName("progress").item(0);

            String name = playerEl.getAttribute("name");
            int level = Integer.parseInt(playerEl.getAttribute("level"));
            int roomIndex = Integer.parseInt(progressEl.getAttribute("roomIndex")) + 1;

            return new SlotInfo(slot, name, level, roomIndex, timestamp);
        } catch (Exception e) {
            return null;
        }
    }

    public void load(GameManager gameManager, int slot) {
        try {
            File file = new File(GameSaver.getSavePath(slot));
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
            if (progressEl.hasAttribute("enemiesDefeated")) {
                // setter da aggiungere a GameManager
            }

            NodeList zoneNodes = doc.getElementsByTagName("zone");
            for (int i = 0; i < zoneNodes.getLength(); i++) {
                Element zoneEl = (Element) zoneNodes.item(i);
                String zoneId = zoneEl.getAttribute("id");
                Zone zone = gameManager.getZones().stream()
                        .filter(z -> z.getId().equals(zoneId))
                        .findFirst().orElse(null);
                if (zone == null) continue;

                zone.setCompleted(Boolean.parseBoolean(zoneEl.getAttribute("completed")));

                NodeList roomNodes = zoneEl.getElementsByTagName("room");
                for (int j = 0; j < roomNodes.getLength(); j++) {
                    Element roomEl = (Element) roomNodes.item(j);
                    String roomId = roomEl.getAttribute("id");
                    Room room = zone.getRooms().stream()
                            .filter(r -> r.getId().equals(roomId))
                            .findFirst().orElse(null);
                    if (room == null) continue;

                    room.setPuzzleSolved(Boolean.parseBoolean(roomEl.getAttribute("puzzleSolved")));

                    NodeList enemyNodes = roomEl.getElementsByTagName("enemy");
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

                    NodeList npcNodes = roomEl.getElementsByTagName("npc");
                    for (int k = 0; k < npcNodes.getLength(); k++) {
                        Element npcEl = (Element) npcNodes.item(k);
                        String npcId = npcEl.getAttribute("id");
                        boolean rewardGiven = Boolean.parseBoolean(npcEl.getAttribute("rewardGiven"));
                        room.getNpcs().stream()
                                .filter(n -> n.getId().equals(npcId))
                                .findFirst().ifPresent(n -> n.setRewardGiven(rewardGiven));
                    }

                    NodeList itemNodes = roomEl.getElementsByTagName("item");
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
                NodeList invItems = inventoryEl.getElementsByTagName("item");
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

    public void load(GameManager gameManager) { load(gameManager, 1); }
}