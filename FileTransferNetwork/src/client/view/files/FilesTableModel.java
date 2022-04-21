package client.view.files;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import client.network.Client;
import server.view.Observer;

@SuppressWarnings("serial")
public class FilesTableModel extends AbstractTableModel implements Observer<Client> {

	private List<String> files;
	private final String[] columnNames = { "Name" };
	
	public FilesTableModel() {
		files = new ArrayList<String>();
	}
	
	@Override
	public int getRowCount() {
		return files.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return files.get(rowIndex);
	}
	
	@Override
	public void update(Client c) {
		files = c.getFilesList();
		fireTableStructureChanged();
	}

}
