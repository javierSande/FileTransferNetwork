package client.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

import common.messages.*;

public class Emisor extends Thread {
	private Client client;
	
	private String receiverIp;
	private int port;
	
	private ServerSocket socket;
	private ObjectOutputStream out;
	
	private String file;

	public Emisor(Client client, String file) throws IOException {
		this.client = client;
		this.socket = new ServerSocket(0);
		this.port = socket.getLocalPort();
		this.file = file;
	}
	
	public int getPort() {
		return port;
	}
	
	public void sendFile() {  
		byte[] b = new byte[1024]; 
		File f = new File(client.getFilePath(file));  
		try {
			InputStream ins = new FileInputStream(f);
			int n = ins.read(b);
			while (n != -1) {
				out.writeObject( new DataMessage(client.getIp(), receiverIp, b.clone(), n));
				out.flush();
				n = ins.read(b);
			}
			ins.close();
			
			out.writeObject(new EOFMessage(client.getIp(), receiverIp));
		} catch (IOException e) { 
			e.getStackTrace();
		}
	}

	@Override
	public void run() {	
		try {
			client.getSemaphore().acquire();
			
			System.out.println(String.format("Start transmission of %s", file));
			
			Socket s = socket.accept();
			out = new ObjectOutputStream(s.getOutputStream());
			receiverIp = s.getInetAddress().getHostAddress();
			
			sendFile();
			
			System.out.println(String.format("End transmission of %s", file));
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
			System.out.println(String.format("Filed to send %s", file));
			e.printStackTrace();
		} finally {
			client.getSemaphore().release();
		}
	}
}