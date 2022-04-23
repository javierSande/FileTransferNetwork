/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common.messages;

import java.util.Set;

public class UserUpdateMessage extends Message {
	
	private static final long serialVersionUID = 1L;
	private int id;
	private Set<String> dataToShare;

	public UserUpdateMessage(String origin, String destination, int id, Set<String> dataToShare) {
		super(MessageType.USER_UPDATE, origin, destination);
		this.id = id;
		this.dataToShare = dataToShare;
	}
	
	public int getId() {
		return id;
	}
	
	public Set<String> getDataToShare() {
		return dataToShare;
	}
}
