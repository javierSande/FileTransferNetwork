/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common.messages;

import common.User;

public class ClientServerReadyMessage extends Message {

	private static final long serialVersionUID = 1L;
	private int port;
	private User receiver;
	private String file;

	public ClientServerReadyMessage(String origin, String destination, int port, User receiver, String file) {
		super(MessageType.CLIENT_SERVER_READY, origin, destination);
		this.port = port;
		this.receiver = receiver;
		this.file = file;
	}
	
	public int getPort() {
		return port;
	}
	
	public User getReceiver() {
		return receiver;
	}
	
	public String getFile() {
		return file;
	}
}
