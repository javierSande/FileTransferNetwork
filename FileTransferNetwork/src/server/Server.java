/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import common.Observable;
import common.Observer;
import common.User;
import common.messages.Message;
import server.console.ServerConsole;
import server.data.ClientsTable;
import server.data.FilesTable;
import server.network.ClientListener;
import server.view.ServerWindow;

public class Server implements Observable<Server> {
	
	private String ip;
	private int port;
	
	private ClientsTable clientsTable;
	private FilesTable filesTable;
	
	private ServerSocket socket;
	
	private volatile boolean active;
	
	// activeLock: used to atomically check if the server is active
	private ReentrantLock activeLock = new ReentrantLock();
	// userIdsLock: used to atomically access the number of users and assign
	// an index to a new user
	private ReentrantLock userIdsLock = new ReentrantLock();
	// connectionsLock: used to atomically access the connections list (contains
	// the ClientListeners)
	private ReentrantLock connectionsLock = new ReentrantLock();
	// observersLock: used to atomically access the observers list (tables for the GUI)
	private ReentrantLock observersLock = new ReentrantLock();
	
	private volatile List<ClientListener> connections;
	private volatile int nUsers;
	
	private volatile List<Observer<Server>> observers;
	
	public Server() throws IOException {
		active = true;
		socket = new ServerSocket(0);
		ip = InetAddress.getLocalHost().getHostAddress();
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
	 * Then, starts to send terminate the connections.
	 * Finally, waits to all the client listeners to end their sessions. This is done through
	 * the arrival of the ConfirmTerminateMessage to each ClientListener.
	 **/
	
	public void endSession() throws Exception {
		activeLock.lock();
		active = false;
		activeLock.unlock();
		
		connectionsLock.lock();
		for (ClientListener l: connections)
			l.endConnection();
		connectionsLock.unlock();
		
		while(!connections.isEmpty());
		socket.close();
	}
	
	
	/* public void addConnection(Socket s)
	 * 
	 * Registers a new client listener
	 * 
	 * First, checks the server status.
	 * If it is active, creates a Client listener for the new user and registers it as an observer of the server.
	 * If not, closes the socket with the new user.
	 **/
	
	public void addConnection(ClientListener l) throws Exception {
		activeLock.lock();
		if (active) {
			connectionsLock.lock();
			connections.add(l);
			connectionsLock.unlock();
		} else {
			l.endConnection();
		}
		activeLock.unlock();
	}
	
	
	/* public void removeConnection(ClientListener c)
	 * 
	 * Removes a client listener
	 **/
	
	public void removeConnection(ClientListener c) {
		connectionsLock.lock();
		connections.remove(c);
		connectionsLock.unlock();
	}
	
	
	/* public void sendMessageToUser(int id, Message m)
	 * 
	 * Sends a message to a user
	 **/
	
	public void sendMessageToUser(int id, Message m) throws IOException {
		getUser(id).sendMessage(m);
	}
	
	
	/*
	 * Observable methods
	 */
	
	@Override
	public void addObserver(Observer<Server> o) {
		observersLock.lock();
		observers.add(o);
		observersLock.unlock();
	}
	
	
	public void notifyUpdate() {
		observersLock.lock();
		Server s = this;
		for (Observer<Server> o: observers)
			SwingUtilities.invokeLater(new Runnable() {
				
			    @Override
			    public void run() {
			        o.update(s);
			    }
	
			});
		observersLock.unlock();
		
		connectionsLock.lock();
		for (ClientListener l: connections)
			l.update(s);
		connectionsLock.unlock();
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
				ClientListener c = new ClientListener(server, s);
				c.start();
			}
			
		}  catch (SocketException e) {
			ServerConsole.print("Socket closed!");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "SERVER ERROR", JOptionPane.ERROR_MESSAGE);	
			e.printStackTrace();
		}
	}
}
