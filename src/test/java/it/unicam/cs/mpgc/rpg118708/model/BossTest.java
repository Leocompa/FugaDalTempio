package it.unicam.cs.mpgc.rpg118708.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Boss (meccanismo di enrage).
 */
class BossTest {

    private Boss boss;

    @BeforeEach
    void setUp() {
        Stats stats = new Stats(100, 12, 5, 3);
        CombatAction attacco = new CombatAction("atk", "Attacca", CombatActionType.ATTACK, 0);
        boss = new Boss("boss1", "Re dei Guardiani", "Signore del Caos", stats, 50, List.of(attacco));
    }

    @Test
    void nonEnragedAllaCreazione() {
        assertFalse(boss.isEnraged());
    }

    @Test
    void checkEnrageNonSiAttivaConHpAlti() {
        // HP a 100%, non sotto la metà
        assertFalse(boss.checkEnrage());
        assertFalse(boss.isEnraged());
    }

    @Test
    void checkEnrageSiAttivaQuandoHpSottoMeta() {
        boss.takeDamage(1000); // porta HP a 0 (max 100, difesa 5: serve almeno 105 lordi)
        // forziamo direttamente
        boss.getStats().setCurrentHp(49); // sotto 50% di 100
        assertTrue(boss.checkEnrage());
        assertTrue(boss.isEnraged());
    }

    @Test
    void enrageAumentaAttacco() {
        int attackPre = boss.getStats().getAttack();
        boss.getStats().setCurrentHp(49);
        boss.checkEnrage();
        assertEquals(attackPre + 5, boss.getStats().getAttack());
    }

    @Test
    void enrageAvvieneUnaSolaVolta() {
        boss.getStats().setCurrentHp(49);
        boss.checkEnrage();
        int attackDopoEnrage = boss.getStats().getAttack();
        // seconda chiamata non deve aumentare ulteriormente
        assertFalse(boss.checkEnrage());
        assertEquals(attackDopoEnrage, boss.getStats().getAttack());
    }

    @Test
    void checkEnrageSiAttivaEsattamenteAllaMeta() {
        // La condizione è currentHp <= maxHp/2, quindi a esattamente 50% si attiva
        boss.getStats().setCurrentHp(50);
        assertTrue(boss.checkEnrage());
    }

    @Test
    void titoloCorreetto() {
        assertEquals("Signore del Caos", boss.getTitle());
    }
}
