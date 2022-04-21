package common;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Set;

public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	
	private String ip;
	private int serverPort;
	private int clientPort;
	
	private transient ObjectOutputStream out;
	private transient ObjectInputStream in;
	
	private Set<String> sharedData;
	
	public User(String name, Socket socket, ObjectInputStream in, ObjectOutputStream out, Set<String> sharedData) {
		this.name = name;
		ip = socket.getInetAddress().getHostAddress();
		clientPort = socket.getPort();
		serverPort = socket.getLocalPort();
		
		this.in = in;
		this.out = out;
		this.sharedData = sharedData;	
	}
	
	// Getters and setters
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String getIp() {
		return ip;
	}
	
	public int getServerPort() {
		return serverPort;
	}

	public int getClientPort() {
		return clientPort;
	}

	public ObjectOutputStream getOut() {
		return out;
	}

	public ObjectInputStream getIn() {
		return in;
	}

	public Set<String> getSharedData() {
		return sharedData;
	}
	
	public void setSharedData(Set<String> files) {
		sharedData = files;
		
	}

	// Add data to share
	public void shareData(String filepath) {
		sharedData.add(filepath);
	}
	
	public String toString() {
		return String.format("%d %s (%s:%d)", id, name, ip, serverPort);
	}
}
