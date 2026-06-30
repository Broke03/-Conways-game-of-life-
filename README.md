# Conways Spel des Levens

Dit project is een Java-uitwerking van Conways Spel des Levens met een extra alternatieve celsoort, een grafische interface en een simulatie-terminal.

## Functies
- Twee celsoorten: Conway en Alternatief
- Bewerken op het bord met penseel, alternatief penseel of wissen
- Starten, pauzeren, hervatten en herstellen van de simulatie
- Snelheid aanpassen en in-/uitzoomen
- Live statistieken en een alleen-lezen simulatie-terminal

## Vereisten
- Java 21
- Maven

## Project starten
```bash
mvn test -q
mvn -q exec:java -Dexec.mainClass=Main
```

## Bediening
- Linksklik: plaatst geselecteerd penseel
- Rechtsklik: plaats een alternatieve cel
- Shift + klik: wis een cel
- Ctrl + muiswiel: zoom in of uit
