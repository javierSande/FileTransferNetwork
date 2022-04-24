/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package client.network;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

import javax.swing.JOptionPane;

import client.Client;
import common.messages.DataMessage;
import common.messages.ErrorMessage;
import common.messages.Message;
import common.messages.MessageType;

public class Receptor extends Thread {
	
	private Client client;
	
	private Socket socket;
	private ObjectInputStream in;
	
	private String file;

	public Receptor(Client client, String ip, int port, String file) throws IOException {
		this.client = client;
		this.socket = new Socket(ip,port);
		this.file = file;
	}
	
	public void saveFile() throws Exception {  
		File dir = new File(client.getDir());
		File f = new File(dir, file);
		f.createNewFile();

		RandomAccessFile fw = new RandomAccessFile(f, "rw"); 
		
		Message m = (Message) in.readObject();
		
		while (m.type == MessageType.DATA) {
			byte[] b = ((DataMessage) m).getData();
			int size = ((DataMessage) m).getSize();
			fw.write (b, 0, size);
			m = (Message) in.readObject();
		}
		
		fw.close();
		
		if (m.type == MessageType.ERROR) {
			ErrorMessage em = (ErrorMessage) m;
			JOptionPane.showMessageDialog(null, em.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
		} else if (m.type == MessageType.EOF)
			client.addSharedFile(file, f.getAbsolutePath());
		else 
			throw new Exception("Failed to transfer file");
		
		in.close();   
		socket.close();
	}

	@Override
	public void run() {	
		try {
			in = new ObjectInputStream(socket.getInputStream());
			saveFile();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
			System.err.print(e.getMessage());
			e.printStackTrace();
		}
	}
}
