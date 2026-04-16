# Fuga dal Tempio

Gioco di ruolo 2D a piattaforme sviluppato in Java con JavaFX.  
Il giocatore veste i panni di un ladro intrappolato nel Tempio di Persepoli e deve combattere nemici, raccogliere oggetti e fuggire attraverso cinque stanze, affrontando il boss finale.

---

## Funzionalità principali

- Esplorazione a piattaforme con fisica (gravità, salto, movimento)
- Combattimento a turni con nemici e boss (attacco, mossa speciale, pozione, fuga)
- Inventario con oggetti: pozioni, amuleto, pergamena di fuoco, talismano
- Sistema di progressione: XP, livelli, scaling dei nemici
- NPC con dialogo e ricompensa
- Trappole attive nelle stanze
- Salvataggio e caricamento su 3 slot (persistenza XML)
- Schermata di game over, vittoria e menu principale

---

## Prerequisiti

- Java 25
- Gradle (incluso il wrapper `gradlew`)

---

## Come eseguire

```bash
git clone https://github.com/Leocompa/FugaDalTempio.git
cd FugaDalTempio
./gradlew run
```

Su Windows:

```bat
gradlew.bat run
```

---

## Struttura del progetto

```
src/main/java/it/unicam/cs/mpgc/rpg118708/
├── Main.java               # Punto di ingresso e composition root
├── controller/             # GameController, CombatController, WorldBuilder, WorldFactory
├── engine/                 # GameManager, CombatManager, GameState, CombatResult
├── model/                  # Player, Enemy, Boss, Stats, Inventory, Item, Room, Zone, NPC, Trap
├── persistence/            # GamePersistence (interfaccia), XmlGamePersistence, SlotInfo
└── view/                   # ExplorationScene, CombatScene, StartScene, SaveSlotScene, VictoryScene
```

---

## Uso di strumenti di AI

Durante lo sviluppo è stato utilizzato **Claude (Anthropic)** tramite Claude Code CLI.

Non essendo un appassionato di videogiochi, ho usato l'AI come primo supporto per scegliere il tipo di gioco da sviluppare. Nelle fasi successive ha operato come assistente tecnico per la revisione del codice, l'applicazione dei principi SOLID e la documentazione.

Le scelte progettuali e il codice principale sono opera mia. Claude ha avuto il ruolo di revisore, non di autore.

Per una descrizione dettagliata vedere la **[Wiki del repository](../../wiki)**.
