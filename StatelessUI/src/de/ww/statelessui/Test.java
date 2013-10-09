package de.ww.statelessui;

import test.TestModel;
import de.ww.statelessui.exceptions.NoModelAnnotationException;
import de.ww.statelessui.generator.Generator;

public class Test {

	/**
	 * @param args
	 * @throws NoModelAnnotationException 
	 */
	public static void main(String[] args) throws NoModelAnnotationException {
		TestModel tm = new TestModel();
		Generator g = new Generator(tm);
		
		System.out.println(g.generateKnockoutModel());

	}

}
