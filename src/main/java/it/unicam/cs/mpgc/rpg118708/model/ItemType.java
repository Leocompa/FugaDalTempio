package it.unicam.cs.mpgc.rpg118708.model;

/**
 * Categorizza gli oggetti raccoglibili nel gioco.
 *
 * <ul>
 *   <li>{@link #POTION}   — ripristina HP durante o fuori dal combattimento</li>
 *   <li>{@link #AMULET}   — equipaggiabile: aumenta difesa e HP massimi</li>
 *   <li>{@link #SCROLL}   — consumabile: aumenta l'attacco per un turno</li>
 *   <li>{@link #TALISMAN} — consumabile: dimezza il prossimo danno ricevuto</li>
 * </ul>
 */
public enum ItemType {
    /** Pozione che ripristina HP durante o fuori dal combattimento. */
    POTION,
    /** Amuleto equipaggiabile che aumenta difesa e HP massimi. */
    AMULET,
    /** Pergamena consumabile che aumenta l'attacco per un turno. */
    SCROLL,
    /** Talismano consumabile che dimezza il prossimo danno ricevuto. */
    TALISMAN
}
