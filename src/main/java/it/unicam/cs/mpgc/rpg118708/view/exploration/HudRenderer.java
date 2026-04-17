package it.unicam.cs.mpgc.rpg118708.view.exploration;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.engine.GameState;
import it.unicam.cs.mpgc.rpg118708.model.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Disegna il HUD (Head-Up Display) della scena di esplorazione.
 *
 * <p>Responsabilità unica: renderizzare la barra superiore (nome, HP, XP,
 * zona, stanza) e la barra inferiore (tasti, inventario rapido).
 * Non gestisce logica di gioco né fisica.</p>
 */
class HudRenderer {

    private static final int INTERACT_RANGE = 60;

    private final GraphicsContext gc;
    private final GameManager     gameManager;
    private final int             W, H;

    /**
     * Costruisce il renderer del HUD.
     *
     * @param gc          il contesto grafico su cui disegnare
     * @param gameManager il gestore dello stato di gioco
     * @param w           larghezza del canvas
     * @param h           altezza del canvas
     */
    HudRenderer(GraphicsContext gc, GameManager gameManager, int w, int h) {
        this.gc          = gc;
        this.gameManager = gameManager;
        this.W           = w;
        this.H           = h;
    }

    /**
     * Disegna il HUD completo per il frame corrente.
     *
     * @param nearExit     {@code true} se il giocatore è vicino all'uscita
     * @param nearEntrance {@code true} se il giocatore è vicino all'ingresso
     */
    void render(boolean nearExit, boolean nearEntrance) {
        renderTopHud();
        renderBottomHud(nearExit, nearEntrance);
    }

    private void renderTopHud() {
        Player player = gameManager.getPlayer();
        Stats  stats  = player.getStats();
        Zone   zone   = gameManager.getCurrentZone();

        gc.setFill(Color.web("#0e0e1a", 0.96));
        gc.fillRect(0, 0, W, 58);
        gc.setFill(Color.web("#534AB7", 0.5));
        gc.fillRect(0, 56, W, 2);

        gc.setFont(new Font("Monospaced", 15));
        gc.setFill(Color.web("#EEEDFE"));
        gc.fillText(player.getName(), 16, 20);

        renderHpBar(stats);
        renderXpBar(stats);
        renderZoneTitle(zone);
        renderRoomInfo(zone);
    }

    private void renderHpBar(Stats stats) {
        int barX = 16, barY = 28, barW = 160, barH = 10;
        double hpRatio = (double) stats.getCurrentHp() / stats.getMaxHp();
        Color hpColor = hpRatio > 0.5 ? Color.web("#1D9E75")
                      : hpRatio > 0.25 ? Color.web("#EF9F27")
                      : Color.web("#E24B4A");
        gc.setFill(Color.web("#1a1a2a"));
        gc.fillRoundRect(barX, barY, barW, barH, 4, 4);
        gc.setFill(hpColor);
        gc.fillRoundRect(barX, barY, barW * hpRatio, barH, 4, 4);
        gc.setFont(new Font("Monospaced", 11));
        gc.setFill(hpColor);
        gc.fillText("HP", barX + barW + 8, barY + 9);
        gc.setFill(Color.web("#cccccc"));
        gc.fillText(stats.getCurrentHp() + "/" + stats.getMaxHp(), barX + barW + 28, barY + 9);
    }

    private void renderXpBar(Stats stats) {
        int xpBarX = 16, xpBarY = 44, xpBarW = 160, xpBarH = 8;
        double xpRatio = (double) stats.getCurrentXp() / stats.getXpToNextLevel();
        gc.setFill(Color.web("#1a1a2a"));
        gc.fillRoundRect(xpBarX, xpBarY, xpBarW, xpBarH, 4, 4);
        gc.setFill(Color.web("#7F77DD"));
        gc.fillRoundRect(xpBarX, xpBarY, xpBarW * xpRatio, xpBarH, 4, 4);
        gc.setFont(new Font("Monospaced", 11));
        gc.setFill(Color.web("#7F77DD"));
        gc.fillText("XP", xpBarX + xpBarW + 8, xpBarY + 7);
        gc.setFill(Color.web("#EF9F27"));
        gc.fillText("LV." + stats.getLevel(), xpBarX + xpBarW + 28, xpBarY + 7);
    }

    private void renderZoneTitle(Zone zone) {
        String zoneName = zone.getName();
        double titleW = zoneName.length() * 9.5;
        gc.setFont(new Font("Monospaced", 15));
        gc.setFill(Color.web("#FAC775"));
        gc.fillText(zoneName, W / 2.0 - titleW / 2, 34);
    }

    private void renderRoomInfo(Zone zone) {
        Room room       = gameManager.getCurrentRoom();
        int currentRoom = zone.getCurrentRoomIndex() + 1;
        int totalRooms  = zone.getRooms().size();
        gc.setFont(new Font("Monospaced", 14));
        gc.setFill(Color.web("#AFA9EC"));
        gc.fillText("Stanza " + currentRoom + " / " + totalRooms, W - 230, 22);
        gc.setFont(new Font("Monospaced", 12));
        gc.setFill(Color.web("#9990dd"));
        gc.fillText(room.getName(), W - 230, 42);
    }

    private void renderBottomHud(boolean nearExit, boolean nearEntrance) {
        gc.setFill(Color.web("#0e0e1a", 0.96));
        gc.fillRect(0, H - 46, W, 46);
        gc.setFill(Color.web("#534AB7", 0.5));
        gc.fillRect(0, H - 46, W, 2);

        int hudY    = H - 16;
        int spacing = W / 8;
        gc.setFont(new Font("Monospaced", 13));

        renderHudKey("← →",     "muoviti",    12,          hudY, 40);
        renderHudKey("↑",        "salta",      spacing,     hudY, 20);

        if (isNearInteractable(nearExit, nearEntrance)) {
            renderHudKey("[E]", "interagisci", spacing * 2, hudY, 36);
        }
        if (gameManager.getState() == GameState.GAME_OVER) {
            renderHudKey("[R]", "riprova",    spacing * 3, hudY, 36);
        }

        renderHudKey("[CTRL+S]", "salva",     spacing * 4, hudY, 82);
        renderHudKey("[ESC]",    "menu",       spacing * 5, hudY, 54);

        renderInventorySummary(hudY, spacing);
    }

    private boolean isNearInteractable(boolean nearExit, boolean nearEntrance) {
        int px    = gameManager.getPlayer().getX();
        int itemX = W / 2;
        int npcX  = (int)(W * 0.65);
        boolean nearItem = gameManager.getCurrentRoom().getItems().stream()
                .anyMatch(i -> Math.abs(px - itemX) < INTERACT_RANGE);
        boolean nearNpc  = gameManager.getCurrentRoom().getNpcs().stream()
                .anyMatch(n -> Math.abs(px - npcX) < INTERACT_RANGE);
        return nearExit || nearEntrance || nearItem || nearNpc;
    }

    private void renderHudKey(String key, String label, int x, int y, int labelOffset) {
        gc.setFill(Color.web("#7F77DD"));
        gc.fillText(key, x, y);
        gc.setFill(Color.web("#888"));
        gc.fillText(label, x + labelOffset, y);
    }

    private void renderInventorySummary(int hudY, int spacing) {
        Player player = gameManager.getPlayer();
        long potions = player.getInventory().getItems().stream()
                .filter(i -> i instanceof Potion).count();
        gc.setFill(Color.web("#EF9F27"));
        gc.fillText("pozioni:", spacing * 6, hudY);
        gc.setFill(Color.web("#888"));
        gc.fillText(String.valueOf(potions), spacing * 6 + 78, hudY);

        String nonPotions = player.getInventory().getItems().stream()
                .filter(i -> !(i instanceof Potion))
                .map(Item::getName)
                .reduce((a, b) -> a + " " + b)
                .orElse("vuoto");
        gc.setFill(Color.web("#EF9F27"));
        gc.fillText("oggetti:", spacing * 7, hudY);
        gc.setFill(Color.web("#888"));
        gc.fillText(nonPotions, spacing * 7 + 72, hudY);
    }
}
