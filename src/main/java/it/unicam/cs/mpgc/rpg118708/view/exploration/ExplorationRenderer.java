package it.unicam.cs.mpgc.rpg118708.view.exploration;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.engine.GameState;
import it.unicam.cs.mpgc.rpg118708.model.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Set;

/**
 * Responsabile del rendering grafico della scena di esplorazione.
 *
 * <p>Riceve il contesto grafico e le informazioni di stato necessarie
 * tramite {@link #render} e metodi di notifica. Non contiene logica
 * di gioco: legge solo lo stato e disegna sul {@link GraphicsContext}.</p>
 */
class ExplorationRenderer {

    private final GraphicsContext gc;
    private final GameManager gameManager;
    private final int W, H, GROUND_Y;

    private static final int TILE        = 32;
    private static final int PLAYER_W    = 24;
    private static final int PLAYER_H    = 32;
    private static final int INTERACT_RANGE = 60;

    private int    enemyWarningTimer = 0;
    private int    saveMessageTimer  = 0;
    private String saveMessage       = "";
    private String dialogueText      = "";

    /**
     * Costruisce il renderer per la scena di esplorazione.
     *
     * @param gc         il contesto grafico del canvas su cui disegnare
     * @param gameManager il gestore dello stato di gioco da cui leggere i dati
     * @param w          larghezza del canvas in pixel
     * @param h          altezza del canvas in pixel
     * @param groundY    coordinata Y del suolo
     */
    ExplorationRenderer(GraphicsContext gc, GameManager gameManager, int w, int h, int groundY) {
        this.gc          = gc;
        this.gameManager = gameManager;
        this.W           = w;
        this.H           = h;
        this.GROUND_Y    = groundY;
    }

    /** Attiva il flash visivo di avviso nemico per circa 60 frame. */
    void triggerEnemyWarning() { enemyWarningTimer = 60; }

    /**
     * Mostra un messaggio di salvataggio in sovrimpressione per circa 2 secondi.
     *
     * @param msg il testo da visualizzare
     */
    void showSaveMessage(String msg) {
        saveMessage      = msg;
        saveMessageTimer = 120;
    }

    /** Imposta il testo del dialogo da mostrare in sovrimpressione. */
    void showDialogue(String text) { dialogueText = text; }

    /** Rimuove il testo del dialogo dalla schermata. */
    void clearDialogue()           { dialogueText = ""; }

    /**
     * Esegue un frame completo di rendering.
     *
     * @param frame        numero di frame dall'avvio, usato per le animazioni
     * @param nearExit     {@code true} se il giocatore è vicino all'uscita
     * @param nearEntrance {@code true} se il giocatore è vicino all'ingresso
     * @param onGround     {@code true} se il giocatore è a terra
     * @param keysPressed  insieme dei tasti premuti in questo frame
     */
    void render(long frame, boolean nearExit, boolean nearEntrance,
                boolean onGround, Set<KeyCode> keysPressed) {
        gc.setFill(Color.web("#0d0d14"));
        gc.fillRect(0, 0, W, H);

        renderGrid();
        renderGround();
        renderRoom(frame, nearExit, nearEntrance);
        renderPlayer(frame, onGround, keysPressed);
        renderEnemyWarning();
        renderSaveMessage();
        renderHUD(nearExit, nearEntrance);
        renderDialogue();

        GameState state = gameManager.getState();
        if (state == GameState.GAME_OVER) renderOverlay("GAME OVER", "premi R per riprovare", "#A32D2D");
        if (state == GameState.VICTORY)   renderOverlay("VITTORIA!", "hai completato il tempio!", "#1D9E75");
    }

    private void renderGrid() {
        gc.setStroke(Color.web("#161622"));
        gc.setLineWidth(0.5);
        for (int x = 0; x < W; x += TILE) gc.strokeLine(x, 0, x, H);
        for (int y = 0; y < H; y += TILE) gc.strokeLine(0, y, W, y);
    }

    private void renderGround() {
        gc.setFill(Color.web("#1e1e30"));
        gc.fillRect(0, GROUND_Y + PLAYER_H, W, H - GROUND_Y - PLAYER_H);
        gc.setFill(Color.web("#3a3a55"));
        gc.fillRect(0, GROUND_Y + PLAYER_H, W, 4);
    }

    private void renderRoom(long frame, boolean nearExit, boolean nearEntrance) {
        renderTraps();
        renderItems(frame);
        renderNpcs();
        renderEnemies();
        renderDoors(nearExit, nearEntrance);
    }

    private void renderTraps() {
        int trapX = W / 4;
        for (Trap trap : gameManager.getCurrentRoom().getTraps()) {
            gc.setFill(trap.isActive() ? Color.web("#EF9F27") : Color.web("#BA7517"));
            gc.fillRect(trapX, GROUND_Y + PLAYER_H - 14, 32, 14);
            gc.setFill(Color.web("#fff"));
            gc.fillPolygon(
                    new double[]{trapX+4, trapX+10, trapX+16, trapX+22, trapX+28},
                    new double[]{GROUND_Y + PLAYER_H - 14,
                                 GROUND_Y + PLAYER_H - 26,
                                 GROUND_Y + PLAYER_H - 14,
                                 GROUND_Y + PLAYER_H - 26,
                                 GROUND_Y + PLAYER_H - 14}, 5);
        }
    }

    private void renderItems(long frame) {
        int itemX = W / 2;
        int px    = gameManager.getPlayer().getX();
        for (Item item : gameManager.getCurrentRoom().getItems()) {
            double bob = Math.sin(frame * 0.05) * 3;
            gc.setFill(Color.web("#EF9F27"));
            gc.fillOval(itemX, GROUND_Y + PLAYER_H - 40 + bob, 16, 16);
            gc.setFill(Color.web("#FCDE5A"));
            gc.fillOval(itemX + 3, GROUND_Y + PLAYER_H - 37 + bob, 6, 6);
            if (Math.abs(px - itemX) < INTERACT_RANGE) {
                gc.setFill(Color.web("#EF9F27"));
                gc.setFont(new Font("Monospaced", 12));
                gc.fillText("[E] raccogli", itemX - 10, GROUND_Y + PLAYER_H - 50 + bob);
            }
        }
    }

    private void renderNpcs() {
        int npcX = (int)(W * 0.65);
        int px   = gameManager.getPlayer().getX();
        for (NPC npc : gameManager.getCurrentRoom().getNpcs()) {
            int baseY = GROUND_Y + PLAYER_H;
            int cx    = npcX + 10;

            gc.setFill(Color.web("#0A6B50"));
            gc.fillPolygon(
                new double[]{cx - 14, cx + 14, cx + 16, cx - 16},
                new double[]{baseY - 26, baseY - 26, baseY, baseY}, 4);

            gc.setFill(Color.web("#1D9E75"));
            gc.fillRoundRect(cx - 10, baseY - 50, 20, 26, 5, 5);

            gc.setFill(Color.web("#0A6B50"));
            gc.fillRoundRect(cx - 18, baseY - 48, 10, 18, 3, 3);
            gc.fillRoundRect(cx + 8,  baseY - 48, 10, 18, 3, 3);

            gc.setFill(Color.web("#D4A96A"));
            gc.fillOval(cx - 13, baseY - 33, 7, 7);
            gc.fillOval(cx + 7,  baseY - 33, 7, 7);


            gc.setFill(Color.web("#0A6B50"));
            gc.fillOval(cx - 11, baseY - 68, 22, 22);

            gc.setFill(Color.web("#D4A96A"));
            gc.fillOval(cx - 8, baseY - 65, 16, 15);

            gc.setFill(Color.web("#1D5C4A"));
            gc.fillOval(cx - 5, baseY - 61, 4, 4);
            gc.fillOval(cx + 2, baseY - 61, 4, 4);

            gc.setFill(Color.web("#6B4A14"));
            gc.fillRect(cx - 20, baseY - 74, 3, 74);
            gc.setFill(Color.web("#1D9E75"));
            gc.fillOval(cx - 23, baseY - 82, 9, 9);
            gc.setFill(Color.web("#5DCAA5", 0.75));
            gc.fillOval(cx - 22, baseY - 81, 7, 7);

            gc.setFill(Color.web("#EF9F27"));
            gc.fillOval(cx - 2, baseY - 48, 4, 4);

            if (Math.abs(px - npcX) < INTERACT_RANGE) {
                gc.setFill(Color.web("#5DCAA5"));
                gc.setFont(new Font("Monospaced", 12));
                gc.fillText("[E] parla", npcX - 4, baseY - 90);
            }
        }
    }

    private void renderEnemies() {
        int enemyX = (int)(W * 0.55);
        for (Enemy enemy : gameManager.getCurrentRoom().getEnemies()) {
            if (!enemy.isAlive()) continue;

            boolean isBoss = enemy instanceof Boss;
            int cx   = enemyX + 14;
            int baseY = GROUND_Y + PLAYER_H;
            int spriteH = isBoss ? 80 : 62;

            int barW = isBoss ? 60 : 50, barH = 5;
            int barX = cx - barW / 2;
            int barY = baseY - spriteH - 14;
            double hpRatio = (double) enemy.getStats().getCurrentHp() / enemy.getStats().getMaxHp();
            gc.setFill(Color.web("#2a2a40"));
            gc.fillRoundRect(barX, barY, barW, barH, 2, 2);
            gc.setFill(hpRatio > 0.5 ? Color.web("#D85A30") : Color.web("#E24B4A"));
            gc.fillRoundRect(barX, barY, barW * hpRatio, barH, 2, 2);
            gc.setFont(new Font("Monospaced", 10));
            gc.setFill(Color.web("#FAECE7", 0.8));
            gc.fillText(enemy.getName(), barX, barY - 2);

            drawExplorationEnemySprite(cx, baseY, isBoss);
        }
    }

    private void drawExplorationEnemySprite(int cx, int baseY, boolean isBoss) {
        if (isBoss) {
            gc.setFill(Color.web("#1a0e06"));
            gc.fillRoundRect(cx - 17, baseY - 16, 13, 16, 2, 2);
            gc.fillRoundRect(cx + 4,  baseY - 16, 13, 16, 2, 2);
            gc.setFill(Color.web("#5C1A1A"));
            gc.fillRoundRect(cx - 16, baseY - 36, 12, 21, 2, 2);
            gc.fillRoundRect(cx + 4,  baseY - 36, 12, 21, 2, 2);
            gc.setFill(Color.web("#2a0a0a"));
            gc.fillRoundRect(cx - 17, baseY - 40, 34, 6, 2, 2);
            gc.setFill(Color.web("#7A0000"));
            gc.fillRoundRect(cx - 16, baseY - 60, 32, 22, 5, 5);
            gc.setFill(Color.web("#9E1010"));
            gc.fillRoundRect(cx - 10, baseY - 58, 20, 15, 3, 3);
            gc.setFill(Color.web("#5C1A1A"));
            gc.fillRoundRect(cx - 26, baseY - 60, 12, 12, 3, 3);
            gc.fillRoundRect(cx + 14, baseY - 60, 12, 12, 3, 3);
            gc.setFill(Color.web("#5C1A1A"));
            gc.fillRoundRect(cx - 25, baseY - 50, 10, 18, 2, 2);
            gc.fillRoundRect(cx + 15, baseY - 50, 10, 18, 2, 2);
            gc.setFill(Color.web("#C43030"));
            gc.fillRoundRect(cx - 13, baseY - 78, 26, 20, 5, 5);
            gc.setFill(Color.web("#5C1A1A"));
            gc.fillRoundRect(cx - 14, baseY - 82, 28, 14, 4, 4);
            gc.setFill(Color.web("#EF9F27"));
            int[] crownX = {cx-10, cx-8, cx-2, cx+2, cx+8, cx+10};
            int[] crownH = {6, 10, 7, 10, 7, 6};
            for (int i = 0; i < crownX.length; i++) {
                gc.fillRect(crownX[i], baseY - 82 - crownH[i], 4, crownH[i]);
            }
            gc.setFill(Color.web("#FF4040"));
            gc.fillOval(cx - 9, baseY - 74, 6, 5);
            gc.fillOval(cx + 3, baseY - 74, 6, 5);
        } else {
            gc.setFill(Color.web("#1a0e06"));
            gc.fillRoundRect(cx - 13, baseY - 13, 10, 13, 2, 2);
            gc.fillRoundRect(cx + 3,  baseY - 13, 10, 13, 2, 2);
            gc.setFill(Color.web("#7A2E14"));
            gc.fillRoundRect(cx - 12, baseY - 30, 9, 18, 2, 2);
            gc.fillRoundRect(cx + 3,  baseY - 30, 9, 18, 2, 2);
            gc.setFill(Color.web("#3D1A0A"));
            gc.fillRoundRect(cx - 13, baseY - 34, 26, 5, 1, 1);
            gc.setFill(Color.web("#993C1D"));
            gc.fillRoundRect(cx - 13, baseY - 52, 26, 20, 4, 4);
            gc.setFill(Color.web("#C4481F"));
            gc.fillRoundRect(cx - 7, baseY - 50, 14, 13, 2, 2);
            gc.setFill(Color.web("#7A2E14"));
            gc.fillRoundRect(cx - 21, baseY - 50, 9, 16, 3, 3);
            gc.fillRoundRect(cx + 12, baseY - 50, 9, 16, 3, 3);
            gc.setFill(Color.web("#D85A30"));
            gc.fillRoundRect(cx - 9, baseY - 66, 18, 15, 4, 4);
            gc.setFill(Color.web("#7A2E14"));
            gc.fillRoundRect(cx - 10, baseY - 68, 20, 11, 3, 3);
            gc.setFill(Color.web("#EF9F27"));
            gc.fillRect(cx - 1, baseY - 76, 4, 10);
            gc.setFill(Color.web("#FAECE7"));
            gc.fillOval(cx - 7, baseY - 63, 5, 5);
            gc.fillOval(cx + 3, baseY - 63, 5, 5);
            gc.setFill(Color.web("#aaaaaa"));
            gc.fillRect(cx + 19, baseY - 72, 3, 52);
            gc.setFill(Color.web("#EF9F27"));
            gc.fillRect(cx + 15, baseY - 46, 11, 4);
            gc.setFill(Color.web("#8B6914"));
            gc.fillRect(cx + 20, baseY - 42, 2, 10);
        }
    }

    private void renderDoors(boolean nearExit, boolean nearEntrance) {
        gc.setFill(Color.web("#534AB7"));
        gc.fillRoundRect(W - 60, GROUND_Y + PLAYER_H - 60, 28, 60, 4, 4);
        gc.setFill(Color.web("#AFA9EC"));
        gc.setFont(new Font("Monospaced", 11));
        gc.fillText("USCITA", W - 62, GROUND_Y + PLAYER_H - 65);

        if (nearExit) {
            gc.setFill(Color.web("#534AB7", 0.85));
            gc.fillRoundRect(W - 160, GROUND_Y + PLAYER_H - 90, 100, 20, 4, 4);
            gc.setFill(Color.web("#AFA9EC"));
            gc.setFont(new Font("Monospaced", 12));
            gc.fillText("[E] avanza", W - 152, GROUND_Y + PLAYER_H - 75);
        }

        gc.setFill(Color.web("#534AB7"));
        gc.fillRoundRect(12, GROUND_Y + PLAYER_H - 60, 28, 60, 4, 4);
        if (gameManager.getCurrentZone().getCurrentRoomIndex() > 0) {
            gc.setFill(Color.web("#AFA9EC"));
            gc.setFont(new Font("Monospaced", 11));
            gc.fillText("INDIETRO", 4, GROUND_Y + PLAYER_H - 65);
            if (nearEntrance) {
                gc.setFill(Color.web("#534AB7", 0.85));
                gc.fillRoundRect(12, GROUND_Y + PLAYER_H - 90, 130, 20, 4, 4);
                gc.setFill(Color.web("#AFA9EC"));
                gc.setFont(new Font("Monospaced", 12));
                gc.fillText("[E] torna indietro", 16, GROUND_Y + PLAYER_H - 75);
            }
        } else {
            gc.setFill(Color.web("#3a3a55"));
            gc.setFont(new Font("Monospaced", 11));
            gc.fillText("ENTRATA", 4, GROUND_Y + PLAYER_H - 65);
        }
    }

    private void renderPlayer(long frame, boolean onGround, Set<KeyCode> keysPressed) {
        Player player = gameManager.getPlayer();
        int px = player.getX();
        int py = player.getY();
        boolean moving = keysPressed.contains(KeyCode.LEFT)  || keysPressed.contains(KeyCode.A)
                      || keysPressed.contains(KeyCode.RIGHT) || keysPressed.contains(KeyCode.D);

        int drawY = onGround && !moving
                ? py + (int)(Math.sin(frame * 0.04) * 1.5)
                : py;

        double shadowScale = onGround ? 1.0 : Math.max(0.3, 1.0 - (GROUND_Y - py) * 0.004);
        int shadowW = (int)(28 * shadowScale);
        int shadowX = px + PLAYER_W / 2 - shadowW / 2;
        gc.setFill(Color.web("#000000", 0.25));
        gc.fillOval(shadowX, GROUND_Y + PLAYER_H - 3, shadowW, 5);

        gc.setFill(Color.web("#534AB7"));

        if (!onGround) {
            gc.fillRoundRect(px + 4,  drawY + 26, 7, 8, 2, 2);
            gc.fillRoundRect(px + 13, drawY + 26, 7, 8, 2, 2);
            gc.fillRoundRect(px - 6,  drawY + 12, 6, 10, 2, 2);
            gc.fillRoundRect(px + PLAYER_W, drawY + 12, 6, 10, 2, 2);
        } else if (moving) {
            int leg = (int)(frame / 6) % 2;
            gc.fillRoundRect(px + 2,  drawY + 32, 8, leg == 0 ? 14 : 8,  2, 2);
            gc.fillRoundRect(px + 14, drawY + 32, 8, leg == 0 ? 8  : 14, 2, 2);
            gc.fillRoundRect(px - 6,  leg == 0 ? drawY + 24 : drawY + 20, 6, 10, 2, 2);
            gc.fillRoundRect(px + PLAYER_W, leg == 0 ? drawY + 20 : drawY + 24, 6, 10, 2, 2);
        } else {
            gc.fillRoundRect(px + 2,  drawY + 32, 8, 12, 2, 2);
            gc.fillRoundRect(px + 14, drawY + 32, 8, 12, 2, 2);
            gc.fillRoundRect(px - 6,  drawY + 22, 6, 10, 2, 2);
            gc.fillRoundRect(px + PLAYER_W, drawY + 22, 6, 10, 2, 2);
        }

        gc.fillRoundRect(px - 2, drawY + 20, PLAYER_W + 4, 12, 3, 3);

        gc.setFill(Color.web("#7F77DD"));
        gc.fillRoundRect(px, drawY, PLAYER_W, 20, 4, 4);

        gc.setFill(Color.web("#EEEDFE"));
        int eyeX = player.getDirection() == Direction.RIGHT ? px + 14 : px + 4;
        gc.fillOval(eyeX, drawY + 6, 6, 6);
    }

    private void renderEnemyWarning() {
        if (enemyWarningTimer <= 0) return;
        enemyWarningTimer--;
        gc.setFill(Color.web("#D85A30", 0.85));
        gc.fillRect(0, 0, W, H);
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Monospaced", 30));
        gc.fillText("! NEMICO !", W / 2.0 - 90, H / 2.0);
        gc.setFont(new Font("Monospaced", 16));
        gc.fillText("preparati al combattimento...", W / 2.0 - 130, H / 2.0 + 40);
    }

    private void renderSaveMessage() {
        if (saveMessageTimer <= 0) return;
        saveMessageTimer--;
        gc.setFill(Color.web("#1D9E75", 0.90));
        gc.fillRoundRect(W / 2.0 - 110, 50, 220, 38, 8, 8);
        gc.setFill(Color.web("#E1F5EE"));
        gc.setFont(new Font("Monospaced", 14));
        gc.fillText(saveMessage, W / 2.0 - 65, 75);
    }

    private void renderDialogue() {
        if (gameManager.getState() != GameState.DIALOGUE || dialogueText.isEmpty()) return;
        gc.setFill(Color.web("#13131f", 0.92));
        gc.fillRoundRect(60, H - 160, W - 120, 100, 8, 8);
        gc.setStroke(Color.web("#534AB7"));
        gc.setLineWidth(1);
        gc.strokeRoundRect(60, H - 160, W - 120, 100, 8, 8);
        gc.setFill(Color.web("#AFA9EC"));
        gc.setFont(new Font("Monospaced", 13));
        gc.fillText(dialogueText, 80, H - 120, W - 160);
    }

    private void renderOverlay(String title, String subtitle, String color) {
        gc.setFill(Color.web(color, 0.7));
        gc.fillRect(0, 0, W, H);
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Monospaced", 34));
        gc.fillText(title, W / 2.0 - 90, H / 2.0);
        gc.setFont(new Font("Monospaced", 16));
        gc.fillText(subtitle, W / 2.0 - 110, H / 2.0 + 40);
    }

    void renderHUD(boolean nearExit, boolean nearEntrance) {
        renderTopHud();
        renderBottomHud(nearExit, nearEntrance);
    }

    private void renderTopHud() {
        Player player = gameManager.getPlayer();
        Stats  stats  = player.getStats();
        Zone   zone   = gameManager.getCurrentZone();

        gc.setFill(Color.web("#13131f", 0.92));
        gc.fillRect(0, 0, W, 52);
        gc.setStroke(Color.web("#2a2a40"));
        gc.setLineWidth(0.5);
        gc.strokeLine(0, 52, W, 52);

        gc.setFont(new Font("Monospaced", 14));
        gc.setFill(Color.web("#AFA9EC"));
        gc.fillText(player.getName(), 16, 19);

        renderHpBar(stats);
        renderXpBar(stats);
        renderZoneTitle(zone);
        renderRoomInfo(zone);
    }

    private void renderHpBar(Stats stats) {
        int barX = 16, barY = 26, barW = 150, barH = 10;
        gc.setFill(Color.web("#2a2a40"));
        gc.fillRoundRect(barX, barY, barW, barH, 4, 4);
        double hpRatio = (double) stats.getCurrentHp() / stats.getMaxHp();
        Color hpColor = hpRatio > 0.5 ? Color.web("#1D9E75")
                      : hpRatio > 0.25 ? Color.web("#EF9F27")
                      : Color.web("#E24B4A");
        gc.setFill(hpColor);
        gc.fillRoundRect(barX, barY, barW * hpRatio, barH, 4, 4);
        gc.setFont(new Font("Monospaced", 11));
        gc.setFill(Color.web("#888"));
        gc.fillText("HP " + stats.getCurrentHp() + "/" + stats.getMaxHp(), barX + barW + 8, barY + 9);
    }

    private void renderXpBar(Stats stats) {
        int xpBarX = 16, xpBarY = 40, xpBarW = 150, xpBarH = 8;
        gc.setFill(Color.web("#2a2a40"));
        gc.fillRoundRect(xpBarX, xpBarY, xpBarW, xpBarH, 4, 4);
        double xpRatio = (double) stats.getCurrentXp() / stats.getXpToNextLevel();
        gc.setFill(Color.web("#7F77DD"));
        gc.fillRoundRect(xpBarX, xpBarY, xpBarW * xpRatio, xpBarH, 4, 4);
        gc.setFont(new Font("Monospaced", 11));
        gc.setFill(Color.web("#888"));
        gc.fillText("XP  LV." + stats.getLevel(), xpBarX + xpBarW + 8, xpBarY + 7);
    }

    private void renderZoneTitle(Zone zone) {
        String zoneName = zone.getName();
        double titleW = zoneName.length() * 9.0;
        gc.setFont(new Font("Monospaced", 14));
        gc.setFill(Color.web("#EF9F27"));
        gc.fillText(zoneName, W / 2.0 - titleW / 2, 32);
    }

    private void renderRoomInfo(Zone zone) {
        Room room       = gameManager.getCurrentRoom();
        int currentRoom = zone.getCurrentRoomIndex() + 1;
        int totalRooms  = zone.getRooms().size();
        gc.setFont(new Font("Monospaced", 14));
        gc.setFill(Color.web("#AFA9EC"));
        gc.fillText("Stanza " + currentRoom + " / " + totalRooms, W - 230, 20);
        gc.setFont(new Font("Monospaced", 12));
        gc.setFill(Color.web("#666"));
        gc.fillText(room.getName(), W - 230, 38);
    }

    private void renderBottomHud(boolean nearExit, boolean nearEntrance) {
        gc.setFill(Color.web("#13131f", 0.92));
        gc.fillRect(0, H - 46, W, 46);
        gc.setStroke(Color.web("#2a2a40"));
        gc.setLineWidth(0.5);
        gc.strokeLine(0, H - 46, W, H - 46);

        int hudY    = H - 16;
        int spacing = W / 8;
        gc.setFont(new Font("Monospaced", 13));

        renderHudKey("← →",     "muoviti",    12,          hudY, 40);
        renderHudKey("↑",        "salta",      spacing,     hudY, 20);

        boolean nearInteractable = isNearInteractable(nearExit, nearEntrance);
        if (nearInteractable) {
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
