package de.ww.statelessui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ermöglicht, anzugeben aus welchem Feld des Models der Wert für einen
 * Parameter in einer Controller-Methode geholt werden soll.
 * @author wiw39784
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface From {
	String value();
}
