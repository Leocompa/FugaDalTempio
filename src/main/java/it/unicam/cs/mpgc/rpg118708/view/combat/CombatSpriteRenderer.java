package it.unicam.cs.mpgc.rpg118708.view.combat;

import it.unicam.cs.mpgc.rpg118708.model.Boss;
import it.unicam.cs.mpgc.rpg118708.model.Enemy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Renderer degli sprite del combattimento su canvas JavaFX.
 *
 * <p>Contiene esclusivamente la logica di disegno dei personaggi (giocatore
 * e nemico) nella scena di combattimento. Tutti i metodi sono statici: gli
 * sprite vengono disegnati una volta sola alla costruzione della scena, non
 * a ogni frame.</p>
 *
 * <p>Per il nemico distingue automaticamente tra guardia normale e boss
 * ({@link Boss}) applicando uno schema visivo diverso.</p>
 */
public class CombatSpriteRenderer {

    private CombatSpriteRenderer() {}

    /**
     * Disegna lo sprite del giocatore (ladro) su un canvas 120×160.
     * L'aspetto cambia visivamente in base al livello: spallacci al LV2,
     * armatura più scura al LV3, bordi dorati al LV4, mantello e indaco
     * profondo dal LV5.
     *
     * @param gc    il contesto grafico del canvas di destinazione
     * @param level il livello corrente del giocatore
     */
    public static void drawPlayer(GraphicsContext gc, int level) {
        String bodyColor = level >= 5 ? "#2E2680" : level >= 3 ? "#453CA6" : "#534AB7";
        String headColor = level >= 5 ? "#6A61CC" : level >= 3 ? "#6E67CC" : "#7F77DD";
        String beltColor = level >= 4 ? "#EF9F27" : "#3C3489";

        if (level >= 5) {
            gc.setFill(Color.web("#130d2a", 0.88));
            gc.fillPolygon(
                    new double[]{30, 90, 76, 44},
                    new double[]{64, 64, 156, 156}, 4);
        }

        gc.setFill(Color.web(bodyColor));
        gc.fillRoundRect(38, 112, 18, 34, 4, 4);
        gc.fillRoundRect(64, 112, 18, 34, 4, 4);
        gc.fillRoundRect(18, 72, 14, 28, 4, 4);
        gc.fillRoundRect(88, 72, 14, 28, 4, 4);
        gc.fillRoundRect(28, 62, 64, 52, 6, 6);

        if (level >= 3) {
            gc.setFill(Color.web(level >= 5 ? "#221c5a" : "#312a8a"));
            gc.fillRoundRect(14, 64, 18, 12, 3, 3);
            gc.fillRoundRect(88, 64, 18, 12, 3, 3);
        }

        if (level >= 4) {
            gc.setFill(Color.web("#EF9F27", 0.55));
            gc.fillRect(28, 62, 64, 2);
            gc.fillRect(28, 112, 64, 2);
        }

        if (level >= 2) {
            gc.setFill(Color.web("#5DCAA5"));
            gc.fillOval(55, 80, 10, 10);
        }

        gc.setFill(Color.web(headColor));
        gc.fillRoundRect(34, 18, 52, 46, 10, 10);

        if (level >= 3) {
            gc.setFill(Color.web("#0e0c22"));
            gc.fillArc(28, 10, 64, 34, 0, 180, javafx.scene.shape.ArcType.ROUND);
        }

        gc.setFill(Color.web("#EEEDFE"));
        gc.fillOval(64, 32, 10, 10);

        gc.setFill(Color.web(beltColor));
        gc.fillRoundRect(28, 106, 64, 10, 3, 3);

        if (level >= 5) {
            gc.setFill(Color.web("#FCDE5A"));
            gc.fillOval(56, 107, 8, 8);
        }

        gc.setFill(Color.web("#EF9F27"));
        gc.fillRoundRect(100, 70, 8, 36, 3, 3);
        gc.setFill(Color.web("#FCDE5A"));
        gc.fillRoundRect(98, 66, 12, 8, 2, 2);

        if (level >= 4) {
            gc.setFill(Color.web("#8B6914"));
            gc.fillRoundRect(102, 98, 4, 10, 2, 2);
            gc.setFill(Color.web("#FCDE5A"));
            gc.fillOval(100, 106, 8, 8);
        }
    }

    /**
     * Disegna lo sprite del nemico su un canvas 120×160.
     * Se il nemico è un'istanza di {@link Boss} viene usato uno schema visivo
     * distinto (corona dorata, colori cremisi, occhi rossi).
     *
     * @param gc    il contesto grafico del canvas di destinazione
     * @param enemy il nemico da rappresentare
     */
    public static void drawEnemy(GraphicsContext gc, Enemy enemy) {
        if (enemy instanceof Boss) drawBoss(gc);
        else                       drawGuard(gc);
    }

    private static void drawGuard(GraphicsContext gc) {
        gc.setFill(Color.web("#1a0e06"));
        gc.fillRoundRect(34, 140, 18, 20, 3, 3);
        gc.fillRoundRect(68, 140, 18, 20, 3, 3);
        gc.setFill(Color.web("#7A2E14"));
        gc.fillRoundRect(36, 106, 16, 36, 3, 3);
        gc.fillRoundRect(68, 106, 16, 36, 3, 3);
        gc.setFill(Color.web("#3D1A0A"));
        gc.fillRoundRect(26, 100, 68, 8, 2, 2);
        gc.setFill(Color.web("#993C1D"));
        gc.fillRoundRect(26, 60, 68, 42, 6, 6);
        gc.setFill(Color.web("#C4481F"));
        gc.fillRoundRect(40, 64, 40, 28, 4, 4);
        gc.setFill(Color.web("#7A2E14"));
        gc.fillRoundRect(14, 62, 16, 16, 4, 4);
        gc.fillRoundRect(90, 62, 16, 16, 4, 4);
        gc.setFill(Color.web("#7A2E14"));
        gc.fillRoundRect(16, 76, 12, 28, 3, 3);
        gc.fillRoundRect(92, 76, 12, 28, 3, 3);
        gc.setFill(Color.web("#993C1D"));
        gc.fillRoundRect(48, 52, 24, 10, 2, 2);
        gc.setFill(Color.web("#D85A30"));
        gc.fillRoundRect(34, 28, 52, 36, 8, 8);
        gc.setFill(Color.web("#7A2E14"));
        gc.fillRoundRect(32, 22, 56, 22, 6, 6);
        gc.setFill(Color.web("#EF9F27"));
        gc.fillRect(57, 8, 6, 16);
        gc.setFill(Color.web("#FCDE5A"));
        gc.fillPolygon(new double[]{60, 54, 66}, new double[]{2, 12, 12}, 3);
        gc.setFill(Color.web("#FAECE7"));
        gc.fillOval(42, 36, 12, 10);
        gc.fillOval(66, 36, 12, 10);
        gc.setFill(Color.web("#400000"));
        gc.fillOval(45, 38, 6, 6);
        gc.fillOval(69, 38, 6, 6);
        gc.setFill(Color.web("#aaaaaa"));
        gc.fillRect(108, 52, 4, 82);
        gc.setFill(Color.web("#EF9F27"));
        gc.fillRect(102, 78, 18, 5);
        gc.setFill(Color.web("#8B6914"));
        gc.fillRect(109, 83, 2, 16);
        gc.setFill(Color.web("#EF9F27"));
        gc.fillOval(107, 98, 6, 6);
    }

    private static void drawBoss(GraphicsContext gc) {
        gc.setFill(Color.web("#1a0808"));
        gc.fillRoundRect(32, 138, 22, 22, 3, 3);
        gc.fillRoundRect(66, 138, 22, 22, 3, 3);
        gc.setFill(Color.web("#5C1A1A"));
        gc.fillRoundRect(34, 104, 18, 36, 3, 3);
        gc.fillRoundRect(68, 104, 18, 36, 3, 3);
        gc.setFill(Color.web("#2a0a0a"));
        gc.fillRoundRect(24, 98, 72, 8, 2, 2);
        gc.setFill(Color.web("#7A0000"));
        gc.fillRoundRect(22, 58, 76, 42, 6, 6);
        gc.setFill(Color.web("#9E1010"));
        gc.fillRoundRect(36, 62, 48, 30, 4, 4);
        gc.setFill(Color.web("#EF9F27"));
        gc.fillPolygon(new double[]{60, 52, 68}, new double[]{66, 82, 82}, 3);
        gc.setFill(Color.web("#5C1A1A"));
        gc.fillRoundRect(8,  60, 18, 22, 4, 4);
        gc.fillRoundRect(94, 60, 18, 22, 4, 4);
        gc.setFill(Color.web("#5C1A1A"));
        gc.fillRoundRect(10, 80, 14, 28, 3, 3);
        gc.fillRoundRect(96, 80, 14, 28, 3, 3);
        gc.setFill(Color.web("#C43030"));
        gc.fillRoundRect(32, 28, 56, 34, 8, 8);
        gc.setFill(Color.web("#5C1A1A"));
        gc.fillRoundRect(30, 20, 60, 26, 6, 6);
        gc.setFill(Color.web("#EF9F27"));
        int[] cx  = {34, 42, 52, 60, 68, 76};
        int[] chs = {8, 14, 10, 14, 10, 8};
        for (int i = 0; i < cx.length; i++) {
            gc.fillRect(cx[i], 20 - chs[i], 5, chs[i]);
        }
        gc.setFill(Color.web("#FCDE5A"));
        gc.fillOval(43, 4, 8, 8);
        gc.fillOval(63, 4, 8, 8);
        gc.setFill(Color.web("#FF4040"));
        gc.fillOval(40, 34, 14, 12);
        gc.fillOval(66, 34, 14, 12);
        gc.setFill(Color.web("#800000"));
        gc.fillOval(44, 37, 6, 6);
        gc.fillOval(70, 37, 6, 6);
    }
}
