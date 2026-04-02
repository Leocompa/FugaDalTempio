package it.unicam.cs.mpgc.rpg118708.persistence;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.model.Stats;
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

        } catch (Exception e) {
            System.err.println("Errore durante il caricamento: " + e.getMessage());
        }
    }
}