package it.unicam.cs.mpgc.rpg118708.view;

import javafx.scene.Scene;

/**
 * Interfaccia comune a tutte le scene dell'interfaccia grafica.
 *
 * <p>Consente al {@link it.unicam.cs.mpgc.rpg118708.controller.GameController}
 * di lavorare con le scene tramite un'astrazione anziché dipendere direttamente
 * dalle classi concrete (DIP). Ogni implementazione costruisce la propria
 * {@link Scene} JavaFX e la espone tramite questo contratto.</p>
 */
public interface GameScene {

    /**
     * Restituisce la scena JavaFX pronta per essere impostata sullo stage.
     *
     * @return la scena JavaFX
     */
    Scene getScene();
}
