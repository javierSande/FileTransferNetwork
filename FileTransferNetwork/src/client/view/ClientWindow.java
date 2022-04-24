/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package client.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import client.Client;
import client.view.files.ServerFilesTablePanel;
import client.view.files.SharedFilesTablePanel;
import client.view.users.UsersTablePanel;

@SuppressWarnings("serial")
public class ClientWindow extends JFrame {

    private UsersTablePanel usersPanel;
    private ServerFilesTablePanel serverFilesPanel;
    private SharedFilesTablePanel sharedFilesPanel;

    private Client client;

    public ClientWindow(Client client) {
    	this.client = client;
    	init();
    }

    private boolean init() {
    	
    	this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle(String.format("Client %d", client.getId()));
        this.setPreferredSize(new Dimension(540, 580));
        this.setLocation(25, 50);
        this.setBackground(Color.WHITE);
        this.setLayout(null);

        //Users Panel
        usersPanel = new UsersTablePanel(client);
        usersPanel.setSize(new Dimension(500, 250));
        usersPanel.setLocation(new Point(20,20));
        this.add(usersPanel);
        

        //Server Files Panel
        serverFilesPanel = new ServerFilesTablePanel(client);
        serverFilesPanel.setSize(new Dimension(240, 250));
        serverFilesPanel.setLocation(new Point(20,280));
        this.add(serverFilesPanel);
        
        //Shared Files Panel
        sharedFilesPanel = new SharedFilesTablePanel(client);
        sharedFilesPanel.setSize(new Dimension(240, 250));
        sharedFilesPanel.setLocation(new Point(280,280));
        this.add(sharedFilesPanel);


        this.setResizable(false);
        this.setSize(540, 580);

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