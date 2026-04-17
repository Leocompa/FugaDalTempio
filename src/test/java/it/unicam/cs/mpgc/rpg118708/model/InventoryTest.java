package it.unicam.cs.mpgc.rpg118708.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Inventory.
 */
class InventoryTest {

    private Inventory inventory;
    private Item pozione;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        pozione = new Potion("p1", "Pozione", 15);
    }

    @Test
    void inventarioVuotoAllaCreazione() {
        assertEquals(0, inventory.size());
        assertFalse(inventory.isFull());
    }

    @Test
    void addItemRestituisceTrueEAggiungeOggetto() {
        assertTrue(inventory.addItem(pozione));
        assertEquals(1, inventory.size());
        assertTrue(inventory.hasItem(pozione));
    }

    @Test
    void removeItemRimoveOggetto() {
        inventory.addItem(pozione);
        assertTrue(inventory.removeItem(pozione));
        assertEquals(0, inventory.size());
        assertFalse(inventory.hasItem(pozione));
    }

    @Test
    void removeItemNonPresenteRestituisceFalse() {
        assertFalse(inventory.removeItem(pozione));
    }

    @Test
    void isFullDopoDecimoOggetto() {
        for (int i = 0; i < 10; i++) {
            inventory.addItem(new Potion("i" + i, "Item" + i, 5));
        }
        assertTrue(inventory.isFull());
    }

    @Test
    void addItemRestituisceFalseSeInventarioPieno() {
        for (int i = 0; i < 10; i++) {
            inventory.addItem(new Potion("i" + i, "Item" + i, 5));
        }
        Item extra = new Potion("extra", "Extra", 5);
        assertFalse(inventory.addItem(extra));
        assertEquals(10, inventory.size());
    }

    @Test
    void getItemsRestituisceVistaImmutabile() {
        inventory.addItem(pozione);
        assertThrows(UnsupportedOperationException.class,
                () -> inventory.getItems().add(pozione));
    }
}
