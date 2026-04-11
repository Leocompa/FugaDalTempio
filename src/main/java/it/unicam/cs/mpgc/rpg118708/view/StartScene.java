package it.unicam.cs.mpgc.rpg118708.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class StartScene {

    private Scene scene;
    private TextField nameField;
    private Button newGameButton;
    private Button loadGameButton;
    private Button exitButton;

    public StartScene() {
        buildScene();
    }

    private void buildScene() {
        Label title = new Label("Fuga dal Tempio");
        title.setFont(new Font("Monospaced", 32));
        title.setStyle("-fx-text-fill: #EF9F27;");

        Label subtitle = new Label("Un ladro. Un tempio. Nessuna via d'uscita.");
        subtitle.setFont(new Font("Monospaced", 14));
        subtitle.setStyle("-fx-text-fill: #888780;");

        Label nameLabel = new Label("Inserisci il nome del tuo ladro:");
        nameLabel.setFont(new Font("Monospaced", 13));
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
                -fx-font-size: 14px;
                -fx-padding: 8px;
                """);

        newGameButton = new Button("Nuova partita");
        newGameButton.setPrefWidth(240);
        newGameButton.setStyle("""
                -fx-background-color: #534AB7;
                -fx-text-fill: #EEEDFE;
                -fx-font-family: Monospaced;
                -fx-font-size: 14px;
                -fx-border-radius: 4;
                -fx-background-radius: 4;
                -fx-padding: 10px;
                -fx-cursor: hand;
                """);

        loadGameButton = new Button("Carica partita");
        loadGameButton.setPrefWidth(240);
        loadGameButton.setStyle("""
                -fx-background-color: #1e1e30;
                -fx-text-fill: #AFA9EC;
                -fx-font-family: Monospaced;
                -fx-font-size: 14px;
                -fx-border-color: #534AB7;
                -fx-border-radius: 4;
                -fx-background-radius: 4;
                -fx-padding: 10px;
                -fx-cursor: hand;
                """);

        exitButton = new Button("Esci");
        exitButton.setPrefWidth(240);
        exitButton.setStyle("""
        -fx-background-color: #1e1e30;
        -fx-text-fill: #555;
        -fx-font-family: Monospaced;
        -fx-font-size: 14px;
        -fx-border-color: #2a2a40;
        -fx-border-radius: 4;
        -fx-background-radius: 4;
        -fx-padding: 10px;
        -fx-cursor: hand;
        """);
        exitButton.setOnAction(e -> javafx.application.Platform.exit());

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: #0d0d14;");
        root.getChildren().addAll(title, subtitle, nameLabel, nameField, newGameButton, loadGameButton, exitButton);

        scene = new Scene(root, 800, 600);
    }

    public Scene getScene() { return scene; }
    public String getPlayerName() { return nameField.getText().trim(); }
    public Button getNewGameButton() { return newGameButton; }
    public Button getLoadGameButton() { return loadGameButton; }
}