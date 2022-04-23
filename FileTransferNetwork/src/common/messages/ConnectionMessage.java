/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common.messages;

import java.util.Set;

public class ConnectionMessage extends Message {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private Set<String> dataToShare;

	public ConnectionMessage(String origin, String destination, String name, Set<String> dataToShare) {
		super(MessageType.CONNECTION, origin, destination);
		this.dataToShare = dataToShare;
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public Set<String> getDataToShare() {
		return dataToShare;
	}
}
