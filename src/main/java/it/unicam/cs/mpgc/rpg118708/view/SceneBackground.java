package it.unicam.cs.mpgc.rpg118708.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Utility per il rendering dello sfondo a mattoncini comune a tutte le scene del gioco.
 *
 * <p>Replica la stessa griglia usata in {@code ExplorationRenderer.renderBackground()}
 * così da mantenere coerenza visiva tra esplorazione e menu. Il metodo statico
 * {@link #createCanvas(double, double)} è il punto di ingresso consigliato:
 * crea un {@link Canvas} pronto da inserire come primo figlio di uno
 * {@link javafx.scene.layout.StackPane}.</p>
 */
public class SceneBackground {

    private SceneBackground() {}

    /**
     * Crea un {@link Canvas} delle dimensioni specificate con lo sfondo
     * a mattoncini già renderizzato.
     *
     * @param w larghezza in pixel
     * @param h altezza in pixel
     * @return un Canvas con sfondo e vignetta disegnati
     */
    public static Canvas createCanvas(double w, double h) {
        Canvas canvas = new Canvas(w, h);
        render(canvas.getGraphicsContext2D(), w, h);
        return canvas;
    }

    /**
     * Disegna lo sfondo a mattoncini e la vignetta scura sui bordi sul
     * contesto grafico fornito.
     *
     * @param gc il contesto grafico su cui disegnare
     * @param w  larghezza dell'area di disegno
     * @param h  altezza dell'area di disegno
     */
    public static void render(GraphicsContext gc, double w, double h) {
        gc.setFill(Color.web("#080810"));
        gc.fillRect(0, 0, w, h);

        int bW = 50, bH = 22, gap = 2;
        String[] shades = {"#131320", "#111118", "#161626", "#12121e", "#141422"};
        for (int row = 0; row * (bH + gap) < h + bH; row++) {
            int y       = row * (bH + gap);
            int offsetX = (row % 2 == 0) ? 0 : (bW + gap) / 2;
            for (int col = -1; col * (bW + gap) - offsetX < w + bW; col++) {
                int x = col * (bW + gap) - offsetX;
                gc.setFill(Color.web(shades[Math.abs((row * 3 + col * 2) % shades.length)]));
                gc.fillRect(x, y, bW, bH);
            }
        }

        gc.setFill(Color.web("#000000", 0.65));
        gc.fillRect(0,     0,     120, h);
        gc.fillRect(w-120, 0,     120, h);
        gc.fillRect(0,     0,     w,   100);
        gc.fillRect(0,     h-100, w,   100);
    }
}
