package it.unicam.cs.mpgc.rpg118708.controller;

import it.unicam.cs.mpgc.rpg118708.engine.CombatManager;
import it.unicam.cs.mpgc.rpg118708.engine.CombatResult;
import it.unicam.cs.mpgc.rpg118708.model.CombatAction;
import it.unicam.cs.mpgc.rpg118708.model.CombatActionType;

/**
 * Controller del combattimento: fa da ponte tra la UI ({@link it.unicam.cs.mpgc.rpg118708.view.combat.CombatScene})
 * e la logica di combattimento ({@link CombatManager}).
 *
 * <p>Traduce i comandi dell'utente (tipo di azione) in {@link CombatAction}
 * concrete e delega l'esecuzione al {@link CombatManager}. Notifica il
 * {@link GameController} degli esiti rilevanti (vittoria, sconfitta, fuga,
 * caricamento) tramite callback {@link Runnable}.</p>
 */
public class CombatController {

    private final CombatManager combatManager;
    private Runnable onVictory;
    private Runnable onDefeat;
    private Runnable onFlee;
    private Runnable onLoad;

    /**
     * Crea il controller associandolo al gestore di combattimento corrente.
     *
     * @param combatManager il gestore della logica di combattimento
     */
    public CombatController(CombatManager combatManager) {
        this.combatManager = combatManager;
    }

    /**
     * Gestisce l'azione scelta dal giocatore e restituisce un messaggio descrittivo.
     * Se il combattimento termina, invoca automaticamente la callback appropriata.
     *
     * @param type il tipo di azione selezionata
     * @return messaggio da mostrare nel log di combattimento
     */
    public String handlePlayerAction(CombatActionType type) {
        CombatAction action = buildAction(type);
        CombatResult result = combatManager.executePlayerAction(action);
        return resolveResult(result);
    }

    /**
     * Esegue il turno del nemico e restituisce un messaggio descrittivo.
     * Se il combattimento termina, invoca automaticamente la callback appropriata.
     *
     * @return messaggio da mostrare nel log di combattimento
     */
    public String handleEnemyTurn() {
        CombatResult result = combatManager.executeEnemyTurn();
        return resolveResult(result);
    }

    private CombatAction buildAction(CombatActionType type) {
        return switch (type) {
            case ATTACK  -> new CombatAction("attack",  "Attacca",      CombatActionType.ATTACK,  0);
            case SPECIAL -> new CombatAction("special", "Lama d'ombra", CombatActionType.SPECIAL, 5);
            case HEAL    -> new CombatAction("heal",    "Usa pozione",  CombatActionType.HEAL,   15);
            case FLEE    -> new CombatAction("flee",    "Fuggi",        CombatActionType.FLEE,    0);
        };
    }

    private String resolveResult(CombatResult result) {
        return switch (result) {
            case VICTORY         -> { if (onVictory != null) onVictory.run(); yield "Nemico sconfitto!"; }
            case VICTORY_LEVELUP -> { if (onVictory != null) onVictory.run(); yield "Livello aumentato!"; }
            case DEFEAT          -> { if (onDefeat  != null) onDefeat.run();  yield "Sei caduto..."; }
            case FLED            -> { if (onFlee    != null) onFlee.run();    yield "Sei fuggito."; }
            case ONGOING         -> "";
        };
    }

    /**
     * Registra la callback invocata al termine di un combattimento vinto.
     *
     * @param onVictory la callback di vittoria
     */
    public void setOnVictory(Runnable onVictory) { this.onVictory = onVictory; }

    /**
     * Registra la callback invocata al termine di un combattimento perso.
     *
     * @param onDefeat la callback di sconfitta
     */
    public void setOnDefeat(Runnable onDefeat) { this.onDefeat = onDefeat; }

    /**
     * Registra la callback invocata quando il giocatore fugge dal combattimento.
     *
     * @param onFlee la callback di fuga
     */
    public void setOnFlee(Runnable onFlee) { this.onFlee = onFlee; }

    /**
     * Registra la callback invocata quando il giocatore sceglie di caricare un salvataggio.
     *
     * @param onLoad la callback di caricamento
     */
    public void setOnLoad(Runnable onLoad) { this.onLoad = onLoad; }

    /** Restituisce il gestore della logica di combattimento. @return il {@link CombatManager} */
    public CombatManager getCombatManager() { return combatManager; }

    /** Restituisce la callback di vittoria. @return la callback, o {@code null} se non registrata */
    public Runnable getOnVictory() { return onVictory; }

    /** Restituisce la callback di sconfitta. @return la callback, o {@code null} se non registrata */
    public Runnable getOnDefeat() { return onDefeat; }

    /** Restituisce la callback di fuga. @return la callback, o {@code null} se non registrata */
    public Runnable getOnFlee() { return onFlee; }

    /** Restituisce la callback di caricamento. @return la callback, o {@code null} se non registrata */
    public Runnable getOnLoad() { return onLoad; }
}
