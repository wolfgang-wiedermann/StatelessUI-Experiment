package de.ww.statelessui.generator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

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
	private Object generateKnockoutHandlers(Class<?> controllerClass, Class<?> modelClass) {
		StringBuffer code = new StringBuffer();
		
		Method methods[] = controllerClass.getMethods();
		for(Method m : methods) {
			if(m.isAnnotationPresent(HandlerMethod.class)) {
				HandlerMethod annotation = m.getAnnotation(HandlerMethod.class);
				String methodName = m.getName();
				code.append("\n\t/*\n\t * Aufruf einer Controller-Methode\n\t */");
				code.append("\n\tself."+methodName+" = function(");
				String methodBody = "";
				for(int i = 0; i < m.getParameterTypes().length; i++) {
					boolean containsFrom = false;
					for(Annotation paramAnnotation : m.getParameterAnnotations()[i]) {
						if(paramAnnotation instanceof From) {
							containsFrom = true;
							methodBody += "\t\tvar param"+i+" = "+((From)paramAnnotation).value()+";\n";
						}
					}
					if(!containsFrom) {
						code.append("param"+i);
					}
				}
				code.append(") {\n"+methodBody+"\n");
				code.append("\t//TODO: make a "+annotation.type().toString()+"-Request to "+annotation.pathPattern()+"\n");
				code.append("\t};\n");
			}
		}
		
		return code.toString();
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
