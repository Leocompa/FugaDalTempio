package it.unicam.cs.mpgc.rpg118708.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe NPC.
 */
class NPCTest {

    @Test
    void hasRewardVeroSePremioDisponibile() {
        Item premio = new Potion("p1", "Pozione", 15);
        NPC npc = new NPC("npc1", "Saggio", "Benvenuto!", premio);
        assertTrue(npc.hasReward());
    }

    @Test
    void hasRewardFalsoSenzaPremio() {
        NPC npc = new NPC("npc2", "Mercante", "Niente da vendere.");
        assertFalse(npc.hasReward());
    }

    @Test
    void collectRewardRestituiscePremioPrimaVolta() {
        Item premio = new Potion("p1", "Pozione", 15);
        NPC npc = new NPC("npc1", "Saggio", "Eccoti!", premio);
        assertEquals(premio, npc.collectReward());
    }

    @Test
    void collectRewardRestituisceNullSecondaVolta() {
        Item premio = new Potion("p1", "Pozione", 15);
        NPC npc = new NPC("npc1", "Saggio", "Eccoti!", premio);
        npc.collectReward();
        assertNull(npc.collectReward());
    }

    @Test
    void hasRewardFalsoDopoConsegna() {
        Item premio = new Potion("p1", "Pozione", 15);
        NPC npc = new NPC("npc1", "Saggio", "Eccoti!", premio);
        npc.collectReward();
        assertFalse(npc.hasReward());
    }

    @Test
    void collectRewardSenzaPremioDaNull() {
        NPC npc = new NPC("npc2", "Mercante", "Non ho niente.");
        assertNull(npc.collectReward());
    }

    @Test
    void dialogoCorretto() {
        NPC npc = new NPC("npc3", "Guardiano", "Fermati!");
        assertEquals("Fermati!", npc.getDialogue());
    }
}
