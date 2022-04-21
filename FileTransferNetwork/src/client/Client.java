package client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import javax.swing.JOptionPane;

import client.data.FilesTable;
import client.data.UsersList;
import client.network.ServerListener;
import client.view.ClientWindow;
import common.User;
import common.messages.*;
import server.view.Observable;
import server.view.Observer;

public class Client implements Observable<Client> {
	
	public static final int MAX_TRANSMISSIONS = 1;
	
	private int id;
	private String name;
	private String ip;
	private String serverIp;
	private int port;
	
	private String clientDir = "";
	
	private Map<String,String> filesToShare;
	
	private UsersList usersOnServer;
	private FilesTable filesOnServer;
	private List<Observer<Client>> observers;
	
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	private ServerListener listener;
	
	private Semaphore transmissionSempaphore;

	public Client(String name, String serverIp, int port) throws IOException {
		super();
		this.name = name;
		this.serverIp = serverIp;
		this.port = port;
		
		this.filesToShare = new HashMap<String,String>();
		
		this.socket = new Socket(serverIp, port);
		
		this.usersOnServer = new UsersList();
		this.filesOnServer = new FilesTable();
		this.observers = new ArrayList<Observer<Client>>();
		
		transmissionSempaphore = new Semaphore(MAX_TRANSMISSIONS);
		
		System.out.println(String.format("ServerIP: %s", serverIp));
		System.out.println(String.format("ServerPort: %d", port));
		
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	
		System.out.println("Channels initialized");
	}
	
	/*
	 * Getters and setters
	 */
	
	public int getId() {
		return id;
	}
	
	public void initDir() {
		clientDir = String.format("Client%d", id);
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
	}
	
	public Semaphore getSemaphore() {
		return transmissionSempaphore;
	}
	
	
	public List<User> getUserList() {
		return usersOnServer.getUsers();
	}
	
	
	/*
	 * Manage shared files methods
	 */
	
	public String getFilePath(String file) {
		return filesToShare.get(file);
	}
	
	public Set<String> getSharedFiles() {
		return new HashSet<String>(filesToShare.keySet());
	}
	
	public void addSharedFiles(Map<String, String> files) {
		filesToShare.putAll(files);
		sendUserData();
		notifyChange();
	}
	
	public void deleteSharedFiles(List<String> file) {
		for (String f: file)
			deleteSharedFile(f);
		sendUserData();
		notifyChange();
	}
	
	public void deleteSharedFile(String file) {
		filesToShare.remove(file);
	}
	
	public List<String> getFiles() {
		List<String> files = new ArrayList<String>();
		for (String f: filesOnServer.getFiles()) {
			if (!filesToShare.containsKey(f))
				files.add(f);
		}
		return files;
	}
	
	
	/* public void updateServerInfo(List<User> users, List<String> files) 
	 * 
	 * Método encargado de actualizar los datos que el cliente almacena sobre el estado del servidor.
	 * Además, notificará a sus observadores de que se han realizado cambios.
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
		observers.add(o);
		o.update(this);
	}
	
	public void notifyChange() {
		for (Observer<Client> o: observers) {
			o.update(this);
		}
	}
	
	
	/* private boolean startConnection()
	 * 
	 * Método encargado de iniciar la conexión con el servidor
	 * 
	 * En primer lugar enviará un mensaje de solicitud de conexión y esperará a la confirmación del servidor.
	 * Una vez establecida esta conexion, solicitará la información de los usuarios al servidor.
	 * La respuesta del servidor incluirá la lista de usuarios conectados y los archivos disponibles.
	 * 
	 * Devolverá true en caso de que la conexión sea satisfactoria y false en caso contrario.
	 * */
	
	private boolean startConnection() {
		System.out.println("Starting connection...");
		try {			
			ip = socket.getLocalAddress().getHostAddress();
			
			out.writeObject(new ConnectionMessage(ip, serverIp, name, new HashSet<String>(filesToShare.keySet())));
			
			Message m = (Message) in.readObject();
			
			while (m.type != MessageType.CONFIRM_CONNECTION) {
				m = (Message) in.readObject();
			}
			
			ConfirmConnectionMessage cm = (ConfirmConnectionMessage) m;
			id = cm.getUser().getId();
			out.writeObject(new UserListMessage(ip, serverIp));
			
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
		System.out.println("Connected to server!");
		return true;
	}
	
	
	public void sendUserData() {
		try {
			out.writeObject(new UserUpdateMessage(ip, serverIp, id, getSharedFiles()));
			out.flush();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	
	/* public void endConnection()
	 * 
	 * Ends the connection with the server.
	 * Sends a terminate connection message. 
	 * The confirmation message will be received by the server listener.
	 * */
	
	public void endConnection() {
		System.out.println("Starting disconnection...");
		listener.interrupt();
		try {
			out.writeObject(new TerminateMessage(ip, serverIp));
			out.flush();
		} catch (Exception e) {
			System.out.println("Failed to disconnect from server!");
			e.printStackTrace();
		}
	}
	
	
	/* public void requestFile(String file)
	 * 
	 * Request a file to the server.
	 * */
	
	public void requestFile(String file) {
		try {
			out.writeObject(new FileRequestMessage(ip, serverIp, file));
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
		Client client;
		
		try {
			do {
				String ip = JOptionPane.showInputDialog("Input the server IP: ");
				int port = Integer.parseInt(JOptionPane.showInputDialog("Input the server Port: "));
				String name = JOptionPane.showInputDialog("Input your username: ");
				
				client = new Client(name, ip, port);
			} while(!client.startConnection());
			
			client.initDir();
			
			Client myClient = client;
			myClient.setListener(new ServerListener(client));
			myClient.listener.start();
			
			new ClientWindow(myClient);
			
			myClient.listener.join();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		System.exit(0);
	}
}
