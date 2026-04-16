package it.unicam.cs.mpgc.rpg118708.persistence;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.model.Item;
import it.unicam.cs.mpgc.rpg118708.model.Player;
import it.unicam.cs.mpgc.rpg118708.model.Stats;
import it.unicam.cs.mpgc.rpg118708.model.Zone;
import it.unicam.cs.mpgc.rpg118708.model.Room;
import it.unicam.cs.mpgc.rpg118708.model.Enemy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

/**
 * Implementazione legacy del salvataggio su file XML.
 *
 * @deprecated Usare {@link XmlGamePersistence} che implementa {@link GamePersistence}.
 *             Questa classe è mantenuta per compatibilità temporanea.
 */
@Deprecated
public class GameSaver {

    private static final String SAVE_DIR = "saves/";
    private static final int MAX_SLOTS = 3;

    public GameSaver() {
        new File(SAVE_DIR).mkdirs();
    }

    public static String getSavePath(int slot) {
        return SAVE_DIR + "save_" + slot + ".xml";
    }

    public void save(GameManager gameManager, int slot) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("savegame");
            doc.appendChild(root);

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            root.setAttribute("timestamp", timestamp);

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
            progressEl.setAttribute("zoneIndex", String.valueOf(gameManager.getCurrentZoneIndex()));
            progressEl.setAttribute("roomIndex", String.valueOf(
                    gameManager.getCurrentZone().getCurrentRoomIndex()));
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
                    roomEl.setAttribute("puzzleSolved", String.valueOf(room.isPuzzleSolved()));
                    for (Enemy enemy : room.getEnemies()) {
                        Element enemyEl = doc.createElement("enemy");
                        enemyEl.setAttribute("id", enemy.getId());
                        enemyEl.setAttribute("alive", String.valueOf(enemy.isAlive()));
                        enemyEl.setAttribute("hp", String.valueOf(enemy.getStats().getCurrentHp()));
                        roomEl.appendChild(enemyEl);
                    }
                    for (Item item : room.getItems()) {
                        Element itemEl = doc.createElement("item");
                        itemEl.setAttribute("id", item.getId());
                        roomEl.appendChild(itemEl);
                    }
                    for (var npc : room.getNpcs()) {
                        Element npcEl = doc.createElement("npc");
                        npcEl.setAttribute("id", npc.getId());
                        npcEl.setAttribute("rewardGiven", String.valueOf(npc.isRewardGiven()));
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

    public void save(GameManager gameManager) {
        save(gameManager, 1);
    }

    public static int getMaxSlots() { return MAX_SLOTS; }
}