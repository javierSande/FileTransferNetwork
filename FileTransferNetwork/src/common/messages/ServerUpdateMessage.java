/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common.messages;

import java.util.List;
import java.util.Set;

import common.User;

public class ServerUpdateMessage extends Message {
	private static final long serialVersionUID = 1L;
	private List<User> userList;
	private Set<String> fileList;
	
	public ServerUpdateMessage(String origin, String destination, List<User> userList, Set<String> fileList) {
		super(MessageType.SERVER_UPDATE, origin, destination);
		this.userList = userList;
		this.fileList = fileList;
	}

	public List<User> getUserList() {
		return userList;
	}
	
	public Set<String> getFileList() {
		return fileList;
	}
}