package server.view.files;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import server.network.Server;


public class FilesTablePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public FilesTablePanel(Server server) {
		setLayout(new BorderLayout());
		
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 2), "Files available",
				TitledBorder.LEFT, TitledBorder.TOP));
		FilesTableModel filesTableModel = new FilesTableModel();
		server.addObserver(filesTableModel);

		JTable table = new JTable(filesTableModel);
		table.setFillsViewportHeight(true);
		
		this.add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		
		setPreferredSize(new Dimension(300, 500));
	}
}
