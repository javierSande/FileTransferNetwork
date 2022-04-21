package client.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import client.Client;
import common.exceptions.DisconnectionException;
import common.exceptions.MessageException;
import common.messages.*;

public class ServerListener extends Thread{
	
	
	private Client client;
	
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	private boolean active;

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
			System.out.println("Client listenning to the server");
			
			while (active) {
				try {
					Message m = (Message) in.readObject();
					switch(m.type) {
					case SEND_REQUEST:
						SendRequestMessage srm = (SendRequestMessage) m;
						System.out.println(String.format("Client %d requested file %s", srm.getReceiver().getId(), srm.getFile()));
						
						Emisor e = new Emisor(client, srm.getFile());
						e.start();
						out.writeObject(new ClientServerReadyMessage(client.getIp(), client.getServerIp(), e.getPort(),srm.getReceiver(), srm.getFile()));
						out.flush();
						break;
						
					case SERVER_CLIENT_READY:
						ServerClientReadyMessage sm = (ServerClientReadyMessage) m;
						Receptor r = new Receptor(client, sm.getIp(), sm.getPort(), sm.getFile());
						r.start();
						break;
						
					case SERVER_UPDATE:
						ServerUpdateMessage sum = (ServerUpdateMessage) m;
						client.updateServerInfo(sum.getUserList(), sum.getFileList());
						break;
						
					case TERMINATE:
						out.writeObject(new ConfirmTerminateMessage(client.getIp(), client.getServerIp()));
						out.flush();
						throw new DisconnectionException("Server disconnected!");
						
					case CONFIRM_TERMINATE:
						out.close();
						in.close();
						socket.close();
						System.out.println("Disconnected to server!");
						active = false;
						break;
						
					case ERROR:
						JOptionPane.showMessageDialog(null,((ErrorMessage)m).getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
						break;
						
					default:
						throw new MessageException("Invalid message");
					} 
				} catch (MessageException e){
					out.writeObject(new ErrorMessage(client.getIp(), client.getServerIp(), e.getMessage()));
					System.err.println(e.getMessage());
				}
			}
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
}
