package it.unicam.cs.mpgc.rpg118708.view.menu;

import it.unicam.cs.mpgc.rpg118708.view.GameScene;
import it.unicam.cs.mpgc.rpg118708.model.Player;
import it.unicam.cs.mpgc.rpg118708.model.Stats;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
     * @param player   il personaggio del giocatore al termine della partita
     * @param onMenu   callback invocata al click su "Torna al menu"
     */
    public VictoryScene(Player player, Runnable onMenu) {
        this.scene = buildScene(player, onMenu);
    }

    private Scene buildScene(Player player, Runnable onMenu) {
        Stats stats = player.getStats();

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: #0a1a0a;");

        Label title = new Label("Hai completato il tempio!");
        title.setFont(new Font("Monospaced", 30));
        title.setStyle("-fx-text-fill: #EF9F27;");

        Label subtitle = new Label("Il ladro " + player.getName() + " è fuggito con il tesoro.");
        subtitle.setFont(new Font("Monospaced", 16));
        subtitle.setStyle("-fx-text-fill: #888;");

        Label statsLabel = new Label(
                "Livello finale:  " + stats.getLevel() + "\n" +
                "HP:              " + stats.getCurrentHp() + " / " + stats.getMaxHp() + "\n" +
                "ATK:             " + stats.getAttack() + "\n" +
                "DEF:             " + stats.getDefense()
        );
        statsLabel.setFont(new Font("Monospaced", 16));
        statsLabel.setStyle("-fx-text-fill: #ccc;");

        Button menuBtn = new Button("Torna al menu  ▶");
        menuBtn.setStyle("""
                -fx-background-color: #854F0B;
                -fx-text-fill: #FAEEDA;
                -fx-font-family: Monospaced;
                -fx-font-size: 16px;
                -fx-background-radius: 4;
                -fx-padding: 14px 32px;
                -fx-cursor: hand;
                """);
        menuBtn.setOnAction(e -> { if (onMenu != null) onMenu.run(); });

        root.getChildren().addAll(title, subtitle, statsLabel, menuBtn);

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        return new Scene(root, screen.getWidth(), screen.getHeight());
    }

    /** @return la scena JavaFX pronta per essere impostata sullo stage */
    public Scene getScene() { return scene; }
}
