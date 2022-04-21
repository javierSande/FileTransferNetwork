package server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import common.User;
import server.view.Observable;
import server.data.ClientsTable;
import server.data.FilesTable;
import server.view.Observer;
import server.view.ServerWindow;

public class Server implements Observable<Server> {
	
	private String ip;
	private int port;
	
	private ClientsTable clientsTable;
	private FilesTable filesTable;
	
	private ServerSocket socket;
	
	ReentrantLock lock = new ReentrantLock();
	
	private List<ClientListener> connections;
	private int nUsers;
	
	private List<Observer<Server>> observers;
	
	public Server() throws IOException {
		socket = new ServerSocket(0);
		ip = socket.getInetAddress().getHostAddress();
		port = socket.getLocalPort();
		
		clientsTable = new ClientsTable();
		filesTable = new FilesTable();
		nUsers = 0;
		
		connections = new ArrayList<ClientListener>();
		observers = new ArrayList<Observer<Server>>();
	}
	
	/*
	 * Getters and setters
	 */
	
	public String getIp() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public List<User> getUsers() {
		return clientsTable.getUsers();
	}
	
	public User getSender(String file) {
		return filesTable.getSender(file);
	}
	
	public Set<String> getFiles() {
		return filesTable.getFiles();
	}
	
	/*
	 * Manage users
	 */
	
	public void removeUser(User u) {
		clientsTable.removeUser(u);
		filesTable.removeUserFiles(u);
		
		for (Observer<Server> o: observers) {
			o.update(this);
		}
	}
	
	public void addUser(User u) {
		lock.lock();
		u.setId(++nUsers);
		lock.unlock();
		clientsTable.addUser(u);
		filesTable.addUserFiles(u);
		
		notifyUpdate();
	}
	
	public void updateUser(int id, Set<String> files) {
		User u = clientsTable.getUser(id);
		filesTable.removeUserFiles(u);
		u.setSharedData(files);
		filesTable.addUserFiles(u);
		
		notifyUpdate();
	}
	
	public User getUser(int id) {
		return clientsTable.getUser(id);
	}
	
	
	/* 
	 * Manage session
	 */
	
	public void endSession() throws Exception {
		for (ClientListener l: connections) {
			if (l.isActive())
				l.endConnection();
		}
		socket.close();
	}
	
	public void removeConnection(ClientListener c) {
		connections.remove(c);
		removeObserver(c);
	}
	
	
	/*
	 * Observable methods
	 */
	
	@Override
	public void addObserver(Observer<Server> o) {
		observers.add(o);
	}
	
	@Override
	public void removeObserver(Observer<Server> o) {
		observers.remove(o);
	}
	
	public void notifyUpdate() {
		for (Observer<Server> o: observers) {
			o.update(this);
		}
	}
	
	
	/*
	 *  Main method
	 */
	
	public static void main(String arg[]) {
		try {
			Server server = new Server();
			
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						new ServerWindow(server);
					}
				});
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
			
			while(true) {
				Socket s = server.socket.accept();
				ClientListener c = new ClientListener(server, s);
				server.connections.add(c);
				server.addObserver(c);
				c.start();
			}
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);	
			e.printStackTrace();
		}
	}
}
