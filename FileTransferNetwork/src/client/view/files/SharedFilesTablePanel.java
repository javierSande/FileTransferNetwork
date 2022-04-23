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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.Client;

public class SharedFilesTablePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private Client client;
	
	private SharedFilesTableModel filesTableModel;
	private JTable table;
	
	private JButton addButton;
	private JButton removeButton;
	
	private JPanel buttonsPanel;

	public SharedFilesTablePanel(Client client) {
		this.client = client;
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 2), "Shared Files",
				TitledBorder.LEFT, TitledBorder.TOP));
		filesTableModel = new SharedFilesTableModel();
		client.addObserver(filesTableModel);

		table = new JTable(filesTableModel);
		table.setFillsViewportHeight(true);
		table.setSize(new Dimension(200, 200));
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		      public void valueChanged(ListSelectionEvent e) {
		          int[] selectedRow = table.getSelectedRows();

		          if (selectedRow.length > 0) 
		        	  removeButton.setEnabled(true);
		          else
		        	  removeButton.setEnabled(false);
		        }

			});
		
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(200, 200));
		
		this.add(scrollPane, BorderLayout.PAGE_START);
		
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		this.add(buttonsPanel);
		
		addButton = new JButton("Add");
		addButton.setPreferredSize(new Dimension(100, 20));
		addButton.addActionListener(this);
		addButton.setForeground(Color.white);
		addButton.setContentAreaFilled(false);
		addButton.setBackground(new Color(106, 196, 114, 77));
		addButton.setOpaque(true);
		addButton.setBorderPainted(false);
		buttonsPanel.add(addButton);
		
		removeButton = new JButton("Remove");
		removeButton.setPreferredSize(new Dimension(100, 20));
		removeButton.addActionListener(this);
		removeButton.setForeground(Color.white);
		removeButton.setBackground(Color.red);
		removeButton.setOpaque(true);
		removeButton.setBorderPainted(false);
		removeButton.setEnabled(false);
		buttonsPanel.add(removeButton);
		
		setPreferredSize(new Dimension(200, 250));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == removeButton) {	
			int rows[] = table.getSelectedRows();
			List<String> files = new ArrayList<String>();
			for (int i: rows)
				files.add((String)filesTableModel.getValueAt(i, 0));
			client.deleteSharedFiles(files);
			
			removeButton.setEnabled(false);	
		} else if (e.getSource() == addButton) {
			Map<String, String> files = selectFiles();
			client.addSharedFiles(files);
		}
	}
	
    public Map<String, String> selectFiles() {
    	Map<String, String> files = new HashMap<String, String>();
    	
    	this.setVisible(true);
    	JFileChooser jfc = new JFileChooser();
	    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    jfc.setMultiSelectionEnabled(true);
	    if( jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ){
	        for(File f: jfc.getSelectedFiles())
	        	files.put(f.getName(), f.getAbsolutePath());
	    }
	    return files;
    }
}
