package it.unicam.cs.mpgc.rpg118708.view.menu;

import it.unicam.cs.mpgc.rpg118708.view.GameScene;
import it.unicam.cs.mpgc.rpg118708.view.SceneBackground;
import it.unicam.cs.mpgc.rpg118708.view.combat.CombatSpriteRenderer;
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
 * Schermata di vittoria mostrata al termine del gioco.
 *
 * <p>Riceve i dati del giocatore e una callback per tornare al menu principale.
 * Non contiene logica di gioco: è responsabile esclusivamente del layout e
 * degli stili della schermata di completamento.</p>
 */
public class VictoryScene implements GameScene {

    private final Scene scene;

    /**
     * Costruisce la schermata di vittoria.
     *
     * @param player il personaggio del giocatore al termine della partita
     * @param onMenu callback invocata al click su "Torna al menu"
     */
    public VictoryScene(Player player, Runnable onMenu) {
        this.scene = buildScene(player, onMenu);
    }

    private Scene buildScene(Player player, Runnable onMenu) {
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double W = screen.getWidth();
        double H = screen.getHeight();

        Canvas bg = SceneBackground.createCanvas(W, H);

        Stats stats = player.getStats();

        Canvas playerCanvas = new Canvas(120, 160);
        CombatSpriteRenderer.drawPlayer(playerCanvas.getGraphicsContext2D(), stats.getLevel());

        Label levelBadge = new Label("LV. " + stats.getLevel());
        levelBadge.getStyleClass().add("label-gold");

        VBox spriteBox = new VBox(10, playerCanvas, levelBadge);
        spriteBox.setAlignment(Pos.CENTER);
        spriteBox.setPadding(new Insets(20));
        spriteBox.getStyleClass().add("sprite-box-victory");

        Label title = new Label("Fuga riuscita!");
        title.getStyleClass().add("label-title-md");

        Label subtitle = new Label("Il ladro " + player.getName() + " è fuggito con il tesoro.");
        subtitle.getStyleClass().add("label-subtitle");

        Label divider = new Label("──────────────────────");
        divider.getStyleClass().add("label-divider");

        Label statsLabel = new Label(
                "Livello finale:  " + stats.getLevel()          + "\n" +
                "HP rimasti:      " + stats.getCurrentHp()
                        + " / " + stats.getMaxHp()              + "\n" +
                "ATK:             " + stats.getAttack()          + "\n" +
                "DEF:             " + stats.getDefense()
        );
        statsLabel.getStyleClass().add("label-body");

        Button menuBtn = new Button("Torna al menu  ▶");
        menuBtn.getStyleClass().add("btn-gold");
        menuBtn.setOnAction(e -> { if (onMenu != null) onMenu.run(); });

        VBox infoBox = new VBox(16, title, subtitle, divider, statsLabel, menuBtn);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        HBox mainRow = new HBox(40, spriteBox, infoBox);
        mainRow.setAlignment(Pos.CENTER);
        mainRow.setPadding(new Insets(48));
        mainRow.setMaxWidth(720);
        mainRow.getStyleClass().add("game-panel");

        StackPane root = new StackPane(bg, mainRow);
        Scene scene = new Scene(root, W, H);
        scene.getStylesheets().add(getClass().getResource("/game.css").toExternalForm());
        return scene;
    }

    /** @return la scena JavaFX pronta per essere impostata sullo stage */
    public Scene getScene() { return scene; }
}
