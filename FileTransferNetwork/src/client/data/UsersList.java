/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package client.data;

import java.util.ArrayList;
import java.util.List;

import common.User;
import common.monitors.MonitorSyn;

public class UsersList extends MonitorSyn {
	
	/* UsersList
	 * 
	 * This class represents the list of connected users to the server.
	 * It is implemented as a Monitor because it has the readers-writers problem.
	 * For this same reason, each method that tries to read or write data should
	 * follow the protocol of the Monitor class.
	 */
	
	private volatile List<User> users;
	
	public UsersList() {
		users = new ArrayList<User>();
	}
	
	public synchronized void setUsers(List<User> list) {
		startWrite();
		users = list;
		endWrite();
	}
	
	public List<User> getUsers() {
		startRead();
		List<User> list = new ArrayList<User>();
		for (User u: users) 
			list.add(u);
		endRead();
		return list;
	}
}
