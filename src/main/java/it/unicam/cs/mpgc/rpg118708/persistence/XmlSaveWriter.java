package it.unicam.cs.mpgc.rpg118708.persistence;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Serializza lo stato di una partita in un file XML.
 *
 * <p>Responsabilità unica: costruire il Document XML a partire dal
 * {@link GameManager} e scriverlo su disco nello slot indicato.
 * Non conosce la logica di gioco né la struttura degli altri componenti
 * di persistenza.</p>
 */
class XmlSaveWriter {

    /**
     * Serializza lo stato corrente della partita nel file XML corrispondente
     * allo slot indicato.
     *
     * @param gameManager lo stato di gioco da serializzare
     * @param slot        il numero dello slot di destinazione
     * @throws Exception in caso di errore di I/O o di costruzione del documento
     */
    void write(GameManager gameManager, int slot) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();

        Element root = doc.createElement("savegame");
        doc.appendChild(root);
        root.setAttribute("timestamp", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        root.appendChild(buildPlayerElement(doc, gameManager.getPlayer()));
        root.appendChild(buildProgressElement(doc, gameManager));
        root.appendChild(buildZonesElement(doc, gameManager));
        root.appendChild(buildInventoryElement(doc, gameManager.getPlayer()));

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(doc),
                new StreamResult(new File(XmlGamePersistence.getSavePath(slot))));
    }

    private Element buildPlayerElement(Document doc, Player player) {
        Stats stats = player.getStats();
        Element el = doc.createElement("player");
        el.setAttribute("name",          player.getName());
        el.setAttribute("hp",            String.valueOf(stats.getCurrentHp()));
        el.setAttribute("maxHp",         String.valueOf(stats.getMaxHp()));
        el.setAttribute("attack",        String.valueOf(stats.getAttack()));
        el.setAttribute("defense",       String.valueOf(stats.getDefense()));
        el.setAttribute("level",         String.valueOf(stats.getLevel()));
        el.setAttribute("xp",            String.valueOf(stats.getCurrentXp()));
        el.setAttribute("xpToNextLevel", String.valueOf(stats.getXpToNextLevel()));
        return el;
    }

    private Element buildProgressElement(Document doc, GameManager gameManager) {
        Element el = doc.createElement("progress");
        el.setAttribute("zoneIndex",        String.valueOf(gameManager.getCurrentZoneIndex()));
        el.setAttribute("roomIndex",        String.valueOf(gameManager.getCurrentZone().getCurrentRoomIndex()));
        el.setAttribute("enemiesDefeated",  String.valueOf(gameManager.getTotalEnemiesDefeated()));
        return el;
    }

    private Element buildZonesElement(Document doc, GameManager gameManager) {
        Element zonesEl = doc.createElement("zones");
        for (Zone zone : gameManager.getZones()) {
            Element zoneEl = doc.createElement("zone");
            zoneEl.setAttribute("id",        zone.getId());
            zoneEl.setAttribute("completed", String.valueOf(zone.isCompleted()));
            for (Room room : zone.getRooms()) {
                zoneEl.appendChild(buildRoomElement(doc, room));
            }
            zonesEl.appendChild(zoneEl);
        }
        return zonesEl;
    }

    private Element buildRoomElement(Document doc, Room room) {
        Element roomEl = doc.createElement("room");
        roomEl.setAttribute("id", room.getId());
        for (Enemy enemy : room.getEnemies()) {
            Element enemyEl = doc.createElement("enemy");
            enemyEl.setAttribute("id",    enemy.getId());
            enemyEl.setAttribute("alive", String.valueOf(enemy.isAlive()));
            enemyEl.setAttribute("hp",    String.valueOf(enemy.getStats().getCurrentHp()));
            roomEl.appendChild(enemyEl);
        }
        for (Item item : room.getItems()) {
            Element itemEl = doc.createElement("item");
            itemEl.setAttribute("id", item.getId());
            roomEl.appendChild(itemEl);
        }
        for (NPC npc : room.getNpcs()) {
            Element npcEl = doc.createElement("npc");
            npcEl.setAttribute("id",          npc.getId());
            npcEl.setAttribute("rewardGiven", String.valueOf(npc.isRewardGiven()));
            roomEl.appendChild(npcEl);
        }
        return roomEl;
    }

    private Element buildInventoryElement(Document doc, Player player) {
        Element inventoryEl = doc.createElement("inventory");
        for (Item item : player.getInventory().getItems()) {
            Element itemEl = doc.createElement("item");
            itemEl.setAttribute("id",    item.getId());
            itemEl.setAttribute("name",  item.getName());
            itemEl.setAttribute("type",  item.getType().name());
            itemEl.setAttribute("value", String.valueOf(item.getValue()));
            inventoryEl.appendChild(itemEl);
        }
        return inventoryEl;
    }
}
