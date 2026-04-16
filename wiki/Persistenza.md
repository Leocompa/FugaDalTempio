# Organizzazione dei dati e persistenza

## Struttura dati in memoria

Lo stato della partita è organizzato in una gerarchia di oggetti:

```
GameManager
├── Player
│   ├── Stats  (HP, ATK, DEF, livello, XP)
│   └── Inventory  (lista di Item)
└── List<Zone>
    └── Zone
        └── List<Room>
            ├── List<Enemy>  (con Stats individuali)
            ├── List<Trap>
            ├── List<Item>
            └── List<NPC>
```

`GameManager` mantiene anche l'indice della zona corrente, l'indice della stanza corrente (delegato a `Zone`) e il contatore dei nemici sconfitti.

---

## Persistenza su file XML

La persistenza è gestita dall'interfaccia `GamePersistence`, implementata da `XmlGamePersistence`.

### Posizione dei file

I salvataggi vengono scritti nella cartella `saves/` nella directory di esecuzione:

```
saves/
├── save_1.xml
├── save_2.xml
└── save_3.xml
```

### Struttura del file XML

```xml
<savegame timestamp="16/04/2026 14:30">

  <player name="Ladro"
          hp="35" maxHp="50"
          attack="12" defense="5"
          level="2" xp="10" xpToNextLevel="40"/>

  <progress zoneIndex="0" roomIndex="2" enemiesDefeated="2"/>

  <zones>
    <zone id="zone1" completed="false">
      <room id="r1">
        <npc id="npc1" rewardGiven="true"/>
      </room>
      <room id="r2">
        <enemy id="e_r2" alive="false" hp="0"/>
      </room>
      <room id="r3">
        <enemy id="e_r3" alive="true" hp="20"/>
        <item id="potion2"/>
      </room>
      ...
    </zone>
  </zones>

  <inventory>
    <item id="potion1" name="Pozione" type="POTION" value="15"/>
    <item id="amulet1" name="Amuleto del Tempio" type="AMULET" value="0"/>
  </inventory>

</savegame>
```

### Cosa viene salvato

| Dato | Come viene salvato |
|---|---|
| Statistiche giocatore | Attributi dell'elemento `<player>` |
| Progressione (zona/stanza) | Attributi dell'elemento `<progress>` |
| Nemici sconfitti totali | Attributo `enemiesDefeated` in `<progress>` |
| Stato di ogni nemico | `alive` e `hp` attuali per ogni `<enemy>` |
| NPC (ricompensa già data) | Attributo `rewardGiven` per ogni `<npc>` |
| Oggetti rimasti nelle stanze | Solo l'id degli item ancora presenti |
| Inventario del giocatore | Id, nome, tipo e valore di ogni item |
| Timestamp | Attributo dell'elemento radice `<savegame>` |

### Caricamento

Al caricamento:
1. Viene creato un mondo fresco tramite `WorldFactory`
2. Viene costruita una mappa `id → Item` di tutti gli oggetti del mondo prima di modificare qualsiasi stanza
3. Lo stato salvato viene applicato sopra il mondo fresco: statistiche del giocatore, indici di progressione, stato di ogni nemico, NPC e oggetti delle stanze, inventario

Questo approccio garantisce che gli oggetti che non compaiono nel salvataggio (perché già raccolti) non vengano ripristinati.

---

## Informazioni degli slot

`XmlGamePersistence` espone `getSlotInfo(int slot)` che restituisce un oggetto `SlotInfo` (nome, livello, numero stanza, timestamp) senza caricare l'intera partita. Viene usato da `SaveSlotScene` per mostrare il riepilogo di ogni slot.
