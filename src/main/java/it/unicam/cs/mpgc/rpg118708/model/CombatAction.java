package it.unicam.cs.mpgc.rpg118708.model;

/**
 * Descrive una singola azione eseguibile durante il combattimento.
 *
 * <p>Una {@code CombatAction} è un valore immutabile che combina il tipo di
 * azione ({@link CombatActionType}), il label mostrato in UI e un valore
 * di potenza aggiuntivo sommato all'attacco base nel calcolo del danno o della cura.</p>
 */
public class CombatAction {

    private final String id;
    private final String label;
    private final CombatActionType type;
    private final int power;

    /**
     * Crea una nuova azione di combattimento.
     *
     * @param id    identificatore univoco
     * @param label testo visualizzato nel pulsante di combattimento
     * @param type  categoria dell'azione
     * @param power bonus numerico aggiunto all'attacco o alla cura base
     */
    public CombatAction(String id, String label, CombatActionType type, int power) {
        this.id = id;
        this.label = label;
        this.type = type;
        this.power = power;
    }

    public String getId() { return id; }
    public String getLabel() { return label; }
    public CombatActionType getType() { return type; }
    public int getPower() { return power; }
}
