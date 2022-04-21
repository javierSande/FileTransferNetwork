package client.data;

import java.util.ArrayList;
import java.util.List;

import common.User;

public class UsersList {
	private List<User> users;
	
	public UsersList() {
		users = new ArrayList<User>();
	}
	
	public synchronized void setUsers(List<User> list) {
		users = list;
	}
	
	public synchronized List<User> getUsers() {
		List<User> list = new ArrayList<User>();
		for (User u: users) 
			list.add(u);
		return list;
	}
}
