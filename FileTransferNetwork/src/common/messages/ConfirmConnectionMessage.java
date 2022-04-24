/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common.messages;

import common.User;

public class ConfirmConnectionMessage extends Message {
	
	private static final long serialVersionUID = 1L;
	private User user;

	public ConfirmConnectionMessage(String origin, String destination, User user) {
		super(MessageType.CONFIRM_CONNECTION, origin, destination);
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}

}