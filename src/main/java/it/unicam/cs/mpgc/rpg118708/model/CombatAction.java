package it.unicam.cs.mpgc.rpg118708.model;

public class CombatAction {

    private final String id;
    private final String label;
    private final CombatActionType type;
    private final int power;

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