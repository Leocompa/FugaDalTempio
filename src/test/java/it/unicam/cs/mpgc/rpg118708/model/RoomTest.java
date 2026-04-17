package it.unicam.cs.mpgc.rpg118708.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Room.
 */
class RoomTest {

    private Room stanza;
    private Enemy nemico;

    @BeforeEach
    void setUp() {
        stanza = new Room("r1", "Corridoio");
        Stats stats = new Stats(30, 8, 2, 1);
        CombatAction atk = new CombatAction("atk", "Attacca", CombatActionType.ATTACK, 0);
        nemico = new Enemy("e1", "Guardia", stats, 10, List.of(atk));
    }

    @Test
    void stanzaVuotaSempreRipulita() {
        assertTrue(stanza.isCleared());
    }

    @Test
    void stanzaConNemicoVivoNonRipulita() {
        stanza.addEnemy(nemico);
        assertFalse(stanza.isCleared());
    }

    @Test
    void stanzaRipulitaDopoSconfittaNemico() {
        stanza.addEnemy(nemico);
        nemico.takeDamage(1000);
        assertTrue(stanza.isCleared());
    }

    @Test
    void addItemERemoveItem() {
        Item pozione = new Potion("p1", "Pozione", 15);
        stanza.addItem(pozione);
        assertTrue(stanza.getItems().contains(pozione));
        stanza.removeItem(pozione);
        assertFalse(stanza.getItems().contains(pozione));
    }

    @Test
    void clearItemsRimuoveTutto() {
        stanza.addItem(new Potion("p1", "Pozione", 15));
        stanza.addItem(new Potion("p2", "Pozione2", 15));
        stanza.clearItems();
        assertTrue(stanza.getItems().isEmpty());
    }

    @Test
    void listeRestituisconoVistaImmutabile() {
        assertThrows(UnsupportedOperationException.class,
                () -> stanza.getEnemies().add(nemico));
        assertThrows(UnsupportedOperationException.class,
                () -> stanza.getItems().add(new Potion("x", "X", 0)));
    }

    @Test
    void addNpcERecuperabile() {
        NPC npc = new NPC("npc1", "Saggio", "Ciao!");
        stanza.addNpc(npc);
        assertTrue(stanza.getNpcs().contains(npc));
    }

    @Test
    void addTrapERecuperabile() {
        Trap trappola = new Trap("t1", 5);
        stanza.addTrap(trappola);
        assertTrue(stanza.getTraps().contains(trappola));
    }

    @Test
    void idENomeCorretti() {
        assertEquals("r1", stanza.getId());
        assertEquals("Corridoio", stanza.getName());
    }
}
