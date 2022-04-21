package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import common.User;
import server.view.Observable;
import server.data.ClientsTable;
import server.data.FilesTable;
import server.network.ClientListener;
import server.view.Observer;
import server.view.ServerWindow;

public class Server implements Observable<Server> {
	
	private String ip;
	private int port;
	
	private ClientsTable clientsTable;
	private FilesTable filesTable;
	
	private ServerSocket socket;
	
	private boolean active;
	
	private ReentrantLock activeLock = new ReentrantLock();
	private ReentrantLock userIdsLock = new ReentrantLock();
	private ReentrantLock connectionsLock = new ReentrantLock();
	
	private CyclicBarrier closeBarrier;
	
	private List<ClientListener> connections;
	private int nUsers;
	
	private List<Observer<Server>> observers;
	
	public Server() throws IOException {
		active = true;
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
	
	public boolean isActive() {
		return active;
	}
	
	public String getIp() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public List<User> getUsers() {
		return clientsTable.getUsers();
	}
	
	public User getUser(int id) {
		return clientsTable.getUser(id);
	}
	
	public User getSender(String file) {
		return filesTable.getSender(file);
	}
	
	public Set<String> getFiles() {
		return filesTable.getFiles();
	}
	
	public CyclicBarrier getCloseBarrier() {
		return closeBarrier;
	}
	
	
	/*
	 * User management methods
	 */
	
	/* public void removeUser(User u)
	 * 
	 * Removes the user and its files from the registers.
	 **/
	
	public void removeUser(User u) {
		clientsTable.removeUser(u);
		filesTable.removeUserFiles(u);
		
		notifyUpdate();
	}
	
	/* public void addUser(User u)
	 * 
	 * First, assigns an identifier to the user.
	 * Then, ads the user and its files to the register and notifies the observers.
	 **/
	
	public void addUser(User u) {
		userIdsLock.lock();
		u.setId(++nUsers);
		userIdsLock.unlock();
		clientsTable.addUser(u);
		filesTable.addUserFiles(u);
		
		notifyUpdate();
	}
	
	/* public void updateUser(int id, Set<String> files)
	 * 
	 * Updates the user files and notifies the observers.
	 **/
	
	public void updateUser(int id, Set<String> files) {
		User u = clientsTable.getUser(id);
		filesTable.removeUserFiles(u);
		u.setSharedData(files);
		filesTable.addUserFiles(u);
		
		notifyUpdate();
	}
	
	
	/* 
	 * Connection management methods
	 */
	
	/* public void endSession()
	 * 
	 * Ends the session with all the active clients
	 * 
	 * First, updates the server status to inactive to avoid new clients to connect.
	 * Second, creates a barrier to synchronize the clients disconnection.
	 * Third, starts to send terminate the connections.
	 * Finally, waits to all the client listeners to end their sessions.
	 **/
	
	public void endSession() throws Exception {
		activeLock.lock();
		active = false;
		activeLock.unlock();
		
		closeBarrier = new CyclicBarrier(connections.size() + 1);
		for (ClientListener l: connections) {
			if (l.isActive())
				l.endConnection();
		}
		socket.close();
		closeBarrier.await();
	}
	
	
	/* public void addConnection(Socket s)
	 * 
	 * Registers a new client listener
	 * 
	 * First, checks the server status.
	 * If it is active, creates a Client listener for the new user and registers it as an observer of the server.
	 * If not, closes the socket with the new user.
	 **/
	
	public void addConnection(Socket s) throws IOException {
		activeLock.lock();
		if (active) {
			connectionsLock.lock();
			ClientListener c = new ClientListener(this, s);
			c.start();
			connections.add(c);
			addObserver(c);
			connectionsLock.unlock();
		} else {
			s.close();
		}
		activeLock.unlock();
	}
	
	
	/* public void removeConnection(ClientListener c)
	 * 
	 * Removes a client listener
	 * 
	 * First, checks the server status and removes the listener.
	 * If it is not active, it waits all the clients to disconnect.
	 **/
	
	public void removeConnection(ClientListener c) throws InterruptedException, BrokenBarrierException {
		activeLock.lock();
		boolean active = isActive();
		connectionsLock.lock();
		connections.remove(c);
		connectionsLock.unlock();
		activeLock.unlock();
		
		if (!active)
			closeBarrier.await();
	}
	
	
	/*
	 * Observable methods
	 */
	
	@Override
	public synchronized void addObserver(Observer<Server> o) {
		observers.add(o);
	}
	
	@Override
	public synchronized void removeObserver(Observer<Server> o) {
		observers.remove(o);
	}
	
	public synchronized void notifyUpdate() {
		for (Observer<Server> o: observers) {
			o.update(this);
		}
	}
	
	
	/*
	 * Main method
	 * 
	 * Creates the server, its GUI and waits for new connections.
	 **/
	
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
			
			while(server.isActive()) {
				Socket s = server.socket.accept();
				server.addConnection(s);
			}
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "SERVER ERROR", JOptionPane.ERROR_MESSAGE);	
			e.printStackTrace();
		}
	}
}