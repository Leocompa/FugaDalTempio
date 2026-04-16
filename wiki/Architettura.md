# Architettura: responsabilità, classi e interfacce

## Struttura dei package

```
it.unicam.cs.mpgc.rpg118708
├── Main.java
├── controller/
├── engine/
├── model/
├── persistence/
└── view/
    ├── combat/
    ├── exploration/
    └── menu/
```

La separazione in package rispecchia la separazione delle responsabilità:

| Package | Responsabilità |
|---|---|
| `model` | Dati puri del dominio di gioco (nessuna logica applicativa) |
| `engine` | Logica di gioco (combattimento, stato partita) senza dipendenze dalla UI |
| `controller` | Coordinamento tra engine e view; costruzione del mondo |
| `persistence` | Salvataggio e caricamento dello stato di gioco |
| `view/combat` | Scena del combattimento a turni |
| `view/exploration` | Scena di esplorazione con game loop |
| `view/menu` | Schermate di menu, salvataggio e vittoria |

---

## Responsabilità individuate

### Responsabilità del dominio (model)

- Rappresentare le entità del gioco (giocatore, nemici, stanze, oggetti)
- Mantenere i dati di stato senza applicare regole di gioco complesse
- Garantire che i dati siano coerenti (es. HP non negativi, inventario con capienza massima)

### Responsabilità della logica (engine)

- Gestire lo stato globale della partita (quale stanza, quale stato)
- Eseguire il combattimento a turni e calcolarne l'esito
- Non dipendere in alcun modo dalla UI

### Responsabilità del coordinamento (controller)

- Tradurre gli eventi della UI in operazioni sull'engine
- Gestire le transizioni tra le scene
- Costruire il mondo di gioco tramite `WorldFactory`

### Responsabilità della persistenza

- Serializzare e deserializzare lo stato di gioco
- Non contenere logica di gioco

### Responsabilità della presentazione (view)

- Renderizzare lo stato corrente del gioco
- Catturare l'input dell'utente
- Non contenere logica di gioco

---

## Classi e interfacce

### `Main`
**Composition root** dell'applicazione. È l'unico posto in cui le implementazioni concrete vengono istanziate e iniettate. Tutto il resto del codice dipende da interfacce.

---

### Package `model`

#### `Player`
Rappresenta il personaggio del giocatore. Aggrega `Stats` (statistiche), `Inventory` (oggetti) e la posizione nella scena. Delega i calcoli numerici a `Stats`.

#### `Enemy`
Nemico con un set di `CombatAction` tra cui sceglie casualmente ogni turno. Contiene le statistiche e la ricompensa XP.

#### `Boss` *(estende Enemy)*
Aggiunge il meccanismo di **enrage**: quando gli HP scendono sotto il 50%, l'attacco aumenta di `ENRAGE_ATTACK_BONUS`. L'enrage si attiva una sola volta per combattimento.

#### `Stats`
Gestisce tutti i valori numerici di un'entità: HP, attacco, difesa, livello, XP. Calcola il danno effettivo tenendo conto della difesa, gestisce il level-up e i bonus da equipaggiamento.

#### `Inventory`
Gestisce la collezione di oggetti del giocatore con capienza massima. Espone la lista in sola lettura; le modifiche passano obbligatoriamente per `addItem` e `removeItem`.

#### `Item`
Oggetto raccoglibile o utilizzabile. Immutabile dopo la creazione. Il tipo (`ItemType`) determina il comportamento, gestito da `CombatManager`.

#### `ItemType` *(enum)*
`POTION`, `AMULET`, `SCROLL`, `TALISMAN`

#### `Room`
Contenitore passivo di entità: nemici, trappole, oggetti, NPC. Non contiene logica di gioco. Le liste interne sono esposte in sola lettura.

#### `Zone`
Sequenza ordinata di stanze con un indice corrente. Gestisce la navigazione avanti/indietro tra le stanze.

#### `NPC`
Personaggio non giocante con dialogo fisso e ricompensa consegnata una sola volta.

#### `Trap`
Trappola con collisione AABB e cooldown. Infligge danno al giocatore quando le hitbox si sovrappongono.

#### `CombatAction`
Valore immutabile che descrive un'azione di combattimento: tipo, label e potenza aggiuntiva.

#### `CombatActionType` *(enum)*
`ATTACK`, `SPECIAL`, `HEAL`, `FLEE`

#### `Direction` *(enum)*
`LEFT`, `RIGHT` — direzione orizzontale del personaggio per il rendering.

---

### Package `engine`

#### `GameManager`
Gestisce lo stato globale della partita: zona e stanza corrente, transizioni di stato, raccolta oggetti, respawn. Non conosce la UI.

#### `CombatManager`
Esegue la logica del combattimento a turni: calcola i danni con varianza, gestisce gli effetti degli oggetti consumabili (pergamena, talismano), determina l'esito. Restituisce `CombatResult`; non interagisce con la UI.

#### `GameState` *(enum)*
`EXPLORING`, `COMBAT`, `DIALOGUE`, `GAME_OVER`, `VICTORY`

#### `CombatResult` *(enum)*
`ONGOING`, `VICTORY`, `VICTORY_LEVELUP`, `DEFEAT`, `FLED`

---

### Package `controller`

#### `GameController`
Controller principale. Coordina le transizioni tra le scene (menu → esplorazione → combattimento → salvataggio). Dipende dalle interfacce `GamePersistence` e `WorldFactory`, non dalle implementazioni concrete (DIP).

#### `CombatController`
Fa da ponte tra `CombatScene` e `CombatManager`. Traduce il tipo di azione scelto dall'utente in una `CombatAction` concreta. Notifica il `GameController` degli esiti rilevanti tramite callback `Runnable`.

#### `WorldFactory` *(interfaccia)*
Contratto per la costruzione del mondo di gioco. Permette di sostituire la generazione delle zone senza modificare `GameController`.

#### `WorldBuilder` *(implementa WorldFactory)*
Costruisce il mondo in modo hardcoded: zone, stanze, nemici, NPC, trappole, oggetti. Applica lo scaling progressivo ai nemici in base all'indice globale della stanza.

---

### Package `persistence`

#### `GamePersistence` *(interfaccia)*
Contratto per salvataggio e caricamento. Permette di sostituire l'implementazione (XML, JSON, database) senza modificare la logica di gioco.

#### `XmlGamePersistence` *(implementa GamePersistence)*
Implementazione della persistenza tramite file XML su 3 slot. Gestisce save, load, lettura delle informazioni degli slot e verifica dell'esistenza dei salvataggi.

#### `SlotInfo`
Oggetto immutabile con le informazioni sintetiche di uno slot (nome, livello, stanza, timestamp) da mostrare nella schermata di selezione.

---

### Package `view`

Il package `view` è suddiviso in tre sotto-package in base alla responsabilità della scena.

#### `view.exploration`

##### `ExplorationScene`
Scena di esplorazione con game loop (`AnimationTimer`). Gestisce esclusivamente: input da tastiera, fisica del personaggio (gravità, salto, vincolo al suolo), collisioni con trappole e nemici, interazioni con oggetti e NPC. Notifica il `GameController` tramite callback (`onEnterCombat`, `onZoneComplete`, `onSave`, `onExit`). Tutto il rendering è delegato a `ExplorationRenderer`.

##### `ExplorationRenderer`
Responsabile di tutto il disegno su `Canvas` durante l'esplorazione. Riceve lo stato necessario a ogni frame tramite `render(frame, nearExit, nearEntrance, onGround, keysPressed)`. Include: griglia di sfondo, terreno, rendering della stanza (trappole, oggetti, NPC, nemici con barra HP, porte), sprite del giocatore con animazione di camminata (braccia e gambe oscillanti), posa di salto, idle bobbing e ombra a terra, overlay di game-over e dialogo, HUD superiore (HP, XP, livello, zona, stanza) e HUD inferiore contestuale (`[E]` e `[R]` solo quando rilevanti).

#### `view.combat`

##### `CombatScene`
Scena del combattimento a turni. Costruisce l'interfaccia (pulsanti, barre HP, log) e gestisce le interazioni utente. Delega la logica di gioco a `CombatController`, il disegno degli sprite a `CombatSpriteRenderer` e le schermate di fine combattimento a `CombatVictoryScreen` / `CombatDefeatScreen`. Il metodo `refresh()` aggiorna le singole sezioni tramite metodi privati dedicati.

##### `CombatSpriteRenderer`
Renderer degli sprite del combattimento su canvas JavaFX. Contiene esclusivamente la logica di disegno del giocatore e del nemico (metodi statici). Distingue automaticamente tra guardia normale e boss applicando schemi visivi diversi.

##### `CombatVictoryScreen`
Schermata mostrata al termine di un combattimento vinto. Riceve giocatore, nemico e una callback per continuare. Non contiene logica di gioco.

##### `CombatDefeatScreen`
Schermata mostrata al termine di un combattimento perso. Offre due opzioni (ricominciare o caricare) tramite callback. Non contiene logica di gioco.

#### `view.menu`

##### `StartScene`
Schermata iniziale con campo nome, pulsanti nuova partita, carica e esci.

##### `SaveSlotScene`
Schermata di selezione slot riutilizzabile in modalità salvataggio e caricamento. Dipende dall'interfaccia `GamePersistence`. In modalità salvataggio, se lo slot è già occupato mostra un dialogo di conferma prima di sovrascrivere i dati esistenti.

##### `VictoryScene`
Schermata di vittoria finale. Riceve i dati del giocatore e una callback per tornare al menu. Non contiene logica di gioco.
