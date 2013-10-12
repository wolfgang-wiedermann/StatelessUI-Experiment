package de.ww.statelessui.executor;

import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import de.ww.statelessui.annotations.HandlerMethod;
import de.ww.statelessui.annotations.HttpMethod;

public class ControllerMethodExecutor {
	
	private Object controller;
	private HashMap<HttpMethod, UrlBinding> urlBinding = new HashMap<HttpMethod, UrlBinding>(); 
	
	public ControllerMethodExecutor(Object controller) {
		this.controller = controller;
		this.initializeUrlBinding();
	}
	
	private void initializeUrlBinding() {
		for(Method m : this.controller.getClass().getMethods()) {
			if(m.isAnnotationPresent(HandlerMethod.class)) {
				bindMethod(m);
			}
		}
	}
	
	private void bindMethod(Method m) {
		HttpMethod http = m.getAnnotation(HandlerMethod.class).type();
		UrlBinding binding = this.urlBinding.get(http);
		if(binding == null) {
			binding = new UrlBinding();
			this.urlBinding.put(http, binding);
		}
		binding.bind(m, controller);
	}
	
	public ControllerMethodCommand find(String httpMethod, String url) {
		HttpMethod method = HttpMethod.valueOf(httpMethod);
		return this.urlBinding.get(method).find(url);
	}
	
	public Object exec(HttpServletRequest request) {
		ControllerMethodCommand cmd = this.find(request.getMethod(), request.getPathInfo());
		//return cmd.exec(params);
		return null;
	}

}
