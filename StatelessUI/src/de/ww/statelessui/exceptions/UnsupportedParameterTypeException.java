package de.ww.statelessui.exceptions;

public class UnsupportedParameterTypeException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnsupportedParameterTypeException(Exception ex) {
		super(ex);
	}
	
	public UnsupportedParameterTypeException() {
		super();
	}	

}
