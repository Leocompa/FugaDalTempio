package it.unicam.cs.mpgc.rpg118708.engine;

import it.unicam.cs.mpgc.rpg118708.model.Boss;
import it.unicam.cs.mpgc.rpg118708.model.CombatAction;
import it.unicam.cs.mpgc.rpg118708.model.CombatActionType;
import it.unicam.cs.mpgc.rpg118708.model.Enemy;
import it.unicam.cs.mpgc.rpg118708.model.Item;
import it.unicam.cs.mpgc.rpg118708.model.ItemType;
import it.unicam.cs.mpgc.rpg118708.model.Player;

public class CombatManager {

    private final Player player;
    private Enemy enemy;
    private boolean playerTurn;
    private CombatResult lastResult;

    public CombatManager(Player player) {
        this.player = player;
        this.playerTurn = true;
        this.lastResult = CombatResult.ONGOING;
    }

    public void startCombat(Enemy enemy) {
        this.enemy = enemy;
        this.playerTurn = true;
        this.lastResult = CombatResult.ONGOING;
    }

    public CombatResult executePlayerAction(CombatAction action) {
        if (!playerTurn || lastResult != CombatResult.ONGOING) return lastResult;

        switch (action.getType()) {
            case ATTACK -> {
                int damage = computeDamage(player.getStats().getAttack(), action.getPower());
                enemy.takeDamage(damage);
                checkBossEnrage();
            }
            case SPECIAL -> {
                int damage = computeDamage((int)(player.getStats().getAttack() * 1.5), action.getPower());
                enemy.takeDamage(damage);
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

    public CombatResult executeEnemyTurn() {
        if (playerTurn || lastResult != CombatResult.ONGOING) return lastResult;

        CombatAction action = enemy.chooseAction();

        switch (action.getType()) {
            case ATTACK -> {
                int damage = computeDamage(enemy.getStats().getAttack(), action.getPower());
                player.takeDamage(damage);
            }
            case SPECIAL -> {
                int damage = computeDamage(enemy.getStats().getAttack() * 2, action.getPower());
                player.takeDamage(damage);
            }
            case HEAL -> {
                enemy.heal(action.getPower());
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

    private int computeDamage(int baseAttack, int actionPower) {
        int raw = baseAttack + actionPower;
        int variance = (int) (Math.random() * 3);
        return Math.max(1, raw + variance);
    }

    private void checkBossEnrage() {
        if (enemy instanceof Boss boss) {
            boss.checkEnrage();
        }
    }

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
}