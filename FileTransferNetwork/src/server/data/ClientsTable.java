package server.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.User;

public class ClientsTable {
	private Map<Integer, User> users;
	
	public ClientsTable() {
		users = new HashMap<Integer, User>();
	}
	
	public synchronized List<User> getUsers() {
		List<User> list = new ArrayList<User>();
		for (User u: users.values()) 
			list.add(u);
		return list;
	}
	
	public synchronized User getUser(int id) {
		return users.get(id);
	}
	
	public synchronized void addUser(User user) {
		if (!users.containsKey(user.getId()))
			users.put(user.getId(), user);
	}
	
	public synchronized void removeUser(User user) {
		if (users.containsKey(user.getId()))
			users.remove(user.getId());
	}
}
