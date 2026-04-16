package it.unicam.cs.mpgc.rpg118708.view.combat;

import it.unicam.cs.mpgc.rpg118708.model.Enemy;
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
 * Schermata mostrata al termine di un combattimento vinto.
 *
 * <p>Riceve i dati del giocatore, del nemico sconfitto e una callback
 * per continuare l'esplorazione. Non contiene logica di gioco.</p>
 */
public class CombatVictoryScreen {

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
        Stats ps = player.getStats();

        VBox root = new VBox(16);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #0d1f0d;");

        Label title = new Label("Nemico sconfitto!");
        title.setFont(new Font("Monospaced", 22));
        title.setStyle("-fx-text-fill: #5DCAA5;");

        Label xpLabel = new Label("+" + enemy.getXpReward() + " XP guadagnati");
        xpLabel.setFont(new Font("Monospaced", 14));
        xpLabel.setStyle("-fx-text-fill: #EF9F27;");

        root.getChildren().addAll(title, xpLabel);

        if (leveledUp) {
            Label lvLabel = new Label("Livello aumentato!  LV. " + ps.getLevel());
            lvLabel.setFont(new Font("Monospaced", 18));
            lvLabel.setStyle("-fx-text-fill: #FAC775;");

            Label statsLabel = new Label(
                    "HP max:  " + ps.getMaxHp()         + "\n" +
                    "ATK:     " + ps.getAttack()         + "\n" +
                    "DEF:     " + ps.getDefense()        + "\n" +
                    "XP:      " + ps.getCurrentXp()
                             + " / " + ps.getXpToNextLevel());
            statsLabel.setFont(new Font("Monospaced", 14));
            statsLabel.setStyle("-fx-text-fill: #ccc;");
            root.getChildren().addAll(lvLabel, statsLabel);
        } else {
            Label xpProgress = new Label(
                    "XP: " + ps.getCurrentXp() + " / " + ps.getXpToNextLevel());
            xpProgress.setFont(new Font("Monospaced", 12));
            xpProgress.setStyle("-fx-text-fill: #888;");
            root.getChildren().add(xpProgress);
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
        continueBtn.setOnAction(e -> { if (onContinue != null) onContinue.run(); });
        root.getChildren().add(continueBtn);

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        return new Scene(root, screen.getWidth(), screen.getHeight());
    }

    /** @return la scena JavaFX pronta per essere impostata sullo stage */
    public Scene getScene() { return scene; }
}
