package it.unicam.cs.mpgc.rpg118708.view.menu;

import it.unicam.cs.mpgc.rpg118708.view.GameScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Schermata iniziale del gioco.
 *
 * <p>Mostra il titolo, un campo per inserire il nome del personaggio e
 * i pulsanti per avviare una nuova partita, caricare una partita salvata
 * o uscire dall'applicazione. Lo sfondo riproduce la griglia di mattoncini
 * delle stanze di esplorazione per coerenza visiva. I listener sui pulsanti
 * sono registrati esternamente dal
 * {@link it.unicam.cs.mpgc.rpg118708.controller.GameController}.</p>
 */
public class StartScene implements GameScene {

    private static final int W = 800;
    private static final int H = 600;

    private Scene scene;
    private TextField nameField;
    private Button newGameButton;
    private Button loadGameButton;
    private Button exitButton;
    private Label errorLabel;

    /** Costruisce e inizializza la scena del menu principale. */
    public StartScene() {
        buildScene();
    }

    private void buildScene() {
        Canvas bg = new Canvas(W, H);
        renderBricks(bg.getGraphicsContext2D());

        Label title = new Label("Fuga dal Tempio");
        title.setFont(new Font("Monospaced", 38));
        title.setStyle("-fx-text-fill: #EF9F27;");

        Label subtitle = new Label("Un ladro. Un tempio. Nessuna via d'uscita.");
        subtitle.setFont(new Font("Monospaced", 16));
        subtitle.setStyle("-fx-text-fill: #888780;");

        Label nameLabel = new Label("Inserisci il nome del tuo ladro:");
        nameLabel.setFont(new Font("Monospaced", 15));
        nameLabel.setStyle("-fx-text-fill: #ccc;");

        nameField = new TextField();
        nameField.setPromptText("nome...");
        nameField.setMaxWidth(240);
        nameField.setStyle("""
                -fx-background-color: #1e1e30;
                -fx-text-fill: #ffffff;
                -fx-prompt-text-fill: #555;
                -fx-border-color: #3a3a55;
                -fx-border-radius: 4;
                -fx-background-radius: 4;
                -fx-font-family: Monospaced;
                -fx-font-size: 16px;
                -fx-padding: 10px;
                """);

        newGameButton = new Button("Nuova partita");
        newGameButton.setPrefWidth(280);
        newGameButton.setStyle("""
                -fx-background-color: #534AB7;
                -fx-text-fill: #EEEDFE;
                -fx-font-family: Monospaced;
                -fx-font-size: 16px;
                -fx-border-radius: 4;
                -fx-background-radius: 4;
                -fx-padding: 12px;
                -fx-cursor: hand;
                """);

        loadGameButton = new Button("Carica partita");
        loadGameButton.setPrefWidth(280);
        loadGameButton.setStyle("""
                -fx-background-color: #1e1e30;
                -fx-text-fill: #AFA9EC;
                -fx-font-family: Monospaced;
                -fx-font-size: 16px;
                -fx-border-color: #534AB7;
                -fx-border-radius: 4;
                -fx-background-radius: 4;
                -fx-padding: 12px;
                -fx-cursor: hand;
                """);

        exitButton = new Button("Esci");
        exitButton.setPrefWidth(280);
        exitButton.setStyle("""
                -fx-background-color: #1e1e30;
                -fx-text-fill: #E24B4A;
                -fx-font-family: Monospaced;
                -fx-font-size: 16px;
                -fx-border-color: #5a2222;
                -fx-border-radius: 4;
                -fx-background-radius: 4;
                -fx-padding: 12px;
                -fx-cursor: hand;
                """);
        exitButton.setOnAction(e -> javafx.application.Platform.exit());

        errorLabel = new Label();
        errorLabel.setFont(new Font("Monospaced", 13));
        errorLabel.setStyle("-fx-text-fill: #E24B4A;");
        errorLabel.setVisible(false);

        VBox panel = new VBox(20);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(48));
        panel.setMaxWidth(440);
        panel.setStyle("""
                -fx-background-color: rgba(8, 8, 16, 0.90);
                -fx-border-color: #2a2a42;
                -fx-border-width: 2;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                """);
        panel.getChildren().addAll(title, subtitle, nameLabel, nameField,
                errorLabel, newGameButton, loadGameButton, exitButton);

        StackPane root = new StackPane(bg, panel);
        scene = new Scene(root, W, H);
    }

    /**
     * Disegna la griglia di mattoncini identica allo sfondo delle stanze
     * di esplorazione, più una vignetta scura sui bordi per dare profondità.
     */
    private void renderBricks(GraphicsContext gc) {
        gc.setFill(Color.web("#080810"));
        gc.fillRect(0, 0, W, H);

        int bW = 50, bH = 22, gap = 2;
        String[] shades = {"#131320", "#111118", "#161626", "#12121e", "#141422"};
        for (int row = 0; row * (bH + gap) < H + bH; row++) {
            int y       = row * (bH + gap);
            int offsetX = (row % 2 == 0) ? 0 : (bW + gap) / 2;
            for (int col = -1; col * (bW + gap) - offsetX < W + bW; col++) {
                int x = col * (bW + gap) - offsetX;
                gc.setFill(Color.web(shades[Math.abs((row * 3 + col * 2) % shades.length)]));
                gc.fillRect(x, y, bW, bH);
            }
        }

        // Vignetta: quattro fasce scure ai bordi per incorniciare il pannello
        gc.setFill(Color.web("#00000080"));
        gc.fillRect(0,     0,     100, H);       // sinistra
        gc.fillRect(W-100, 0,     100, H);       // destra
        gc.fillRect(0,     0,     W,   80);      // superiore
        gc.fillRect(0,     H-80,  W,   80);      // inferiore
    }

    /** @return la scena JavaFX pronta per essere impostata sullo stage */
    public Scene getScene() { return scene; }

    /** @return il testo inserito dall'utente nel campo nome, ripulito da spazi */
    public String getPlayerName() { return nameField.getText().trim(); }

    /**
     * Mostra un messaggio di errore inline sotto il campo nome.
     *
     * @param message il testo da mostrare
     */
    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    /** Nasconde l'eventuale messaggio di errore. */
    public void clearError() {
        errorLabel.setVisible(false);
    }

    /** @return il pulsante "Nuova partita", su cui il controller registra il listener */
    public Button getNewGameButton() { return newGameButton; }

    /** @return il pulsante "Carica partita", su cui il controller registra il listener */
    public Button getLoadGameButton() { return loadGameButton; }
}
