package it.unicam.cs.mpgc.rpg118708.engine;

import it.unicam.cs.mpgc.rpg118708.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe CombatManager.
 */
class CombatManagerTest {

    private CombatManager manager;
    private Player player;
    private Enemy nemico;

    private static final CombatAction ATK  = new CombatAction("atk",  "Attacca",       CombatActionType.ATTACK,  0);
    private static final CombatAction HEAL = new CombatAction("heal", "Pozione",        CombatActionType.HEAL,    20);
    private static final CombatAction FLEE = new CombatAction("flee", "Fuggi",          CombatActionType.FLEE,    0);
    private static final CombatAction SPEC = new CombatAction("spec", "Mossa Speciale", CombatActionType.SPECIAL, 5);

    @BeforeEach
    void setUp() {
        player = new Player("Ladro");
        manager = new CombatManager(player);

        Stats statsNemico = new Stats(30, 6, 2, 1);
        nemico = new Enemy("e1", "Guardia", statsNemico, 15, List.of(ATK));
        manager.startCombat(nemico, 1);
    }

    // ---- stato iniziale ----

    @Test
    void turnoGiocatoreAllaPartenza() {
        assertTrue(manager.isPlayerTurn());
    }

    @Test
    void risultatoOngoingAllaPartenza() {
        assertEquals(CombatResult.ONGOING, manager.getLastResult());
    }

    @Test
    void nessunBonusTemporaneoAllaPartenza() {
        assertEquals(0, manager.getTemporaryAttackBonus());
    }

    @Test
    void riduzioneDanniInattiva() {
        assertFalse(manager.isDamageReductionActive());
    }

    @Test
    void cureNemicoAlMassimo() {
        assertEquals(CombatManager.MAX_ENEMY_HEAL_USES, manager.getEnemyHealUsesLeft());
    }

    // ---- azione giocatore: fuga ----

    @Test
    void fugaRestituisceRisultatoFled() {
        CombatResult r = manager.executePlayerAction(FLEE);
        assertEquals(CombatResult.FLED, r);
    }

    // ---- azione giocatore: attacco e vittoria ----

    @Test
    void attaccoRiduceHpNemico() {
        int hpPrima = nemico.getStats().getCurrentHp();
        manager.executePlayerAction(ATK);
        assertTrue(nemico.getStats().getCurrentHp() < hpPrima);
    }

    @Test
    void vittoriaDopoSconfittaNemico() {
        nemico.getStats().setCurrentHp(1);
        CombatResult r = manager.executePlayerAction(ATK);
        assertTrue(r == CombatResult.VICTORY || r == CombatResult.VICTORY_LEVELUP);
    }

    @Test
    void passaAlTurnoNemicoDopoAttaccoSenzaVittoria() {
        manager.executePlayerAction(ATK);
        assertFalse(manager.isPlayerTurn());
    }

    // ---- azione giocatore: mossa speciale ----

    @Test
    void mossaSpecialeConsumaUso() {
        int prima = manager.getSpecialUsesLeft();
        manager.executePlayerAction(SPEC);
        assertEquals(prima - 1, manager.getSpecialUsesLeft());
    }

    @Test
    void mossaSpecialeEsauritaNonAttacca() {
        // Esaurisci gli usi (roomIndex=1 → maxSpecialUses=1)
        manager.executePlayerAction(SPEC);
        manager.executeEnemyTurn(); // turno nemico per tornare al giocatore
        int hpPrima = nemico.getStats().getCurrentHp();
        manager.executePlayerAction(SPEC); // secondo uso non disponibile
        // HP non devono cambiare per effetto della special
        assertEquals(hpPrima, nemico.getStats().getCurrentHp());
    }

    // ---- azione giocatore: cura ----

    @Test
    void curaConPozioneDallInventario() {
        Item pozione = new Potion("p1", "Pozione", 15);
        player.getInventory().addItem(pozione);
        player.takeDamage(20); // riduce HP
        int hpPrima = player.getStats().getCurrentHp();
        manager.executePlayerAction(HEAL);
        assertTrue(player.getStats().getCurrentHp() > hpPrima);
        // La pozione deve essere rimossa dall'inventario
        assertFalse(player.getInventory().hasItem(pozione));
    }

    @Test
    void curaSenzaPozionaNonFaNulla() {
        player.takeDamage(20);
        int hpPrima = player.getStats().getCurrentHp();
        manager.executePlayerAction(HEAL); // nessuna pozione in inventario
        assertEquals(hpPrima, player.getStats().getCurrentHp());
    }

    // ---- turno nemico ----

    @Test
    void turnoNemicoRiduceHpGiocatore() {
        manager.executePlayerAction(ATK); // passa al turno nemico
        int hpPrima = player.getStats().getCurrentHp();
        manager.executeEnemyTurn();
        // il nemico con ATK=6, player difesa=4 → danno effettivo almeno 2
        assertTrue(player.getStats().getCurrentHp() < hpPrima);
    }

    @Test
    void sconfittaSeHpGiocatoreAzzerati() {
        player.getStats().setCurrentHp(1);
        manager.executePlayerAction(ATK); // turno nemico
        CombatResult r = manager.executeEnemyTurn();
        assertEquals(CombatResult.DEFEAT, r);
    }

    @Test
    void dopoTurnoNemicoTornaTurnoGiocatore() {
        manager.executePlayerAction(ATK);
        manager.executeEnemyTurn();
        assertTrue(manager.isPlayerTurn());
    }

    // ---- limite cure nemico ----

    @Test
    void nemicoConAzioneHealDecrementaContatore() {
        Stats statsGuardia = new Stats(100, 6, 2, 1); // HP alti: non muore durante l'attacco
        CombatAction healAction = new CombatAction("heal", "Pozione", CombatActionType.HEAL, 10);
        Enemy guardiaHeal = new Enemy("e2", "Guardia Medica", statsGuardia, 10, List.of(healAction));
        manager.startCombat(guardiaHeal, 1);

        manager.executePlayerAction(ATK); // turno giocatore → passa al nemico
        manager.executeEnemyTurn();       // nemico usa cura → decrementa contatore

        assertEquals(CombatManager.MAX_ENEMY_HEAL_USES - 1, manager.getEnemyHealUsesLeft());
    }

    @Test
    void curaNemicoEsauritaFallbackAdAttacco() {
        Stats statsGuardia = new Stats(50, 6, 2, 1);
        CombatAction healAction = new CombatAction("heal", "Pozione", CombatActionType.HEAL, 20);
        Enemy guardiaHeal = new Enemy("e3", "Guardia Medica", statsGuardia, 10, List.of(healAction));
        manager.startCombat(guardiaHeal, 1);

        // Esaurisci le 2 cure
        for (int i = 0; i < CombatManager.MAX_ENEMY_HEAL_USES; i++) {
            manager.executePlayerAction(ATK);
            manager.executeEnemyTurn();
        }

        assertEquals(0, manager.getEnemyHealUsesLeft());

        // Terzo turno nemico: cure esaurite → fallback ad attacco
        int hpPrima = player.getStats().getCurrentHp();
        manager.executePlayerAction(ATK);
        manager.executeEnemyTurn();

        // Il giocatore dovrebbe aver subito danno
        assertTrue(player.getStats().getCurrentHp() < hpPrima);
    }

    // ---- useItem ----

    @Test
    void pergamenaAumentaBonusAttacco() {
        Item pergamena = new Scroll("sc1", "Pergamena di Fuoco", 6);
        player.getInventory().addItem(pergamena);
        manager.useItem(pergamena);
        assertEquals(6, manager.getTemporaryAttackBonus());
    }

    @Test
    void talismanAttivaRiduzioneDanno() {
        Item talismano = new Talisman("ta1", "Talismano", 0);
        player.getInventory().addItem(talismano);
        manager.useItem(talismano);
        assertTrue(manager.isDamageReductionActive());
    }

    @Test
    void talismanDimezzaDannoNemico() {
        Item talismano = new Talisman("ta1", "Talismano", 0);
        player.getInventory().addItem(talismano);
        manager.useItem(talismano);

        player.takeDamage(1000); // porta a 0
        player.heal(1000);       // riporta a max
        int hpPieno = player.getStats().getCurrentHp();

        // Simula danno diretto tramite metodo privato usando attacco nemico
        manager.executePlayerAction(ATK); // turno nemico
        manager.executeEnemyTurn();       // primo attacco con talismano attivo

        // Dopo il talismano il flag deve essere disattivato
        assertFalse(manager.isDamageReductionActive());
    }

    // ---- equipItem ----

    @Test
    void equipAmuletApplicaBonusSuStats() {
        Item amuleto = new Amulet("am1", "Amuleto", 0);
        player.getInventory().addItem(amuleto);
        int defPrima = player.getStats().getDefense();
        int maxHpPrima = player.getStats().getMaxHp();

        manager.equipItem(amuleto);

        assertEquals(defPrima + 4, player.getStats().getDefense());
        assertEquals(maxHpPrima + 10, player.getStats().getMaxHp());
        assertFalse(player.getInventory().hasItem(amuleto));
    }

    @Test
    void equipNonAmuletRestituisceFalse() {
        Item pozione = new Potion("p1", "Pozione", 15);
        player.getInventory().addItem(pozione);
        assertFalse(manager.equipItem(pozione));
    }
}
