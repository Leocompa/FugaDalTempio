package it.unicam.cs.mpgc.rpg118708.view.exploration;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.model.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Disegna le entità della stanza nella scena di esplorazione.
 *
 * <p>Responsabilità unica: renderizzare trappole, oggetti, NPC, nemici
 * (vivi e sconfitti) e le porte di ingresso/uscita. Non gestisce logica
 * di gioco né HUD.</p>
 */
class RoomEntityRenderer {

    private static final int PLAYER_H       = 32;
    private static final int INTERACT_RANGE = 60;

    private final GraphicsContext gc;
    private final GameManager     gameManager;
    private final int             W, H, GROUND_Y;

    /**
     * Costruisce il renderer delle entità della stanza.
     *
     * @param gc          il contesto grafico su cui disegnare
     * @param gameManager il gestore dello stato di gioco
     * @param w           larghezza del canvas
     * @param h           altezza del canvas
     * @param groundY     coordinata Y del suolo
     */
    RoomEntityRenderer(GraphicsContext gc, GameManager gameManager, int w, int h, int groundY) {
        this.gc          = gc;
        this.gameManager = gameManager;
        this.W           = w;
        this.H           = h;
        this.GROUND_Y    = groundY;
    }

    /**
     * Disegna tutte le entità della stanza corrente.
     *
     * @param frame        numero di frame, usato per le animazioni
     * @param nearExit     {@code true} se il giocatore è vicino all'uscita
     * @param nearEntrance {@code true} se il giocatore è vicino all'ingresso
     */
    void render(long frame, boolean nearExit, boolean nearEntrance) {
        renderTraps(frame);
        renderItems(frame);
        renderNpcs();
        renderEnemies();
        renderDoors(nearExit, nearEntrance);
    }

    private void renderTraps(long frame) {
        int trapX = W / 4;
        int px    = gameManager.getPlayer().getX();
        for (Trap trap : gameManager.getCurrentRoom().getTraps()) {
            int baseY = GROUND_Y + PLAYER_H;
            boolean active = trap.isActive();

            if (active) {
                double pulse = 0.15 + 0.12 * Math.abs(Math.sin(frame * 0.06));
                gc.setFill(Color.web("#E24B4A", pulse));
                gc.fillOval(trapX - 20, baseY - 50, 72, 50);
            }

            gc.setFill(Color.web("#1a1a2a", 0.5));
            gc.fillOval(trapX - 4, baseY - 4, 40, 6);

            gc.setFill(Color.web("#1e1e30"));
            gc.fillRoundRect(trapX, baseY - 10, 32, 10, 2, 2);
            gc.setFill(Color.web("#2a2a45"));
            gc.fillRoundRect(trapX + 2, baseY - 9, 28, 6, 1, 1);

            int[] bladeOffsets = {2, 8, 14, 20, 26};
            for (int bx : bladeOffsets) {
                String bladeColor = active ? "#aaaacc" : "#555566";
                String edgeColor  = active ? "#ddddef" : "#7a7a8a";
                String baseColor  = active ? "#666688" : "#3a3a4a";
                int bladeH        = active ? 18 : 12;

                gc.setFill(Color.web(baseColor));
                gc.fillPolygon(
                        new double[]{trapX + bx,     trapX + bx + 4, trapX + bx + 2},
                        new double[]{baseY - 10,      baseY - 10,    baseY - 10 - bladeH}, 3);

                gc.setFill(Color.web(bladeColor));
                gc.fillPolygon(
                        new double[]{trapX + bx + 1, trapX + bx + 3, trapX + bx + 2},
                        new double[]{baseY - 10,      baseY - 10,    baseY - 10 - bladeH + 2}, 3);

                gc.setFill(Color.web(edgeColor, 0.7));
                gc.fillRect(trapX + bx + 1, baseY - 10 - bladeH + 2, 1, bladeH - 4);

                if (active) {
                    gc.setFill(Color.web("#8B0000", 0.7));
                    gc.fillOval(trapX + bx + 1, baseY - 10, 2, 2);
                }
            }

            if (active) {
                gc.setFont(new Font("Monospaced", 14));
                gc.setFill(Color.web("#E24B4A"));
                gc.fillText("⚠", trapX + 10, baseY - 30);

                if (Math.abs(px - trapX) < INTERACT_RANGE + 20) {
                    gc.setFill(Color.web("#E24B4A", 0.85));
                    gc.fillRoundRect(trapX - 22, baseY - 56, 76, 18, 4, 4);
                    gc.setFill(Color.web("#FAECE7"));
                    gc.setFont(new Font("Monospaced", 11));
                    gc.fillText("! TRAPPOLA", trapX - 16, baseY - 43);
                }
            }
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
            boolean isBoss = enemy instanceof Boss;
            int cx    = enemyX + 14;
            int baseY = GROUND_Y + PLAYER_H;

            if (!enemy.isAlive()) {
                drawDefeatedEnemySprite(cx, baseY, isBoss);
                continue;
            }

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

    private void drawDefeatedEnemySprite(int cx, int baseY, boolean isBoss) {
        int groundY = baseY - 2;

        if (isBoss) {
            gc.setFill(Color.web("#1a0a0a", 0.45));
            gc.fillOval(cx - 34, groundY - 6, 68, 10);

            gc.setFill(Color.web("#1a0e06"));
            gc.fillRoundRect(cx - 8,  groundY - 10, 13, 10, 2, 2);
            gc.fillRoundRect(cx + 8,  groundY - 10, 13, 10, 2, 2);

            gc.setFill(Color.web("#5C1A1A"));
            gc.fillRoundRect(cx - 20, groundY - 14, 21, 10, 2, 2);
            gc.fillRoundRect(cx + 4,  groundY - 14, 21, 10, 2, 2);

            gc.setFill(Color.web("#7A0000"));
            gc.fillRoundRect(cx - 30, groundY - 16, 60, 12, 4, 4);
            gc.setFill(Color.web("#9E1010"));
            gc.fillRoundRect(cx - 20, groundY - 15, 40, 8, 3, 3);

            gc.setFill(Color.web("#C43030"));
            gc.fillRoundRect(cx + 28, groundY - 18, 18, 14, 4, 4);
            gc.setFill(Color.web("#5C1A1A"));
            gc.fillRoundRect(cx + 27, groundY - 20, 20, 10, 3, 3);

            gc.setFill(Color.web("#EF9F27", 0.6));
            gc.fillRect(cx + 30, groundY - 22, 3, 6);
            gc.fillRect(cx + 36, groundY - 23, 3, 7);

            gc.setFill(Color.web("#FF4040", 0.5));
            gc.fillOval(cx + 31, groundY - 17, 5, 4);
            gc.fillOval(cx + 37, groundY - 17, 5, 4);

        } else {
            gc.setFill(Color.web("#1a0a0a", 0.40));
            gc.fillOval(cx - 26, groundY - 5, 52, 8);

            gc.setFill(Color.web("#1a0e06"));
            gc.fillRoundRect(cx - 6,  groundY - 8, 10, 8, 2, 2);
            gc.fillRoundRect(cx + 6,  groundY - 8, 10, 8, 2, 2);

            gc.setFill(Color.web("#7A2E14"));
            gc.fillRoundRect(cx - 16, groundY - 12, 16, 8, 2, 2);
            gc.fillRoundRect(cx + 4,  groundY - 12, 16, 8, 2, 2);

            gc.setFill(Color.web("#993C1D"));
            gc.fillRoundRect(cx - 22, groundY - 14, 44, 10, 4, 4);
            gc.setFill(Color.web("#C4481F"));
            gc.fillRoundRect(cx - 14, groundY - 13, 28, 7, 2, 2);

            gc.setFill(Color.web("#D85A30"));
            gc.fillRoundRect(cx + 20, groundY - 16, 14, 12, 4, 4);
            gc.setFill(Color.web("#7A2E14"));
            gc.fillRoundRect(cx + 19, groundY - 18, 16, 9, 3, 3);

            gc.setFill(Color.web("#EF9F27", 0.5));
            gc.fillRect(cx + 23, groundY - 19, 3, 5);

            gc.setFill(Color.web("#FAECE7", 0.4));
            gc.fillOval(cx + 21, groundY - 14, 4, 4);
            gc.fillOval(cx + 27, groundY - 14, 4, 4);

            gc.setFill(Color.web("#aaaaaa", 0.6));
            gc.fillRect(cx + 34, groundY - 6, 18, 3);
            gc.setFill(Color.web("#EF9F27", 0.6));
            gc.fillRect(cx + 30, groundY - 8, 10, 3);
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
}
