Java-Model
----------

 @Model(name="demomodel", 
       scalars={"selected", "search"},
       lists={"list", "list2"})
 public class DemoModel {
    private String name, vorname;
    // ... getter und setter zu name und vorname
 }

Java-Controller
---------------

 @Controller(model="demomodel")
 public class DemoController {

    @HandlerMethod(type=HttpMethod.GET, pathPattern="/{id}", update="demomodel.selected")
    public TestModel getById(@From("demomodel.search.id") @As("id") String id) {
        // TODO: Code zur Suche nach Objekten der Klasse DemoModel anhand des Namens    
        return ergebnisDerSuche;
    }

    @HandlerMethod(type=HttpMethod.GET, pathPattern="/?name={name}", update="demomodel.list")
    public List<TestModel> findByName(@From("demomodel.search.name") @As("name") String name) {
        // TODO: Code zur Suche nach Objekten der Klasse DemoModel anhand des Namens    
        return ergebnisDerSuche;
    }

    @HandlerMethod(type=HttpMethod.POST)
    public void create(@From("demomodel.selected") @As("model") String model) {
        // TODO: Code zum Speichern eines neuen Eintrags
    }

    @HandlerMethod(type=HttpMethod.PUT, update="demomodel.selected")
    public TestModel update(@From("demomodel.selected") @As("model") String model) {
        // TODO: Code zum Speichern des Eintrags
    }

    //?? @BackgroundUpdate(for="demomodel.list2")
    // public List<TestModel> autoUpdateList2(
 } 

Javascript-Code
---------------

keiner erforderlich, das Model kann aber durch eigenen Code erweitert werden. (Beispiel folgt demnächst)

HTML5-Code
----------

 ...
 <head>
 <script src="./jquery-2.0.3.js"></script>
 <script src="./knockout-2.3.0.js"></script>
 <script src="./framework/model.js"></script>
 </head>
 <body>
 <label for="name_tf">Name</label><input id="name_tf" type="text" data-bind="value: demomodel.selected.name"/><br/>
 <label for="forname_tf">Vorname</label><input id="vorname_tf" type="text" data-bind="value: demomodel.selected.vorname"/><br/>
 <br/>
 <br/>
