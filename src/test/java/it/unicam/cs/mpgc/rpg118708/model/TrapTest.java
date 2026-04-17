package it.unicam.cs.mpgc.rpg118708.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Trap.
 */
class TrapTest {

    private Trap trappola;
    private Player player;

    @BeforeEach
    void setUp() {
        trappola = new Trap("t1", 10);
        player = new Player("Ladro");
        // Posiziona il giocatore sopra la trappola (default trapX=200, trapY=488)
        player.moveTo(200, 456); // py+ph (456+32=488) = trapY → overlapsY: 488>488 è false
    }

    @Test
    void attivaAllaCreazione() {
        assertTrue(trappola.isActive());
    }

    @Test
    void deactivateDisattivaTrappola() {
        trappola.deactivate();
        assertFalse(trappola.isActive());
    }

    @Test
    void trappolaDisattivaNonInfliggeDanno() {
        player.moveTo(200, 457); // in collisione
        trappola.deactivate();
        int hpPrima = player.getStats().getCurrentHp();
        trappola.trigger(player);
        assertEquals(hpPrima, player.getStats().getCurrentHp());
    }

    @Test
    void nessunDannoFuoriDallaHitbox() {
        player.moveTo(0, 0); // lontano dalla trappola
        int hpPrima = player.getStats().getCurrentHp();
        trappola.trigger(player);
        assertEquals(hpPrima, player.getStats().getCurrentHp());
    }

    @Test
    void dannoInflittoDentroLaHitbox() {
        // trapX=200, trapY=488; hitbox trappola: x[204..208], y[488..502]
        // player: px=205, py+32=489 → overlapsY: 489>488 e 457<502
        player.moveTo(205, 457);
        int hpPrima = player.getStats().getCurrentHp();
        trappola.trigger(player);
        assertTrue(player.getStats().getCurrentHp() < hpPrima);
    }

    @Test
    void cooldownImpedisceSecondoDannoImmediato() {
        player.moveTo(205, 457);
        trappola.trigger(player); // primo trigger → danno + cooldown
        int hpDopoPrimo = player.getStats().getCurrentHp();
        trappola.trigger(player); // secondo trigger immediato → bloccato da cooldown
        assertEquals(hpDopoPrimo, player.getStats().getCurrentHp());
    }

    @Test
    void valoriCorretti() {
        assertEquals("t1", trappola.getId());
        assertEquals(10, trappola.getDamage());
    }
}
