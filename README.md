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

L'AI è stata impiegata come strumento di supporto per:

- **Progettazione architetturale**: discussione e verifica dell'applicazione dei principi SOLID, identificazione di responsabilità delle classi e revisione del design a livello di package
- **Revisione e ottimizzazione del codice**: identificazione di bug, rimozione di codice duplicato e morto, miglioramento dell'incapsulamento
- **Refactoring**: estrazione di metodi privati, separazione delle responsabilità, parametrizzazione di logica hardcoded
- **Supporto alla scrittura della documentazione**: struttura Javadoc delle classi

Tutto il codice prodotto è stato compreso, discusso e validato personalmente. Le scelte di design (struttura dei package, interfacce, flusso di gioco) sono state prese dallo studente; l'AI ha operato come revisore e assistente tecnico.

Per una descrizione dettagliata vedere la **[Wiki del repository](../../wiki)**.
