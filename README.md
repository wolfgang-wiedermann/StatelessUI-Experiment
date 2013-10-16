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

Zielsetzung
-----------

Zielsetzung des Experiment ist die Entwicklung eines einfachen Frameworks, das die Entwicklung eines 
serverseitigen Modells, bestehend aus verschiedenen Model-Klassen und zugehörigen Controller-Klassen
ermöglicht. Diese Klassen und ihre Methoden sollen durch möglichst einfache Annotationen an
das dann automatisch via Reflection generierte knockout.js-Model gebunden werden.

Das bedeutet Konkret: 
* Die Methoden des jeweiligen Controllers können einfach, durch angabe ihres Namens im data-bind-Attribut an Ereignisse gebunden werden
* Die Attribute des Modells können durch Angabe des Models und Attribut-Namens im data-bind-Attribut gebunden werden (z. B. data-bind="value: model.attribut") 
* Die Inhalte des Models werden automatisch zwischen Client und Server übertragen, wenn ein Parameter
  der aufgerufenen Controller-Methode vom Typ des Models ist.
  
Beispiel
========

Das folgende Beispiel soll zeigen, wie diese Bindungen praktisch untern Nutzung des Frameworks umgesetzt werden sollen.

Java-Model
----------

@Model(name="demomodel", 
       controller=DemoController.class)
public class DemoModel {
	private String name, vorname;
	// ... getter und setter zu name und vorname
}

Java-Controller
---------------

public class DemoController {

	@HandlerMethod(type=HttpMethod.GET, pathPattern="/{id}")
	public TestModel getByName(@From("demomodel.name") @As("id") String name) {
		// TODO: Code zur Suche nach Objekten der Klasse DemoModel anhand des Namens	
		return ergebnisDerSuche;
	}
}	

Javascript-Code
---------------

keiner erforderlich, das Model kann aber durch eigenen Code erweitert werden.
(Beispiel folgt demnächst)

HTML5-Code
----------

...
<head>
<script src="./jquery-2.0.3.js"></script>
<script src="./knockout-2.3.0.js"></script>
<script src="./framework/model.js"></script>
</head>
<body>
<label for="name_tf">Name</label><input id="name_tf" type="text" data-bind="value: demomodel.name"/><br/>
<label for="forname_tf">Vorname</label><input id="vorname_tf" type="text" data-bind="value: demomodel.vorname"/><br/>
<br/>
<br/>
<button data-bind="click: getByName">Nach Name suchen</button>
</body>
...