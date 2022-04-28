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

import common.User;
import common.monitors.MonitorSyn;

public class ClientsTable extends MonitorSyn {
	
	/* ClientsTable
	 *  
	 * This class represent a table that pairs every client identifier
	 * with the client itself. Its goal is to speed up the process of 
	 * checking the data of a given user, taking into account that the
	 * access to a hash map is done in constant time (O(1)).
	 * If we didn't have this hash map, we would have to traverse the
	 * clients list in linear time (O(n)) every time we want to check
	 * something of a client.
	 * As we have the readers-writers problem, each method that tries to
	 * read or write data should follow the protocol of the Monitor class.
	 */
	
	private volatile Map<Integer, User> users;
	
	public ClientsTable() {
		users = new HashMap<Integer, User>();
	}
	
	public List<User> getUsers() {
		startRead();
		List<User> list = new ArrayList<User>();
		for (User u: users.values()) 
			list.add(u.clone());
		endRead();
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
		endWrite();
	}
	
	public synchronized void removeUser(User user) {
		startWrite();
		if (users.containsKey(user.getId()))
			users.remove(user.getId());
		endWrite();
	}
}
