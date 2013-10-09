package test;

import de.ww.statelessui.annotations.From;
import de.ww.statelessui.annotations.HandlerMethod;
import de.ww.statelessui.annotations.HttpMethod;


public class TestController {

	@HandlerMethod(type=HttpMethod.GET)
	public TestModel getByPrimaryKey(@From("test.name") String key) {
		return null;
	}
	
	@HandlerMethod(type=HttpMethod.POST)
	public TestModel postModel(@From("test") TestModel model) {
		return model;
	}

}
