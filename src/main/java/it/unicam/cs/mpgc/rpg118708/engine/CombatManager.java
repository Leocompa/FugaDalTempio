package it.unicam.cs.mpgc.rpg118708.engine;

import it.unicam.cs.mpgc.rpg118708.model.Boss;
import it.unicam.cs.mpgc.rpg118708.model.CombatAction;
import it.unicam.cs.mpgc.rpg118708.model.CombatActionType;
import it.unicam.cs.mpgc.rpg118708.model.Enemy;
import it.unicam.cs.mpgc.rpg118708.model.Item;
import it.unicam.cs.mpgc.rpg118708.model.ItemType;
import it.unicam.cs.mpgc.rpg118708.model.Player;

/**
 * Gestisce la logica di un singolo combattimento a turni.
 *
 * <p>Responsabilità di questa classe: eseguire le azioni del giocatore e del nemico,
 * calcolare i danni con varianza casuale, gestire gli effetti degli oggetti consumabili
 * (Pergamena, Talismano) e determinare l'esito del combattimento ({@link CombatResult}).
 * Non conosce la UI: restituisce solo risultati che la view interpreterà.</p>
 *
 * <p>Un combattimento inizia con {@link #startCombat(Enemy, int)} e procede
 * alternando {@link #executePlayerAction(CombatAction)} e {@link #executeEnemyTurn()}
 * finché il risultato non è diverso da {@link CombatResult#ONGOING}.</p>
 */
public class CombatManager {

    private static final int AMULET_DEF_BONUS = 4;
    private static final int AMULET_HP_BONUS  = 10;

    public static final int MAX_ENEMY_HEAL_USES = 2;

    private final Player player;
    private Enemy enemy;
    private boolean playerTurn;
    private CombatResult lastResult;
    private int specialUsesLeft;
    private int maxSpecialUses;
    private int temporaryAttackBonus = 0;
    private boolean damageReductionActive = false;
    private int enemyHealUsesLeft = MAX_ENEMY_HEAL_USES;
    private CombatAction lastEnemyAction;

    /**
     * Crea un gestore di combattimento associato al giocatore.
     *
     * @param player il giocatore protagonista dei combattimenti
     */
    public CombatManager(Player player) {
        this.player = player;
        this.playerTurn = true;
        this.lastResult = CombatResult.ONGOING;
    }

    /**
     * Inizializza un nuovo combattimento contro il nemico specificato.
     *
     * @param enemy     il nemico da affrontare
     * @param roomIndex l'indice della stanza corrente, usato per determinare
     *                  il numero di usi speciali disponibili
     */
    public void startCombat(Enemy enemy, int roomIndex) {
        this.enemy = enemy;
        this.playerTurn = true;
        this.lastResult = CombatResult.ONGOING;
        this.maxSpecialUses = Math.max(1, roomIndex);
        this.specialUsesLeft = this.maxSpecialUses;
        this.enemyHealUsesLeft = MAX_ENEMY_HEAL_USES;
        this.lastEnemyAction = null;
    }

    /**
     * Esegue l'azione scelta dal giocatore per il turno corrente.
     *
     * @param action l'azione da eseguire
     * @return il risultato aggiornato del combattimento
     */
    public CombatResult executePlayerAction(CombatAction action) {
        if (!playerTurn || lastResult != CombatResult.ONGOING) return lastResult;

        switch (action.getType()) {
            case ATTACK -> {
                int damage = computeDamage(player.getStats().getAttack(), action.getPower());
                enemy.takeDamage(damage);
                checkBossEnrage();
            }
            case SPECIAL -> {
                if (specialUsesLeft <= 0) break;
                int damage = computeDamage((int)(player.getStats().getAttack() * 1.5), action.getPower());
                enemy.takeDamage(damage);
                specialUsesLeft--;
                checkBossEnrage();
            }
            case HEAL -> {
                Item potion = findPotion();
                if (potion != null) {
                    player.heal(action.getPower());
                    player.getInventory().removeItem(potion);
                }
            }
            case FLEE -> {
                lastResult = CombatResult.FLED;
                return lastResult;
            }
        }

        if (!enemy.isAlive()) {
            boolean leveledUp = player.gainXp(enemy.getXpReward());
            lastResult = leveledUp ? CombatResult.VICTORY_LEVELUP : CombatResult.VICTORY;
            return lastResult;
        }

        playerTurn = false;
        return CombatResult.ONGOING;
    }

    /**
     * Esegue il turno del nemico, scegliendone l'azione in modo casuale.
     *
     * @return il risultato aggiornato del combattimento
     */
    public CombatResult executeEnemyTurn() {
        if (playerTurn || lastResult != CombatResult.ONGOING) return lastResult;

        CombatAction action = enemy.chooseAction();
        lastEnemyAction = action;

        switch (action.getType()) {
            case ATTACK -> {
                int damage = computeDamage(enemy.getStats().getAttack(), action.getPower());
                applyDamageToPlayer(damage);
            }
            case SPECIAL -> {
                int damage = computeDamage(enemy.getStats().getAttack() * 2, action.getPower());
                applyDamageToPlayer(damage);
            }
            case HEAL -> {
                if (enemyHealUsesLeft > 0) {
                    enemy.heal(action.getPower());
                    enemyHealUsesLeft--;
                } else {
                    // cure esaurite: il nemico ripara all'attacco base
                    int damage = computeDamage(enemy.getStats().getAttack(), 0);
                    applyDamageToPlayer(damage);
                    lastEnemyAction = new CombatAction("fallback", "Attacca", CombatActionType.ATTACK, 0);
                }
            }
            default -> {}
        }

        if (!player.isAlive()) {
            lastResult = CombatResult.DEFEAT;
            return lastResult;
        }

        playerTurn = true;
        return CombatResult.ONGOING;
    }

    /**
     * Equipaggia un amuleto dall'inventario, applicando il bonus alle statistiche.
     * Se era già equipaggiato un amuleto, il bonus precedente viene prima rimosso.
     *
     * @param item l'oggetto da equipaggiare
     * @return {@code true} se l'oggetto è stato equipaggiato
     */
    public boolean equipItem(Item item) {
        if (item.getType() == ItemType.AMULET) {
            if (player.hasEquipped()) {
                player.getStats().removeEquipBonus(AMULET_DEF_BONUS, AMULET_HP_BONUS);
            }
            player.equip(item);
            player.getStats().applyEquipBonus(AMULET_DEF_BONUS, AMULET_HP_BONUS);
            player.getInventory().removeItem(item);
            return true;
        }
        return false;
    }

    /**
     * Usa un oggetto consumabile dall'inventario applicandone l'effetto immediato.
     *
     * @param item l'oggetto da usare
     * @return messaggio descrittivo dell'effetto, stringa vuota se l'oggetto non è usabile
     */
    public String useItem(Item item) {
        if (item.getType() == ItemType.SCROLL) {
            temporaryAttackBonus = item.getValue();
            player.getInventory().removeItem(item);
            return "Pergamena usata — ATK +" + item.getValue() + " per questo turno!";
        }
        if (item.getType() == ItemType.TALISMAN) {
            damageReductionActive = true;
            player.getInventory().removeItem(item);
            return "Talismano attivato — il prossimo attacco nemico sarà dimezzato!";
        }
        return "";
    }

    /**
     * Calcola il danno effettivo aggiungendo un bonus temporaneo e una varianza casuale.
     * Il bonus temporaneo viene azzerato dopo l'uso.
     */
    private int computeDamage(int baseAttack, int actionPower) {
        int raw = baseAttack + actionPower + temporaryAttackBonus;
        temporaryAttackBonus = 0;
        int variance = (int) (Math.random() * 3);
        return Math.max(1, raw + variance);
    }

    /**
     * Applica il danno al giocatore, dimezzandolo se il talismano è attivo.
     * Azzera il flag di riduzione danno dopo l'uso.
     */
    private void applyDamageToPlayer(int damage) {
        if (damageReductionActive) {
            damage = Math.max(1, damage / 2);
            damageReductionActive = false;
        }
        player.takeDamage(damage);
    }

    /** Verifica e attiva l'enrage del boss se le condizioni sono soddisfatte. */
    private void checkBossEnrage() {
        if (enemy instanceof Boss boss) {
            boss.checkEnrage();
        }
    }

    /** Cerca la prima pozione disponibile nell'inventario del giocatore. */
    private Item findPotion() {
        return player.getInventory().getItems().stream()
                .filter(i -> i.getType() == ItemType.POTION)
                .findFirst()
                .orElse(null);
    }

    public Player getPlayer() { return player; }
    public Enemy getEnemy() { return enemy; }
    public boolean isPlayerTurn() { return playerTurn; }
    public CombatResult getLastResult() { return lastResult; }
    public int getSpecialUsesLeft() { return specialUsesLeft; }
    public int getMaxSpecialUses() { return maxSpecialUses; }
    public int getTemporaryAttackBonus() { return temporaryAttackBonus; }
    public boolean isDamageReductionActive() { return damageReductionActive; }
    /** @return il numero di cure ancora disponibili per il nemico nel combattimento corrente */
    public int getEnemyHealUsesLeft() { return enemyHealUsesLeft; }

    /** @return l'ultima azione eseguita dal nemico, o {@code null} se il nemico non ha ancora agito */
    public CombatAction getLastEnemyAction() { return lastEnemyAction; }
}
