package it.unicam.cs.mpgc.rpg118708.model;

/**
 * Categorizza le azioni disponibili durante un combattimento.
 *
 * <ul>
 *   <li>{@link #ATTACK}  — attacco base, sempre disponibile</li>
 *   <li>{@link #SPECIAL} — attacco potenziato, con un numero limitato di usi</li>
 *   <li>{@link #HEAL}    — cura tramite pozione o rigenerazione del nemico</li>
 *   <li>{@link #FLEE}    — tenta la fuga dal combattimento</li>
 * </ul>
 */
public enum CombatActionType {
    ATTACK,
    SPECIAL,
    HEAL,
    FLEE
}
