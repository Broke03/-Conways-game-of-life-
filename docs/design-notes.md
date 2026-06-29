# Design Notes

## Gekozen ontwerp

Dit project gebruikt een `1000 x 1000` speelveld, maar slaat alleen levende cellen op in een `Map<GridPosition, Cell>`.
Daardoor blijft het geheugenverbruik laag en kan de view toch soepel over een groot grid scrollen en zoomen.

## Samenwerking tussen celtypes

Ik heb bewust deze regel gekozen:

- Overleven kijkt naar het **totale aantal buren** van beide typen samen.
- Geboorte kijkt naar het **aantal buren van hetzelfde type**.
- Als meerdere geboortevoorwaarden tegelijk geldig zouden zijn, wint het type met de meeste eigen buurcellen.

Dit zorgt ervoor dat beide soorten elkaar echt kunnen beinvloeden, zonder dat de geboorte-regels onduidelijk worden.

## Design patterns

- `Observer pattern`
  `GameClock` houdt listeners bij en stuurt ticks naar subscribers.
  `LifeSimulation` houdt weer GUI-listeners bij en ververst de schermen automatisch.
  De update-loop draait via een Swing `Timer` op de Event Dispatch Thread, zodat extra
  thread-synchronisatie (`synchronized`) niet nodig is.

- `Strategy pattern`
  Elke celregel zit in een aparte `CellRule` implementatie.
  Daardoor kun je later makkelijk een derde celsoort toevoegen zonder bestaande regels stuk te maken.

- `Factory pattern`
  `CellFactory` maakt de juiste concrete celklasse aan op basis van `CellType`.
  Hierdoor hoeft `GameBoard` niet te weten welke concrete subclass het moet maken.

## SOLID en OO

- `SRP`
  `GameClock` doet alleen timing, `GameBoard` alleen bordlogica, `LifeSimulation` alleen simulatiebeheer, en de Swing-klassen alleen presentatie.

- `OCP`
  Nieuwe regels of celtypes kun je toevoegen via nieuwe `CellRule` en `Cell` implementaties.

- `Dependency Injection`
  `LifeSimulation` krijgt `GameClock`, `CellFactory` en `RuleBook` van buitenaf mee.

- `Inheritance`
  `ConwayCell` en `AlternativeCell` erven van de abstracte basisclass `Cell`.
