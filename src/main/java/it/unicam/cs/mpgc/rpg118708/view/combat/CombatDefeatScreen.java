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

        Canvas playerCanvas = new Canvas(120, 160);
        CombatSpriteRenderer.drawPlayer(playerCanvas.getGraphicsContext2D(), ps.getLevel());
        playerCanvas.setOpacity(0.30);

        Label defeatedLabel = new Label("✝");
        defeatedLabel.getStyleClass().add("label-defeated-icon");

        VBox spriteBox = new VBox(8, playerCanvas, defeatedLabel);
        spriteBox.setAlignment(Pos.CENTER);
        spriteBox.setPadding(new Insets(16, 20, 16, 20));
        spriteBox.getStyleClass().add("sprite-box-defeat");

        Label title = new Label("Sei caduto...");
        title.getStyleClass().add("label-title-defeat");

        Label msg = new Label("Il tempio ha inghiottito la tua anima.");
        msg.getStyleClass().add("label-defeat-msg");

        Label statsLabel = new Label(
                "Livello raggiunto:  " + ps.getLevel()     + "\n" +
                "XP accumulati:      " + ps.getCurrentXp());
        statsLabel.getStyleClass().add("label-body-sm");

        Button retryBtn = new Button("Ricomincia dall'inizio");
        retryBtn.setPrefWidth(260);
        retryBtn.getStyleClass().add("btn-defeat");
        retryBtn.setOnAction(e -> { if (onRetry != null) onRetry.run(); });

        Button loadBtn = new Button("Carica ultimo salvataggio");
        loadBtn.setPrefWidth(260);
        loadBtn.getStyleClass().add("btn-primary-sm");
        loadBtn.setOnAction(e -> { if (onLoad != null) onLoad.run(); });

        VBox infoBox = new VBox(16, title, msg, statsLabel, retryBtn, loadBtn);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        HBox mainRow = new HBox(40, spriteBox, infoBox);
        mainRow.setAlignment(Pos.CENTER);
        mainRow.setPadding(new Insets(48));
        mainRow.setMaxWidth(680);
        mainRow.getStyleClass().add("game-panel-defeat");

        StackPane root = new StackPane(bg, mainRow);
        Scene scene = new Scene(root, W, H);
        scene.getStylesheets().add(getClass().getResource("/game.css").toExternalForm());
        return scene;
    }

    /** @return la scena JavaFX pronta per essere impostata sullo stage */
    public Scene getScene() { return scene; }
}
