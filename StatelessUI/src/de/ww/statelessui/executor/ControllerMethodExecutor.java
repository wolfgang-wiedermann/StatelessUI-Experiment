package de.ww.statelessui.executor;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ClassUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.ww.statelessui.annotations.As;
import de.ww.statelessui.annotations.From;
import de.ww.statelessui.annotations.HandlerMethod;
import de.ww.statelessui.annotations.HttpMethod;
import de.ww.statelessui.exceptions.UnsupportedParameterTypeException;

public class ControllerMethodExecutor {
	
	private Object controller;
	private String modelname;
	private HashMap<HttpMethod, UrlBinding> urlBinding = new HashMap<HttpMethod, UrlBinding>(); 
	
	public ControllerMethodExecutor(Object controller, String modelname) {
		this.controller = controller;
		this.modelname = modelname;
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
		binding.bind(m, controller, modelname);
	}
	
	public ControllerMethodCommand find(String httpMethod, String url) {
		HttpMethod method = HttpMethod.valueOf(httpMethod);
		return this.urlBinding.get(method).find(url);
	}
	
	public Object exec(HttpServletRequest request) 
			throws UnsupportedParameterTypeException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		ControllerMethodCommand cmd = this.find(request.getMethod(), request.getPathInfo());
		Object params[] = getParameters(cmd.getMethod(), request);
		
		//DEBUG: Begin
		ObjectMapper mapper = new ObjectMapper();
		System.out.println("Method: "+cmd.getMethod().getName());
		for(Object o : params) {
			System.out.println("Parameter: "+o.toString());
		}
		//DEBUG: End
		
		return cmd.exec(params);
	
	}

	/**
	 * This method extracts the parameters for calling the given Method
	 * from the given request
	 * 
	 * @param method
	 * @param request
	 * @return
	 * @throws UnsupportedParameterTypeException 
	 */
	private Object[] getParameters(Method method, HttpServletRequest request) throws UnsupportedParameterTypeException {
		Object params[] = new Object[method.getParameterTypes().length];
		for(int i = 0; i < params.length; i++) {
			Class<?> type = method.getParameterTypes()[i];
			if(ClassUtils.isPrimitiveOrWrapper(type) || type.equals(String.class)) {
				params[i] = getPrimitiveParam(i, method, request);
			} else {
				params[i] = getComplexParam(i, method, request);
			}
		}
		return params;
	}
	
	/**
	 * Ermittelt einen evtl. vorhandenen komplexen Aufrufparameter (i.d.R. das Model!)
	 * @param i
	 * @param method
	 * @param request
	 * @return
	 * @throws UnsupportedParameterTypeException
	 */
	private Object getComplexParam(int i, Method method, HttpServletRequest request) throws UnsupportedParameterTypeException {
		HttpMethod httpMethod = method.getAnnotation(HandlerMethod.class).type();
		if(this.isComplexParamSupportedByHttpMethod(httpMethod)) {
			ObjectMapper mapper = new ObjectMapper();		
			try {
				return mapper.readValue(request.getInputStream(), method.getParameterTypes()[i]);
			} catch(Exception ex) {
				throw new UnsupportedParameterTypeException(ex);
			}
		} else {
			throw new UnsupportedParameterTypeException();
		}
	}

	private Object getPrimitiveParam(int i, Method method, HttpServletRequest request) {
		String from = null, as = null;
		
		String pathPattern = method.getAnnotation(HandlerMethod.class).pathPattern();
		HttpMethod httpMethod = method.getAnnotation(HandlerMethod.class).type();
		
		for(Annotation a : method.getParameterAnnotations()[i]) {
			if(a instanceof From) {
				from = ((From) a).value();
			} else if(a instanceof As) {
				as = ((As) a).value();
			}
		}
		// Pfad-Parameter identifizieren
		if(pathPattern.matches(".*\\{"+as+"\\}.*")) {
			// TODO: hier fehlt noch die Umwandlung in einen passenden Typ != String
			return getParamFromPath(request.getPathInfo(), pathPattern, as);
		} else {
			// Sonst von Query-Parameter ausgehen!
			return request.getParameter(as);
		}
	}
	
	private String getParamFromPath(String path, String pathPattern, String paramName) {
		pathPattern = "/"+this.modelname+pathPattern;
		String split[] = pathPattern.split("\\{"+paramName+"\\}");
		String before = split[0];
		before = before.replaceAll("\\{[^\\{\\}/]*\\}", "[^/]");
		String value = path.replaceAll(before, "");
		
		if(split.length > 1) {
			String after = split[1];
			after = after.replaceAll("\\{[^\\{\\}/]*\\}", "[^/]");
			value = value.replaceAll(after, "");
		}
		
		return value;
	}
	
	private boolean isComplexParamSupportedByHttpMethod(HttpMethod m) {
		return m.equals(HttpMethod.POST) || m.equals(HttpMethod.PUT);
	}
}
