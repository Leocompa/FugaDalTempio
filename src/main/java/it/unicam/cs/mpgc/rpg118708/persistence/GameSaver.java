package it.unicam.cs.mpgc.rpg118708.persistence;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.model.Player;
import it.unicam.cs.mpgc.rpg118708.model.Stats;
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

public class GameSaver {

    private static final String SAVE_PATH = "savegame.xml";

    public void save(GameManager gameManager) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("savegame");
            doc.appendChild(root);

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
            root.appendChild(progressEl);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(new File(SAVE_PATH)));

        } catch (Exception e) {
            System.err.println("Errore durante il salvataggio: " + e.getMessage());
        }
    }
}