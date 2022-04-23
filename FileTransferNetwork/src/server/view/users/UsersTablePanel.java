/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package server.view.users;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import server.Server;


public class UsersTablePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public UsersTablePanel(Server server) {
		setLayout(new BorderLayout());
		
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 2), "Users online",
				TitledBorder.LEFT, TitledBorder.TOP));
		UsersTableModel usersTableModel = new UsersTableModel();
		server.addObserver(usersTableModel);

		JTable table = new JTable(usersTableModel);
		table.setFillsViewportHeight(true);
		
		this.add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		
		setPreferredSize(new Dimension(500, 300));
	}
}
