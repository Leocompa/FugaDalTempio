# Fuga dal Tempio — Wiki

Documentazione tecnica del progetto sviluppato per il corso di **Metodologie di Programmazione / Modellazione e Gestione della Conoscenza**, AA 2025/26.

---

## Indice

| Pagina | Contenuto |
|---|---|
| [Funzionalità](Funzionalita) | Elenco delle funzionalità implementate |
| [Architettura](Architettura) | Responsabilità individuate, classi e interfacce |
| [Persistenza](Persistenza) | Organizzazione dei dati e meccanismo di salvataggio |
| [Estendibilità](Estendibilita) | Meccanismi per integrare nuove funzionalità |
| [Dichiarazione AI](Dichiarazione-AI) | Dichiarazione dettagliata uso strumenti di AI |

---

## Descrizione del progetto

**Fuga dal Tempio** è un gioco di ruolo 2D a piattaforme in cui il giocatore controlla un ladro intrappolato nel Tempio di Persepoli. L'obiettivo è attraversare cinque stanze, affrontare nemici e il boss finale, raccogliere oggetti e fuggire con il tesoro.

Il progetto è strutturato attorno a una separazione netta tra **logica di gioco** (package `engine` e `model`) e **interfaccia grafica** (package `view`), con un layer `controller` che coordina le due parti. Questa architettura rende l'applicazione pronta per essere estesa su dispositivi diversi (desktop, mobile, web) semplicemente sostituendo il layer di presentazione.
