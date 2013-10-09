package de.ww.statelessui.generator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import de.ww.statelessui.annotations.As;
import de.ww.statelessui.annotations.From;
import de.ww.statelessui.annotations.HandlerMethod;
import de.ww.statelessui.annotations.Model;
import de.ww.statelessui.exceptions.NoModelAnnotationException;

/**
 * Step 1:Generates knockout.js-Model from @Model-Annotated Class
 * @author wiw39784
 */
public class Generator {

	private Object model;
	
	public Generator(Object model) {
		this.model = model;
	}
	
	/**
	 * Generiert aus dem Model, das dem Generator zugeordnet ist ein
	 * knockout.js-Model
	 * @return
	 * @throws NoModelAnnotationException
	 */
	public String generateKnockoutModel() throws NoModelAnnotationException {
		Class<?> modelClass = this.model.getClass();
		if(!modelClass.isAnnotationPresent(Model.class)) {
			throw new NoModelAnnotationException();
		}
		StringBuffer code = new StringBuffer();
		code.append("function GeneratedViewModel() {\n");
		code.append("\tvar self = this;\n");
		String modelName = ((Model)modelClass.getAnnotation(Model.class)).name();
		Class<?> controllerClass = ((Model)modelClass.getAnnotation(Model.class)).controller();
		
		// Attribute rausschreiben
		Method methods[] = modelClass.getDeclaredMethods();
		for(Method m : methods) {
			if(m.getName().startsWith("get")) {
				String fieldName = convertMethodName(m.getName());				
				code.append("\tself."+fieldName+" = ko.observable(\"todo\");\n");
			}
		}
		
		// Handler-Funktionen rausschreiben
		code.append(generateKnockoutHandlers(controllerClass, modelClass));
		
		code.append("\n}\nvar "+modelName+" = new GeneratedViewModel();\nko.applyBindings("+modelName+");\n");
		return code.toString();
	}
	
	/**
	 * Generiert die erforderlichen Handler, passend zum im Model angegebenen Controller
	 * @param controller
	 * @return
	 */
	private String generateKnockoutHandlers(Class<?> controllerClass, Class<?> modelClass) {
		StringBuffer code = new StringBuffer();
		
		Method methods[] = controllerClass.getMethods();
		for(Method m : methods) {
			if(m.isAnnotationPresent(HandlerMethod.class)) {
				code.append(generateSingleKnockoutHandler(m));
			}
		}
		
		return code.toString();
	}
	
	/**
	 * Erstellt eine einzelne Handler-Funktion
	 * @param m
	 * @return
	 */
	public String generateSingleKnockoutHandler(Method m) {
		StringBuffer code = new StringBuffer();
		HandlerMethod annotation = m.getAnnotation(HandlerMethod.class);
		String methodName = m.getName();
		code.append("\n\t/*\n\t * Aufruf einer Controller-Methode\n\t */");
		code.append("\n\tself."+methodName+" = function(");
		StringBuffer methodBody = new StringBuffer();
		for(int i = 0; i < m.getParameterTypes().length; i++) {
			From from = null;
			As as = null;
			for(Annotation paramAnnotation : m.getParameterAnnotations()[i]) {
				if(paramAnnotation instanceof From) {				
					from = (From)paramAnnotation;
				} else if(paramAnnotation instanceof As) {
					as = (As)paramAnnotation;
				}
			}
			generateCodeForParameterHandling(from, as, i, methodBody, code);
		}
		code.append(") {\n"+methodBody.toString()+"\n");
		
		switch(annotation.type()) {
			case GET:
				code.append(generateGetRequestFor(annotation.pathPattern()));
				break;
			case PUT: 
				break;
			case POST: 
				break;
			case DELETE: 
				break;
			default: 
				throw new RuntimeException("Unsupported Type");
		}
		code.append("\t//TODO: make a "+annotation.type().toString()+"-Request to "+annotation.pathPattern()+"\n");
		code.append("\t};\n");
		
		return code.toString();
	}
	
	/**
	 * Handles a single Parameter from an Java-Method
	 * @param from
	 * @param as
	 * @param i
	 * @param methodBody
	 * @param code
	 */
	private void generateCodeForParameterHandling(From from, As as, int i, StringBuffer methodBody, StringBuffer code) {
		if(from != null) {
			if(as == null) {
				methodBody.append("\t\tvar param"+i+" = "+from.value()+";\n");
			} else {
				methodBody.append("\t\tvar "+as.value()+" = "+from.value()+";\n");
			}
		} else {
			code.append("param"+i);
		}
	}
	
	/**
	 * Writes the Code for sending a GET-Request to the origin
	 * @param pathPattern
	 * @return
	 */
	private String generateGetRequestFor(String pathPattern) {
		return "$.ajax();"; // TODO: ausprogrammieren!
	}

	/**
	 * Konvertiert den Methoden-Namen einer getter-Methode in einen Feld-Namen
	 * @param methodName
	 * @return
	 */
	public String convertMethodName(String methodName) {
		String temp = methodName.substring(3);
		temp = temp.substring(0,1).toLowerCase()+temp.substring(1);
		return temp;
	}

}
