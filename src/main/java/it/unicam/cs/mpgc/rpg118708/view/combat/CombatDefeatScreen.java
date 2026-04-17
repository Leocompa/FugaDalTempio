package it.unicam.cs.mpgc.rpg118708.view.combat;

import it.unicam.cs.mpgc.rpg118708.view.GameScene;
import it.unicam.cs.mpgc.rpg118708.view.SceneBackground;
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
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double W = screen.getWidth();
        double H = screen.getHeight();

        Canvas bg = SceneBackground.createCanvas(W, H);

        Stats ps = player.getStats();

        // ---- sprite del personaggio (dissolvenza: sconfitto) ----
        Canvas playerCanvas = new Canvas(120, 160);
        CombatSpriteRenderer.drawPlayer(playerCanvas.getGraphicsContext2D(), ps.getLevel());
        playerCanvas.setOpacity(0.30);

        Label defeatedLabel = new Label("✝");
        defeatedLabel.setFont(new Font("Monospaced", 22));
        defeatedLabel.setStyle("-fx-text-fill: #E24B4A; -fx-opacity: 0.7;");

        VBox spriteBox = new VBox(8, playerCanvas, defeatedLabel);
        spriteBox.setAlignment(Pos.CENTER);
        spriteBox.setPadding(new Insets(16, 20, 16, 20));
        spriteBox.setStyle("""
                -fx-background-color: rgba(20, 8, 8, 0.75);
                -fx-border-color: #5a2222;
                -fx-border-width: 1;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                """);

        // ---- riquadro info ----
        Label title = new Label("Sei caduto...");
        title.setFont(new Font("Monospaced", 26));
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
        retryBtn.setPrefWidth(260);
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
        loadBtn.setPrefWidth(260);
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

        VBox infoBox = new VBox(16, title, msg, statsLabel, retryBtn, loadBtn);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        // ---- riga principale: sprite + info ----
        HBox mainRow = new HBox(40, spriteBox, infoBox);
        mainRow.setAlignment(Pos.CENTER);
        mainRow.setPadding(new Insets(48));
        mainRow.setMaxWidth(680);
        mainRow.setStyle("""
                -fx-background-color: rgba(16, 4, 4, 0.92);
                -fx-border-color: #5a2222;
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
