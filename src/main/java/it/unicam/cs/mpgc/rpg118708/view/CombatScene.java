package it.unicam.cs.mpgc.rpg118708.view;

import it.unicam.cs.mpgc.rpg118708.controller.CombatController;
import it.unicam.cs.mpgc.rpg118708.engine.CombatResult;
import it.unicam.cs.mpgc.rpg118708.model.CombatActionType;
import it.unicam.cs.mpgc.rpg118708.model.Enemy;
import it.unicam.cs.mpgc.rpg118708.model.Player;
import it.unicam.cs.mpgc.rpg118708.model.Stats;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class CombatScene {

    private Scene scene;
    private Label logLabel;
    private Label playerHpLabel;
    private Label enemyHpLabel;
    private Label playerStatsLabel;
    private Label enemyStatsLabel;
    private Label turnLabel;
    private Button attackButton;
    private Button specialButton;
    private Button healButton;
    private Button fleeButton;
    private Canvas playerCanvas;
    private Canvas enemyCanvas;
    private VBox rootBox;
    private Stage stage;
    private final CombatController controller;

    public CombatScene(CombatController controller) {
        this.controller = controller;
        buildScene();
    }

    public void setStage(Stage stage) { this.stage = stage; }

    private void buildScene() {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #0d0d14;");
        this.rootBox = root;

        HBox hudRow = buildHudRow();
        HBox battleArea = buildBattleArea();
        VBox logArea = buildLogArea();
        HBox actionArea = buildActionArea();

        root.getChildren().addAll(hudRow, battleArea, logArea, actionArea);
        scene = new Scene(root, 800, 600);
    }

    private HBox buildHudRow() {
        turnLabel = new Label("turno del ladro");
        turnLabel.setFont(new Font("Monospaced", 12));
        turnLabel.setStyle("-fx-text-fill: #AFA9EC;");

        HBox hud = new HBox(turnLabel);
        hud.setAlignment(Pos.CENTER_LEFT);
        hud.setPadding(new Insets(10, 20, 10, 20));
        hud.setStyle("-fx-background-color: #13131f; -fx-border-color: #2a2a40; -fx-border-width: 0 0 0.5 0;");
        return hud;
    }

    private HBox buildBattleArea() {
        playerCanvas = new Canvas(120, 160);
        enemyCanvas = new Canvas(120, 160);
        drawPlayerSprite(playerCanvas.getGraphicsContext2D());
        drawEnemySprite(enemyCanvas.getGraphicsContext2D());

        playerHpLabel = new Label("HP: 30 / 30");
        playerHpLabel.setFont(new Font("Monospaced", 12));
        playerHpLabel.setStyle("-fx-text-fill: #7F77DD;");

        playerStatsLabel = new Label("ATK: 10  DEF: 3  LV: 1");
        playerStatsLabel.setFont(new Font("Monospaced", 11));
        playerStatsLabel.setStyle("-fx-text-fill: #555;");

        enemyHpLabel = new Label("HP: ?? / ??");
        enemyHpLabel.setFont(new Font("Monospaced", 12));
        enemyHpLabel.setStyle("-fx-text-fill: #D85A30;");

        enemyStatsLabel = new Label("ATK: ?  DEF: ?");
        enemyStatsLabel.setFont(new Font("Monospaced", 11));
        enemyStatsLabel.setStyle("-fx-text-fill: #555;");

        VBox playerBox = new VBox(8, playerCanvas, playerHpLabel, playerStatsLabel);
        playerBox.setAlignment(Pos.CENTER);

        VBox enemyBox = new VBox(8, enemyCanvas, enemyHpLabel, enemyStatsLabel);
        enemyBox.setAlignment(Pos.CENTER);

        Label vsLabel = new Label("VS");
        vsLabel.setFont(new Font("Monospaced", 24));
        vsLabel.setStyle("-fx-text-fill: #3a3a55;");

        HBox battle = new HBox(60, playerBox, vsLabel, enemyBox);
        battle.setAlignment(Pos.CENTER);
        battle.setPadding(new Insets(30));
        battle.setStyle("-fx-background-color: #11111c; -fx-border-color: #2a2a40; -fx-border-width: 0 0 0.5 0;");
        battle.setPrefHeight(260);
        return battle;
    }

    private VBox buildLogArea() {
        logLabel = new Label("Il nemico ti fissa. Cosa fai?");
        logLabel.setFont(new Font("Monospaced", 13));
        logLabel.setStyle("-fx-text-fill: #ccc;");
        logLabel.setWrapText(true);

        VBox log = new VBox(logLabel);
        log.setPadding(new Insets(16, 20, 16, 20));
        log.setPrefHeight(80);
        log.setStyle("-fx-background-color: #0f0f1a; -fx-border-color: #2a2a40; -fx-border-width: 0 0 0.5 0;");
        return log;
    }

    private HBox buildActionArea() {
        attackButton = buildButton("Attacca", "#534AB7", "#EEEDFE");
        specialButton = buildButton("Lama d'ombra", "#854F0B", "#EF9F27");
        healButton = buildButton("Usa pozione", "#0F6E56", "#5DCAA5");
        fleeButton = buildButton("Fuggi", "#993C1D", "#F0997B");

        attackButton.setOnAction(e -> handleAction(CombatActionType.ATTACK));
        specialButton.setOnAction(e -> handleAction(CombatActionType.SPECIAL));
        healButton.setOnAction(e -> handleAction(CombatActionType.HEAL));
        fleeButton.setOnAction(e -> handleAction(CombatActionType.FLEE));

        HBox actions = new HBox(12, attackButton, specialButton, healButton, fleeButton);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(16));
        actions.setStyle("-fx-background-color: #0d0d14;");
        return actions;
    }

    private Button buildButton(String text, String bg, String fg) {
        Button btn = new Button(text);
        btn.setPrefWidth(160);
        btn.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-text-fill: %s;
                -fx-font-family: Monospaced;
                -fx-font-size: 13px;
                -fx-background-radius: 4;
                -fx-padding: 10px;
                -fx-cursor: hand;
                """, bg, fg));
        return btn;
    }

    private void handleAction(CombatActionType type) {
        if (!controller.getCombatManager().isPlayerTurn()) return;
        setButtonsDisabled(true);

        Player player = controller.getCombatManager().getPlayer();
        Enemy enemy = controller.getCombatManager().getEnemy();
        int hpBefore = enemy.getStats().getCurrentHp();

        controller.handlePlayerAction(type);
        int damage = hpBefore - enemy.getStats().getCurrentHp();

        if (damage > 0) {
            logLabel.setText("Colpisci " + enemy.getName() + " per " + damage + " danni!");
        }

        refresh();

        CombatResult result = controller.getCombatManager().getLastResult();

        if (result == CombatResult.VICTORY || result == CombatResult.VICTORY_LEVELUP) {
            logLabel.setText(enemy.getName() + " sconfitto! +" + enemy.getXpReward() + " XP");
            turnLabel.setText("vittoria!");
            refresh();
            javafx.application.Platform.runLater(() -> showEndButtons(true));
            return;
        }

        if (result == CombatResult.FLED) {
            logLabel.setText("Sei fuggito dal combattimento.");
            javafx.application.Platform.runLater(() -> showEndButtons(false));
            return;
        }

        javafx.animation.PauseTransition pause =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.2));
        pause.setOnFinished(e -> {
            int playerHpBefore = player.getStats().getCurrentHp();
            controller.handleEnemyTurn();
            int enemyDamage = playerHpBefore - player.getStats().getCurrentHp();

            CombatResult afterResult = controller.getCombatManager().getLastResult();

            if (enemyDamage > 0) {
                String actionLabel = enemy.getAvailableActions()
                        .get((int)(Math.random() * enemy.getAvailableActions().size())).getLabel();
                logLabel.setText(enemy.getName() + " usa " + actionLabel
                        + " — subisci " + enemyDamage + " danni!");
            }

            if (afterResult == CombatResult.DEFEAT) {
                turnLabel.setText("sconfitta");
                refresh();
                javafx.application.Platform.runLater(() -> showEndButtons(false));
                return;
            }

            refresh();
            setButtonsDisabled(false);
        });
        pause.play();
    }

    private void setButtonsDisabled(boolean disabled) {
        attackButton.setDisable(disabled);
        specialButton.setDisable(disabled);
        healButton.setDisable(disabled);
        fleeButton.setDisable(disabled);
    }

    public void refresh() {
        Player player = controller.getCombatManager().getPlayer();
        Enemy enemy = controller.getCombatManager().getEnemy();
        if (player == null || enemy == null) return;

        Stats ps = player.getStats();
        Stats es = enemy.getStats();

        playerHpLabel.setText("HP: " + ps.getCurrentHp() + " / " + ps.getMaxHp());
        playerStatsLabel.setText("ATK: " + ps.getAttack() + "  DEF: " + ps.getDefense() + "  LV: " + ps.getLevel());
        enemyHpLabel.setText("HP: " + es.getCurrentHp() + " / " + es.getMaxHp());
        enemyStatsLabel.setText("ATK: " + es.getAttack() + "  DEF: " + es.getDefense());
        turnLabel.setText(controller.getCombatManager().isPlayerTurn() ? "turno del ladro" : "turno del nemico");
    }

    private void drawPlayerSprite(GraphicsContext gc) {
        gc.setFill(Color.web("#7F77DD"));
        gc.fillRoundRect(40, 20, 40, 40, 8, 8);
        gc.setFill(Color.web("#EEEDFE"));
        gc.fillOval(52, 32, 8, 8);
        gc.setFill(Color.web("#534AB7"));
        gc.fillRoundRect(30, 60, 60, 50, 6, 6);
        gc.setFill(Color.web("#3C3489"));
        gc.fillRoundRect(35, 110, 20, 36, 4, 4);
        gc.fillRoundRect(65, 110, 20, 36, 4, 4);
        gc.setFill(Color.web("#EF9F27"));
        gc.fillRoundRect(84, 68, 30, 8, 4, 4);
    }

    private void drawEnemySprite(GraphicsContext gc) {
        gc.setFill(Color.web("#993C1D"));
        gc.fillRoundRect(38, 10, 44, 40, 6, 6);
        gc.setFill(Color.web("#FAECE7"));
        gc.fillOval(44, 22, 10, 10);
        gc.fillOval(66, 22, 10, 10);
        gc.setFill(Color.web("#D85A30"));
        gc.fillRoundRect(24, 50, 72, 56, 6, 6);
        gc.setFill(Color.web("#993C1D"));
        gc.fillRoundRect(30, 106, 22, 36, 4, 4);
        gc.fillRoundRect(68, 106, 22, 36, 4, 4);
        gc.setFill(Color.web("#EF9F27"));
        gc.fillPolygon(new double[]{60, 48, 72}, new double[]{0, 18, 18}, 3);
    }

    private void showEndButtons(boolean victory) {
        attackButton.setDisable(true);
        specialButton.setDisable(true);
        healButton.setDisable(true);
        fleeButton.setDisable(true);

        if (victory) {
            showVictoryScreen();
        } else {
            showDefeatScreen();
        }
    }

    private void showVictoryScreen() {
        Player player = controller.getCombatManager().getPlayer();
        Enemy enemy = controller.getCombatManager().getEnemy();
        Stats ps = player.getStats();
        boolean leveledUp = controller.getCombatManager()
                .getLastResult() == CombatResult.VICTORY_LEVELUP;

        VBox overlay = new VBox(16);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(new Insets(40));
        overlay.setStyle("-fx-background-color: #0d1f0d;");

        Label title = new Label("Nemico sconfitto!");
        title.setFont(new Font("Monospaced", 22));
        title.setStyle("-fx-text-fill: #5DCAA5;");

        Label xpLabel = new Label("+" + enemy.getXpReward() + " XP guadagnati");
        xpLabel.setFont(new Font("Monospaced", 14));
        xpLabel.setStyle("-fx-text-fill: #EF9F27;");

        overlay.getChildren().addAll(title, xpLabel);

        if (leveledUp) {
            Label lvLabel = new Label("Livello aumentato!  LV. " + ps.getLevel());
            lvLabel.setFont(new Font("Monospaced", 18));
            lvLabel.setStyle("-fx-text-fill: #FAC775;");

            Label statsLabel = new Label(
                    "HP max:  " + ps.getMaxHp() + "\n" +
                            "ATK:     " + ps.getAttack() + "\n" +
                            "DEF:     " + ps.getDefense() + "\n" +
                            "XP:      " + ps.getCurrentXp() + " / " + ps.getXpToNextLevel()
            );
            statsLabel.setFont(new Font("Monospaced", 14));
            statsLabel.setStyle("-fx-text-fill: #ccc;");

            overlay.getChildren().addAll(lvLabel, statsLabel);
        } else {
            Label xpProgress = new Label(
                    "XP: " + ps.getCurrentXp() + " / " + ps.getXpToNextLevel());
            xpProgress.setFont(new Font("Monospaced", 12));
            xpProgress.setStyle("-fx-text-fill: #888;");
            overlay.getChildren().add(xpProgress);
        }

        Button continueBtn = new Button("Continua  ▶");
        continueBtn.setStyle("""
            -fx-background-color: #1D9E75;
            -fx-text-fill: #E1F5EE;
            -fx-font-family: Monospaced;
            -fx-font-size: 14px;
            -fx-background-radius: 4;
            -fx-padding: 10px 24px;
            -fx-cursor: hand;
            """);
        continueBtn.setOnAction(e -> {
            Runnable onV = controller.getOnVictory();
            if (onV != null) onV.run();
        });

        overlay.getChildren().add(continueBtn);

        if (stage != null) {
            stage.setScene(new Scene(overlay, 800, 600));
        } else {
            rootBox.getChildren().clear();
            rootBox.getChildren().add(overlay);
        }
    }

    private void showDefeatScreen() {
        Player player = controller.getCombatManager().getPlayer();
        Stats ps = player.getStats();

        VBox overlay = new VBox(16);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(new Insets(40));
        overlay.setStyle("-fx-background-color: #1f0d0d;");

        Label title = new Label("Sei caduto...");
        title.setFont(new Font("Monospaced", 22));
        title.setStyle("-fx-text-fill: #E24B4A;");

        Label msg = new Label("Il tempio ha inghiottito la tua anima.");
        msg.setFont(new Font("Monospaced", 13));
        msg.setStyle("-fx-text-fill: #888;");

        Label statsLabel = new Label(
                "Livello raggiunto:  " + ps.getLevel() + "\n" +
                        "XP accumulati:      " + ps.getCurrentXp()
        );
        statsLabel.setFont(new Font("Monospaced", 13));
        statsLabel.setStyle("-fx-text-fill: #ccc;");

        Button retryBtn = new Button("Riprova  ▶");
        retryBtn.setStyle("""
            -fx-background-color: #A32D2D;
            -fx-text-fill: #FCEBEB;
            -fx-font-family: Monospaced;
            -fx-font-size: 14px;
            -fx-background-radius: 4;
            -fx-padding: 10px 24px;
            -fx-cursor: hand;
            """);
        retryBtn.setOnAction(e -> {
            Runnable onD = controller.getOnDefeat();
            if (onD != null) onD.run();
        });

        overlay.getChildren().addAll(title, msg, statsLabel, retryBtn);

        if (stage != null) {
            stage.setScene(new Scene(overlay, 800, 600));
        } else {
            rootBox.getChildren().clear();
            rootBox.getChildren().add(overlay);
        }
    }


    public Scene getScene() { return scene; }
    public Label getLogLabel() { return logLabel; }
}