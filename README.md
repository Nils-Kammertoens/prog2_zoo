# Blatt 07: Zoo

## Reflexion

### 1. Generics

- Generics helfen, falsche Tier-Gehege-Kombinationen bereits zur Compile-Zeit zu verhindern.
- Beispiel: In ein `Aquarium<Trout>` kann nur eine `Trout` aufgenommen werden. Ein `Snake`-Objekt passt dort nicht hinein.
- Beispiel: `CatHouse extends Enclosure<Lion>` akzeptiert nur `Lion`. Ein `Tiger` oder ein anderes Tier wird vom Typchecker abgelehnt.
- Dadurch sind weniger Casts nötig und viele Fehler entstehen gar nicht erst zur Laufzeit.

### 2. Logging

- Logging ist sinnvoller als `IO.println`, weil Meldungen nach Wichtigkeit gefiltert werden können.
- Außerdem kann man Logger gezielt konfigurieren, Handler austauschen und Ausgaben später z.B. in Dateien oder andere Systeme schreiben.
- `INFO`: normale Zoo-Aktionen, z.B. Methode wurde aufgerufen, Tier wird aufgenommen, Gehege wird gesucht.
-  `WARNING`: erwartbare Problemsituationen, z.B. Gehege nicht gefunden, Tier nicht gefunden oder eine ungültige Aktion kann nicht ausgeführt werden.
- `SEVERE`: schwere Inkonsistenzen, z.B. ein Tier wurde als vorhanden erkannt, kann aber nicht entfernt werden, oder ein Tier kommt in mehreren Gehegen vor.

### 3. Streams

- Streams helfen besonders bei Abfragen über alle Gehege hinweg, z.B. mit `flatMap`, um aus vielen Gehege-Listen eine gemeinsame Tierliste zu machen.
- `filter` ist nützlich für Abfragen wie Säugetiere oder Prädikate.
- `groupingBy` und `counting` machen Zählungen kompakter als verschachtelte Schleifen.
- Unübersichtlich wird es, wenn zu viel Logik in einer einzigen Stream-Pipeline steckt. Dann sind private Hilfsmethoden wie `categoryName(...)` lesbarer.
