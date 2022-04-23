/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common.messages;

public class EOFMessage extends Message {

	private static final long serialVersionUID = 1L;

	public EOFMessage(String origin, String destination) {
		super(MessageType.EOF, origin, destination);
	}

}
