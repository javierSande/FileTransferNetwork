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
	
	// TODO users.values() java lo considera una modificación
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
