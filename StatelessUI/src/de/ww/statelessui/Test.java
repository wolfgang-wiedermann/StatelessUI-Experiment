package de.ww.statelessui;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import test.TestModel;
import de.ww.statelessui.exceptions.NoModelAnnotationException;
import de.ww.statelessui.generator.Generator;

public class Test {

	/**
	 * @param args
	 * @throws NoModelAnnotationException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws NoModelAnnotationException, IOException {
		TestModel tm = new TestModel();
		Generator g = new Generator(tm);
		
		System.out.println(g.generateKnockoutModel());
				
		PrintWriter pw = new PrintWriter(new FileWriter("./html/model.js"));
		pw.print(g.generateKnockoutModel());
		pw.close();		
	}

}
