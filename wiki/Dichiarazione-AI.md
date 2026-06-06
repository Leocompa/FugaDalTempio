# Dichiarazione uso strumenti di AI

## Strumento utilizzato

**Claude Code (Anthropic)** — assistente AI sviluppato da Anthropic, utilizzato tramite la CLI ufficiale **Claude Code** integrata nel terminale di sviluppo.

---

## Come è stato utilizzato

### Fase iniziale: scelta del progetto

Non essendo un appassionato di videogiochi, avevo poche idee di partenza su cosa sviluppare. Ho usato Claude per esplorare alcune tipologie di gioco e individuare un'idea compatibile con i requisiti del corso e con le mie competenze. La scelta finale — un RPG 2D a piattaforme con combattimento a turni — è stata presa da me dopo aver valutato le opzioni proposte.

### Fase di progettazione

Prima di scrivere codice ho discusso con Claude l'organizzazione dei package e la distribuzione delle responsabilità tra le classi, verificando che l'architettura rispettasse i principi SOLID. Le domande erano del tipo: *"Questa classe ha troppe responsabilità?"*, *"Dove è meglio mettere questa logica?"*. Le decisioni finali sono state prese da me.

### Fase di sviluppo

Durante lo sviluppo ho usato Claude come strumento di revisione:

- **Identificazione di bug**: ho mostrato porzioni di codice chiedendo di individuare errori logici o comportamenti inattesi
- **Rimozione di codice inutilizzato**: Claude ha segnalato metodi e variabili non più usati dopo refactoring successivi
- **Ottimizzazione**: suggerimenti su come semplificare logica ridondante o migliorare la leggibilità
- **Refactoring**: estrazione di metodi privati da metodi troppo lunghi, separazione di responsabilità, suddivisione del package `view` in sotto-package (`combat`, `exploration`, `menu`)
- **Refactoring grafico**: migrazione degli stili inline della view verso un foglio di stile CSS centralizzato (`game.css`), con definizione delle classi semantiche per pulsanti, label e pannelli

### Fase di testing

Ho usato Claude per definire i casi di test significativi da coprire e per scrivere le classi JUnit 5 corrispondenti (package `src/test`). I test coprono le classi del layer `model` e `engine`, che contengono tutta la logica di gioco verificabile senza dipendenze da JavaFX. I casi da testare (es. limite cure nemico, enrage del boss, capienza inventario) li ho individuati io; Claude ha tradotto i requisiti in codice di test.

### Fase di documentazione

Ho usato Claude per strutturare la Javadoc delle classi principali e per organizzare le pagine di questa Wiki. I contenuti descrivono scelte progettuali che avevo già preso; l'AI ha aiutato a renderle più chiare e complete.

---

## Ruolo dello studente

Le funzionalità del gioco, l'architettura generale, il codice implementato e le scelte progettuali sono opera mia. Ogni modifica suggerita dall'AI è stata letta, compresa e validata prima di essere applicata — alcune sono state scartate o modificate perché non corrispondevano all'intenzione originale.

Claude ha avuto il ruolo di revisore e assistente tecnico, non di autore.
