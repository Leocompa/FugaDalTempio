package it.unicam.cs.mpgc.rpg118708.model;

public class NPC {

    private final String id;
    private final String name;
    private final String dialogue;
    private final Item reward;
    private boolean rewardGiven;

    public NPC(String id, String name, String dialogue, Item reward) {
        this.id = id;
        this.name = name;
        this.dialogue = dialogue;
        this.reward = reward;
        this.rewardGiven = false;
    }

    public NPC(String id, String name, String dialogue) {
        this(id, name, dialogue, null);
    }

    public Item collectReward() {
        if (reward != null && !rewardGiven) {
            rewardGiven = true;
            return reward;
        }
        return null;
    }

    public boolean hasReward() { return reward != null && !rewardGiven; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDialogue() { return dialogue; }
    public Item getReward() { return reward; }
    public boolean isRewardGiven() { return rewardGiven; }
    public void setRewardGiven(boolean rewardGiven) { this.rewardGiven = rewardGiven; }
}