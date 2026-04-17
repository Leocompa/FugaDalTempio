package it.unicam.cs.mpgc.rpg118708.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Zone.
 */
class ZoneTest {

    private Zone zona;
    private Room stanza1;
    private Room stanza2;

    @BeforeEach
    void setUp() {
        zona = new Zone("z1", "Tempio");
        stanza1 = new Room("r1", "Entrata");
        stanza2 = new Room("r2", "Corridoio");
        zona.addRoom(stanza1);
        zona.addRoom(stanza2);
    }

    @Test
    void primaStanzaCorrenteAllaCreazione() {
        assertEquals(stanza1, zona.getCurrentRoom());
    }

    @Test
    void advanceRoomPassaAllaStanzaSuccessiva() {
        assertTrue(zona.advanceRoom());
        assertEquals(stanza2, zona.getCurrentRoom());
    }

    @Test
    void advanceRoomFalsoSullUltimaStanza() {
        zona.advanceRoom(); // ora siamo su stanza2
        assertFalse(zona.advanceRoom());
        assertEquals(stanza2, zona.getCurrentRoom());
    }

    @Test
    void goBackTornaAllaStanzaPrecedente() {
        zona.advanceRoom();
        assertTrue(zona.goBack());
        assertEquals(stanza1, zona.getCurrentRoom());
    }

    @Test
    void goBackFalsoSullaPrimaStanza() {
        assertFalse(zona.goBack());
    }

    @Test
    void isLastRoomFalsoSullaPrima() {
        assertFalse(zona.isLastRoom());
    }

    @Test
    void isLastRoomVeroSullUltima() {
        zona.advanceRoom();
        assertTrue(zona.isLastRoom());
    }

    @Test
    void allRoomsClearedVeroConStanzeVuote() {
        assertTrue(zona.allRoomsCleared());
    }

    @Test
    void allRoomsClearedFalsoConNemicoVivo() {
        Stats stats = new Stats(30, 8, 2, 1);
        CombatAction atk = new CombatAction("atk", "Attacca", CombatActionType.ATTACK, 0);
        stanza1.addEnemy(new Enemy("e1", "Guardia", stats, 10, List.of(atk)));
        assertFalse(zona.allRoomsCleared());
    }

    @Test
    void nonCompletaAllaCreazione() {
        assertFalse(zona.isCompleted());
    }

    @Test
    void setCompletedFunziona() {
        zona.setCompleted(true);
        assertTrue(zona.isCompleted());
    }

    @Test
    void idENomeCorretti() {
        assertEquals("z1", zona.getId());
        assertEquals("Tempio", zona.getName());
    }
}
