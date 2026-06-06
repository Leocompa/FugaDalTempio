package it.unicam.cs.mpgc.rpg118708.view.menu;

import it.unicam.cs.mpgc.rpg118708.view.GameScene;
import it.unicam.cs.mpgc.rpg118708.view.SceneBackground;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

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
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double W = screen.getWidth();
        double H = screen.getHeight();

        Canvas bg = SceneBackground.createCanvas(W, H);

        Label title = new Label("Fuga dal Tempio");
        title.getStyleClass().add("label-title");

        Label subtitle = new Label("Un ladro. Un tempio. Nessuna via d'uscita.");
        subtitle.getStyleClass().add("label-subtitle");
        subtitle.setWrapText(true);
        subtitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label nameLabel = new Label("Inserisci il nome del tuo ladro:");
        nameLabel.getStyleClass().add("label-body");

        nameField = new TextField();
        nameField.setPromptText("nome...");
        nameField.setMaxWidth(240);
        nameField.getStyleClass().add("name-field");

        newGameButton = new Button("Nuova partita");
        newGameButton.setPrefWidth(280);
        newGameButton.getStyleClass().add("btn-primary");

        loadGameButton = new Button("Carica partita");
        loadGameButton.setPrefWidth(280);
        loadGameButton.getStyleClass().add("btn-secondary");

        exitButton = new Button("Esci");
        exitButton.setPrefWidth(280);
        exitButton.getStyleClass().add("btn-danger");
        exitButton.setOnAction(e -> javafx.application.Platform.exit());

        errorLabel = new Label();
        errorLabel.getStyleClass().add("label-error");
        errorLabel.setVisible(false);

        VBox panel = new VBox(20);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(48));
        panel.setMaxWidth(500);
        panel.getStyleClass().add("game-panel");
        panel.getChildren().addAll(title, subtitle, nameLabel, nameField,
                errorLabel, newGameButton, loadGameButton, exitButton);

        StackPane root = new StackPane(bg, panel);
        scene = new Scene(root, W, H);
        scene.getStylesheets().add(getClass().getResource("/game.css").toExternalForm());
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
