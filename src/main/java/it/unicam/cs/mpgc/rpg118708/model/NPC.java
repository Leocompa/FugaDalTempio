package it.unicam.cs.mpgc.rpg118708.model;

import it.unicam.cs.mpgc.rpg118708.model.exception.InvalidNameException;

/**
 * Rappresenta un personaggio non giocante (NPC) con cui il giocatore può interagire.
 *
 * <p>Un NPC ha un dialogo fisso e può offrire una ricompensa una sola volta.
 * Una volta consegnata, la ricompensa non è più disponibile: {@link #collectReward()}
 * restituisce {@code null} alle chiamate successive.</p>
 */
public class NPC {

    private final String id;
    private final String name;
    private final String dialogue;
    private final Item reward;
    private boolean rewardGiven;

    /**
     * Crea un NPC con dialogo e ricompensa.
     *
     * @param id       identificatore univoco (non null né vuoto)
     * @param name     nome visualizzato (non null né vuoto)
     * @param dialogue testo del dialogo mostrato al giocatore (non null né vuoto)
     * @param reward   oggetto offerto come ricompensa (può essere {@code null})
     * @throws InvalidNameException se {@code id}, {@code name} o {@code dialogue} sono null o vuoti
     */
    public NPC(String id, String name, String dialogue, Item reward) {
        if (id == null || id.isBlank())             throw new InvalidNameException("l'id dell'NPC non può essere null o vuoto");
        if (name == null || name.isBlank())         throw new InvalidNameException("il nome dell'NPC non può essere null o vuoto");
        if (dialogue == null || dialogue.isBlank()) throw new InvalidNameException("il dialogo dell'NPC non può essere null o vuoto");
        this.id = id;
        this.name = name;
        this.dialogue = dialogue;
        this.reward = reward;
        this.rewardGiven = false;
    }

    /**
     * Crea un NPC senza ricompensa.
     *
     * @param id       identificatore univoco (non null né vuoto)
     * @param name     nome visualizzato (non null né vuoto)
     * @param dialogue testo del dialogo mostrato al giocatore (non null né vuoto)
     * @throws InvalidNameException se {@code id}, {@code name} o {@code dialogue} sono null o vuoti
     */
    public NPC(String id, String name, String dialogue) {
        this(id, name, dialogue, null);
    }

    /**
     * Consegna la ricompensa al giocatore, se ancora disponibile.
     * Dopo la prima chiamata con esito positivo, la ricompensa è marcata
     * come consegnata e non sarà più restituita.
     *
     * @return l'oggetto ricompensa, o {@code null} se assente o già consegnato
     */
    public Item collectReward() {
        if (reward != null && !rewardGiven) {
            rewardGiven = true;
            return reward;
        }
        return null;
    }

    /**
     * Verifica se la ricompensa esiste e non è ancora stata consegnata.
     *
     * @return {@code true} se c'è una ricompensa disponibile
     */
    public boolean hasReward() { return reward != null && !rewardGiven; }

    public String getId() { return id; }

    public String getName() { return name; }

    public String getDialogue() { return dialogue; }

    /** @return la ricompensa, o {@code null} — anche se già consegnata */
    public Item getReward() { return reward; }

    public boolean isRewardGiven() { return rewardGiven; }

    /**
     * Imposta il flag di ricompensa già consegnata (usato dal caricamento da slot).
     *
     * @param rewardGiven {@code true} se la ricompensa è da considerarsi già consegnata
     */
    public void setRewardGiven(boolean rewardGiven) { this.rewardGiven = rewardGiven; }
}
