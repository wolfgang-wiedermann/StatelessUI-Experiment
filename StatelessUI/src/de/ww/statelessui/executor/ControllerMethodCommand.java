package de.ww.statelessui.executor;

import java.lang.reflect.Method;

public class ControllerMethodCommand {

	private Method method;
	private Object controller;
	
	public ControllerMethodCommand(Method method, Object controller) {
		this.method = method;
		this.controller = controller;
	}
	
	public Object exec(Object params[]) {
		return null;
	}
}
