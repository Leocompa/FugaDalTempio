package it.unicam.cs.mpgc.rpg118708.engine;

/**
 * Rappresenta lo stato corrente della partita.
 *
 * <ul>
 *   <li>{@link #EXPLORING} — il giocatore si muove nella scena di esplorazione</li>
 *   <li>{@link #COMBAT}    — è in corso un combattimento a turni</li>
 *   <li>{@link #DIALOGUE}  — il giocatore sta interagendo con un NPC</li>
 *   <li>{@link #GAME_OVER} — il giocatore è morto</li>
 *   <li>{@link #VICTORY}   — il giocatore ha completato il tempio</li>
 * </ul>
 */
public enum GameState {
    EXPLORING,
    COMBAT,
    DIALOGUE,
    GAME_OVER,
    VICTORY
}
