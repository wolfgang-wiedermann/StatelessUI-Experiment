package de.ww.statelessui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Value of this Annotation describes under which name it
 * can be used within the pathPattern
 * 
 * @author wiw39784
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface As {
	String value();
}
