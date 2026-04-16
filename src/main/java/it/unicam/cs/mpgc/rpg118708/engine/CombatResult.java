package it.unicam.cs.mpgc.rpg118708.engine;

/**
 * Esito di un'azione o di un turno di combattimento.
 *
 * <ul>
 *   <li>{@link #ONGOING}        — il combattimento è ancora in corso</li>
 *   <li>{@link #VICTORY}        — il giocatore ha sconfitto il nemico</li>
 *   <li>{@link #VICTORY_LEVELUP}— vittoria con level-up del giocatore</li>
 *   <li>{@link #DEFEAT}         — il giocatore è stato sconfitto</li>
 *   <li>{@link #FLED}           — il giocatore è fuggito dal combattimento</li>
 * </ul>
 */
public enum CombatResult {
    ONGOING,
    VICTORY,
    VICTORY_LEVELUP,
    DEFEAT,
    FLED
}
