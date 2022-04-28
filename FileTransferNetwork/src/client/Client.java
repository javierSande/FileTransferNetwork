/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import client.console.ClientConsole;
import client.console.ClientConsole.Writer;
import client.data.FilesSet;
import client.data.SharedFilesMap;
import client.data.UsersList;
import client.network.ServerListener;
import client.view.ClientWindow;
import common.Observable;
import common.Observer;
import common.User;
import common.messages.*;

public class Client implements Observable<Client> {
	
	public static final int MAX_TRANSMISSIONS = 5;
	
	private int id;
	private String name;
	private String ip;
	private String serverIp;
	private int port;
	
	private String clientDir = "";
	
	private SharedFilesMap filesToShare;
	
	private UsersList usersOnServer;
	private FilesSet filesOnServer;
	private volatile List<Observer<Client>> observers;
	
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	private ServerListener listener;
	
	private Semaphore transmissionSemaphore;
	private ReentrantLock observersLock;
	
	// outputLock: used to atomically access the output stream
	private transient ReentrantLock outputLock = new ReentrantLock();

	public Client(String name, String serverIp, int port) throws IOException {
		super();
		this.name = name;
		this.serverIp = serverIp;
		this.port = port;
		
		this.filesToShare = new SharedFilesMap();
		
		this.socket = new Socket(serverIp, port);
		
		this.usersOnServer = new UsersList();
		this.filesOnServer = new FilesSet();
		this.observers = new ArrayList<Observer<Client>>();
		
		transmissionSemaphore = new Semaphore(MAX_TRANSMISSIONS);
		observersLock = new ReentrantLock();
		
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}
	
	/*
	 * Getters and setters
	 */
	
	public int getId() {
		return id;
	}
	
	public void setDir() {
		clientDir = String.format("Client%d", id);
		// Creates a new directory to store the downloads
		File f = new File(clientDir); 
		f.mkdir();
	}
	
	public String getDir() {
		return clientDir;
	}

	public String getIp() {
		return ip;
	}

	public String getServerIp() {
		return serverIp;
	}

	public int getPort() {
		return port;
	}
	
	public Socket getSocket() {
		return socket;
	}

	public ObjectOutputStream getOut() {
		return out;
	}

	public ObjectInputStream getIn() {
		return in;
	}
	
	private void setListener(ServerListener serverListener) {
		listener = serverListener;
		listener.start();
	}
	
	public Semaphore getSemaphore() {
		return transmissionSemaphore;
	}
	
	
	public List<User> getUserList() {
		return usersOnServer.getUsers();
	}
	
	
	/*
	 * Manage shared files methods
	 */
	
	public String getFilePath(String file) {
		return filesToShare.getFilePath(file);
	}
	
	public Set<String> getSharedFiles() {
		return filesToShare.getFiles();
	}
	
	public void addSharedFiles(Map<String, String> files) {
		filesToShare.addFiles(files);
		sendUserData();
		notifyChange();
	}
	
	public void addSharedFile(String file, String path) {
		filesToShare.addFile(file, path);
		sendUserData();
		notifyChange();
	}
	
	public void deleteSharedFiles(List<String> files) {
		for (String f: files)
			filesToShare.deleteFile(f);
		sendUserData();
		notifyChange();
	}
	
	public void deleteSharedFile(String file) {
		filesToShare.deleteFile(file);
		sendUserData();
		notifyChange();
	}
	
	public List<String> getFiles() {
		List<String> files = new ArrayList<String>();
		Set<String> sharedFiles = filesToShare.getFiles();
		for (String f: filesOnServer.getFiles()) {
			if (!sharedFiles.contains(f))
				files.add(f);
		}
		return files;
	}
	
	
	/* public void updateServerInfo(List<User> users, List<String> files) 
	 * 
	 * Updates the server information stored in the client.
	 * */
	
	public void updateServerInfo(List<User> users, Set<String> files) {
		usersOnServer.setUsers(users);
		filesOnServer.setFiles(files);
		notifyChange();
	}
	
	
	/*
	 * Observable methods
	 */
	
	@Override
	public void addObserver(Observer<Client> o) {
		observersLock.lock();
		observers.add(o);
		o.update(this);
		observersLock.unlock();
	}
	
	public void notifyChange() {
		observersLock.lock();
		Client c = this;
		for (Observer<Client> o: observers)
			SwingUtilities.invokeLater(new Runnable() {
	
			    @Override
			    public void run() {
			        o.update(c);
			    }
	
			});
		observersLock.unlock();
	}

	
	/* private boolean startConnection()
	 * 
	 * Starts the connection with the server
	 * 
	 * First, sends a connection request message and waits for the server confirmation. 
	 * The confirmation message will contain its client id.
	 * Once the confirmation is received, it will request the server's users information and waits for response.
	 * Finally, it will store the user info and the available files.
	 * 
	 * Returns true in case the connection is successfully established.
	 * */
	
	private boolean startConnection() {
		ClientConsole.print(Writer.CLIENT, "Starting connection...");
		try {			
			ip = socket.getLocalAddress().getHostAddress();
			
			out.writeObject(new ConnectionMessage(ip, serverIp, name, new HashSet<String>(filesToShare.getFiles())));
			
			Message m = (Message) in.readObject();
			
			if (m.type != MessageType.CONFIRM_CONNECTION) {
				JOptionPane.showMessageDialog(null, "Incorrect response from server");
				return false;
			}
			
			ConfirmConnectionMessage cm = (ConfirmConnectionMessage) m;
			id = cm.getUser().getId();
			sendMessage(new UserListMessage(ip, serverIp));
			
			m = (Message) in.readObject();
			
			while (m.type != MessageType.CONFIRM_USER_LIST) {
				m = (Message) in.readObject();
			}
			
			ConfirmUserListMessage um = (ConfirmUserListMessage) m;
			updateServerInfo(um.getUserList(), um.getFileList());
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
			return false;
		}
		ClientConsole.print(Writer.CLIENT, "Connected to server!");
		return true;
	}
	
	
	/* public void sendUserData()
	 * 
	 * Sends the information of the client to the server.
	 * It is invoked when there is any change on the shared files list.
	 * */
	
	public synchronized void sendUserData() {
		try {
			sendMessage(new UserUpdateMessage(ip, serverIp, id, getSharedFiles()));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	
	/* public void sendMessage(Message m)
	 * 
	 * Sends a message through the output stream of the client.
	 * This function is invoked whenever we want to output any message once the
	 * message object is created.
	 * 
	 * It locks the outputLock to ensure that we are only writing this message.
	 * Then it flushes to ensure that we output everything pending in the output
	 * list. Finally, the lock is unlocked.
	 */
	
	public void sendMessage(Message m) throws IOException {
		outputLock.lock();
		out.writeObject(m);
		out.flush();
		outputLock.unlock();
	}

	
	/* public void endConnection()
	 * 
	 * Ends the connection with the server.
	 * Sends a terminate connection message. 
	 * The confirmation message will be received by the server listener.
	 * */
	
	public void endConnection() {
		ClientConsole.print(Writer.CLIENT, "Starting disconnection...");
		try {
			sendMessage(new TerminateMessage(ip, serverIp));
		} catch (Exception e) {
			ClientConsole.print(Writer.CLIENT, "Failed to disconnect from server!");
			e.printStackTrace();
		}
	}
	
	
	/* public void requestFile(String file)
	 * 
	 * Request a file to the server.
	 * */
	
	public void requestFile(String file) {
		try {
			sendMessage(new FileRequestMessage(ip, serverIp, file));
			ClientConsole.print(Writer.CLIENT, String.format("Requesting file %s", file));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	/*
	 * Main method
	 * 
	 * Creates the client, starts the connection and its GUI.
	 **/
	
	public static void main(String args[]) {
		Client client = null;
		String ip, name;
		int port;
		boolean connected = false;
		
		try {
			do {
				try {
					ip = JOptionPane.showInputDialog("Input the server IP: ", "localhost");
					if (ip == null) System.exit(0);
					
					port = Integer.parseInt(JOptionPane.showInputDialog("Input the server Port: "));
					name = JOptionPane.showInputDialog("Input your username: ");
					
					client = new Client(name, ip, port);
					connected = client.startConnection();
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "Invalid data", "ERROR", JOptionPane.ERROR_MESSAGE);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Connection rejected", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			} while(!connected);
			
			client.setDir();
			
			Client myClient = client;
			myClient.setListener(new ServerListener(client));
			
			new ClientWindow(myClient);
			
			myClient.listener.join();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		System.exit(0);
	}
}
