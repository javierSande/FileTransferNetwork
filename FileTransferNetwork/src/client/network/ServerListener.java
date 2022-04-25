/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package client.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import client.Client;
import client.console.ClientConsole;
import client.console.ClientConsole.Writer;
import common.exceptions.DisconnectionException;
import common.exceptions.MessageException;
import common.messages.*;

public class ServerListener extends Thread{
	
	
	private Client client;
	
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	private volatile boolean active;

	public ServerListener(Client client) throws IOException {
		this.client = client;
		this.socket = client.getSocket();
		this.in = client.getIn();
		this.out = client.getOut();
		this.active = true;
	}

	@Override
	public void run() {	
		try {
			while (active) {
				try {
					Message m = (Message) in.readObject();
					switch(m.type) {
					
					case CONFIRM_USER_LIST:
						// Server confirmation of the list of users (it is requested by the client
						// the moment it is created)
						ConfirmUserListMessage um = (ConfirmUserListMessage) m;
						client.updateServerInfo(um.getUserList(), um.getFileList());
						break;
						
					case SEND_REQUEST:
						// Message that the server uses to tell us that this is the selected client to
						// send a request to another client that made the request
						SendRequestMessage srm = (SendRequestMessage) m;
						ClientConsole.print(Writer.LISTENER, String.format("Client %d requested file %s", srm.getReceiver().getId(), srm.getFile()));
						
						Emisor e = new Emisor(client, srm.getFile());
						e.start();
						ClientConsole.print(Writer.LISTENER, String.format("Sending file %s", srm.getFile()));
						client.sendMessage(new ClientServerReadyMessage(client.getIp(), client.getServerIp(), e.getPort(),srm.getReceiver(), srm.getFile()));
						break;
						
					case SERVER_CLIENT_READY:
						// Message that the server uses to tell the client that requested a file that there
						// is a chosen sender client to establish the connection
						ServerClientReadyMessage sm = (ServerClientReadyMessage) m;
						Receptor r = new Receptor(client, sm.getIp(), sm.getPort(), sm.getFile());
						r.start();
						ClientConsole.print(Writer.LISTENER, String.format("Receiving file %s", sm.getFile()));
						break;
						
					case SERVER_UPDATE:
						// Message received from the server whenever there is a data update of the users connected
						ServerUpdateMessage sum = (ServerUpdateMessage) m;
						client.updateServerInfo(sum.getUserList(), sum.getFileList());
						break;
						
					case TERMINATE:
						// Message received by all the clients when the server ends the session
						client.sendMessage(new ConfirmTerminateMessage(client.getIp(), client.getServerIp()));
						throw new DisconnectionException("Server disconnected!");
						
					case CONFIRM_TERMINATE:
						// Confirmation of end of session received by the client from the server when the client
						// wants to end the connection
						out.close();
						in.close();
						socket.close();
						active = false;
						break;
						
					case ERROR:
						// Error message, mainly for unavailable files purposes
						JOptionPane.showMessageDialog(null,((ErrorMessage)m).getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
						break;
						
					default:
						throw new MessageException("Invalid message");
					} 
				} catch (MessageException e){
					client.sendMessage(new ErrorMessage(client.getIp(), client.getServerIp(), e.getMessage()));
					System.err.println(e.getMessage());
				}
			}
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} finally {
			ClientConsole.print(Writer.LISTENER, "Disconnected from server!");
		}
	}
}
