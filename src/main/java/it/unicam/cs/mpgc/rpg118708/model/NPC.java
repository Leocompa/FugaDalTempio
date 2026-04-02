package it.unicam.cs.mpgc.rpg118708.model;

public class NPC {

    private final String id;
    private final String name;
    private final String dialogue;

    public NPC(String id, String name, String dialogue) {
        this.id = id;
        this.name = name;
        this.dialogue = dialogue;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDialogue() { return dialogue; }
}