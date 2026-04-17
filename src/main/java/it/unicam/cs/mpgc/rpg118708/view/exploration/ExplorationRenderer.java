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
 * Orchestratore del rendering grafico della scena di esplorazione.
 *
 * <p>Coordina i sotto-renderer specializzati: disegna direttamente sfondo,
 * suolo, giocatore e overlay, e delega le entità della stanza a
 * {@link RoomEntityRenderer} e il HUD a {@link HudRenderer}.
 * Non contiene logica di gioco: legge solo lo stato e disegna sul
 * {@link GraphicsContext}.</p>
 */
class ExplorationRenderer implements SceneRenderer {

    private final GraphicsContext    gc;
    private final GameManager        gameManager;
    private final int                W, H, GROUND_Y;
    private final HudRenderer        hudRenderer;
    private final RoomEntityRenderer roomRenderer;

    private static final int PLAYER_W = 24;
    private static final int PLAYER_H = 32;

    private int    enemyWarningTimer = 0;
    private int    saveMessageTimer  = 0;
    private String saveMessage       = "";
    private String dialogueText      = "";

    /**
     * Costruisce il renderer di esplorazione e i suoi sotto-renderer.
     *
     * @param gc          il contesto grafico del canvas su cui disegnare
     * @param gameManager il gestore dello stato di gioco da cui leggere i dati
     * @param w           larghezza del canvas in pixel
     * @param h           altezza del canvas in pixel
     * @param groundY     coordinata Y del suolo
     */
    ExplorationRenderer(GraphicsContext gc, GameManager gameManager, int w, int h, int groundY) {
        this.gc          = gc;
        this.gameManager = gameManager;
        this.W           = w;
        this.H           = h;
        this.GROUND_Y    = groundY;
        this.hudRenderer  = new HudRenderer(gc, gameManager, w, h);
        this.roomRenderer = new RoomEntityRenderer(gc, gameManager, w, h, groundY);
    }

    /** Attiva il flash visivo di avviso nemico per circa 60 frame. */
    public void triggerEnemyWarning() { enemyWarningTimer = 60; }

    /**
     * Mostra un messaggio di salvataggio in sovrimpressione per circa 2 secondi.
     *
     * @param msg il testo da visualizzare
     */
    public void showSaveMessage(String msg) {
        saveMessage      = msg;
        saveMessageTimer = 120;
    }

    /** Imposta il testo del dialogo da mostrare in sovrimpressione. */
    public void showDialogue(String text) { dialogueText = text; }

    /** Rimuove il testo del dialogo dalla schermata. */
    public void clearDialogue()           { dialogueText = ""; }

    /**
     * Esegue un frame completo di rendering.
     *
     * @param frame        numero di frame dall'avvio, usato per le animazioni
     * @param nearExit     {@code true} se il giocatore è vicino all'uscita
     * @param nearEntrance {@code true} se il giocatore è vicino all'ingresso
     * @param onGround     {@code true} se il giocatore è a terra
     * @param keysPressed  insieme dei tasti premuti in questo frame
     */
    public void render(long frame, boolean nearExit, boolean nearEntrance,
                boolean onGround, Set<KeyCode> keysPressed) {
        gc.setFill(Color.web("#080810"));
        gc.fillRect(0, 0, W, H);

        renderBackground();
        renderGround();
        roomRenderer.render(frame, nearExit, nearEntrance);
        renderPlayer(frame, onGround, keysPressed);
        renderEnemyWarning();
        renderSaveMessage();
        hudRenderer.render(nearExit, nearEntrance);
        renderDialogue();

        GameState state = gameManager.getState();
        if (state == GameState.GAME_OVER) renderOverlay("GAME OVER", "premi R per riprovare", "#A32D2D");
        if (state == GameState.VICTORY)   renderOverlay("VITTORIA!", "hai completato il tempio!", "#1D9E75");
    }

    private void renderBackground() {
        int bW = 50, bH = 22, gap = 2;
        String[] shades = {"#131320", "#111118", "#161626", "#12121e", "#141422"};
        for (int row = 0; row * (bH + gap) < GROUND_Y + PLAYER_H + bH; row++) {
            int y = row * (bH + gap);
            int offsetX = (row % 2 == 0) ? 0 : (bW + gap) / 2;
            for (int col = -1; col * (bW + gap) - offsetX < W + bW; col++) {
                int x = col * (bW + gap) - offsetX;
                gc.setFill(Color.web(shades[Math.abs((row * 3 + col * 2) % shades.length)]));
                gc.fillRect(x, y, bW, bH);
            }
        }
        renderWallTorches();
    }

    private void renderWallTorches() {
        int torchY = GROUND_Y + PLAYER_H - 90;
        int[] tX   = {(int)(W * 0.2), (int)(W * 0.5), (int)(W * 0.8)};
        for (int tx : tX) {
            gc.setFill(Color.web("#EF9F27", 0.06));
            gc.fillOval(tx - 90, torchY - 90, 180, 180);
            gc.setFill(Color.web("#EF9F27", 0.05));
            gc.fillOval(tx - 55, torchY - 55, 110, 110);
            gc.setFill(Color.web("#3a2a10"));
            gc.fillRect(tx - 6, torchY - 4, 12, 6);
            gc.fillRect(tx - 2, torchY + 2, 4, 18);
            gc.setFill(Color.web("#EF9F27", 0.95));
            gc.fillOval(tx - 6, torchY - 18, 12, 16);
            gc.setFill(Color.web("#FACC5A", 0.85));
            gc.fillOval(tx - 4, torchY - 14, 8, 10);
            gc.setFill(Color.web("#FFFFFF", 0.45));
            gc.fillOval(tx - 2, torchY - 11, 4, 5);
        }
    }

    private void renderGround() {
        gc.setFill(Color.web("#161626"));
        gc.fillRect(0, GROUND_Y + PLAYER_H, W, H - GROUND_Y - PLAYER_H);
        gc.setFill(Color.web("#3a3a55"));
        gc.fillRect(0, GROUND_Y + PLAYER_H, W, 3);
        gc.setStroke(Color.web("#1e1e30"));
        gc.setLineWidth(1);
        for (int y = GROUND_Y + PLAYER_H + 20; y < H; y += 20)
            gc.strokeLine(0, y, W, y);
        for (int x = 0; x < W; x += 80)
            gc.strokeLine(x, GROUND_Y + PLAYER_H, x, H);
        for (int x = 40; x < W; x += 80)
            gc.strokeLine(x, GROUND_Y + PLAYER_H + 20, x, H);
    }

    private void renderPlayer(long frame, boolean onGround, Set<KeyCode> keysPressed) {
        Player player = gameManager.getPlayer();
        int    level  = player.getStats().getLevel();
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

        String bodyColor = level >= 5 ? "#2E2680" : level >= 3 ? "#453CA6" : "#534AB7";
        String headColor = level >= 5 ? "#6A61CC" : level >= 3 ? "#6E67CC" : "#7F77DD";
        String beltColor = level >= 4 ? "#EF9F27" : "#3C3489";

        if (level >= 5) {
            gc.setFill(Color.web("#130d2a", 0.80));
            gc.fillPolygon(
                    new double[]{px - 2,  px + PLAYER_W + 2, px + PLAYER_W - 2, px + 2},
                    new double[]{drawY + 20, drawY + 20,      drawY + 48,        drawY + 48}, 4);
        }

        gc.setFill(Color.web(bodyColor));

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

        if (level >= 3) {
            gc.setFill(Color.web(level >= 5 ? "#221c5a" : "#312a8a"));
            gc.fillRoundRect(px - 6, drawY + 20, 6, 8, 2, 2);
            gc.fillRoundRect(px + PLAYER_W, drawY + 20, 6, 8, 2, 2);
        }

        if (level >= 4) {
            gc.setFill(Color.web("#EF9F27", 0.50));
            gc.fillRect(px - 2, drawY + 20, PLAYER_W + 4, 2);
            gc.fillRect(px - 2, drawY + 30, PLAYER_W + 4, 2);
        }

        gc.setFill(Color.web(beltColor));
        gc.fillRoundRect(px - 1, drawY + 30, PLAYER_W + 2, 3, 1, 1);

        if (level >= 5) {
            gc.setFill(Color.web("#FCDE5A"));
            gc.fillOval(px + PLAYER_W / 2 - 2, drawY + 29, 4, 5);
        }

        gc.setFill(Color.web(headColor));
        gc.fillRoundRect(px, drawY, PLAYER_W, 20, 4, 4);

        if (level >= 3) {
            gc.setFill(Color.web("#0e0c22"));
            gc.fillArc(px - 2, drawY - 4, PLAYER_W + 4, 14, 0, 180,
                    javafx.scene.shape.ArcType.ROUND);
        }

        if (level >= 2) {
            gc.setFill(Color.web("#5DCAA5"));
            gc.fillOval(px + 9, drawY + 22, 6, 6);
        }

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
}
