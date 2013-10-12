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

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object getController() {
		return controller;
	}

	public void setController(Object controller) {
		this.controller = controller;
	}
}
