package it.unicam.cs.mpgc.rpg118708.controller;

import it.unicam.cs.mpgc.rpg118708.engine.CombatManager;
import it.unicam.cs.mpgc.rpg118708.engine.CombatResult;
import it.unicam.cs.mpgc.rpg118708.model.CombatAction;
import it.unicam.cs.mpgc.rpg118708.model.CombatActionType;

public class CombatController {

    private final CombatManager combatManager;
    private Runnable onVictory;
    private Runnable onDefeat;
    private Runnable onFlee;

    public CombatController(CombatManager combatManager) {
        this.combatManager = combatManager;
    }

    public String handlePlayerAction(CombatActionType type) {
        CombatAction action = buildAction(type);
        CombatResult result = combatManager.executePlayerAction(action);
        return resolveResult(result);
    }

    public String handleEnemyTurn() {
        CombatResult result = combatManager.executeEnemyTurn();
        return resolveResult(result);
    }

    private CombatAction buildAction(CombatActionType type) {
        return switch (type) {
            case ATTACK  -> new CombatAction("attack",  "Attacca",          CombatActionType.ATTACK,  0);
            case SPECIAL -> new CombatAction("special", "Lama d'ombra",     CombatActionType.SPECIAL, 5);
            case HEAL    -> new CombatAction("heal",    "Usa pozione",      CombatActionType.HEAL,   15);
            case FLEE    -> new CombatAction("flee",    "Fuggi",            CombatActionType.FLEE,    0);
        };
    }

    private String resolveResult(CombatResult result) {
        return switch (result) {
            case VICTORY        -> { if (onVictory != null) onVictory.run(); yield "Nemico sconfitto!"; }
            case VICTORY_LEVELUP-> { if (onVictory != null) onVictory.run(); yield "Livello aumentato!"; }
            case DEFEAT         -> { if (onDefeat  != null) onDefeat.run();  yield "Sei caduto..."; }
            case FLED           -> { if (onFlee    != null) onFlee.run();    yield "Sei fuggito."; }
            case ONGOING        -> "";
        };
    }
    private Runnable onLoad;

    public void setOnLoad(Runnable onLoad) { this.onLoad = onLoad; }
    public Runnable getOnLoad() { return onLoad; }

    public void setOnVictory(Runnable onVictory) { this.onVictory = onVictory; }
    public void setOnDefeat(Runnable onDefeat) { this.onDefeat = onDefeat; }
    public void setOnFlee(Runnable onFlee) { this.onFlee = onFlee; }
    public CombatManager getCombatManager() { return combatManager; }

    public Runnable getOnVictory() { return onVictory; }
    public Runnable getOnDefeat() { return onDefeat; }
    public Runnable getOnFlee() { return onFlee; }
}