/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import common.messages.Message;
import common.monitors.MonitorSyn;

public class User extends MonitorSyn implements Serializable {
	
	/* User
	 * 
	 * This class represents in a Monitor all the data required by the application of
	 * every user connected to the server to work correctly.
	 * Its goal is to represent the clients internally in the application.
	 * As these data is going to be written and read at different points of the
	 * application flow, it has the readers-writers problem. For this same reason,
	 * each method that tries to read or write data should follow the protocol of the
	 * Monitor class. 
	 */
	
	private static final long serialVersionUID = 1L;
	private int id;
	private final String name;
	
	private final String ip;
	private final int serverPort;
	private final int clientPort;
	
	// outputLock: used to atomically access the output stream
	private transient ReentrantLock outputLock = new ReentrantLock();
	
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
	
	private User(int id, String name, String ip, int clientPort, int serverPort, Set<String> sharedData) {
		this.id = id;
		this.name = name;
		this.ip = ip;
		this.clientPort = serverPort;
		this.serverPort = serverPort;
		this.sharedData = new HashSet<String>(sharedData);	
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
	
	public void sendMessage(Message m) throws IOException {
		outputLock.lock();
		out.writeObject(m);
		out.flush();
		outputLock.unlock();
	}

	public Set<String> getSharedData() {
		startRead();
		Set<String> data =  sharedData;
		endRead();
		return data;
	}
	
	public synchronized void setSharedData(Set<String> files) {
		startWrite();
		sharedData = files;
		endWrite();
	}

	// Add data to share
	public synchronized void shareData(String filepath) {
		startWrite();
		sharedData.add(filepath);
		endWrite();
	}
	
	public String toString() {
		startRead();
		String s =  String.format("%d %s (%s:%d)", id, name, ip, serverPort);
		endRead();
		return s;
	}
	
	public User clone() {
		startRead();
		User u = (new User(id, name, ip, clientPort, serverPort, sharedData));
		endRead();
		return u;
	}
}
