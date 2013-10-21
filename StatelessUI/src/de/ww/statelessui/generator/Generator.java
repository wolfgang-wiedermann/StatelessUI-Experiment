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
	private String modelName;
	
	public Generator(Object model) {
		this.model = model;	
		this.modelName = ((Model)model.getClass().getAnnotation(Model.class)).name();
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
		Class<?> controllerClass = ((Model)modelClass.getAnnotation(Model.class)).controller();
		code.append("\tself."+modelName+" = {};\n");
		
		code.append("\tfunction "+this.firstLetterToUpper(modelName)+"(input) { \n");
		code.append("\t\tvar self = this;\n");
		code.append("\t\tif(arguments.length === 0) {\n");
		// Attribute rausschreiben
		Method methods[] = modelClass.getDeclaredMethods();
		for(Method m : methods) {
			if(m.getName().startsWith("get")) {
				String fieldName = convertMethodName(m.getName());				
				code.append("\t\t\tself."+fieldName+" = ko.observable(\"\");\n");
			}
		}
		code.append("\t\t} else {\n");
		
		// Attribute rausschreiben		
		for(Method m : methods) {
			if(m.getName().startsWith("get")) {
				String fieldName = convertMethodName(m.getName());				
				code.append("\t\t\tself."+fieldName+" = ko.observable(input."+fieldName+");\n");
			}
		}
		code.append("\t\t}\n\t}\n");
		
		code.append("\tself."+modelName+".selected = new "+this.firstLetterToUpper(modelName)+"();\n");
		code.append("\tself."+modelName+".list = [new "+this.firstLetterToUpper(modelName)+"()];\n");
		
		// Handler-Funktionen rausschreiben
		code.append(generateKnockoutHandlers(controllerClass, modelClass));
		
		code.append("\n}\nvar model = new GeneratedViewModel();\n");
		code.append("$(document).ready(function() { ko.applyBindings(model); });\n");
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
	private String generateSingleKnockoutHandler(Method m) {
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
				code.append(generatePutRequestFor(annotation.pathPattern()));
				break;
			case POST: 
				code.append(generatePostRequestFor(annotation.pathPattern()));
				break;
			case DELETE: 
				code.append(generateDeleteRequestFor(annotation.pathPattern()));
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
				methodBody.append("\t\tvar param"+i+" = self."+from.value()+"();\n");
			} else {
				methodBody.append("\t\tvar "+as.value()+" = self."+from.value()+"();\n");
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
		String path = this.getAbsolutePath(pathPattern);
		return "\t\t$.ajax({\n\t\t\turl:'"+path+"',\n\t\t\t"
				+"type: 'GET', \n\t\t\t"
				//+"dataType: 'json', \n\t\t\t"	
				+"cache:false, \n\t\t\t"
				+"success: function(data) {\n\t\t\t"
				+"alert('Erfolg: '+JSON.stringify(data)+' -- '+data); \n\t\t\t"
				+"},"
				+"error: function(error) {\n\t\t\t"
				+"alert('Fehler: '+JSON.stringify(error)); \n\t\t\t"
				+"},"
				+"});\n";		
	}
	
	/**
	 * Writes the Code for sending a PUT-Request to the origin
	 * @param pathPattern
	 * @return
	 */
	private String generatePutRequestFor(String pathPattern) {
		String path = this.getAbsolutePath(pathPattern);
		return "\t\t$.ajax({\n\t\t\turl:'"+path+"',\n\t\t\t"
				+"type: 'PUT', \n\t\t\t"
				+"data: self."+modelName+", \n\t\t\t"
				+"dataType: 'json', \n\t\t\t"				
				+"});\n";
	}
	
	/**
	 * Writes the Code for sending a POST-Request to the origin
	 * @param pathPattern
	 * @return
	 */
	private String generatePostRequestFor(String pathPattern) {
		String path = this.getAbsolutePath(pathPattern);
		return "\t\t$.ajax({\n\t\t\turl:'"+path+"',\n\t\t\t"
				+"type: 'POST', \n\t\t\t"
				+"data: self."+modelName+", \n\t\t\t"
				+"dataType: 'json', \n\t\t\t"				
				+"});\n";
	}
	
	/**
	 * Writes the Code for sending a DELETE-Request to the origin
	 * @param pathPattern
	 * @return
	 */
	private String generateDeleteRequestFor(String pathPattern) {
		String path = this.getAbsolutePath(pathPattern);
		return "\t\t$.ajax({\n\t\t\turl:'"+path+"',\n\t\t\t"
				+"type: 'DELETE', \n\t\t\t"
				//+"data: self."+modelName+", \n\t\t\t" // wahrscheinlich unnötig?
				+"dataType: 'json', \n\t\t\t"				
				+"});\n";
	}

	/**
	 * Konvertiert den Methoden-Namen einer getter-Methode in einen Feld-Namen
	 * @param methodName
	 * @return
	 */
	private String convertMethodName(String methodName) {
		String temp = methodName.substring(3);
		temp = temp.substring(0,1).toLowerCase()+temp.substring(1);
		return temp;
	}
	
	/**
	 * Konvertiert das 1. Zeichen einer Zeichenkette zu einem Großbuchstaben.
	 * @param name
	 * @return
	 */
	private String firstLetterToUpper(String name) {
		if(name.length() > 1)
			return name.substring(0, 1).toUpperCase()+name.substring(1);
		else
			return name.toUpperCase();
	}
	
	/**
	 * Erstellt aus dem Path-Pattern einen konkreten Pfad für einen Ajax-Call
	 * @param pathPattern
	 * @return
	 */
	private String getAbsolutePath(String pathPattern) {
		String pathAppendix = pathPattern.replaceAll("\\{", "'+").replaceAll("\\}", "+'");
		String path = "./framework/"+this.getConvertedModelClassName()+"/"+pathAppendix;
		path = path.replaceAll("//", "/");
		return path;
	}
	
	/**
	 * Converts the classname of the model from its usual camel case to lower case
	 * @return
	 */
	public String getConvertedModelClassName() {
		String name = this.model.getClass().getName();
		name = name.substring(name.lastIndexOf(".")+1);
		return name.toLowerCase();
	}

}
