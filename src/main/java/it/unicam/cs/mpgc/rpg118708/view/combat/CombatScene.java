package it.unicam.cs.mpgc.rpg118708.view.combat;

import it.unicam.cs.mpgc.rpg118708.view.GameScene;
import it.unicam.cs.mpgc.rpg118708.controller.CombatController;
import it.unicam.cs.mpgc.rpg118708.engine.CombatManager;
import it.unicam.cs.mpgc.rpg118708.engine.CombatResult;
import it.unicam.cs.mpgc.rpg118708.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Scena del combattimento a turni.
 *
 * <p>Costruisce l'interfaccia grafica del combattimento (pulsanti azioni,
 * barre HP, log testuale, inventario) e delega la logica di gioco al
 * {@link it.unicam.cs.mpgc.rpg118708.controller.CombatController}.
 * Gestisce anche le schermate di game-over e vittoria mostrate al termine
 * dello scontro.</p>
 */
public class CombatScene implements GameScene {

    private Scene scene;
    private Label logLabel;
    private Label playerHpLabel;
    private Label enemyHpLabel;
    private Label playerStatsLabel;
    private Label enemyStatsLabel;
    private Label turnLabel;
    private Label inventoryLabel;
    private Button attackButton;
    private Button specialButton;
    private Button healButton;
    private Button fleeButton;
    private Button equipButton;
    private Canvas playerCanvas;
    private Canvas enemyCanvas;
    private Stage stage;
    private final CombatController controller;

    /**
     * Costruisce la scena del combattimento.
     *
     * @param controller il controller che gestisce la logica del combattimento
     */
    public CombatScene(CombatController controller) {
        this.controller = controller;
        buildScene();
    }

    /**
     * Imposta lo stage corrente, necessario per la sostituzione della scena
     * al termine del combattimento.
     *
     * @param stage lo stage JavaFX principale
     */
    public void setStage(Stage stage) { this.stage = stage; }

    private void buildScene() {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #0d0d14;");

        HBox hudRow = buildHudRow();
        HBox battleArea = buildBattleArea();
        HBox inventoryArea = buildInventoryArea();
        VBox logArea = buildLogArea();
        HBox actionArea = buildActionArea();

        root.getChildren().addAll(hudRow, battleArea, inventoryArea, logArea, actionArea);
        javafx.geometry.Rectangle2D screen =
                javafx.stage.Screen.getPrimary().getVisualBounds();
        scene = new Scene(root, screen.getWidth(), screen.getHeight());
    }

    private HBox buildHudRow() {
        turnLabel = new Label("turno del ladro");
        turnLabel.setFont(new Font("Monospaced", 14));
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
        int playerLevel = controller.getCombatManager().getPlayer().getStats().getLevel();
        CombatSpriteRenderer.drawPlayer(playerCanvas.getGraphicsContext2D(), playerLevel);
        CombatSpriteRenderer.drawEnemy(enemyCanvas.getGraphicsContext2D(),
                controller.getCombatManager().getEnemy());

        playerHpLabel = new Label("HP: 30 / 30");
        playerHpLabel.setFont(new Font("Monospaced", 14));
        playerHpLabel.setStyle("-fx-text-fill: #7F77DD;");

        playerStatsLabel = new Label("ATK: 10  DEF: 3  LV: 1");
        playerStatsLabel.setFont(new Font("Monospaced", 13));
        playerStatsLabel.setStyle("-fx-text-fill: #555;");

        enemyHpLabel = new Label("HP: ?? / ??");
        enemyHpLabel.setFont(new Font("Monospaced", 14));
        enemyHpLabel.setStyle("-fx-text-fill: #D85A30;");

        enemyStatsLabel = new Label("ATK: ?  DEF: ?");
        enemyStatsLabel.setFont(new Font("Monospaced", 13));
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

    private HBox buildInventoryArea() {
        Label invTitle = new Label("Inventario:");
        invTitle.setFont(new Font("Monospaced", 14));
        invTitle.setStyle("-fx-text-fill: #534AB7;");

        inventoryLabel = new Label("vuoto");
        inventoryLabel.setFont(new Font("Monospaced", 14));
        inventoryLabel.setStyle("-fx-text-fill: #888;");

        Label equippedTitle = new Label("Equipaggiato:");
        equippedTitle.setFont(new Font("Monospaced", 14));
        equippedTitle.setStyle("-fx-text-fill: #EF9F27; -fx-padding: 0 0 0 24;");

        Label equippedLabel = new Label("nessuno");
        equippedLabel.setFont(new Font("Monospaced", 14));
        equippedLabel.setStyle("-fx-text-fill: #888;");
        equippedLabel.setId("equipped-label");

        HBox row = new HBox(12, invTitle, inventoryLabel, equippedTitle, equippedLabel);
        row.setPadding(new Insets(8, 20, 8, 20));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #0a0a12; -fx-border-color: #2a2a40; -fx-border-width: 0 0 0.5 0;");
        return row;
    }

    private VBox buildLogArea() {
        logLabel = new Label("Il nemico ti fissa. Cosa fai?");
        logLabel.setFont(new Font("Monospaced", 15));
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
        equipButton = buildButton("Equipaggia", "#1e1e30", "#AFA9EC");

        attackButton.setOnAction(e -> handleAction(CombatActionType.ATTACK));
        specialButton.setOnAction(e -> handleAction(CombatActionType.SPECIAL));
        healButton.setOnAction(e -> handleAction(CombatActionType.HEAL));
        fleeButton.setOnAction(e -> handleAction(CombatActionType.FLEE));
        equipButton.setOnAction(e -> handleEquip());

        HBox actions = new HBox(12, attackButton, specialButton, healButton, fleeButton, equipButton);
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
                -fx-font-size: 15px;
                -fx-background-radius: 4;
                -fx-padding: 10px;
                -fx-cursor: hand;
                """, bg, fg));
        return btn;
    }

    private void handleAction(CombatActionType type) {
        if (!controller.getCombatManager().isPlayerTurn()) return;
        setButtonsDisabled(true);

        if (type == CombatActionType.SPECIAL) {
            if (controller.getCombatManager().getSpecialUsesLeft() <= 0) {
                logLabel.setText("Hai esaurito le mosse speciali per questo combattimento!");
                setButtonsDisabled(false);
                return;
            }
            int usesLeft = controller.getCombatManager().getSpecialUsesLeft() - 1;
            int usesMax = controller.getCombatManager().getMaxSpecialUses();
            logLabel.setText("Usi mossa speciale! (" + usesLeft + "/" + usesMax + " rimasti)");
        }

        Player player = controller.getCombatManager().getPlayer();
        Enemy enemy = controller.getCombatManager().getEnemy();
        int hpBefore = enemy.getStats().getCurrentHp();
        int playerHpBefore = player.getStats().getCurrentHp();

        controller.handlePlayerAction(type);

        int damage = hpBefore - enemy.getStats().getCurrentHp();
        int healed = player.getStats().getCurrentHp() - playerHpBefore;

        if (type == CombatActionType.HEAL) {
            if (healed > 0) {
                logLabel.setText("Usi una pozione — recuperi " + healed + " HP! ("
                        + player.getStats().getCurrentHp() + "/" + player.getStats().getMaxHp() + ")");
            } else {
                logLabel.setText("Nessuna pozione disponibile!");
            }
        } else if (type == CombatActionType.SPECIAL && damage > 0) {
            logLabel.setText("Lama d'ombra colpisce " + enemy.getName() + " per " + damage + " danni!");
        } else if (damage > 0) {
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
            return;
        }

        javafx.animation.PauseTransition pause =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.2));
        pause.setOnFinished(e -> {
            int playerHpBeforeEnemy = player.getStats().getCurrentHp();
            int enemyHpBefore = enemy.getStats().getCurrentHp();
            controller.handleEnemyTurn();
            int enemyDamage = playerHpBeforeEnemy - player.getStats().getCurrentHp();
            int enemyHealed  = enemy.getStats().getCurrentHp() - enemyHpBefore;

            CombatResult afterResult = controller.getCombatManager().getLastResult();
            String actionLabel = controller.getCombatManager().getLastEnemyAction() != null
                    ? controller.getCombatManager().getLastEnemyAction().getLabel()
                    : "Attacca";

            if (enemyHealed > 0) {
                int usesLeft = controller.getCombatManager().getEnemyHealUsesLeft();
                String usesNote = usesLeft > 0
                        ? " (" + usesLeft + "/" + CombatManager.MAX_ENEMY_HEAL_USES + " cure rimaste)"
                        : " (cure esaurite)";
                logLabel.setText(enemy.getName() + " usa " + actionLabel
                        + " — recupera " + enemyHealed + " HP!" + usesNote);
            } else if (enemyDamage > 0) {
                logLabel.setText(enemy.getName() + " usa " + actionLabel
                        + " — subisci " + enemyDamage + " danni! (HP: "
                        + player.getStats().getCurrentHp() + "/" + player.getStats().getMaxHp() + ")");
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

    private void handleEquip() {
        Player player = controller.getCombatManager().getPlayer();

        Item amulet = player.getInventory().getItems().stream()
                .filter(i -> i instanceof Amulet)
                .findFirst().orElse(null);

        Item usable = player.getInventory().getItems().stream()
                .filter(i -> i instanceof Scroll || i instanceof Talisman)
                .findFirst().orElse(null);

        if (amulet == null && usable == null) {
            logLabel.setText("Nessun oggetto utilizzabile nell'inventario.");
            return;
        }

        if (usable != null) {
            String msg = controller.getCombatManager().useItem(usable);
            logLabel.setText(msg);
            refresh();
            return;
        }

        if (player.hasEquipped() && player.getEquippedItem().getId().equals(amulet.getId())) {
            logLabel.setText("Hai già equipaggiato " + amulet.getName() + "!");
            return;
        }

        controller.getCombatManager().equipItem((Amulet) amulet);
        logLabel.setText("Hai equipaggiato " + amulet.getName() + " — DEF +4, HP max +10!");
        refresh();
    }

    private void setButtonsDisabled(boolean disabled) {
        attackButton.setDisable(disabled);
        specialButton.setDisable(disabled);
        healButton.setDisable(disabled);
        fleeButton.setDisable(disabled);
    }

    /**
     * Aggiorna l'intera UI della scena di combattimento con lo stato corrente
     * di giocatore e nemico (etichette HP/ATK, barre HP, stato pulsanti).
     */
    public void refresh() {
        Player player = controller.getCombatManager().getPlayer();
        Enemy enemy = controller.getCombatManager().getEnemy();
        if (player == null || enemy == null) return;

        refreshLabels(player, enemy);
        refreshSpecialButton();
        refreshHealButton(player);
        refreshEquipButton(player);
        refreshInventoryLabel(player);
    }

    private void refreshLabels(Player player, Enemy enemy) {
        Stats ps = player.getStats();
        Stats es = enemy.getStats();
        playerHpLabel.setText("HP: " + ps.getCurrentHp() + " / " + ps.getMaxHp());
        playerStatsLabel.setText("ATK: " + ps.getAttack() + "  DEF: " + ps.getDefense() + "  LV: " + ps.getLevel());
        enemyHpLabel.setText("HP: " + es.getCurrentHp() + " / " + es.getMaxHp());
        enemyStatsLabel.setText("ATK: " + es.getAttack() + "  DEF: " + es.getDefense());
        turnLabel.setText(controller.getCombatManager().isPlayerTurn() ? "turno del ladro" : "turno del nemico");
    }

    private void refreshSpecialButton() {
        boolean available = controller.getCombatManager().getSpecialUsesLeft() > 0;
        applyButtonStyle(specialButton, available, "#854F0B", "#EF9F27");
    }

    private void refreshHealButton(Player player) {
        long healingItems = player.getInventory().getItems().stream()
                .filter(Item::isHealing).count();
        healButton.setText(healingItems > 0 ? "Usa pozione (" + healingItems + ")" : "Usa pozione");
        applyButtonStyle(healButton, healingItems > 0, "#0F6E56", "#5DCAA5");
    }

    private void refreshEquipButton(Player player) {
        boolean hasUsable = player.getInventory().getItems().stream()
                .anyMatch(i -> i instanceof Amulet || i instanceof Scroll || i instanceof Talisman);
        applyButtonStyle(equipButton, hasUsable, "#1e1e30", "#AFA9EC");
    }

    private void refreshInventoryLabel(Player player) {
        String text = player.getInventory().getItems().stream()
                .map(Item::getName)
                .reduce((a, b) -> a + "  " + b)
                .orElse("vuoto");
        inventoryLabel.setText(text);

        Label equippedLabel = (Label) scene.lookup("#equipped-label");
        if (equippedLabel != null) {
            equippedLabel.setText(player.hasEquipped()
                    ? player.getEquippedItem().getName() : "nessuno");
        }
    }

    /**
     * Applica lo stile a un pulsante di azione in base allo stato abilitato/disabilitato.
     *
     * @param btn       il pulsante da aggiornare
     * @param enabled   {@code true} per abilitare il pulsante con i colori attivi
     * @param activeBg  colore di sfondo quando abilitato
     * @param activeFg  colore del testo quando abilitato
     */
    private void applyButtonStyle(Button btn, boolean enabled, String activeBg, String activeFg) {
        btn.setDisable(!enabled);
        btn.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-text-fill: %s;
                -fx-font-family: Monospaced;
                -fx-font-size: 15px;
                -fx-background-radius: 4;
                -fx-padding: 10px;
                -fx-cursor: hand;
                """, enabled ? activeBg : "#2a2a40", enabled ? activeFg : "#555"));
    }

    private void showEndButtons(boolean victory) {
        attackButton.setDisable(true);
        specialButton.setDisable(true);
        healButton.setDisable(true);
        fleeButton.setDisable(true);
        equipButton.setDisable(true);

        if (victory) showVictoryScreen();
        else         showDefeatScreen();
    }

    private void showVictoryScreen() {
        Player player  = controller.getCombatManager().getPlayer();
        Enemy  enemy   = controller.getCombatManager().getEnemy();
        boolean lvUp   = controller.getCombatManager().getLastResult() == CombatResult.VICTORY_LEVELUP;
        stage.setScene(new CombatVictoryScreen(player, enemy, lvUp,
                controller.getOnVictory()).getScene());
    }

    private void showDefeatScreen() {
        Player player = controller.getCombatManager().getPlayer();
        stage.setScene(new CombatDefeatScreen(player,
                controller.getOnDefeat(), controller.getOnLoad()).getScene());
    }

    /**
     * Restituisce la scena JavaFX pronta per essere impostata sullo stage.
     *
     * @return la scena di combattimento
     */
    public Scene getScene() { return scene; }

    /**
     * Restituisce l'etichetta del log di combattimento.
     * Usata dal controller per aggiungere messaggi descrittivi delle azioni.
     *
     * @return il label del log
     */
    public Label getLogLabel() { return logLabel; }
}