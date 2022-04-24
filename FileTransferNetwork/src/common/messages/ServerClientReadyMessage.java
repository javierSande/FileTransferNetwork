/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common.messages;

public class ServerClientReadyMessage extends Message {

	private static final long serialVersionUID = 1L;
	private String ip;
	private int port;
	private String file;

	public ServerClientReadyMessage(String origin, String destination, String ip, int port, String file) {
		super(MessageType.SERVER_CLIENT_READY, origin, destination);
		this.ip = ip;
		this.port = port;
		this.file = file;
	}
	
	public String getIp() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getFile() {
		return file;
	}
}
