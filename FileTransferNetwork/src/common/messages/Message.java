/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common.messages;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class Message implements Serializable {

	public final MessageType type;
	
	private String origin;
	private String destination;
	
	public Message(MessageType type, String origin, String destination) {
		super();
		this.type = type;
		this.origin = origin;
		this.destination = destination;
	}
	
	public String getOrigin() {
		return origin;
	}
	
	public String getDestination() {
		return destination;
	}
}
