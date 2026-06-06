# Architettura: responsabilità, classi e interfacce

## Struttura dei package

```
src/
├── main/
│   ├── java/it/unicam/cs/mpgc/rpg118708/
│   │   ├── Main.java
│   │   ├── controller/
│   │   ├── engine/
│   │   ├── model/
│   │   │   └── exception/
│   │   ├── persistence/
│   │   └── view/
│   │       ├── combat/
│   │       ├── exploration/
│   │       └── menu/
│   └── resources/
│       └── game.css
└── test/
    └── java/it/unicam/cs/mpgc/rpg118708/
        ├── engine/
        └── model/
```

La separazione in package rispecchia la separazione delle responsabilità:

| Package | Responsabilità |
|---|---|
| `model` | Dati puri del dominio di gioco (nessuna logica applicativa) |
| `model.exception` | Eccezioni del dominio per valori non validi al boundary di creazione |
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

#### `Combatant` *(interfaccia)*
Contratto comune a tutte le entità che partecipano al combattimento: `getName()`, `getStats()`, `isAlive()`, `takeDamage(int)`, `heal(int)`. Permette a `CombatManager` di trattare giocatore e nemici in modo uniforme senza conoscere le implementazioni concrete.

#### `Player` *(implementa Combatant)*
Rappresenta il personaggio del giocatore. Aggrega `Stats` (statistiche), `Inventory` (oggetti) e la posizione nella scena. Delega i calcoli numerici a `Stats`.

#### `Enemy` *(implementa Combatant)*
Nemico con un set di `CombatAction` tra cui sceglie casualmente ogni turno. Contiene le statistiche e la ricompensa XP. Espone il metodo hook `onDamageTaken()` (no-op per default), invocato da `CombatManager` dopo ogni danno: le sottoclassi possono sovrascriverlo per reagire senza che il motore debba fare type-checking.

#### `Boss` *(estende Enemy)*
Aggiunge il meccanismo di **enrage**: quando gli HP scendono sotto il 50%, l'attacco aumenta di `ENRAGE_ATTACK_BONUS`. L'enrage si attiva una sola volta per combattimento. Sovrascrive `onDamageTaken()` per richiamare `checkEnrage()` automaticamente, eliminando la necessità di `instanceof Boss` in `CombatManager`.

#### `Stats`
Gestisce tutti i valori numerici di un'entità: HP, attacco, difesa, livello, XP. Calcola il danno effettivo tenendo conto della difesa, gestisce il level-up e i bonus da equipaggiamento.

#### `Inventory`
Gestisce la collezione di oggetti del giocatore con capienza massima. Espone la lista in sola lettura; le modifiche passano obbligatoriamente per `addItem` e `removeItem`.

#### `Item` *(classe astratta)*
Classe base per tutti gli oggetti raccoglibili o utilizzabili. Immutabile dopo la creazione (id, nome, valore). Ogni sottoclasse concreta sovrascrive `getType()` e `applyInCombat(Player, CombatItemContext)`. Gli oggetti curativi sovrascrivono anche `isHealing()` restituendo `true`: `CombatManager` usa questo metodo per trovare oggetti da usare nell'azione HEAL, senza dipendere da `instanceof`. Il factory method statico `Item.create(id, name, ItemType, value)` è usato esclusivamente dal layer di persistenza per deserializzare oggetti da XML. Aggiungere un nuovo tipo di oggetto richiede solo una nuova sottoclasse, senza modificare il codice esistente (OCP).

#### `CombatItemContext` *(interfaccia)*
Contratto esposto agli oggetti durante il loro utilizzo in combattimento. Dichiara `addTemporaryAttackBonus(int)` e `activateDamageReduction()`. Permette alle sottoclassi di `Item` di applicare i propri effetti senza dipendere direttamente da `CombatManager` (DIP). `CombatManager` implementa questa interfaccia.

#### `Potion`, `Amulet`, `Scroll`, `Talisman` *(estendono Item)*
Implementazioni concrete di `Item`. Ogni classe dichiara il proprio `ItemType` tramite `getType()` e implementa il metodo astratto `applyInCombat(Player, CombatItemContext)`, che incapsula il comportamento specifico in combattimento:

| Classe | Effetto di `applyInCombat` |
|---|---|
| `Potion` | Cura il giocatore e si rimuove dall'inventario |
| `Scroll` | Aggiunge un bonus temporaneo all'attacco via `CombatItemContext` e si rimuove |
| `Talisman` | Attiva la riduzione del danno via `CombatItemContext` e si rimuove |
| `Amulet` | Non utilizzato (l'equip avviene tramite flusso separato); restituisce stringa vuota |

`Amulet` espone le costanti pubbliche `DEF_BONUS` e `HP_BONUS` usate da `CombatManager` all'equip.

#### `ItemType` *(enum)*
`POTION`, `AMULET`, `SCROLL`, `TALISMAN` — usato principalmente per la serializzazione XML tramite `Item.create()`.

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

### Package `model.exception`

#### `InvalidNameException` *(extends RuntimeException)*
Eccezione del dominio lanciata quando un identificatore o un nome obbligatorio è `null` o vuoto. Viene sollevata nei costruttori di `Player`, `Enemy`, `Item`, `Room`, `Zone` e `NPC`.

#### `InvalidStatsException` *(extends RuntimeException)*
Eccezione del dominio lanciata quando i valori delle statistiche violano i vincoli del dominio (es. `maxHp ≤ 0`, `attack < 0`). Viene sollevata nel costruttore di `Stats` e in `Enemy` quando `stats` è `null`.

---

### Package `engine`

#### `GameManager`
Gestisce lo stato globale della partita: zona e stanza corrente, transizioni di stato, raccolta oggetti, respawn. Non conosce la UI.

#### `CombatManager` *(implementa CombatItemContext)*
Esegue la logica del combattimento a turni: calcola i danni con varianza, determina l'esito. Implementa `CombatItemContext` esponendo `addTemporaryAttackBonus` e `activateDamageReduction`: gli oggetti applicano i propri effetti chiamando questi metodi, senza che il manager debba fare type-checking. Restituisce `CombatResult`; non interagisce con la UI.

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
Facade pubblica che coordina `XmlSaveWriter` e `XmlSaveReader`. Gestisce gli slot, verifica l'esistenza dei file e traduce le eccezioni in messaggi di errore. Non contiene logica di serializzazione.

#### `XmlSaveWriter`
Serializza lo stato del gioco in un Document XML e scrive il file su disco. Responsabilità unica: costruire la struttura DOM a partire dal `GameManager` e salvarla.

#### `XmlSaveReader`
Deserializza un file XML e applica lo stato salvato al `GameManager`. Responsabilità unica: leggere il DOM e ripristinare statistiche, progressione, stanze, NPC e inventario.

#### `SlotInfo`
Oggetto immutabile con le informazioni sintetiche di uno slot (nome, livello, stanza, timestamp) da mostrare nella schermata di selezione.

---

### Package `view`

Il package `view` è suddiviso in tre sotto-package in base alla responsabilità della scena. Tutte le scene pubbliche implementano l'interfaccia `GameScene`.

#### `GameScene` *(interfaccia)*
Contratto comune a tutte le scene dell'interfaccia grafica. Espone `getScene()` per consentire a `GameController` di lavorare con le scene tramite un'astrazione anziché dipendere dalle classi concrete (DIP).

#### `SceneBackground`
Utility condivisa per il rendering dello sfondo a mattoncini. Espone `createCanvas(w, h)` e `render(gc, w, h)` come metodi statici. Tutte le scene (menu, vittoria, sconfitta) la usano per mantenere la stessa griglia grafica dell'esplorazione e centralizzare il codice di disegno evitando duplicazioni (DRY).

#### `game.css` *(risorsa in `src/main/resources`)*
Foglio di stile JavaFX che centralizza tutti gli stili grafici delle scene: colori, font, padding e stati `:disabled` dei pulsanti. Le classi della view applicano gli stili tramite `getStyleClass()` anziché usare stili inline con `setStyle()`/`setFont()`. Modificare il tema grafico richiede interventi solo su questo file.

---

#### `view.exploration`

##### `ExplorationScene` *(implementa GameScene)*
Orchestratore della scena di esplorazione: gestisce il game loop (`AnimationTimer`) e l'input da tastiera. Delega la fisica del personaggio a `PlayerPhysics`, le interazioni e le collisioni a `ExplorationInteractionHandler` e il rendering a `ExplorationRenderer` (tramite l'interfaccia `SceneRenderer`). Notifica il `GameController` tramite callback.

##### `PlayerPhysics`
Responsabilità unica: aggiornare posizione e velocità verticale del giocatore in base all'input e alla gravità. Gestisce le costanti fisiche espresse in px/s e px/s² (indipendenti dal frame-rate) e lo stato `onGround`. Tiene traccia della posizione come `double` internamente per evitare errori di arrotondamento accumulati; si riallinea automaticamente se il modello viene spostato esternamente (es. respawn). Riceve il delta-time da `ExplorationScene` ad ogni tick del game loop. Non conosce la scena né il renderer.

##### `ExplorationInteractionHandler`
Responsabilità unica: rilevare collisioni con trappole e nemici, aggiornare i flag di prossimità a uscite e ingressi, rispondere all'input [E] per raccolta oggetti, dialoghi NPC e navigazione tra stanze. Utilizza `SceneRenderer` per notifiche visive (warning nemico, dialogo).

##### `SceneRenderer` *(interfaccia, package-private)*
Contratto del renderer di esplorazione. Espone i metodi che `ExplorationScene` e `ExplorationInteractionHandler` devono invocare: `render(...)`, `triggerEnemyWarning()`, `showSaveMessage(String)`, `showDialogue(String)`, `clearDialogue()`.

##### `ExplorationRenderer` *(implementa SceneRenderer)*
Orchestratore del rendering: disegna sfondo, suolo, sprite del giocatore (con animazione livello-dipendente) e overlay. Delega il rendering delle entità della stanza a `RoomEntityRenderer` e il HUD a `HudRenderer`.

##### `HudRenderer`
Responsabilità unica: renderizzare il HUD superiore (nome, barre HP/XP, zona, stanza) e il HUD inferiore (tasti contestuali, inventario rapido).

##### `RoomEntityRenderer`
Responsabilità unica: renderizzare trappole (con glow pulsante se attive), oggetti, NPC, nemici (vivi con barra HP, sconfitti a terra) e porte di ingresso/uscita. Il rendering degli oggetti è tipo-specifico: `Potion` → bottiglia verde, `Scroll` → pergamena con fiamma, `Talisman` → scudo con gemma, `Amulet` → ciondolo dorato.

---

#### `view.combat`

##### `CombatScene` *(implementa GameScene)*
Scena del combattimento a turni. Costruisce l'interfaccia (pulsanti, barre HP, log) e gestisce le interazioni utente. Delega la logica di gioco a `CombatController`, il disegno degli sprite a `CombatSpriteRenderer` e le schermate di fine combattimento a `CombatVictoryScreen` / `CombatDefeatScreen`.

##### `CombatSpriteRenderer`
Renderer degli sprite del combattimento su canvas JavaFX. Classe e metodi pubblici per consentire l'uso anche fuori dal package (es. `VictoryScene`). Disegna il giocatore con aspetto che varia per soglie di livello (LV2: gemma, LV3: spallacci e cappuccio, LV4: bordi dorati, LV5: mantello). Distingue automaticamente tra guardia normale e boss.

##### `CombatVictoryScreen` *(implementa GameScene)*
Schermata mostrata al termine di un combattimento vinto. Sfondo a mattoncini tramite `SceneBackground`. Mostra il canvas del personaggio al livello corrente, XP guadagnati e, in caso di level-up, le nuove statistiche. Non contiene logica di gioco.

##### `CombatDefeatScreen` *(implementa GameScene)*
Schermata mostrata al termine di un combattimento perso. Sfondo a mattoncini tramite `SceneBackground`. Mostra lo sprite del personaggio con opacità ridotta (30%) per comunicare la sconfitta. Offre due opzioni (ricominciare o caricare) tramite callback. Non contiene logica di gioco.

---

#### `view.menu`

##### `StartScene` *(implementa GameScene)*
Schermata iniziale con campo nome, pulsanti nuova partita, carica e esci. Sfondo a mattoncini (`SceneBackground`) con pannello centrale semitrasparente. Mostra un messaggio di errore inline se il nome è vuoto.

##### `SaveSlotScene` *(implementa GameScene)*
Schermata di selezione slot riutilizzabile in modalità salvataggio e caricamento. Sfondo a mattoncini (`SceneBackground`) con pannello centrato. Dipende dall'interfaccia `GamePersistence`. In modalità salvataggio, mostra un dialogo di conferma prima di sovrascrivere uno slot occupato.

##### `VictoryScene` *(implementa GameScene)*
Schermata di vittoria finale. Sfondo a mattoncini (`SceneBackground`). Mostra lo sprite del personaggio al livello raggiunto (tramite `CombatSpriteRenderer`) affiancato alle statistiche finali. Non contiene logica di gioco.

---

## Validazione e gestione degli errori

La validazione degli input avviene esclusivamente **al boundary di creazione degli oggetti**, cioè nei costruttori delle entità del dominio. Il principio adottato è *fail-fast*: un oggetto con dati non validi non viene mai costruito.

| Eccezione (package `model.exception`) | Quando viene lanciata |
|---|---|
| `InvalidNameException` | `id` o `name` null/vuoti in `Player`, `Enemy`, `Item`, `Room`, `Zone`, `NPC` |
| `InvalidStatsException` | `maxHp ≤ 0`, `attack < 0`, `defense < 0`, `level ≤ 0` in `Stats`; `stats == null` in `Enemy` |

Entrambe estendono `RuntimeException`: non costringono il chiamante a un try/catch esplicito, ma interrompono l'esecuzione con un messaggio chiaro. Non vengono usate per gestire eccezioni di I/O o logica di gioco, che restano responsabilità del layer `engine` e `persistence`.

---

## Test

Il progetto include una suite di test JUnit 5 in `src/test`, organizzata in due package che rispecchiano la struttura del codice sorgente.

### Cosa viene testato

I test coprono esclusivamente il layer `model` e `engine`, che contengono tutta la logica verificabile senza dipendenze da JavaFX.

| Classe di test | Cosa verifica |
|---|---|
| `StatsTest` | Danno con difesa, cura, level-up, XP, bonus equipaggiamento |
| `InventoryTest` | Aggiunta/rimozione oggetti, capienza massima, vista immutabile |
| `PlayerTest` | Delega a Stats, equip/unequip, movimento |
| `BossTest` | Meccanismo di enrage (soglia, bonus attacco, attivazione unica) |
| `NPCTest` | Ricompensa consegnata una sola volta, NPC senza premio |
| `TrapTest` | Collisione hitbox, cooldown, disattivazione permanente |
| `RoomTest` | isCleared, gestione oggetti, viste immutabili |
| `ZoneTest` | Navigazione stanze, allRoomsCleared, completamento |
| `CombatManagerTest` | Attacco, fuga, vittoria, sconfitta, mosse speciali, oggetti, limite cure nemico |

### Perché il layer view non è testato

Le classi del package `view` dipendono da JavaFX e richiedono un toolkit grafico inizializzato (runtime JavaFX). Testarle richiederebbe `TestFX` o simili framework di test UI, che esulano dagli obiettivi del corso. La correttezza della view è verificata manualmente durante l'esecuzione del gioco.
