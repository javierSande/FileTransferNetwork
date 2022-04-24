/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package client.view.files;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.Client;


public class ServerFilesTablePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private Client client;
	
	private ServerFilesTableModel filesTableModel;
	private JTable table;
	
	private JButton downloadButton;

	public ServerFilesTablePanel(Client client) {
		this.client = client;
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 2), "Files available",
				TitledBorder.LEFT, TitledBorder.TOP));
		filesTableModel = new ServerFilesTableModel();
		client.addObserver(filesTableModel);

		table = new JTable(filesTableModel);
		table.setFillsViewportHeight(true);
		table.setSize(new Dimension(200, 200));
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		      public void valueChanged(ListSelectionEvent e) {
		          int[] selectedRow = table.getSelectedRows();

		          if (selectedRow.length > 0) 
		        	  downloadButton.setEnabled(true);
		          else
		        	  downloadButton.setEnabled(false);
		        }

			});
		
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(200, 200));
		
		this.add(scrollPane, BorderLayout.PAGE_START);
		
		downloadButton = new JButton("Download");
		downloadButton.setEnabled(false);
		downloadButton.setPreferredSize(new Dimension(50, 20));
		downloadButton.setForeground(Color.white);
		downloadButton.setBackground(Color.blue);
		downloadButton.setOpaque(true);
		downloadButton.setBorderPainted(false);
		downloadButton.setEnabled(false);
		downloadButton.addActionListener(this);
		this.add(downloadButton, BorderLayout.PAGE_END);
		
		setPreferredSize(new Dimension(200, 250));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == downloadButton) {			
			int rows[] = table.getSelectedRows();
			for (int i: rows)
				client.requestFile((String)filesTableModel.getValueAt(i, 0));
			
			downloadButton.setEnabled(false);	
		}
	}
}
