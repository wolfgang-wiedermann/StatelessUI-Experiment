package de.ww.statelessui;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import test.TestModel;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.ww.statelessui.annotations.Model;
import de.ww.statelessui.exceptions.NoModelAnnotationException;
import de.ww.statelessui.exceptions.UnsupportedParameterTypeException;
import de.ww.statelessui.executor.ControllerMethodExecutor;
import de.ww.statelessui.generator.Generator;

@WebServlet(name="RequestHandlerServlet", urlPatterns={"/framework/*"})
public class RequestHandlerServlet extends HttpServlet {

	private static final long serialVersionUID = -1133580085266827686L;
	private String modelJS;
	private Generator generator;
	private Object controller;
	private ControllerMethodExecutor executor;

	public RequestHandlerServlet() 
			throws NoModelAnnotationException, InstantiationException, IllegalAccessException {
		
		// TODO: Feste Bindung ans Model dann durch dynamisches Suchen nach Model-Objekten ersetzen
		TestModel tm = new TestModel();
		this.generator = new Generator(tm);
		this.modelJS = this.generator.generateKnockoutModel();		
		Class<?> controllerClass = tm.getClass().getAnnotation(Model.class).controller();
		this.controller = controllerClass.newInstance();	
		this.executor = new ControllerMethodExecutor(this.controller, 
								this.generator.getConvertedModelClassName());
	}
	
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getPathInfo().endsWith("model.js")) {
			returnModelJS(request, response);
		} else {
			try {
				handleAjaxRequest(request, response);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e);
			}
		} 
	}
	
	public void returnModelJS(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/javascript");
		PrintStream ps = new PrintStream(response.getOutputStream());
		ps.print(this.modelJS);
		ps.flush();
		ps.close();
	}
	
	public void handleAjaxRequest(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, UnsupportedParameterTypeException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		System.out.println(request.getPathInfo());
		Object result = executor.exec(request);
		response.setContentType("text/html");
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), result);		
	}
}
