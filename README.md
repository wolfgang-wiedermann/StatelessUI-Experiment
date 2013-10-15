StatelessUI-Experiment
======================

Experiment für ein UI-Framework unter Nutzung von knockout.js

Das vorliegende Softwareexperiment soll die Machbarkeit einer Idee zur Gestaltung eines Frameworks,
das eine sehr direkte Bindung zwischen den Model-Objekten in Java (im Server) und dem knockout.js-Model
im Client (Javascript im Brower) prüfen.

Grundidee dabei ist es, die serverseitige Verarbeitung vollständig unabhängig von möglichen Sitzungszuständen etc.
zu gestalten und den Zustand der Sitzung etc. ausschließlich im Client zu halten, sodass ein replikationsfreier
lastverteilter Betrieb mit einer großen Zahl von gleichartigen Knoten möglich wird.

Einen Flaschenhals kann innerhalb dieses Konzepts (sofern es erfolgreich ist) nur mehr die Datenbank liefern.
Hier gibt es aber inzwischen sowohl im NoSQL (hbase, cassandra etc.) als auch bei den relationalen Datenbanken
(Mysql mit entsprechender Storage-Engine) Ansätze auch dort eine entsprechend skalierbare Architektur zu bieten.
