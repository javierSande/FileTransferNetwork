package client.data;

import java.util.ArrayList;
import java.util.List;

import common.Monitor;
import common.User;

public class UsersList extends Monitor {
	private List<User> users;
	
	public UsersList() {
		users = new ArrayList<User>();
	}
	
	public synchronized void setUsers(List<User> list) {
		startWrite();
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
