package it.unicam.cs.mpgc.rpg118708.persistence;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementazione della persistenza di gioco tramite file XML.
 * Gestisce salvataggio e caricamento di partite in slot numerati.
 */
public class XmlGamePersistence implements GamePersistence {

    private static final String SAVE_DIR = "saves/";
    private static final int MAX_SLOTS = 3;

    public XmlGamePersistence() {
        new File(SAVE_DIR).mkdirs();
    }

    /**
     * Restituisce il percorso del file di salvataggio per lo slot indicato.
     *
     * @param slot il numero dello slot
     * @return il percorso del file XML
     */
    public static String getSavePath(int slot) {
        return SAVE_DIR + "save_" + slot + ".xml";
    }

    @Override
    public int getMaxSlots() { return MAX_SLOTS; }

    @Override
    public void save(GameManager gameManager, int slot) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("savegame");
            doc.appendChild(root);
            root.setAttribute("timestamp", LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            Player player = gameManager.getPlayer();
            Stats stats = player.getStats();

            Element playerEl = doc.createElement("player");
            playerEl.setAttribute("name", player.getName());
            playerEl.setAttribute("hp", String.valueOf(stats.getCurrentHp()));
            playerEl.setAttribute("maxHp", String.valueOf(stats.getMaxHp()));
            playerEl.setAttribute("attack", String.valueOf(stats.getAttack()));
            playerEl.setAttribute("defense", String.valueOf(stats.getDefense()));
            playerEl.setAttribute("level", String.valueOf(stats.getLevel()));
            playerEl.setAttribute("xp", String.valueOf(stats.getCurrentXp()));
            playerEl.setAttribute("xpToNextLevel", String.valueOf(stats.getXpToNextLevel()));
            root.appendChild(playerEl);

            Element progressEl = doc.createElement("progress");
            progressEl.setAttribute("zoneIndex",
                    String.valueOf(gameManager.getCurrentZoneIndex()));
            progressEl.setAttribute("roomIndex",
                    String.valueOf(gameManager.getCurrentZone().getCurrentRoomIndex()));
            progressEl.setAttribute("enemiesDefeated",
                    String.valueOf(gameManager.getTotalEnemiesDefeated()));
            root.appendChild(progressEl);

            Element zonesEl = doc.createElement("zones");
            for (Zone zone : gameManager.getZones()) {
                Element zoneEl = doc.createElement("zone");
                zoneEl.setAttribute("id", zone.getId());
                zoneEl.setAttribute("completed", String.valueOf(zone.isCompleted()));
                for (Room room : zone.getRooms()) {
                    Element roomEl = doc.createElement("room");
                    roomEl.setAttribute("id", room.getId());
                    for (Enemy enemy : room.getEnemies()) {
                        Element enemyEl = doc.createElement("enemy");
                        enemyEl.setAttribute("id", enemy.getId());
                        enemyEl.setAttribute("alive", String.valueOf(enemy.isAlive()));
                        enemyEl.setAttribute("hp",
                                String.valueOf(enemy.getStats().getCurrentHp()));
                        roomEl.appendChild(enemyEl);
                    }
                    for (Item item : room.getItems()) {
                        Element itemEl = doc.createElement("item");
                        itemEl.setAttribute("id", item.getId());
                        roomEl.appendChild(itemEl);
                    }
                    for (NPC npc : room.getNpcs()) {
                        Element npcEl = doc.createElement("npc");
                        npcEl.setAttribute("id", npc.getId());
                        npcEl.setAttribute("rewardGiven",
                                String.valueOf(npc.isRewardGiven()));
                        roomEl.appendChild(npcEl);
                    }
                    zoneEl.appendChild(roomEl);
                }
                zonesEl.appendChild(zoneEl);
            }
            root.appendChild(zonesEl);

            Element inventoryEl = doc.createElement("inventory");
            for (Item item : player.getInventory().getItems()) {
                Element itemEl = doc.createElement("item");
                itemEl.setAttribute("id", item.getId());
                itemEl.setAttribute("name", item.getName());
                itemEl.setAttribute("type", item.getType().name());
                itemEl.setAttribute("value", String.valueOf(item.getValue()));
                inventoryEl.appendChild(itemEl);
            }
            root.appendChild(inventoryEl);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(new File(getSavePath(slot))));

        } catch (Exception e) {
            System.err.println("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    /**
     * Apre e parsa il file XML dello slot indicato.
     *
     * @param slot il numero dello slot
     * @return il documento XML, o {@code null} se il file non esiste
     * @throws Exception in caso di errore di parsing
     */
    private Document parseDocument(int slot) throws Exception {
        File file = new File(getSavePath(slot));
        if (!file.exists()) return null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = factory.newDocumentBuilder().parse(file);
        doc.getDocumentElement().normalize();
        return doc;
    }

    @Override
    public void load(GameManager gameManager, int slot) {
        try {
            Document doc = parseDocument(slot);
            if (doc == null) return;

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
                gameManager.setTotalEnemiesDefeated(
                        Integer.parseInt(progressEl.getAttribute("enemiesDefeated")));
            }

            // Costruisce la mappa degli item per id PRIMA di svuotare le stanze,
            // così il ripristino per id funziona anche dopo il clear.
            Map<String, Item> itemById = gameManager.getZones().stream()
                    .flatMap(z -> z.getRooms().stream())
                    .flatMap(r -> r.getItems().stream())
                    .collect(Collectors.toMap(Item::getId, i -> i));

            NodeList zoneNodes = doc.getElementsByTagName("zone");
            for (int i = 0; i < zoneNodes.getLength(); i++) {
                Element zoneEl = (Element) zoneNodes.item(i);
                String zoneId = zoneEl.getAttribute("id");
                gameManager.getZones().stream()
                        .filter(z -> z.getId().equals(zoneId))
                        .findFirst().ifPresent(zone -> {
                            zone.setCompleted(Boolean.parseBoolean(
                                    zoneEl.getAttribute("completed")));
                            NodeList roomNodes = zoneEl.getElementsByTagName("room");
                            for (int j = 0; j < roomNodes.getLength(); j++) {
                                loadRoom((Element) roomNodes.item(j), zone, itemById);
                            }
                        });
            }

            Element inventoryEl = (Element) doc.getElementsByTagName("inventory").item(0);
            if (inventoryEl != null) {
                NodeList invItems = inventoryEl.getElementsByTagName("item");
                for (int i = 0; i < invItems.getLength(); i++) {
                    Element itemEl = (Element) invItems.item(i);
                    Item item = new Item(
                            itemEl.getAttribute("id"),
                            itemEl.getAttribute("name"),
                            ItemType.valueOf(itemEl.getAttribute("type")),
                            Integer.parseInt(itemEl.getAttribute("value")));
                    gameManager.getPlayer().getInventory().addItem(item);
                }
            }

        } catch (Exception e) {
            System.err.println("Errore durante il caricamento: " + e.getMessage());
        }
    }

    private void loadRoom(Element roomEl, Zone zone, Map<String, Item> itemById) {
        String roomId = roomEl.getAttribute("id");
        zone.getRooms().stream()
                .filter(r -> r.getId().equals(roomId))
                .findFirst().ifPresent(room -> {
                    NodeList enemyNodes = roomEl.getElementsByTagName("enemy");
                    for (int k = 0; k < enemyNodes.getLength(); k++) {
                        Element enemyEl = (Element) enemyNodes.item(k);
                        String enemyId = enemyEl.getAttribute("id");
                        boolean alive = Boolean.parseBoolean(enemyEl.getAttribute("alive"));
                        int hp = Integer.parseInt(enemyEl.getAttribute("hp"));
                        room.getEnemies().stream()
                                .filter(e -> e.getId().equals(enemyId))
                                .findFirst().ifPresent(e ->
                                        e.getStats().setCurrentHp(alive ? hp : 0));
                    }

                    NodeList npcNodes = roomEl.getElementsByTagName("npc");
                    for (int k = 0; k < npcNodes.getLength(); k++) {
                        Element npcEl = (Element) npcNodes.item(k);
                        String npcId = npcEl.getAttribute("id");
                        boolean rewardGiven = Boolean.parseBoolean(
                                npcEl.getAttribute("rewardGiven"));
                        room.getNpcs().stream()
                                .filter(n -> n.getId().equals(npcId))
                                .findFirst().ifPresent(n -> n.setRewardGiven(rewardGiven));
                    }

                    // Ripristina gli item rimasti nella stanza al momento del salvataggio.
                    // La mappa è costruita prima di svuotare qualsiasi stanza, così la
                    // ricerca per id funziona correttamente.
                    NodeList itemNodes = roomEl.getElementsByTagName("item");
                    room.clearItems();
                    for (int k = 0; k < itemNodes.getLength(); k++) {
                        Element itemEl = (Element) itemNodes.item(k);
                        Item item = itemById.get(itemEl.getAttribute("id"));
                        if (item != null) room.addItem(item);
                    }
                });
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
            Document doc = parseDocument(slot);
            if (doc == null) return "";
            Element playerEl = (Element) doc.getElementsByTagName("player").item(0);
            return playerEl.getAttribute("name");
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public SlotInfo getSlotInfo(int slot) {
        try {
            Document doc = parseDocument(slot);
            if (doc == null) return null;
            String timestamp = doc.getDocumentElement().getAttribute("timestamp");
            Element playerEl = (Element) doc.getElementsByTagName("player").item(0);
            Element progressEl = (Element) doc.getElementsByTagName("progress").item(0);
            return new SlotInfo(
                    slot,
                    playerEl.getAttribute("name"),
                    Integer.parseInt(playerEl.getAttribute("level")),
                    Integer.parseInt(progressEl.getAttribute("roomIndex")) + 1,
                    timestamp);
        } catch (Exception e) {
            return null;
        }
    }
}