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

Durante lo sviluppo del progetto è stato utilizzato **Claude (Anthropic)** come assistente tramite Claude Code CLI.

Il primo contributo dell'AI è stato nella **scelta del tipo di gioco da sviluppare**: non essendo un appassionato di videogiochi, avevo poche idee di partenza. Claude mi ha aiutato a esplorare alcune possibilità e a individuare un'idea adatta alle mie competenze e ai requisiti del progetto, ovvero un RPG 2D a piattaforme con combattimento a turni.

Nelle fasi successive l'AI è stata impiegata come strumento di supporto per:

- **Progettazione architetturale**: discussione e verifica dell'applicazione dei principi SOLID, identificazione delle responsabilità delle classi, revisione del design a livello di package
- **Revisione e ottimizzazione del codice**: identificazione di bug, rimozione di codice duplicato e morto, miglioramento dell'incapsulamento
- **Refactoring**: estrazione di metodi privati, separazione delle responsabilità, parametrizzazione di logica hardcoded
- **Supporto alla scrittura della documentazione**: struttura Javadoc delle classi

L'AI non ha sostituito il mio lavoro: ogni scelta progettuale è stata discussa, compresa e validata personalmente prima di essere applicata. Il codice è stato scritto, modificato e adattato da me; Claude ha operato come revisore e assistente tecnico, non come autore.

Per una descrizione dettagliata vedere la **[Wiki del repository](../../wiki)**.
