package com.ActionItem;

public class InsufficientCredentialsException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2624413371862111716L;
	public InsufficientCredentialsException() {}
	public InsufficientCredentialsException(String message) {
		super(message);
	}
}
