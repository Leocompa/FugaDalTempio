package it.unicam.cs.mpgc.rpg118708.view.combat;

import it.unicam.cs.mpgc.rpg118708.view.GameScene;
import it.unicam.cs.mpgc.rpg118708.view.SceneBackground;
import it.unicam.cs.mpgc.rpg118708.model.Enemy;
import it.unicam.cs.mpgc.rpg118708.model.Player;
import it.unicam.cs.mpgc.rpg118708.model.Stats;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

/**
 * Schermata mostrata al termine di un combattimento vinto.
 *
 * <p>Riceve i dati del giocatore, del nemico sconfitto e una callback
 * per continuare l'esplorazione. Non contiene logica di gioco.</p>
 */
public class CombatVictoryScreen implements GameScene {

    private final Scene scene;

    /**
     * Costruisce la schermata di vittoria in combattimento.
     *
     * @param player     il giocatore al termine del combattimento
     * @param enemy      il nemico sconfitto
     * @param leveledUp  {@code true} se il giocatore ha guadagnato un livello
     * @param onContinue callback invocata al click su "Continua"
     */
    public CombatVictoryScreen(Player player, Enemy enemy, boolean leveledUp, Runnable onContinue) {
        this.scene = buildScene(player, enemy, leveledUp, onContinue);
    }

    private Scene buildScene(Player player, Enemy enemy, boolean leveledUp, Runnable onContinue) {
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double W = screen.getWidth();
        double H = screen.getHeight();

        Canvas bg = SceneBackground.createCanvas(W, H);

        Stats ps = player.getStats();

        Label title = new Label("Nemico sconfitto!");
        title.getStyleClass().add("label-title-victory");

        HBox contentRow = buildContentRow(player, enemy, leveledUp, ps);

        Button continueBtn = new Button("Continua  ▶");
        continueBtn.getStyleClass().add("btn-victory");
        continueBtn.setOnAction(e -> { if (onContinue != null) onContinue.run(); });

        VBox panel = new VBox(20, title, contentRow, continueBtn);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(48));
        panel.setMaxWidth(640);
        panel.getStyleClass().add("game-panel-victory");

        StackPane root = new StackPane(bg, panel);
        Scene scene = new Scene(root, W, H);
        scene.getStylesheets().add(getClass().getResource("/game.css").toExternalForm());
        return scene;
    }

    private HBox buildContentRow(Player player, Enemy enemy, boolean leveledUp, Stats ps) {
        Canvas playerCanvas = new Canvas(120, 160);
        CombatSpriteRenderer.drawPlayer(playerCanvas.getGraphicsContext2D(), ps.getLevel());

        Label levelBadge = new Label("LV. " + ps.getLevel());
        levelBadge.getStyleClass().add(leveledUp ? "label-levelup-badge" : "label-level-badge");

        VBox spriteBox = new VBox(8, playerCanvas, levelBadge);
        spriteBox.setAlignment(Pos.CENTER);
        spriteBox.setPadding(new Insets(16, 20, 16, 20));
        spriteBox.getStyleClass().add("sprite-box-combat-victory");

        VBox statsBox = buildStatsBox(enemy, leveledUp, ps);

        HBox row = new HBox(32, spriteBox, statsBox);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    private VBox buildStatsBox(Enemy enemy, boolean leveledUp, Stats ps) {
        Label xpLabel = new Label("+" + enemy.getXpReward() + " XP guadagnati");
        xpLabel.getStyleClass().add("label-gold-sm");

        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().add(xpLabel);

        if (leveledUp) {
            Label lvLabel = new Label("⬆  Livello aumentato!");
            lvLabel.getStyleClass().add("label-levelup");

            Label statsLabel = new Label(
                    "HP max:  " + ps.getMaxHp()  + "\n" +
                    "ATK:     " + ps.getAttack()  + "\n" +
                    "DEF:     " + ps.getDefense() + "\n" +
                    "XP:      " + ps.getCurrentXp() + " / " + ps.getXpToNextLevel());
            statsLabel.getStyleClass().add("label-body-md");
            box.getChildren().addAll(lvLabel, statsLabel);
        } else {
            Label xpProgress = new Label(
                    "XP: " + ps.getCurrentXp() + " / " + ps.getXpToNextLevel());
            xpProgress.getStyleClass().add("label-xp-progress");
            box.getChildren().add(xpProgress);
        }
        return box;
    }

    /** @return la scena JavaFX pronta per essere impostata sullo stage */
    public Scene getScene() { return scene; }
}
