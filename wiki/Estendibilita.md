# Estendibilità: meccanismi per integrare nuove funzionalità

Il progetto è stato progettato fin dall'inizio per essere estensibile senza richiedere modifiche al codice esistente. I principali meccanismi sono descritti di seguito.

---

## Supporto a più dispositivi (desktop, mobile, web)

La separazione netta tra **logica di gioco** e **presentazione** è il meccanismo centrale di estensibilità.

Il package `engine` e il package `model` non contengono alcuna dipendenza da JavaFX o da qualsiasi altra tecnologia di presentazione. Tutta la logica di combattimento, progressione e gestione dello stato risiede in classi Java pure.

Per portare il gioco su un nuovo dispositivo è sufficiente:

1. Creare nuove implementazioni delle scene nei sotto-package di `view` (es. scene Android, componenti web con GWT o Vaadin)
2. Collegare le nuove scene al `GameController` esistente (o crearne uno nuovo che usi lo stesso `GameManager` e `CombatManager`)

Il layer `engine` rimane invariato.

---

## Nuovi formati di salvataggio

La persistenza è definita dall'interfaccia `GamePersistence`:

```java
public interface GamePersistence {
    void save(GameManager gameManager, int slot);
    void load(GameManager gameManager, int slot);
    boolean saveExists(int slot);
    boolean saveExists();
    String loadPlayerName(int slot);
    SlotInfo getSlotInfo(int slot);
    int getMaxSlots();
}
```

Per aggiungere la persistenza in formato JSON o su database è sufficiente creare una nuova classe che implementi questa interfaccia e iniettarla nel `GameController` dal `Main`. Nessuna altra classe deve essere modificata.

---

## Nuovi mondi e generazione procedurale

Il mondo di gioco è costruito tramite l'interfaccia `WorldFactory`:

```java
public interface WorldFactory {
    List<Zone> buildWorld();
}
```

`WorldBuilder` è l'implementazione attuale (mondo hardcoded con 5 stanze). Per aggiungere:

- **Generazione procedurale**: creare `ProceduralWorldFactory implements WorldFactory`
- **Caricamento da file di configurazione**: creare `XmlWorldFactory implements WorldFactory`
- **Mondo con più zone**: modificare solo `WorldBuilder.buildWorld()` aggiungendo zone alla lista

In tutti i casi, `GameController` riceve la factory tramite costruttore e non sa nulla della strategia di generazione.

---

## Nuovi tipi di nemici

`Enemy` può essere estesa per aggiungere comportamenti speciali. `Boss` ne è già un esempio: aggiunge l'enrage senza modificare `Enemy` o `CombatManager`.

Il meccanismo è il metodo hook `onDamageTaken()`, che `CombatManager` invoca dopo ogni danno inflitto. La classe base non fa nulla; `Boss` lo sovrascrive per attivare l'enrage. `CombatManager` non usa mai `instanceof`.

Per un nuovo tipo di nemico con comportamento diverso (es. un nemico che si cura automaticamente):

1. Estendere `Enemy`
2. Sovrascrivere `onDamageTaken()` per reagire ai danni ricevuti, e/o `chooseAction()` per una logica di scelta diversa
3. `CombatManager` non richiede modifiche

---

## Nuovi oggetti e tipi di oggetto

`Item` è una classe astratta: ogni tipo di oggetto è una sottoclasse concreta. Per aggiungere un nuovo tipo (es. `Shield`):

1. Creare `Shield extends Item` con `getType()` che restituisce il valore dell'enum corrispondente
2. Aggiungere il valore a `ItemType` (usato solo per la serializzazione XML)
3. Aggiungere la gestione dell'effetto in `CombatManager.useItem()` o `equipItem()` tramite `instanceof Shield`
4. Aggiornare `Item.create()` per supportare il nuovo tipo nella deserializzazione

Le classi esistenti (`Potion`, `Amulet`, `Scroll`, `Talisman`, `CombatManager`) non vengono modificate se non per aggiungere il nuovo `case` in `useItem()`/`equipItem()`. Il campo `value` è il parametro numerico dell'effetto (quantità di cura, bonus, ecc.).

---

## Nuovi stati di gioco

`GameState` è un enum. Per aggiungere uno stato (es. `PUZZLE`, `SHOP`, `CUTSCENE`):

1. Aggiungere il valore all'enum
2. Gestire il nuovo stato in `ExplorationScene.update()` e `render()` (package `view.exploration`)
3. Aggiungere le transizioni necessarie in `GameManager`

---

## Nuove zone

La struttura `Zone → List<Room>` è già predisposta per più zone. `GameManager.advanceZone()` gestisce la transizione alla zona successiva. Per aggiungere una seconda zona è sufficiente chiamare `zones.add(buildZoneTwo())` in `WorldBuilder.buildWorld()`.

---

## Riepilogo dei punti di estensione

| Cosa aggiungere | Come |
|---|---|
| Nuova UI (mobile, web) | Nuove implementazioni nei sotto-package di `view` |
| Nuovo formato persistenza | Nuova classe che implementa `GamePersistence` |
| Nuovo tipo di mondo | Nuova classe che implementa `WorldFactory` |
| Nuovo tipo di nemico | Estendere `Enemy` |
| Nuovo tipo di oggetto | Estendere `Item`, aggiungere valore a `ItemType`, gestire in `CombatManager` |
| Nuovo stato di gioco | Aggiungere valore a `GameState`, gestire in `ExplorationScene` |
| Nuova zona | Aggiungere in `WorldBuilder.buildWorld()` |
