/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package server.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.Monitor;
import common.User;

public class ClientsTable extends Monitor {
	private Map<Integer, User> users;
	
	public ClientsTable() {
		users = new HashMap<Integer, User>();
	}
	
	
	/* public synchronized List<User> getUsers()
	 * 
	 * Simple function to get a list of the users currently connected to the server.
	 * The reason for making it synchronized is because Java considers users.values()
	 * as a modification even though it is a read of data. The rest of the synchronized
	 * methods of this class are modifications.
	 */
	public synchronized List<User> getUsers() {
		List<User> list = new ArrayList<User>();
		for (User u: users.values()) 
			list.add(u.clone());
		return list;
	}
	
	public User getUser(int id) {
		startRead();
		User u = users.get(id);
		endRead();
		return u;
	}
	
	public synchronized void addUser(User user) {
		startWrite();
		if (!users.containsKey(user.getId()))
			users.put(user.getId(), user);
		notify();
	}
	
	public synchronized void removeUser(User user) {
		startWrite();
		if (users.containsKey(user.getId()))
			users.remove(user.getId());
		notify();
	}
}
