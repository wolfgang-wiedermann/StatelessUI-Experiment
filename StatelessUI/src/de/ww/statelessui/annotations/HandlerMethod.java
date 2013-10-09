package de.ww.statelessui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HandlerMethod {
	/**
	 * If it should be called by GET, PUT, POST or DELETE
	 * @return
	 */
	HttpMethod type();
	
	/**
	 * Path-Patterns can be like for example "/bla/{id}"
	 * @return
	 */
	String pathPattern() default "[unassigned]";
}
