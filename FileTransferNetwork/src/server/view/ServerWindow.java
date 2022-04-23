/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package server.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import server.Server;
import server.view.files.*;
import server.view.users.UsersTablePanel;

@SuppressWarnings("serial")
public class ServerWindow extends JFrame {

	private JPanel connectionLabel;
	
    private UsersTablePanel usersPanel;
    private FilesTablePanel filesPanel;

    private Server server;

    public ServerWindow(Server server) {
    	this.server = server;
    	init();
    }

    private boolean init() {
    	
    	this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Server");
        this.setPreferredSize(new Dimension(860, 580));
        this.setLocation(25, 50);
        this.setBackground(Color.WHITE);
        this.setLayout(null);
        
        connectionLabel = new JPanel();
        connectionLabel.setLayout(new FlowLayout(FlowLayout.LEFT));
        connectionLabel.setSize(new Dimension(800, 30));
        connectionLabel.setLocation(new Point(20,0));
        this.add(connectionLabel);
        
        JLabel ipLabel = new JLabel(String.format("IP: %s", server.getIp()));
        connectionLabel.add(ipLabel);
        
        connectionLabel.add(Box.createHorizontalStrut(20));
        
        JLabel portLabel = new JLabel(String.format("Port: %d", server.getPort()));
        connectionLabel.add(portLabel);
        

        //Users Panel
        usersPanel = new UsersTablePanel(server);
        usersPanel.setSize(new Dimension(500, 500));
        usersPanel.setLocation(new Point(20,30));
        this.add(usersPanel);
        
        //Files Panel
        filesPanel = new FilesTablePanel(server);
        filesPanel.setSize(new Dimension(300, 500));
        filesPanel.setLocation(new Point(540,30));
        this.add(filesPanel);


        this.setResizable(false);
        this.setSize(860, 640);

        this.pack();
        this.setVisible(true);
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	try {
					server.endSession();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
            }
        });

        return true;
    }
}