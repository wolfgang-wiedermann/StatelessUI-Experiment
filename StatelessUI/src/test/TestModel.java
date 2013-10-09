package test;

import de.ww.statelessui.annotations.Model;

@Model(name="test", 
       controller=TestController.class)
public class TestModel {

	private String name, vorname;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

}
