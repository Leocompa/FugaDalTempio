package it.unicam.cs.mpgc.rpg118708.view.exploration;

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

/**
 * Scena di esplorazione del tempio con visuale a piattaforme 2D.
 *
 * <p>Gestisce il game loop (tramite {@link AnimationTimer}), l'input da tastiera,
 * il rendering su {@link javafx.scene.canvas.Canvas} e le interazioni con
 * nemici, NPC, trappole e oggetti della stanza corrente. Notifica il
 * {@link it.unicam.cs.mpgc.rpg118708.controller.GameController} degli eventi
 * di gioco tramite callback ({@code onEnterCombat}, {@code onZoneComplete},
 * {@code onSave}, {@code onExit}).</p>
 */
public class ExplorationScene {

    private final int W;
    private final int H;
    private static final int TILE = 32;
    private static final int PLAYER_SPEED = 4;
    private static final int PLAYER_W = 24;
    private static final int PLAYER_H = 32;
    private final int GROUND_Y;
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
    private Runnable onExit;

    public void setOnExit(Runnable onExit) { this.onExit = onExit; }

    public ExplorationScene(GameManager gameManager) {
        javafx.geometry.Rectangle2D screen =
                javafx.stage.Screen.getPrimary().getVisualBounds();
        this.W = (int) screen.getWidth();
        this.H = (int) (screen.getHeight() * 0.80);
        this.GROUND_Y = this.H - 100;
        this.gameManager = gameManager;
        buildScene();
    }

    private void buildScene() {
        canvas = new Canvas(W, H);
        gc = canvas.getGraphicsContext2D();

        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox();
        root.setStyle("-fx-background-color: #0d0d14;");
        root.getChildren().add(canvas);

        javafx.geometry.Rectangle2D screen =
                javafx.stage.Screen.getPrimary().getVisualBounds();
        scene = new Scene(root, screen.getWidth(), screen.getHeight());

        scene.setOnKeyPressed(e -> {
            keysPressed.add(e.getCode());
            if (e.getCode() == KeyCode.S && (e.isMetaDown() || e.isControlDown())) {
                if (onSave != null) onSave.run();
            }
            if (e.getCode() == KeyCode.ESCAPE) {
                showExitConfirm();
            }
        });
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

    private void showExitConfirm() {
        gameLoop.stop();

        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Fuga dal Tempio");
        alert.setHeaderText("Vuoi tornare al menu principale?");
        alert.setContentText("I progressi non salvati andranno persi.");

        javafx.scene.control.ButtonType btnSi =
                new javafx.scene.control.ButtonType("Sì, esci");
        javafx.scene.control.ButtonType btnNo =
                new javafx.scene.control.ButtonType("No, continua",
                        javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnSi, btnNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == btnSi) {
                if (onExit != null) onExit.run();
            } else {
                gameLoop.start();
            }
        });
    }

    public void showSaveMessage() {
        saveMessage = "Partita salvata!";
        saveMessageTimer = 120;
    }

    public void setOnSave(Runnable onSave) { this.onSave = onSave; }

    private void update() {
        if (handleGameOverInput()) return;
        if (gameManager.getState() != GameState.EXPLORING) return;

        Player player = gameManager.getPlayer();
        handleMovement(player);
        applyPhysics(player);
        if (checkTrapCollision(player)) return;
        if (checkEnemyCollision(player)) return;
        updateNavigationHints(player);
        handleInteractions(player);
    }

    /** Gestisce il respawn quando il giocatore è in GAME_OVER e preme R. */
    private boolean handleGameOverInput() {
        if (gameManager.getState() == GameState.GAME_OVER) {
            if (keysPressed.contains(KeyCode.R)) {
                gameManager.respawn();
                keysPressed.clear();
            }
            return true;
        }
        return false;
    }

    /** Aggiorna posizione orizzontale e direzione in base ai tasti premuti. */
    private void handleMovement(Player player) {
        int px = player.getX();
        if (keysPressed.contains(KeyCode.LEFT) || keysPressed.contains(KeyCode.A)) {
            px -= PLAYER_SPEED;
            player.setDirection(Direction.LEFT);
        }
        if (keysPressed.contains(KeyCode.RIGHT) || keysPressed.contains(KeyCode.D)) {
            px += PLAYER_SPEED;
            player.setDirection(Direction.RIGHT);
        }
        player.setX(Math.max(0, Math.min(W - PLAYER_W, px)));
    }

    /** Applica gravità, salto e vincolo al suolo. */
    private void applyPhysics(Player player) {
        if ((keysPressed.contains(KeyCode.UP) || keysPressed.contains(KeyCode.W)
                || keysPressed.contains(KeyCode.SPACE)) && onGround) {
            playerVY = JUMP_FORCE;
            onGround = false;
        }
        playerVY += GRAVITY;
        int py = player.getY() + playerVY;
        if (py >= GROUND_Y) {
            py = GROUND_Y;
            playerVY = 0;
            onGround = true;
        }
        player.moveTo(player.getX(), py);
    }

    /**
     * Verifica la collisione con le trappole della stanza.
     *
     * @return {@code true} se il giocatore è morto per una trappola
     */
    private boolean checkTrapCollision(Player player) {
        int trapX = W / 4;
        for (Trap trap : gameManager.getCurrentRoom().getTraps()) {
            trap.setTrapX(trapX);
            trap.setTrapY(GROUND_Y + PLAYER_H - 14);
            trap.trigger(player);
        }
        if (!player.isAlive()) {
            gameManager.setState(GameState.GAME_OVER);
            return true;
        }
        return false;
    }

    /**
     * Verifica la collisione con i nemici vivi della stanza.
     * Se trovata, avvia il combattimento dopo un breve ritardo.
     *
     * @return {@code true} se è scattato un combattimento
     */
    private boolean checkEnemyCollision(Player player) {
        int enemyX = (int)(W * 0.55);
        for (Enemy enemy : gameManager.getCurrentRoom().getEnemies()) {
            if (enemy.isAlive() && collides(
                    player.getX(), player.getY(), PLAYER_W, PLAYER_H,
                    enemyX, GROUND_Y, 32, 40)) {
                enemyWarningTimer = 60;
                javafx.animation.PauseTransition pause =
                        new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
                pause.setOnFinished(e -> {
                    gameManager.enterCombat(enemy);
                    if (onEnterCombat != null) onEnterCombat.run();
                });
                pause.play();
                gameManager.setState(GameState.COMBAT);
                return true;
            }
        }
        return false;
    }

    /** Aggiorna i flag di prossimità a uscita e ingresso. */
    private void updateNavigationHints(Player player) {
        nearExit = player.getX() >= W - PLAYER_W - 80
                && gameManager.getCurrentRoom().isCleared();
        nearEntrance = player.getX() <= 50
                && gameManager.getCurrentZone().getCurrentRoomIndex() > 0;
    }

    /** Gestisce la pressione di E per interagire con uscita, ingresso, oggetti e NPC. */
    private void handleInteractions(Player player) {
        if (nearExit && keysPressed.contains(KeyCode.E)) {
            keysPressed.clear();
            nearExit = false;
            if (!gameManager.advanceRoom()) {
                if (onZoneComplete != null) onZoneComplete.run();
            } else {
                player.moveTo(40, GROUND_Y);
                dialogueText = "";
            }
            return;
        }

        if (nearEntrance && keysPressed.contains(KeyCode.E)) {
            keysPressed.clear();
            nearEntrance = false;
            gameManager.goBackRoom();
            player.moveTo(W - 80, GROUND_Y);
            return;
        }

        if (keysPressed.contains(KeyCode.E)) {
            collectNearbyItems(player);
            interactWithNearbyNPCs(player);
            keysPressed.remove(KeyCode.E);
        }
    }

    /** Raccoglie gli oggetti nella stanza se il giocatore è abbastanza vicino. */
    private void collectNearbyItems(Player player) {
        int itemX = W / 2;
        for (Item item : new ArrayList<>(gameManager.getCurrentRoom().getItems())) {
            if (Math.abs(player.getX() - itemX) < INTERACT_RANGE) {
                gameManager.collectItem(item);
            }
        }
    }

    /** Avvia il dialogo con gli NPC vicini e consegna la ricompensa se disponibile. */
    private void interactWithNearbyNPCs(Player player) {
        int npcX = (int)(W * 0.65);
        for (NPC npc : gameManager.getCurrentRoom().getNpcs()) {
            if (Math.abs(player.getX() - npcX) < INTERACT_RANGE) {
                Item reward = npc.collectReward();
                if (reward != null && player.getInventory().addItem(reward)) {
                    showDialogue(npc.getName() + ": \"" + npc.getDialogue() + "\"\n"
                            + "[Hai ricevuto: " + reward.getName() + "!]");
                } else {
                    showDialogue(npc.getName() + ": \"" + npc.getDialogue() + "\"");
                }
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
        renderEnemyWarning();
        renderSaveMessage();
        renderHUD();
        renderDialogue();

        if (gameManager.getState() == GameState.GAME_OVER) {
            renderOverlay("GAME OVER", "premi R per riprovare", "#A32D2D");
        }
        if (gameManager.getState() == GameState.VICTORY) {
            renderOverlay("VITTORIA!", "hai completato il tempio!", "#1D9E75");
        }
    }

    private void renderEnemyWarning() {
        if (enemyWarningTimer <= 0) return;
        enemyWarningTimer--;
        gc.setFill(Color.web("#D85A30", 0.85));
        gc.fillRect(0, 0, W, H);
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Monospaced", 28));
        gc.fillText("! NEMICO !", W / 2.0 - 80, H / 2.0);
        gc.setFont(new Font("Monospaced", 14));
        gc.fillText("preparati al combattimento...", W / 2.0 - 120, H / 2.0 + 36);
    }

    private void renderSaveMessage() {
        if (saveMessageTimer <= 0) return;
        saveMessageTimer--;
        gc.setFill(Color.web("#1D9E75", 0.90));
        gc.fillRoundRect(W / 2.0 - 100, 50, 200, 36, 8, 8);
        gc.setFill(Color.web("#E1F5EE"));
        gc.setFont(new Font("Monospaced", 13));
        gc.fillText(saveMessage, W / 2.0 - 60, 73);
    }

    private void renderDialogue() {
        if (gameManager.getState() != GameState.DIALOGUE || dialogueText.isEmpty()) return;
        gc.setFill(Color.web("#13131f", 0.92));
        gc.fillRoundRect(60, H - 160, W - 120, 100, 8, 8);
        gc.setStroke(Color.web("#534AB7"));
        gc.setLineWidth(1);
        gc.strokeRoundRect(60, H - 160, W - 120, 100, 8, 8);
        gc.setFill(Color.web("#AFA9EC"));
        gc.setFont(new Font("Monospaced", 12));
        gc.fillText(dialogueText, 80, H - 120, W - 160);
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
        renderTraps();
        renderItems();
        renderNpcs();
        renderEnemies();
        renderDoors();
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
                            GROUND_Y + PLAYER_H - 14},
                    5);
        }
    }

    private void renderItems() {
        int itemX = W / 2;
        int px = gameManager.getPlayer().getX();
        for (Item item : gameManager.getCurrentRoom().getItems()) {
            double bob = Math.sin(frame * 0.05) * 3;
            gc.setFill(Color.web("#EF9F27"));
            gc.fillOval(itemX, GROUND_Y + PLAYER_H - 40 + bob, 16, 16);
            gc.setFill(Color.web("#FCDE5A"));
            gc.fillOval(itemX+3, GROUND_Y + PLAYER_H - 37 + bob, 6, 6);
            if (Math.abs(px - itemX) < INTERACT_RANGE) {
                gc.setFill(Color.web("#EF9F27"));
                gc.setFont(new Font("Monospaced", 11));
                gc.fillText("[E] raccogli", itemX - 10, GROUND_Y + PLAYER_H - 50 + bob);
            }
        }
    }

    private void renderNpcs() {
        int npcX = (int)(W * 0.65);
        int px = gameManager.getPlayer().getX();
        for (NPC npc : gameManager.getCurrentRoom().getNpcs()) {
            gc.setFill(Color.web("#1D9E75"));
            gc.fillRoundRect(npcX, GROUND_Y + PLAYER_H - 40, 20, 32, 4, 4);
            gc.setFill(Color.web("#E1F5EE"));
            gc.fillOval(npcX+4, GROUND_Y + PLAYER_H - 48, 14, 14);
            if (Math.abs(px - npcX) < INTERACT_RANGE) {
                gc.setFill(Color.web("#5DCAA5"));
                gc.setFont(new Font("Monospaced", 11));
                gc.fillText("[E] parla", npcX - 4, GROUND_Y + PLAYER_H - 56);
            }
        }
    }

    private void renderEnemies() {
        int enemyX = (int)(W * 0.55);
        for (Enemy enemy : gameManager.getCurrentRoom().getEnemies()) {
            if (!enemy.isAlive()) continue;
            gc.setFill(Color.web("#D85A30"));
            gc.fillRoundRect(enemyX, GROUND_Y + PLAYER_H - 40, 28, 40, 4, 4);
            gc.setFill(Color.web("#FAECE7"));
            gc.fillOval(enemyX+4, GROUND_Y + PLAYER_H - 36, 8, 8);
            gc.fillOval(enemyX+16, GROUND_Y + PLAYER_H - 36, 8, 8);
        }
    }

    private void renderDoors() {
        gc.setFill(Color.web("#534AB7"));
        gc.fillRoundRect(W - 60, GROUND_Y + PLAYER_H - 60, 28, 60, 4, 4);
        gc.setFill(Color.web("#AFA9EC"));
        gc.setFont(new Font("Monospaced", 10));
        gc.fillText("USCITA", W - 62, GROUND_Y + PLAYER_H - 65);

        if (nearExit) {
            gc.setFill(Color.web("#534AB7", 0.85));
            gc.fillRoundRect(W - 160, GROUND_Y + PLAYER_H - 90, 100, 20, 4, 4);
            gc.setFill(Color.web("#AFA9EC"));
            gc.setFont(new Font("Monospaced", 11));
            gc.fillText("[E] avanza", W - 152, GROUND_Y + PLAYER_H - 75);
        }

        gc.setFill(Color.web("#534AB7"));
        gc.fillRoundRect(12, GROUND_Y + PLAYER_H - 60, 28, 60, 4, 4);
        if (gameManager.getCurrentZone().getCurrentRoomIndex() > 0) {
            gc.setFill(Color.web("#AFA9EC"));
            gc.setFont(new Font("Monospaced", 10));
            gc.fillText("INDIETRO", 4, GROUND_Y + PLAYER_H - 65);
            if (nearEntrance) {
                gc.setFill(Color.web("#534AB7", 0.85));
                gc.fillRoundRect(12, GROUND_Y + PLAYER_H - 90, 120, 20, 4, 4);
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
        renderTopHud();
        renderBottomHud();
    }

    private void renderTopHud() {
        Player player = gameManager.getPlayer();
        Stats stats = player.getStats();
        Zone zone = gameManager.getCurrentZone();

        gc.setFill(Color.web("#13131f", 0.92));
        gc.fillRect(0, 0, W, 48);
        gc.setStroke(Color.web("#2a2a40"));
        gc.setLineWidth(0.5);
        gc.strokeLine(0, 48, W, 48);

        gc.setFont(new Font("Monospaced", 13));
        gc.setFill(Color.web("#AFA9EC"));
        gc.fillText(player.getName(), 16, 18);

        renderHpBar(stats);
        renderXpBar(stats);
        renderZoneTitle(zone);
        renderRoomInfo(zone);
    }

    private void renderHpBar(Stats stats) {
        int barX = 16, barY = 24, barW = 140, barH = 9;
        gc.setFill(Color.web("#2a2a40"));
        gc.fillRoundRect(barX, barY, barW, barH, 4, 4);
        double hpRatio = (double) stats.getCurrentHp() / stats.getMaxHp();
        Color hpColor = hpRatio > 0.5 ? Color.web("#1D9E75")
                : hpRatio > 0.25 ? Color.web("#EF9F27")
                : Color.web("#E24B4A");
        gc.setFill(hpColor);
        gc.fillRoundRect(barX, barY, barW * hpRatio, barH, 4, 4);
        gc.setFont(new Font("Monospaced", 10));
        gc.setFill(Color.web("#888"));
        gc.fillText("HP " + stats.getCurrentHp() + "/" + stats.getMaxHp(), barX + barW + 6, barY + 8);
    }

    private void renderXpBar(Stats stats) {
        int xpBarX = 16, xpBarY = 36, xpBarW = 140, xpBarH = 7;
        gc.setFill(Color.web("#2a2a40"));
        gc.fillRoundRect(xpBarX, xpBarY, xpBarW, xpBarH, 4, 4);
        double xpRatio = (double) stats.getCurrentXp() / stats.getXpToNextLevel();
        gc.setFill(Color.web("#7F77DD"));
        gc.fillRoundRect(xpBarX, xpBarY, xpBarW * xpRatio, xpBarH, 4, 4);
        gc.setFont(new Font("Monospaced", 10));
        gc.setFill(Color.web("#888"));
        gc.fillText("XP  LV." + stats.getLevel(), xpBarX + xpBarW + 6, xpBarY + 6);
    }

    private void renderZoneTitle(Zone zone) {
        String zoneName = zone.getName();
        double titleW = zoneName.length() * 8.5;
        gc.setFont(new Font("Monospaced", 13));
        gc.setFill(Color.web("#EF9F27"));
        gc.fillText(zoneName, W / 2.0 - titleW / 2, 30);
    }

    private void renderRoomInfo(Zone zone) {
        Room room = gameManager.getCurrentRoom();
        int currentRoom = zone.getCurrentRoomIndex() + 1;
        int totalRooms = zone.getRooms().size();
        gc.setFont(new Font("Monospaced", 13));
        gc.setFill(Color.web("#AFA9EC"));
        gc.fillText("Stanza " + currentRoom + " / " + totalRooms, W - 220, 18);
        gc.setFont(new Font("Monospaced", 11));
        gc.setFill(Color.web("#666"));
        gc.fillText(room.getName(), W - 220, 36);
    }

    private void renderBottomHud() {
        gc.setFill(Color.web("#13131f", 0.92));
        gc.fillRect(0, H - 44, W, 44);
        gc.setStroke(Color.web("#2a2a40"));
        gc.setLineWidth(0.5);
        gc.strokeLine(0, H - 44, W, H - 44);

        int hudY = H - 18;
        int spacing = W / 8;
        gc.setFont(new Font("Monospaced", 12));

        renderHudKey("← →", "muoviti",    12,           hudY, 38);
        renderHudKey("↑",   "salta",      spacing,      hudY, 18);
        renderHudKey("[E]", "interagisci", spacing * 2, hudY, 34);
        renderHudKey("[R]", "riprova",    spacing * 3,  hudY, 34);
        renderHudKey("[CTRL+S]", "salva", spacing * 4,  hudY, 78);
        renderHudKey("[ESC]", "menu",     spacing * 5,  hudY, 50);

        renderInventorySummary(hudY, spacing);
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
                .filter(i -> i.getType() == ItemType.POTION)
                .count();
        gc.setFill(Color.web("#EF9F27"));
        gc.fillText("pozioni:", spacing * 6, hudY);
        gc.setFill(Color.web("#888"));
        gc.fillText(String.valueOf(potions), spacing * 6 + 72, hudY);

        String nonPotions = player.getInventory().getItems().stream()
                .filter(i -> i.getType() != ItemType.POTION)
                .map(Item::getName)
                .reduce((a, b) -> a + " " + b)
                .orElse("vuoto");
        gc.setFill(Color.web("#EF9F27"));
        gc.fillText("oggetti:", spacing * 7, hudY);
        gc.setFill(Color.web("#888"));
        gc.fillText(nonPotions, spacing * 7 + 68, hudY);
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