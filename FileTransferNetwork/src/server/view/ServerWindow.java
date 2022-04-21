package server.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import server.view.files.*;
import server.view.users.UsersTablePanel;
import server.network.Server;

@SuppressWarnings("serial")
public class ServerWindow extends JFrame {

    private UsersTablePanel usersPanel;
    private FilesTablePanel filesPanel;

    private Server server;

    public ServerWindow(Server server) {
    	this.server = server;
    	init();
    }

    private boolean init() {
    	
    	this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle(String.format("Server at port: %d", server.getPort()));
        this.setPreferredSize(new Dimension(860, 580));
        this.setLocation(25, 50);
        this.setBackground(Color.WHITE);
        this.setLayout(null);

        //Users Panel
        usersPanel = new UsersTablePanel(server);
        usersPanel.setSize(new Dimension(500, 500));
        usersPanel.setLocation(new Point(20,20));
        this.add(usersPanel);
        
        //Files Panel
        filesPanel = new FilesTablePanel(server);
        filesPanel.setSize(new Dimension(300, 500));
        filesPanel.setLocation(new Point(540,20));
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