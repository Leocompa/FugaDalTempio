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
import javafx.scene.text.Font;
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

        // ---- sprite del personaggio ----
        Canvas playerCanvas = new Canvas(120, 160);
        CombatSpriteRenderer.drawPlayer(playerCanvas.getGraphicsContext2D(), stats.getLevel());

        Label levelBadge = new Label("LV. " + stats.getLevel());
        levelBadge.setFont(new Font("Monospaced", 16));
        levelBadge.setStyle("-fx-text-fill: #EF9F27;");

        VBox spriteBox = new VBox(10, playerCanvas, levelBadge);
        spriteBox.setAlignment(Pos.CENTER);
        spriteBox.setPadding(new Insets(20));
        spriteBox.setStyle("""
                -fx-background-color: rgba(8, 20, 8, 0.80);
                -fx-border-color: #1D9E75;
                -fx-border-width: 1.5;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                """);

        // ---- riquadro statistiche ----
        Label title = new Label("Fuga riuscita!");
        title.setFont(new Font("Monospaced", 30));
        title.setStyle("-fx-text-fill: #EF9F27;");

        Label subtitle = new Label("Il ladro " + player.getName() + " è fuggito con il tesoro.");
        subtitle.setFont(new Font("Monospaced", 15));
        subtitle.setStyle("-fx-text-fill: #888780;");

        Label divider = new Label("──────────────────────");
        divider.setStyle("-fx-text-fill: #2a2a42;");

        Label statsLabel = new Label(
                "Livello finale:  " + stats.getLevel()          + "\n" +
                "HP rimasti:      " + stats.getCurrentHp()
                        + " / " + stats.getMaxHp()              + "\n" +
                "ATK:             " + stats.getAttack()          + "\n" +
                "DEF:             " + stats.getDefense()
        );
        statsLabel.setFont(new Font("Monospaced", 15));
        statsLabel.setStyle("-fx-text-fill: #ccc;");

        Button menuBtn = new Button("Torna al menu  ▶");
        menuBtn.setStyle("""
                -fx-background-color: #854F0B;
                -fx-text-fill: #FAEEDA;
                -fx-font-family: Monospaced;
                -fx-font-size: 15px;
                -fx-background-radius: 4;
                -fx-padding: 12px 28px;
                -fx-cursor: hand;
                """);
        menuBtn.setOnAction(e -> { if (onMenu != null) onMenu.run(); });

        VBox infoBox = new VBox(16, title, subtitle, divider, statsLabel, menuBtn);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        // ---- riga principale: sprite + info ----
        HBox mainRow = new HBox(40, spriteBox, infoBox);
        mainRow.setAlignment(Pos.CENTER);
        mainRow.setPadding(new Insets(48));
        mainRow.setMaxWidth(720);
        mainRow.setStyle("""
                -fx-background-color: rgba(8, 8, 16, 0.90);
                -fx-border-color: #2a2a42;
                -fx-border-width: 2;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                """);

        StackPane root = new StackPane(bg, mainRow);
        return new Scene(root, W, H);
    }

    /** @return la scena JavaFX pronta per essere impostata sullo stage */
    public Scene getScene() { return scene; }
}
