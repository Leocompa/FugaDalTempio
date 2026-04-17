package it.unicam.cs.mpgc.rpg118708.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Stats.
 */
class StatsTest {

    private Stats stats;

    @BeforeEach
    void setUp() {
        stats = new Stats(40, 8, 4, 1);
    }

    // ---- HP iniziali ----

    @Test
    void inizioConHpPieni() {
        assertEquals(40, stats.getCurrentHp());
        assertEquals(40, stats.getMaxHp());
    }

    // ---- takeDamage ----

    @Test
    void dannoVienRidottoDallaDifesa() {
        stats.takeDamage(10); // difesa 4 → danno effettivo 6
        assertEquals(34, stats.getCurrentHp());
    }

    @Test
    void danoInferioreAllaDifesaNonRiduceHp() {
        stats.takeDamage(3); // 3 < difesa 4 → danno effettivo 0
        assertEquals(40, stats.getCurrentHp());
    }

    @Test
    void hpNonScendeSottoZero() {
        stats.takeDamage(1000);
        assertEquals(0, stats.getCurrentHp());
    }

    @Test
    void isDeadDopoHpZero() {
        stats.takeDamage(1000);
        assertTrue(stats.isDead());
    }

    @Test
    void nonMortoConHpPositivi() {
        assertFalse(stats.isDead());
    }

    // ---- heal ----

    @Test
    void healRipristinaPuntiVita() {
        stats.takeDamage(10); // difesa 4 → danno effettivo 6 → hp=34
        stats.heal(4);        // hp=34+4=38
        assertEquals(38, stats.getCurrentHp());
    }

    @Test
    void healNonSuperaMaxHp() {
        stats.heal(100);
        assertEquals(40, stats.getCurrentHp());
    }

    @Test
    void healDopoMorteRipristina() {
        stats.takeDamage(1000);
        stats.heal(20);
        assertEquals(20, stats.getCurrentHp());
        assertFalse(stats.isDead());
    }

    // ---- gainXp / levelUp ----

    @Test
    void nessunLevelUpConXpInsufficienti() {
        assertFalse(stats.gainXp(10)); // soglia livello 1 = 20
        assertEquals(1, stats.getLevel());
    }

    @Test
    void levelUpConXpSufficienti() {
        assertTrue(stats.gainXp(20));
        assertEquals(2, stats.getLevel());
    }

    @Test
    void levelUpAumentaStatistiche() {
        int attackPre = stats.getAttack();
        int defensePre = stats.getDefense();
        stats.gainXp(20);
        assertEquals(attackPre + 2, stats.getAttack());
        assertEquals(defensePre + 1, stats.getDefense());
    }

    @Test
    void levelUpRipristinaHpPieni() {
        stats.takeDamage(10);
        stats.gainXp(20);
        assertEquals(stats.getMaxHp(), stats.getCurrentHp());
    }

    @Test
    void xpEcedentiPassanoAlLivelloSuccessivo() {
        stats.gainXp(25); // soglia=20, avanza a liv 2; xp residui = 5
        assertEquals(5, stats.getCurrentXp());
    }

    @Test
    void doppieLevelUpInUnaSolaChiamata() {
        // Liv1→soglia20, liv2→soglia40; 60 XP dovrebbero far salire a liv3
        stats.gainXp(60);
        assertEquals(3, stats.getLevel());
    }

    // ---- bonus equipaggiamento ----

    @Test
    void applyEquipBonusAumentaDifesaEMaxHp() {
        stats.applyEquipBonus(4, 10);
        assertEquals(8, stats.getDefense());
        assertEquals(50, stats.getMaxHp());
    }

    @Test
    void applyEquipBonusCuraHpCorrente() {
        stats.takeDamage(15); // hp= 40 - (15-4)= 29
        stats.applyEquipBonus(4, 10);
        // maxHp=50, currentHp = min(29+10, 50) = 39
        assertEquals(39, stats.getCurrentHp());
    }

    @Test
    void removeEquipBonusRipristinaValoriPrecedenti() {
        stats.applyEquipBonus(4, 10);
        stats.removeEquipBonus(4, 10);
        assertEquals(4, stats.getDefense());
        assertEquals(40, stats.getMaxHp());
    }

    @Test
    void removeEquipBonusNonPortaHpSottoMaxRidotto() {
        stats.applyEquipBonus(4, 10); // maxHp=50, currentHp=50
        stats.removeEquipBonus(4, 10); // maxHp=40, currentHp=min(50,40)=40
        assertEquals(40, stats.getCurrentHp());
    }
}
