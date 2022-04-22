package client.data;

import java.util.ArrayList;
import java.util.List;

import common.User;

public class UsersList {
	private List<User> users;
	private int nReaders;
	
	public UsersList() {
		users = new ArrayList<User>();
		nReaders = 0;
	}
	
	private synchronized void startRead() {
		nReaders++;
	}
	
	private synchronized void endRead() {
		nReaders--;
		if (nReaders == 0)
			notify();
	}
	
	public synchronized void setUsers(List<User> list) {
		if (nReaders > 0)
			try { wait(); } catch (InterruptedException e) { return; }
		users = list;
		notify();
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
