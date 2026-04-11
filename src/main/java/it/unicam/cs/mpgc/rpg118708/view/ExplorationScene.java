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
import java.util.List;
import java.util.Set;

public class ExplorationScene {

    private static final int W = 800;
    private static final int H = 600;
    private static final int TILE = 32;
    private static final int PLAYER_SPEED = 4;
    private static final int PLAYER_W = 24;
    private static final int PLAYER_H = 32;
    private static final int GROUND_Y = H - 80;
    private static final int INTERACT_RANGE = 60;

    private final GameManager gameManager;
    private Scene scene;
    private Canvas canvas;
    private GraphicsContext gc;
    private final Set<KeyCode> keysPressed = new HashSet<>();
    private AnimationTimer gameLoop;
    private long frame = 0;
    private String dialogueText = "";
    private boolean nearExit = false;
    private int playerVY = 0;
    private boolean onGround = true;
    private static final int GRAVITY = 1;
    private static final int JUMP_FORCE = -14;
    private int enemyWarningTimer = 0;
    private boolean nearEntrance = false;
    private String saveMessage = "";
    private int saveMessageTimer = 0;

    private Runnable onEnterCombat;
    private Runnable onZoneComplete;
    private Runnable onSave;

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
        scene.setOnKeyPressed(e -> {
            keysPressed.add(e.getCode());
            if (e.getCode() == KeyCode.S && e.isMetaDown()) {
                if (onSave != null) onSave.run();
            }
        });

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                frame++;
                update();
                render();
            }
        };
    }

    public void showSaveMessage() {
        saveMessage = "Partita salvata!";
        saveMessageTimer = 120;
    }

    public void setOnSave(Runnable onSave) { this.onSave = onSave; }

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

        if ((keysPressed.contains(KeyCode.UP) || keysPressed.contains(KeyCode.W)
                || keysPressed.contains(KeyCode.SPACE)) && onGround) {
            playerVY = JUMP_FORCE;
            onGround = false;
        }

        playerVY += GRAVITY;
        py = player.getY() + playerVY;

        if (py >= GROUND_Y) {
            py = GROUND_Y;
            playerVY = 0;
            onGround = true;
        }

        px = Math.max(0, Math.min(W - PLAYER_W, px));
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
            if (enemy.isAlive() && collides(px, py, PLAYER_W, PLAYER_H, 500, GROUND_Y, 32, 40)) {
                enemyWarningTimer = 60;
                javafx.animation.PauseTransition pause =
                        new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
                pause.setOnFinished(e -> {
                    gameManager.enterCombat(enemy);
                    if (onEnterCombat != null) onEnterCombat.run();
                });
                pause.play();
                gameManager.setState(GameState.COMBAT);
                return;
            }
        }

        if (nearExit && keysPressed.contains(KeyCode.E)) {
            keysPressed.clear();
            nearExit = false;
            if (!gameManager.advanceRoom()) {
                if (onZoneComplete != null) onZoneComplete.run();
            } else {
                gameManager.getPlayer().moveTo(40, GROUND_Y);
                dialogueText = "";
            }
            return;
        }

        if (keysPressed.contains(KeyCode.E)) {
            for (Item item : new ArrayList<>(room.getItems())) {
                if (Math.abs(px - 400) < INTERACT_RANGE) {
                    gameManager.collectItem(item);
                }
            }
            for (NPC npc : room.getNpcs()) {
                if (Math.abs(px - 600) < INTERACT_RANGE) {
                    showDialogue(npc.getName() + ": \"" + npc.getDialogue() + "\"");
                }
            }
            keysPressed.remove(KeyCode.E);
        }

        nearExit = px >= W - PLAYER_W - 80 && room.isCleared();

        nearEntrance = px <= 50 && gameManager.getCurrentZone().getCurrentRoomIndex() > 0;

        if (nearEntrance && keysPressed.contains(KeyCode.E)) {
            keysPressed.clear();
            nearEntrance = false;
            gameManager.goBackRoom();
            gameManager.getPlayer().moveTo(W - 80, GROUND_Y);
            return;
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
        if (enemyWarningTimer > 0) {
            enemyWarningTimer--;
            gc.setFill(Color.web("#D85A30", 0.85));
            gc.fillRect(0, 0, W, H);
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Monospaced", 28));
            gc.fillText("! NEMICO !", W / 2.0 - 80, H / 2.0);
            gc.setFont(new Font("Monospaced", 14));
            gc.fillText("preparati al combattimento...", W / 2.0 - 120, H / 2.0 + 36);
        }
        if (saveMessageTimer > 0) {
            saveMessageTimer--;
            gc.setFill(Color.web("#1D9E75", 0.90));
            gc.fillRoundRect(W / 2.0 - 100, 50, 200, 36, 8, 8);
            gc.setFill(Color.web("#E1F5EE"));
            gc.setFont(new Font("Monospaced", 13));
            gc.fillText(saveMessage, W / 2.0 - 60, 73);
        }
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
        Player player = gameManager.getPlayer();
        int px = player.getX();
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
            if (Math.abs(px - 400) < INTERACT_RANGE) {
                gc.setFill(Color.web("#EF9F27"));
                gc.setFont(new Font("Monospaced", 11));
                gc.fillText("[E] raccogli", 390, GROUND_Y + PLAYER_H - 50 + bob);
            }
        }

        for (NPC npc : room.getNpcs()) {
            gc.setFill(Color.web("#1D9E75"));
            gc.fillRoundRect(600, GROUND_Y + PLAYER_H - 40, 20, 32, 4, 4);
            gc.setFill(Color.web("#E1F5EE"));
            gc.fillOval(604, GROUND_Y + PLAYER_H - 48, 14, 14);
            if (Math.abs(px - 600) < INTERACT_RANGE) {
                gc.setFill(Color.web("#5DCAA5"));
                gc.setFont(new Font("Monospaced", 11));
                gc.fillText("[E] parla", 594, GROUND_Y + PLAYER_H - 56);
            }
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

        if (nearExit) {
            gc.setFill(Color.web("#534AB7", 0.85));
            gc.fillRoundRect(W - 120, GROUND_Y + PLAYER_H - 90, 100, 20, 4, 4);
            gc.setFill(Color.web("#AFA9EC"));
            gc.setFont(new Font("Monospaced", 11));
            gc.fillText("[E] avanza", W - 112, GROUND_Y + PLAYER_H - 75);
        }

        gc.setFill(Color.web("#534AB7"));
        gc.fillRoundRect(12, GROUND_Y + PLAYER_H - 60, 28, 60, 4, 4);
        if (gameManager.getCurrentZone().getCurrentRoomIndex() > 0) {
            gc.setFill(Color.web("#AFA9EC"));
            gc.setFont(new Font("Monospaced", 10));
            gc.fillText("INDIETRO", 4, GROUND_Y + PLAYER_H - 65);
            if (nearEntrance) {
                gc.setFill(Color.web("#534AB7", 0.85));
                gc.fillRoundRect(12, GROUND_Y + PLAYER_H - 90, 100, 20, 4, 4);
                gc.setFill(Color.web("#AFA9EC"));
                gc.setFont(new Font("Monospaced", 11));
                gc.fillText("[E] torna indietro", 16, GROUND_Y + PLAYER_H - 75);
            }
        } else {
            gc.setFill(Color.web("#3a3a55"));
            gc.setFont(new Font("Monospaced", 10));
            gc.fillText("ENTRATA", 4, GROUND_Y + PLAYER_H - 65);
        }
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

        gc.setFill(Color.web("#13131f", 0.90));
        gc.fillRect(0, 0, W, 40);

        gc.setFont(new Font("Monospaced", 12));
        gc.setFill(Color.web("#AFA9EC"));
        gc.fillText(player.getName(), 12, 24);

        gc.setFill(Color.web("#2a2a40"));
        gc.fillRoundRect(110, 14, 100, 12, 4, 4);
        gc.setFill(Color.web("#E24B4A"));
        double hpRatio = (double) stats.getCurrentHp() / stats.getMaxHp();
        gc.fillRoundRect(110, 14, 100 * hpRatio, 12, 4, 4);
        gc.setFill(Color.web("#ccc"));
        gc.fillText("HP " + stats.getCurrentHp() + "/" + stats.getMaxHp(), 218, 24);

        gc.setFill(Color.web("#888780"));
        gc.fillText("LV." + stats.getLevel(), 330, 24);
        gc.fillText("XP " + stats.getCurrentXp() + "/" + stats.getXpToNextLevel(), 375, 24);

        gc.setFill(Color.web("#555"));
        int currentRoom = gameManager.getCurrentZone().getCurrentRoomIndex() + 1;
        int totalRooms = gameManager.getCurrentZone().getRooms().size();
        gc.fillText(zone.getName() + " — stanza " + currentRoom + "/" + totalRooms
                + ": " + room.getName(), W - 380, 24);

        gc.setFill(Color.web("#13131f", 0.90));
        gc.fillRect(0, H - 40, W, 40);

        gc.setFill(Color.web("#13131f", 0.90));
        gc.fillRect(0, H - 36, W, 36);
        gc.setFill(Color.web("#555"));
        gc.setFont(new Font("Monospaced", 11));
        gc.fillText("← → muoviti", 12, H - 16);
        gc.fillText("↑ salta", 130, H - 16);
        gc.fillText("[E] interagisci", 210, H - 16);
        gc.fillText("[R] riprova", 330, H - 16);
        gc.fillText("[CMD+S] salva", 430, H - 16);
        gc.setFill(Color.web("#534AB7"));
        gc.fillText("inventario:", 560, H - 18);
        List<Item> items = player.getInventory().getItems();
        if (items.isEmpty()) {
            gc.setFill(Color.web("#555"));
            gc.fillText("vuoto", 650, H - 18);
        } else {
            gc.setFill(Color.web("#EF9F27"));
            StringBuilder inv = new StringBuilder();
            for (Item item : items) {
                inv.append(item.getName()).append(" ");
            }
            gc.fillText(inv.toString().trim(), 650, H - 18);
        }
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