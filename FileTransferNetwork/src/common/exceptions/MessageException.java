/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common.exceptions;

@SuppressWarnings("serial")
public class MessageException extends Exception {
	
	public MessageException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public MessageException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public MessageException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public MessageException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public String getMessage() {
		return "Invalid message";
	}
}
