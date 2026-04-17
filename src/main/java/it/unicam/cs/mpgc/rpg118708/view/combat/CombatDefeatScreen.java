package it.unicam.cs.mpgc.rpg118708.view.combat;

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
 * Schermata mostrata al termine di un combattimento perso.
 *
 * <p>Offre al giocatore due opzioni: ricominciare dall'inizio o caricare
 * un salvataggio. Le azioni sono gestite tramite callback esterne.
 * Non contiene logica di gioco.</p>
 */
public class CombatDefeatScreen implements GameScene {

    private final Scene scene;

    /**
     * Costruisce la schermata di sconfitta in combattimento.
     *
     * @param player   il giocatore al momento della sconfitta
     * @param onRetry  callback invocata al click su "Ricomincia dall'inizio"
     * @param onLoad   callback invocata al click su "Carica ultimo salvataggio"
     */
    public CombatDefeatScreen(Player player, Runnable onRetry, Runnable onLoad) {
        this.scene = buildScene(player, onRetry, onLoad);
    }

    private Scene buildScene(Player player, Runnable onRetry, Runnable onLoad) {
        Stats ps = player.getStats();

        VBox root = new VBox(16);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1f0d0d;");

        Label title = new Label("Sei caduto...");
        title.setFont(new Font("Monospaced", 22));
        title.setStyle("-fx-text-fill: #E24B4A;");

        Label msg = new Label("Il tempio ha inghiottito la tua anima.");
        msg.setFont(new Font("Monospaced", 13));
        msg.setStyle("-fx-text-fill: #888;");

        Label statsLabel = new Label(
                "Livello raggiunto:  " + ps.getLevel()     + "\n" +
                "XP accumulati:      " + ps.getCurrentXp());
        statsLabel.setFont(new Font("Monospaced", 13));
        statsLabel.setStyle("-fx-text-fill: #ccc;");

        Button retryBtn = new Button("Ricomincia dall'inizio");
        retryBtn.setStyle("""
                -fx-background-color: #A32D2D;
                -fx-text-fill: #FCEBEB;
                -fx-font-family: Monospaced;
                -fx-font-size: 14px;
                -fx-background-radius: 4;
                -fx-padding: 10px 24px;
                -fx-cursor: hand;
                """);
        retryBtn.setOnAction(e -> { if (onRetry != null) onRetry.run(); });

        Button loadBtn = new Button("Carica ultimo salvataggio");
        loadBtn.setStyle("""
                -fx-background-color: #534AB7;
                -fx-text-fill: #EEEDFE;
                -fx-font-family: Monospaced;
                -fx-font-size: 14px;
                -fx-background-radius: 4;
                -fx-padding: 10px 24px;
                -fx-cursor: hand;
                """);
        loadBtn.setOnAction(e -> { if (onLoad != null) onLoad.run(); });

        root.getChildren().addAll(title, msg, statsLabel, retryBtn, loadBtn);

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        return new Scene(root, screen.getWidth(), screen.getHeight());
    }

    /** @return la scena JavaFX pronta per essere impostata sullo stage */
    public Scene getScene() { return scene; }
}
