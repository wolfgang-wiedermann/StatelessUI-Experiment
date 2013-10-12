package de.ww.statelessui.executor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ww.statelessui.annotations.HandlerMethod;

public class UrlBinding {
	private HashMap<String, ControllerMethodCommand> pathsWithoutParams = new HashMap<String, ControllerMethodCommand>();
	private HashMap<String, ControllerMethodCommand> pathsWithParams = new HashMap<String, ControllerMethodCommand>();
	
	public UrlBinding() {
		
	}
	
	public void bind(Method m, Object controller, String modelname) {
		String pathPattern = "/"+modelname+m.getAnnotation(HandlerMethod.class).pathPattern();
		Matcher matcher = Pattern.compile("\\{[^\\{\\}/]*\\}").matcher(pathPattern);
		if(matcher.find()) {
			bindPathWithParams(m, controller, pathPattern);
		} else {
			bindPathWithoutParams(m, controller, pathPattern);
		}
	}
	
	public ControllerMethodCommand find(String path) {
		ControllerMethodCommand cmd = this.pathsWithoutParams.get(path);
		if(cmd == null) {
			for(String urlRegex : this.pathsWithParams.keySet()) {
				if(path.matches(urlRegex)) {
					cmd = this.pathsWithParams.get(urlRegex);
				}
			}
		}
		if(cmd == null) {
			throw new RuntimeException("No Controller found for given Path");
		}
		return cmd;
	}

	private void bindPathWithoutParams(Method m, Object controller, String pathPattern) {
		ControllerMethodCommand cmd = new ControllerMethodCommand(m, controller);
		this.pathsWithoutParams.put(pathPattern, cmd);
	}
	
	private void bindPathWithParams(Method m, Object controller, String pathPattern) {			
		String pathRegex = pathPattern.replaceAll("\\{[^\\{\\}/]*\\}", "[^/]*");
		ControllerMethodCommand cmd = new ControllerMethodCommand(m, controller);
		this.pathsWithParams.put(pathRegex, cmd);
	}	
}
