package server.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.User;

public class ClientsTable {
	private Map<Integer, User> users;
	
	private int nReaders;
	
	public ClientsTable() {
		nReaders = 0;
		users = new HashMap<Integer, User>();
	}
	
	public synchronized List<User> getUsers() {
		List<User> list = new ArrayList<User>();
		for (User u: users.values()) 
			list.add(u);
		return list;
	}
	
	private synchronized void startRead() {
		nReaders++;
	}
	
	private synchronized void endRead() {
		nReaders--;
		if (nReaders == 0)
			notify();
	}
	
	public User getUser(int id) {
		startRead();
		User u = users.get(id);
		endRead();
		return u;
	}
	
	public synchronized void addUser(User user) {
		if (nReaders > 0)
			try {
				wait();
			} catch (InterruptedException e) { return; }
		if (!users.containsKey(user.getId()))
			users.put(user.getId(), user);
	}
	
	public synchronized void removeUser(User user) {
		if (nReaders > 0)
			try {
				wait();
			} catch (InterruptedException e) { return; }
		if (users.containsKey(user.getId()))
			users.remove(user.getId());
		notify();
	}
}
