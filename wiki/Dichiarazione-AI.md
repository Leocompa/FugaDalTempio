# Dichiarazione uso strumenti di AI

## Strumento utilizzato

**Claude Code (Anthropic)** — assistente AI sviluppato da Anthropic, utilizzato tramite la CLI ufficiale **Claude Code** integrata nel terminale di sviluppo.

---

## Come è stato utilizzato

### Fase iniziale: scelta del progetto

Non essendo un appassionato di videogiochi, avevo poche idee di partenza su cosa sviluppare. Ho usato Claude per esplorare alcune tipologie di gioco e individuare un'idea compatibile con i requisiti del corso e con le mie competenze. La scelta finale — un RPG 2D a piattaforme con combattimento a turni — è stata presa da me dopo aver valutato le opzioni proposte.

### Fase di progettazione

Dopo aver abbozzato il progetto su carta — struttura dei package, distribuzione delle responsabilità, flusso principale del gioco — ho usato Claude per verificare se le idee avessero senso prima di passare al codice. Le proposte dell'AI sono state valutate criticamente: alcune sono state adottate, altre scartate perché non coerenti con l'intenzione originale o con i requisiti del corso.

### Fase di sviluppo

Durante lo sviluppo ho usato Claude come strumento di supporto nei momenti in cui avevo già analizzato il problema ma volevo una conferma prima di intervenire sul codice:

- **Bug e correzioni**: dopo aver individuato autonomamente la zona di codice dove sospettavo l'errore, ho chiesto conferma sui punti che mi risultavano più difficili da interpretare, per evitare di correggere nella direzione sbagliata
- **Refactoring**: estrazione di metodi privati da metodi diventati troppo lunghi, per migliorare la leggibilità senza alterare il comportamento
- **Refactoring grafico**: richiesta di un file CSS centralizzato (`game.css`) per raccogliere gli stili della view, sostituendo la definizione inline sparsa tra le classi JavaFX

### Fase di testing

La fase di test non era esplicitamente richiesta dal progetto, ma ho scelto di implementarla per avere una verifica più solida del comportamento delle classi principali. Ho usato Claude come supporto per la scrittura delle classi JUnit 5 (package `src/test`), che coprono il layer `model` e `engine` — logica di gioco verificabile senza dipendenze da JavaFX. I casi da testare (es. limite cure nemico, enrage del boss, capienza inventario) li ho individuati io; Claude ha aiutato a tradurli in codice di test.

### Fase di documentazione

Ho richiesto a Claude di verificare che il testo fosse scritto in modo chiaro e corretto, senza alterarne il significato o le scelte descritte.

---

### Conclusione

Le funzionalità del gioco, l'architettura generale, il codice implementato e le scelte progettuali sono opera mia. Ogni proposta dell'AI è stata valutata criticamente prima di essere accettata: alcune sono state scartate o modificate perché non coerenti con l'intenzione originale, con il design già definito o con i requisiti del corso.

