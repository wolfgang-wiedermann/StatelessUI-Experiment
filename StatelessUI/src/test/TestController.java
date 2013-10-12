package test;

import java.util.ArrayList;

import de.ww.statelessui.annotations.As;
import de.ww.statelessui.annotations.From;
import de.ww.statelessui.annotations.HandlerMethod;
import de.ww.statelessui.annotations.HttpMethod;


public class TestController {

	@HandlerMethod(type=HttpMethod.GET, pathPattern="/{id}")
	public TestModel getByPrimaryKey(@From("test.nummer") @As("id") String key) {
		return null;
	}
	
	@HandlerMethod(type=HttpMethod.GET, pathPattern="/list")
	public ArrayList<TestModel> getListOfTestModel() {
		ArrayList<TestModel> lst = new ArrayList<TestModel>();
		lst.add(new TestModel());
		lst.add(new TestModel());
		return lst;
	}
	
	@HandlerMethod(type=HttpMethod.POST, pathPattern="/")
	public TestModel postModel(@From("test") TestModel model) {
		return model;
	}
	
	@HandlerMethod(type=HttpMethod.GET, pathPattern="/{id}/sepp")
	public TestModel getTest(@From("test.nummer") @As("id") Integer nummer) {
		return null;
	}

}
