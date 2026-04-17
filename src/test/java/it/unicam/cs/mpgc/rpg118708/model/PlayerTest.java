package it.unicam.cs.mpgc.rpg118708.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Player.
 */
class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("Ladro");
    }

    @Test
    void isAliveAllaCreazione() {
        assertTrue(player.isAlive());
    }

    @Test
    void takeDamageDelegaAStats() {
        int hpPrima = player.getStats().getCurrentHp();
        player.takeDamage(20);
        assertTrue(player.getStats().getCurrentHp() < hpPrima);
    }

    @Test
    void isAliveDopoHpAzzerati() {
        player.takeDamage(1000);
        assertFalse(player.isAlive());
    }

    @Test
    void healDelegaAStats() {
        player.takeDamage(20);
        int hpDopoTakeDamage = player.getStats().getCurrentHp();
        player.heal(10);
        assertTrue(player.getStats().getCurrentHp() > hpDopoTakeDamage);
    }

    @Test
    void equipAmuletSettiEquipment() {
        Item amuleto = new Amulet("a1", "Amuleto", 0);
        assertTrue(player.equip(amuleto));
        assertTrue(player.hasEquipped());
        assertEquals(amuleto, player.getEquippedItem());
    }

    @Test
    void equipNonAmuletRestituisceFalse() {
        Item pozione = new Potion("p1", "Pozione", 15);
        assertFalse(player.equip(pozione));
        assertFalse(player.hasEquipped());
    }

    @Test
    void unequipRimuoveOggetto() {
        Item amuleto = new Amulet("a1", "Amuleto", 0);
        player.equip(amuleto);
        player.unequip();
        assertFalse(player.hasEquipped());
        assertNull(player.getEquippedItem());
    }

    @Test
    void moveToCambiaCoordinate() {
        player.moveTo(100, 200);
        assertEquals(100, player.getX());
        assertEquals(200, player.getY());
    }

    @Test
    void gainXpDelegaAStats() {
        assertFalse(player.gainXp(5));
        assertTrue(player.gainXp(20)); // soglia liv 1 = 20
    }

    @Test
    void nomeCorretto() {
        assertEquals("Ladro", player.getName());
    }

    @Test
    void inventarioAccessibileEVuoto() {
        assertNotNull(player.getInventory());
        assertEquals(0, player.getInventory().size());
    }
}
