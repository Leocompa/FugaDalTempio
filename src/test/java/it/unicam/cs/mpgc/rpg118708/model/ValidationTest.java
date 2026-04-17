package it.unicam.cs.mpgc.rpg118708.model;

import it.unicam.cs.mpgc.rpg118708.model.exception.InvalidNameException;
import it.unicam.cs.mpgc.rpg118708.model.exception.InvalidStatsException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica che i costruttori delle entità del dominio lancino le eccezioni
 * corrette in presenza di argomenti non validi (null o vuoti).
 */
class ValidationTest {

    // ---- Player ----

    @Test
    void playerNomeNullLanciaEccezione() {
        assertThrows(InvalidNameException.class, () -> new Player(null));
    }

    @Test
    void playerNomeVuotoLanciaEccezione() {
        assertThrows(InvalidNameException.class, () -> new Player("   "));
    }

    // ---- Stats ----

    @Test
    void statsMaxHpZeroLanciaEccezione() {
        assertThrows(InvalidStatsException.class, () -> new Stats(0, 5, 2, 1));
    }

    @Test
    void statsAttackNegativoLanciaEccezione() {
        assertThrows(InvalidStatsException.class, () -> new Stats(20, -1, 2, 1));
    }

    @Test
    void statsDefenseNegativaLanciaEccezione() {
        assertThrows(InvalidStatsException.class, () -> new Stats(20, 5, -1, 1));
    }

    @Test
    void statsLevelZeroLanciaEccezione() {
        assertThrows(InvalidStatsException.class, () -> new Stats(20, 5, 2, 0));
    }

    // ---- Item ----

    @Test
    void itemIdNullLanciaEccezione() {
        assertThrows(InvalidNameException.class, () -> new Potion(null, "Pozione", 10));
    }

    @Test
    void itemNomeVuotoLanciaEccezione() {
        assertThrows(InvalidNameException.class, () -> new Potion("p1", "", 10));
    }

    // ---- Enemy ----

    @Test
    void enemyIdNullLanciaEccezione() {
        Stats s = new Stats(20, 5, 2, 1);
        CombatAction a = new CombatAction("atk", "Attacca", CombatActionType.ATTACK, 0);
        assertThrows(InvalidNameException.class, () -> new Enemy(null, "Guardia", s, 10, List.of(a)));
    }

    @Test
    void enemyStatsNullLanciaEccezione() {
        CombatAction a = new CombatAction("atk", "Attacca", CombatActionType.ATTACK, 0);
        assertThrows(InvalidStatsException.class, () -> new Enemy("e1", "Guardia", null, 10, List.of(a)));
    }

    // ---- NPC ----

    @Test
    void npcNomeVuotoLanciaEccezione() {
        assertThrows(InvalidNameException.class, () -> new NPC("n1", "", "Ciao!"));
    }

    @Test
    void npcDialogoNullLanciaEccezione() {
        assertThrows(InvalidNameException.class, () -> new NPC("n1", "Saggio", null));
    }

    // ---- Room ----

    @Test
    void roomIdNullLanciaEccezione() {
        assertThrows(InvalidNameException.class, () -> new Room(null, "Corridoio"));
    }

    @Test
    void roomNomeVuotoLanciaEccezione() {
        assertThrows(InvalidNameException.class, () -> new Room("r1", "  "));
    }

    // ---- Zone ----

    @Test
    void zoneIdNullLanciaEccezione() {
        assertThrows(InvalidNameException.class, () -> new Zone(null, "Tempio"));
    }

    @Test
    void zoneNomeVuotoLanciaEccezione() {
        assertThrows(InvalidNameException.class, () -> new Zone("z1", ""));
    }
}
