package client.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import client.network.Client;
import client.view.files.FilesTablePanel;
import client.view.users.UsersTablePanel;

@SuppressWarnings("serial")
public class ClientWindow extends JFrame {

    private UsersTablePanel usersPanel;
    private FilesTablePanel filesPanel;

    private Client client;

    public ClientWindow(Client client) {
    	this.client = client;
    	init();
    }

    private boolean init() {
    	
    	this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle(String.format("Client %d", client.getId()));
        this.setPreferredSize(new Dimension(760, 320));
        this.setLocation(25, 50);
        this.setBackground(Color.WHITE);
        this.setLayout(null);

        //Users Panel
        usersPanel = new UsersTablePanel(client);
        usersPanel.setSize(new Dimension(500, 250));
        usersPanel.setLocation(new Point(20,20));
        this.add(usersPanel);
        

        //Files Panel
        filesPanel = new FilesTablePanel(client);
        filesPanel.setSize(new Dimension(200, 250));
        filesPanel.setLocation(new Point(540,20));
        this.add(filesPanel);


        this.setResizable(false);
        this.setSize(760, 320);

        this.pack();
        this.setVisible(true);
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	try {
					client.endConnection();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
            }
        });

        return true;
    }
}