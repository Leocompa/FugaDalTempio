package it.unicam.cs.mpgc.rpg118708.view;

import it.unicam.cs.mpgc.rpg118708.engine.GameManager;
import it.unicam.cs.mpgc.rpg118708.engine.GameState;
import it.unicam.cs.mpgc.rpg118708.model.*;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ExplorationScene {

    private static final int W = 800;
    private static final int H = 600;
    private static final int TILE = 32;
    private static final int PLAYER_SPEED = 4;
    private static final int PLAYER_W = 24;
    private static final int PLAYER_H = 32;
    private static final int GROUND_Y = H - 80;

    private final GameManager gameManager;
    private Scene scene;
    private Canvas canvas;
    private GraphicsContext gc;
    private final Set<KeyCode> keysPressed = new HashSet<>();
    private AnimationTimer gameLoop;
    private long frame = 0;
    private String dialogueText = "";

    private Runnable onEnterCombat;
    private Runnable onZoneComplete;

    public ExplorationScene(GameManager gameManager) {
        this.gameManager = gameManager;
        buildScene();
    }

    private void buildScene() {
        canvas = new Canvas(W, H);
        gc = canvas.getGraphicsContext2D();

        VBox root = new VBox(canvas);
        scene = new Scene(root, W, H);

        scene.setOnKeyPressed(e -> keysPressed.add(e.getCode()));
        scene.setOnKeyReleased(e -> keysPressed.remove(e.getCode()));

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                frame++;
                update();
                render();
            }
        };
    }

    private void update() {
        if (gameManager.getState() == GameState.GAME_OVER) {
            if (keysPressed.contains(KeyCode.R)) {
                gameManager.respawn();
                keysPressed.clear();
            }
            return;
        }

        if (gameManager.getState() != GameState.EXPLORING) return;

        Player player = gameManager.getPlayer();
        int px = player.getX();
        int py = player.getY();

        if (keysPressed.contains(KeyCode.LEFT) || keysPressed.contains(KeyCode.A)) {
            px -= PLAYER_SPEED;
            player.setDirection(Direction.LEFT);
        }
        if (keysPressed.contains(KeyCode.RIGHT) || keysPressed.contains(KeyCode.D)) {
            px += PLAYER_SPEED;
            player.setDirection(Direction.RIGHT);
        }

        px = Math.max(0, Math.min(W - PLAYER_W, px));
        py = GROUND_Y;
        player.moveTo(px, py);

        Room room = gameManager.getCurrentRoom();

        for (Trap trap : room.getTraps()) {
            trap.trigger(player);
        }
        if (!player.isAlive()) {
            gameManager.setState(GameState.GAME_OVER);
            return;
        }

        for (Enemy enemy : room.getEnemies()) {
            if (enemy.isAlive() && collides(px, py, PLAYER_W, PLAYER_H,
                    500, GROUND_Y, 32, 40)) {
                gameManager.enterCombat(enemy);
                if (onEnterCombat != null) onEnterCombat.run();
                return;
            }
        }

        if (keysPressed.contains(KeyCode.E)) {
            for (Item item : new ArrayList<>(room.getItems())) {
                gameManager.collectItem(item);
            }
            for (NPC npc : room.getNpcs()) {
                showDialogue(npc.getDialogue());
            }
        }

        if (px >= W - PLAYER_W - 10 && room.isCleared()) {
            if (!gameManager.advanceRoom()) {
                if (onZoneComplete != null) onZoneComplete.run();
            }
        }
    }

    private boolean collides(int ax, int ay, int aw, int ah,
                             int bx, int by, int bw, int bh) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }

    private void render() {
        gc.setFill(Color.web("#0d0d14"));
        gc.fillRect(0, 0, W, H);

        renderGrid();
        renderGround();
        renderRoom();
        renderPlayer();
        renderHUD();

        if (gameManager.getState() == GameState.DIALOGUE && !dialogueText.isEmpty()) {
            gc.setFill(Color.web("#13131f", 0.92));
            gc.fillRoundRect(60, H - 160, W - 120, 100, 8, 8);
            gc.setStroke(Color.web("#534AB7"));
            gc.setLineWidth(1);
            gc.strokeRoundRect(60, H - 160, W - 120, 100, 8, 8);
            gc.setFill(Color.web("#AFA9EC"));
            gc.setFont(new Font("Monospaced", 12));
            gc.fillText(dialogueText, 80, H - 120, W - 160);
        }

        if (gameManager.getState() == GameState.GAME_OVER) {
            renderOverlay("GAME OVER", "premi R per riprovare", "#A32D2D");
        }
        if (gameManager.getState() == GameState.VICTORY) {
            renderOverlay("VITTORIA!", "hai completato il tempio!", "#1D9E75");
        }
    }

    private void renderGrid() {
        gc.setStroke(Color.web("#161622"));
        gc.setLineWidth(0.5);
        for (int x = 0; x < W; x += TILE) {
            gc.strokeLine(x, 0, x, H);
        }
        for (int y = 0; y < H; y += TILE) {
            gc.strokeLine(0, y, W, y);
        }
    }

    private void renderGround() {
        gc.setFill(Color.web("#1e1e30"));
        gc.fillRect(0, GROUND_Y + PLAYER_H, W, H - GROUND_Y - PLAYER_H);
        gc.setFill(Color.web("#3a3a55"));
        gc.fillRect(0, GROUND_Y + PLAYER_H, W, 4);
    }

    private void renderRoom() {
        Room room = gameManager.getCurrentRoom();

        for (Trap trap : room.getTraps()) {
            gc.setFill(trap.isActive() ? Color.web("#EF9F27") : Color.web("#BA7517"));
            gc.fillRect(200, GROUND_Y + PLAYER_H - 14, 32, 14);
            gc.setFill(Color.web("#fff"));
            gc.fillPolygon(
                    new double[]{204, 210, 216, 222, 228},
                    new double[]{GROUND_Y + PLAYER_H - 14,
                            GROUND_Y + PLAYER_H - 26,
                            GROUND_Y + PLAYER_H - 14,
                            GROUND_Y + PLAYER_H - 26,
                            GROUND_Y + PLAYER_H - 14},
                    5);
        }

        for (Item item : room.getItems()) {
            double bob = Math.sin(frame * 0.05) * 3;
            gc.setFill(Color.web("#EF9F27"));
            gc.fillOval(400, GROUND_Y + PLAYER_H - 40 + bob, 16, 16);
            gc.setFill(Color.web("#FCDE5A"));
            gc.fillOval(403, GROUND_Y + PLAYER_H - 37 + bob, 6, 6);
        }

        for (NPC npc : room.getNpcs()) {
            gc.setFill(Color.web("#1D9E75"));
            gc.fillRoundRect(600, GROUND_Y + PLAYER_H - 40, 20, 32, 4, 4);
            gc.setFill(Color.web("#E1F5EE"));
            gc.fillOval(604, GROUND_Y + PLAYER_H - 48, 14, 14);
            gc.setFill(Color.web("#5DCAA5"));
            gc.setFont(new Font("Monospaced", 11));
            gc.fillText("[E]", 598, GROUND_Y + PLAYER_H - 52);
        }

        for (Enemy enemy : room.getEnemies()) {
            if (!enemy.isAlive()) continue;
            gc.setFill(Color.web("#D85A30"));
            gc.fillRoundRect(500, GROUND_Y + PLAYER_H - 40, 28, 40, 4, 4);
            gc.setFill(Color.web("#FAECE7"));
            gc.fillOval(504, GROUND_Y + PLAYER_H - 36, 8, 8);
            gc.fillOval(516, GROUND_Y + PLAYER_H - 36, 8, 8);
        }

        gc.setFill(Color.web("#534AB7"));
        gc.fillRoundRect(W - 40, GROUND_Y + PLAYER_H - 60, 28, 60, 4, 4);
        gc.setFill(Color.web("#AFA9EC"));
        gc.setFont(new Font("Monospaced", 10));
        gc.fillText("USCITA", W - 42, GROUND_Y + PLAYER_H - 65);
    }

    private void renderPlayer() {
        Player player = gameManager.getPlayer();
        int px = player.getX();
        int py = player.getY();
        boolean movingLeft = keysPressed.contains(KeyCode.LEFT) || keysPressed.contains(KeyCode.A);
        boolean movingRight = keysPressed.contains(KeyCode.RIGHT) || keysPressed.contains(KeyCode.D);

        gc.setFill(Color.web("#7F77DD"));
        gc.fillRoundRect(px, py, PLAYER_W, 20, 4, 4);
        gc.setFill(Color.web("#EEEDFE"));
        int eyeX = player.getDirection() == Direction.RIGHT ? px + 14 : px + 4;
        gc.fillOval(eyeX, py + 6, 6, 6);
        gc.setFill(Color.web("#534AB7"));
        gc.fillRoundRect(px - 2, py + 20, PLAYER_W + 4, 12, 3, 3);

        if (movingLeft || movingRight) {
            int leg = (int) (frame / 6) % 2;
            gc.fillRoundRect(px + 2, py + 32, 8, leg == 0 ? 14 : 8, 2, 2);
            gc.fillRoundRect(px + 14, py + 32, 8, leg == 0 ? 8 : 14, 2, 2);
        } else {
            gc.fillRoundRect(px + 2, py + 32, 8, 12, 2, 2);
            gc.fillRoundRect(px + 14, py + 32, 8, 12, 2, 2);
        }
    }

    private void renderHUD() {
        Player player = gameManager.getPlayer();
        Stats stats = player.getStats();
        Room room = gameManager.getCurrentRoom();
        Zone zone = gameManager.getCurrentZone();

        gc.setFill(Color.web("#13131f", 0.85));
        gc.fillRect(0, 0, W, 36);

        gc.setFont(new Font("Monospaced", 12));
        gc.setFill(Color.web("#AFA9EC"));
        gc.fillText(player.getName(), 12, 22);

        gc.setFill(Color.web("#E24B4A"));
        gc.fillRect(120, 12, 100, 8);
        gc.setFill(Color.web("#7F77DD"));
        double hpRatio = (double) stats.getCurrentHp() / stats.getMaxHp();
        gc.fillRect(120, 12, 100 * hpRatio, 8);
        gc.setFill(Color.web("#ccc"));
        gc.fillText("HP " + stats.getCurrentHp() + "/" + stats.getMaxHp(), 228, 22);

        gc.setFill(Color.web("#888780"));
        gc.fillText("LV." + stats.getLevel(), 340, 22);
        gc.fillText("XP " + stats.getCurrentXp() + "/" + stats.getXpToNextLevel(), 390, 22);

        gc.setFill(Color.web("#555"));
        gc.fillText(zone.getName() + " — " + room.getName(), W - 280, 22);
    }

    private void renderOverlay(String title, String subtitle, String color) {
        gc.setFill(Color.web(color, 0.7));
        gc.fillRect(0, 0, W, H);
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Monospaced", 32));
        gc.fillText(title, W / 2.0 - 80, H / 2.0);
        gc.setFont(new Font("Monospaced", 14));
        gc.fillText(subtitle, W / 2.0 - 100, H / 2.0 + 36);
    }

    private void showDialogue(String text) {
        gameManager.setState(GameState.DIALOGUE);
        javafx.animation.PauseTransition pause =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        pause.setOnFinished(e -> gameManager.setState(GameState.EXPLORING));
        pause.play();
        dialogueText = text;
    }

    public void start() { gameLoop.start(); }
    public void stop() { gameLoop.stop(); }
    public Scene getScene() { return scene; }
    public void setOnEnterCombat(Runnable onEnterCombat) { this.onEnterCombat = onEnterCombat; }
    public void setOnZoneComplete(Runnable onZoneComplete) { this.onZoneComplete = onZoneComplete; }
}