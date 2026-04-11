package it.unicam.cs.mpgc.rpg118708;

import it.unicam.cs.mpgc.rpg118708.controller.GameController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fuga dal Tempio");
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        GameController controller = new GameController(primaryStage);
        controller.start();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}