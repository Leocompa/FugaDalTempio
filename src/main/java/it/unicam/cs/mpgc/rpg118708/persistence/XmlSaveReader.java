package it.unicam.cs.mpgc.rpg118708.persistence;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Deserializza lo stato di una partita da un file XML.
 *
 * <p>Responsabilità unica: leggere il file XML dello slot indicato,
 * parsare il Document DOM e applicare i dati recuperati al
 * {@link GameManager} e alle sue entità. Non scrive file né conosce
 * la logica di costruzione del Document.</p>
 */
class XmlSaveReader {

    /**
     * Legge il file XML dello slot indicato e applica lo stato salvato al
     * {@link GameManager}.
     *
     * @param gameManager il gestore di stato su cui applicare i dati caricati
     * @param slot        il numero dello slot da leggere
     * @throws Exception in caso di errore di I/O o di parsing
     */
    void read(GameManager gameManager, int slot) throws Exception {
        Document doc = parseDocument(slot);
        if (doc == null) return;

        applyPlayerStats(doc, gameManager);
        applyProgress(doc, gameManager);
        Map<String, Item> itemById = buildItemByIdMap(gameManager);
        applyZones(doc, gameManager, itemById);
        applyInventory(doc, gameManager);
    }

    /**
     * Legge solo il nome del giocatore dallo slot indicato.
     *
     * @param slot il numero dello slot
     * @return il nome del giocatore, o stringa vuota se il file non esiste
     * @throws Exception in caso di errore di parsing
     */
    String readPlayerName(int slot) throws Exception {
        Document doc = parseDocument(slot);
        if (doc == null) return "";
        Element playerEl = (Element) doc.getElementsByTagName("player").item(0);
        return playerEl.getAttribute("name");
    }

    /**
     * Legge le informazioni sintetiche dello slot per la schermata di selezione.
     *
     * @param slot il numero dello slot
     * @return un oggetto {@link SlotInfo}, o {@code null} se il file non esiste
     * @throws Exception in caso di errore di parsing
     */
    SlotInfo readSlotInfo(int slot) throws Exception {
        Document doc = parseDocument(slot);
        if (doc == null) return null;
        String timestamp   = doc.getDocumentElement().getAttribute("timestamp");
        Element playerEl   = (Element) doc.getElementsByTagName("player").item(0);
        Element progressEl = (Element) doc.getElementsByTagName("progress").item(0);
        return new SlotInfo(
                slot,
                playerEl.getAttribute("name"),
                Integer.parseInt(playerEl.getAttribute("level")),
                Integer.parseInt(progressEl.getAttribute("roomIndex")) + 1,
                timestamp);
    }

    private Document parseDocument(int slot) throws Exception {
        File file = new File(XmlGamePersistence.getSavePath(slot));
        if (!file.exists()) return null;
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(file);
        doc.getDocumentElement().normalize();
        return doc;
    }

    private void applyPlayerStats(Document doc, GameManager gameManager) {
        Element el = (Element) doc.getElementsByTagName("player").item(0);
        Stats stats = gameManager.getPlayer().getStats();
        stats.setMaxHp(Integer.parseInt(el.getAttribute("maxHp")));
        stats.setCurrentHp(Integer.parseInt(el.getAttribute("hp")));
        stats.setAttack(Integer.parseInt(el.getAttribute("attack")));
        stats.setDefense(Integer.parseInt(el.getAttribute("defense")));
        stats.setLevel(Integer.parseInt(el.getAttribute("level")));
        stats.setCurrentXp(Integer.parseInt(el.getAttribute("xp")));
        stats.setXpToNextLevel(Integer.parseInt(el.getAttribute("xpToNextLevel")));
    }

    private void applyProgress(Document doc, GameManager gameManager) {
        Element el = (Element) doc.getElementsByTagName("progress").item(0);
        gameManager.setCurrentZoneIndex(Integer.parseInt(el.getAttribute("zoneIndex")));
        gameManager.getCurrentZone().setCurrentRoomIndex(
                Integer.parseInt(el.getAttribute("roomIndex")));
        if (el.hasAttribute("enemiesDefeated")) {
            gameManager.setTotalEnemiesDefeated(
                    Integer.parseInt(el.getAttribute("enemiesDefeated")));
        }
    }

    private Map<String, Item> buildItemByIdMap(GameManager gameManager) {
        return gameManager.getZones().stream()
                .flatMap(z -> z.getRooms().stream())
                .flatMap(r -> r.getItems().stream())
                .collect(Collectors.toMap(Item::getId, i -> i));
    }

    private void applyZones(Document doc, GameManager gameManager, Map<String, Item> itemById) {
        NodeList zoneNodes = doc.getElementsByTagName("zone");
        for (int i = 0; i < zoneNodes.getLength(); i++) {
            Element zoneEl = (Element) zoneNodes.item(i);
            String zoneId = zoneEl.getAttribute("id");
            gameManager.getZones().stream()
                    .filter(z -> z.getId().equals(zoneId))
                    .findFirst().ifPresent(zone -> {
                        zone.setCompleted(Boolean.parseBoolean(zoneEl.getAttribute("completed")));
                        NodeList roomNodes = zoneEl.getElementsByTagName("room");
                        for (int j = 0; j < roomNodes.getLength(); j++) {
                            applyRoom((Element) roomNodes.item(j), zone, itemById);
                        }
                    });
        }
    }

    private void applyRoom(Element roomEl, Zone zone, Map<String, Item> itemById) {
        String roomId = roomEl.getAttribute("id");
        zone.getRooms().stream()
                .filter(r -> r.getId().equals(roomId))
                .findFirst().ifPresent(room -> {
                    applyRoomEnemies(roomEl, room);
                    applyRoomNpcs(roomEl, room);
                    applyRoomItems(roomEl, room, itemById);
                });
    }

    private void applyRoomEnemies(Element roomEl, Room room) {
        NodeList enemyNodes = roomEl.getElementsByTagName("enemy");
        for (int k = 0; k < enemyNodes.getLength(); k++) {
            Element enemyEl = (Element) enemyNodes.item(k);
            String enemyId = enemyEl.getAttribute("id");
            boolean alive  = Boolean.parseBoolean(enemyEl.getAttribute("alive"));
            int hp         = Integer.parseInt(enemyEl.getAttribute("hp"));
            room.getEnemies().stream()
                    .filter(e -> e.getId().equals(enemyId))
                    .findFirst().ifPresent(e -> e.getStats().setCurrentHp(alive ? hp : 0));
        }
    }

    private void applyRoomNpcs(Element roomEl, Room room) {
        NodeList npcNodes = roomEl.getElementsByTagName("npc");
        for (int k = 0; k < npcNodes.getLength(); k++) {
            Element npcEl = (Element) npcNodes.item(k);
            String npcId      = npcEl.getAttribute("id");
            boolean rewardGiven = Boolean.parseBoolean(npcEl.getAttribute("rewardGiven"));
            room.getNpcs().stream()
                    .filter(n -> n.getId().equals(npcId))
                    .findFirst().ifPresent(n -> n.setRewardGiven(rewardGiven));
        }
    }

    private void applyInventory(Document doc, GameManager gameManager) {
        Element inventoryEl = (Element) doc.getElementsByTagName("inventory").item(0);
        if (inventoryEl == null) return;
        NodeList invItems = inventoryEl.getElementsByTagName("item");
        for (int i = 0; i < invItems.getLength(); i++) {
            Element itemEl = (Element) invItems.item(i);
            Item item = Item.create(
                    itemEl.getAttribute("id"),
                    itemEl.getAttribute("name"),
                    ItemType.valueOf(itemEl.getAttribute("type")),
                    Integer.parseInt(itemEl.getAttribute("value")));
            gameManager.getPlayer().getInventory().addItem(item);
        }
    }

    private void applyRoomItems(Element roomEl, Room room, Map<String, Item> itemById) {
        NodeList itemNodes = roomEl.getElementsByTagName("item");
        room.clearItems();
        for (int k = 0; k < itemNodes.getLength(); k++) {
            Element itemEl = (Element) itemNodes.item(k);
            Item item = itemById.get(itemEl.getAttribute("id"));
            if (item != null) room.addItem(item);
        }
    }
}
