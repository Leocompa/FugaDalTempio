# Funzionalità implementate

## Menu principale

- Inserimento del nome del personaggio
- Avvio di una nuova partita
- Caricamento di una partita salvata (selezione tra 3 slot)
- Uscita dall'applicazione

## Esplorazione

- Movimento orizzontale del personaggio (frecce o WASD)
- Fisica di gioco: gravità, salto, vincolo al suolo
- Avanzamento alla stanza successiva (tasto E vicino all'uscita, solo se la stanza è stata ripulita)
- Ritorno alla stanza precedente (tasto E vicino all'ingresso)
- Raccolta degli oggetti presenti nella stanza (tasto E)
- Interazione con gli NPC: dialogo e ricezione ricompensa (tasto E)
- Collisione con le trappole: danno al giocatore con cooldown per evitare danni ripetuti
- Collisione con i nemici: avvio del combattimento dopo un breve ritardo di preavviso
- HUD superiore: nome giocatore, barra HP colorata (verde/arancione/rosso), barra XP, livello, zona e stanza corrente
- HUD inferiore: guida tasti, contatore pozioni, oggetti nell'inventario
- Salvataggio rapido con CTRL+S
- Conferma uscita al menu con ESC

## Combattimento a turni

Il combattimento si alterna tra il turno del giocatore e quello del nemico.

**Azioni del giocatore:**
- **Attacca** — attacco base, sempre disponibile
- **Lama d'ombra** — mossa speciale con danno potenziato (usi limitati, scalano con la progressione)
- **Usa pozione** — consuma una pozione dall'inventario per ripristinare HP (il pulsante mostra il contatore e si disabilita se non ce ne sono)
- **Fuggi** — abbandona il combattimento e torna all'esplorazione
- **Equipaggia/Usa oggetto** — equipaggia l'amuleto (bonus DEF e HP permanenti) oppure usa pergamena o talismano (effetti istantanei)

**Azioni del nemico:**
- Sceglie casualmente tra attacco, mossa speciale o cura
- I nemici boss hanno il meccanismo di **enrage**: quando gli HP scendono sotto il 50%, l'attacco aumenta

**Oggetti consumabili in combattimento:**
- **Pergamena di Fuoco** — aumenta l'ATK del giocatore per un turno
- **Talismano della Luna** — dimezza il danno del prossimo attacco nemico

## Progressione

- Ogni nemico sconfitto assegna XP al giocatore
- Al raggiungimento della soglia XP si verifica il level-up: aumentano HP massimi, ATK e DEF
- I nemici scalano progressivamente in base alla stanza (ATK, DEF e HP aumentano)
- Il numero di mosse speciali disponibili per combattimento aumenta con i nemici già sconfitti

## NPC

- Tre NPC nelle stanze 1, 4 e 5 offrono dialogo e un oggetto ricompensa
- La ricompensa viene consegnata una sola volta

## Salvataggio e caricamento

- 3 slot di salvataggio indipendenti
- Ogni slot mostra: nome personaggio, livello, numero stanza, data e ora del salvataggio
- Vengono salvati: statistiche del giocatore, inventario, stato di ogni nemico (vivo/morto, HP), stato degli NPC (ricompensa già data), item ancora presenti nelle stanze, indice zona e stanza corrente

## Schermate speciali

- **Game over**: overlay rosso con possibilità di riprovare dalla prima stanza della zona (tasto R) o tornare al menu
- **Vittoria**: schermata con statistiche finali e pulsante per tornare al menu
- **Vittoria in combattimento**: mostra XP guadagnati e, se applicabile, statistiche del level-up
- **Sconfitta in combattimento**: opzione per ricominciare dall'inizio o caricare un salvataggio
