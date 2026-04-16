package it.unicam.cs.mpgc.rpg118708.view.menu;

import it.unicam.cs.mpgc.rpg118708.persistence.GamePersistence;
import it.unicam.cs.mpgc.rpg118708.persistence.SlotInfo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.function.Consumer;

/**
 * Scena di selezione degli slot di salvataggio/caricamento.
 *
 * <p>Funziona in due modalità controllate dal flag {@code isSaveMode}:
 * in modalità salvataggio mostra tutti gli slot, in modalità caricamento
 * disabilita gli slot vuoti. Dipende dall'interfaccia {@link GamePersistence}
 * per leggere le informazioni degli slot senza essere accoppiata a una
 * specifica implementazione di persistenza.</p>
 */
public class SaveSlotScene {

    private Scene scene;
    private final GamePersistence persistence;
    private final boolean isSaveMode;
    private Consumer<Integer> onSlotSelected;

    /**
     * Crea la scena di selezione slot.
     *
     * @param persistence la strategia di persistenza da cui leggere le informazioni degli slot
     * @param isSaveMode  {@code true} per modalità salvataggio, {@code false} per caricamento
     */
    public SaveSlotScene(GamePersistence persistence, boolean isSaveMode) {
        this.persistence = persistence;
        this.isSaveMode = isSaveMode;
        buildScene();
    }

    private void buildScene() {
        VBox root = new VBox(24);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: #0d0d14;");

        Label title = new Label(isSaveMode ? "Scegli uno slot di salvataggio" : "Scegli una partita da caricare");
        title.setFont(new Font("Monospaced", 24));
        title.setStyle("-fx-text-fill: #EF9F27;");

        root.getChildren().add(title);

        for (int slot = 1; slot <= persistence.getMaxSlots(); slot++) {
            root.getChildren().add(buildSlotCard(slot));
        }

        Button backBtn = new Button("← Indietro");
        backBtn.setStyle("""
                -fx-background-color: #1e1e30;
                -fx-text-fill: #AFA9EC;
                -fx-font-family: Monospaced;
                -fx-font-size: 14px;
                -fx-border-color: #534AB7;
                -fx-border-radius: 4;
                -fx-background-radius: 4;
                -fx-padding: 10px 24px;
                -fx-cursor: hand;
                """);
        backBtn.setOnAction(e -> { if (onSlotSelected != null) onSlotSelected.accept(-1); });

        root.getChildren().add(backBtn);

        javafx.geometry.Rectangle2D screen =
                javafx.stage.Screen.getPrimary().getVisualBounds();
        scene = new Scene(root, screen.getWidth(), screen.getHeight());
    }

    /**
     * Costruisce la card grafica per un singolo slot.
     *
     * <p>In modalità salvataggio, se lo slot è già occupato mostra un dialogo di
     * conferma prima di invocare il callback, per prevenire sovrascritture accidentali.</p>
     *
     * @param slot numero dello slot (1-based)
     * @return la riga HBox pronta da aggiungere alla scena
     */
    private HBox buildSlotCard(int slot) {
        SlotInfo info = persistence.getSlotInfo(slot);

        VBox details = new VBox(6);
        details.setAlignment(Pos.CENTER_LEFT);

        Label slotLabel = new Label("Slot " + slot);
        slotLabel.setFont(new Font("Monospaced", 16));
        slotLabel.setStyle("-fx-text-fill: #AFA9EC;");

        if (info != null) {
            Label nameLabel = new Label(info.getPlayerName() + "  —  LV." + info.getLevel()
                    + "  —  Stanza " + info.getRoomNumber());
            nameLabel.setFont(new Font("Monospaced", 14));
            nameLabel.setStyle("-fx-text-fill: #ccc;");

            Label timeLabel = new Label(info.getTimestamp());
            timeLabel.setFont(new Font("Monospaced", 13));
            timeLabel.setStyle("-fx-text-fill: #555;");

            details.getChildren().addAll(slotLabel, nameLabel, timeLabel);
        } else {
            Label emptyLabel = new Label("— vuoto —");
            emptyLabel.setFont(new Font("Monospaced", 14));
            emptyLabel.setStyle("-fx-text-fill: #3a3a55;");
            details.getChildren().addAll(slotLabel, emptyLabel);
        }

        Button actionBtn = new Button(isSaveMode ? "Salva qui" : "Carica");
        actionBtn.setPrefWidth(140);
        boolean disabled = !isSaveMode && info == null;
        actionBtn.setDisable(disabled);
        actionBtn.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-text-fill: %s;
                -fx-font-family: Monospaced;
                -fx-font-size: 15px;
                -fx-background-radius: 4;
                -fx-padding: 12px;
                -fx-cursor: hand;
                """,
                disabled ? "#2a2a40" : "#534AB7",
                disabled ? "#555" : "#EEEDFE"));

        int finalSlot = slot;
        actionBtn.setOnAction(e -> {
            if (onSlotSelected == null) return;
            if (isSaveMode && info != null) {
                javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Sovrascrivere salvataggio?");
                confirm.setHeaderText("Lo slot " + finalSlot + " contiene già una partita.");
                confirm.setContentText("Sovrascrivere " + info.getPlayerName()
                        + " (LV." + info.getLevel() + ") salvata il " + info.getTimestamp() + "?");

                javafx.scene.control.ButtonType btnSi =
                        new javafx.scene.control.ButtonType("Sì, sovrascrivi");
                javafx.scene.control.ButtonType btnNo =
                        new javafx.scene.control.ButtonType("Annulla",
                                javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
                confirm.getButtonTypes().setAll(btnSi, btnNo);

                confirm.showAndWait().ifPresent(response -> {
                    if (response == btnSi) onSlotSelected.accept(finalSlot);
                });
            } else {
                onSlotSelected.accept(finalSlot);
            }
        });

        HBox card = new HBox(20, details, actionBtn);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16, 24, 16, 24));
        card.setPrefWidth(600);
        card.setStyle("""
                -fx-background-color: #13131f;
                -fx-border-color: #2a2a40;
                -fx-border-width: 0.5;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                """);
        HBox.setHgrow(details, javafx.scene.layout.Priority.ALWAYS);
        return card;
    }

    public Scene getScene() { return scene; }
    public void setOnSlotSelected(Consumer<Integer> onSlotSelected) {
        this.onSlotSelected = onSlotSelected;
    }
}