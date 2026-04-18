package it.unicam.cs.mpgc.rpg118708;

import it.unicam.cs.mpgc.rpg118708.controller.GameController;
import it.unicam.cs.mpgc.rpg118708.controller.WorldBuilder;
import it.unicam.cs.mpgc.rpg118708.persistence.XmlGamePersistence;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Punto di ingresso dell'applicazione e composition root.
 *
 * <p>È l'unico posto del progetto in cui le implementazioni concrete
 * ({@link XmlGamePersistence}, {@link WorldBuilder}) vengono istanziate
 * ed iniettate nel {@link GameController}. Il resto del codice dipende
 * esclusivamente dalle interfacce, garantendo il principio DIP.</p>
 */
public class Main extends Application {

    /** Costruisce l'istanza principale dell'applicazione (richiesto da JavaFX). */
    public Main() {}

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fuga dal Tempio");
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);

        GameController controller = new GameController(
                primaryStage,
                new XmlGamePersistence(),
                new WorldBuilder()
        );
        controller.start();
        primaryStage.show();
    }

    /**
     * Avvia l'applicazione JavaFX.
     *
     * @param args argomenti della riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
