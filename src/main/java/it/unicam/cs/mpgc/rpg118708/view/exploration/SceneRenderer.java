package it.unicam.cs.mpgc.rpg118708.view.exploration;

import javafx.scene.input.KeyCode;

import java.util.Set;

/**
 * Contratto del renderer grafico per la scena di esplorazione.
 *
 * <p>Espone solo i metodi che {@link ExplorationScene} deve invocare,
 * disaccoppiando la logica di aggiornamento dall'implementazione concreta
 * del rendering (DIP). Le notifiche di stato ({@code triggerEnemyWarning},
 * {@code showSaveMessage}, ecc.) permettono alla scena di comunicare eventi
 * al renderer senza conoscerne i dettagli grafici.</p>
 */
interface SceneRenderer {

    /**
     * Esegue un frame completo di rendering.
     *
     * @param frame        numero di frame dall'avvio
     * @param nearExit     {@code true} se il giocatore è vicino all'uscita
     * @param nearEntrance {@code true} se il giocatore è vicino all'ingresso
     * @param onGround     {@code true} se il giocatore è a terra
     * @param keysPressed  insieme dei tasti premuti in questo frame
     */
    void render(long frame, boolean nearExit, boolean nearEntrance,
                boolean onGround, Set<KeyCode> keysPressed);

    /** Attiva il flash visivo di avviso nemico. */
    void triggerEnemyWarning();

    /**
     * Mostra un messaggio di salvataggio in sovrimpressione.
     *
     * @param msg il testo da visualizzare
     */
    void showSaveMessage(String msg);

    /** Imposta il testo del dialogo da mostrare in sovrimpressione. */
    void showDialogue(String text);

    /** Rimuove il testo del dialogo dalla schermata. */
    void clearDialogue();
}
