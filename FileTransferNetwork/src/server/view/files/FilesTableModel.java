/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package server.view.files;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import common.Observer;
import server.Server;

@SuppressWarnings("serial")
public class FilesTableModel extends AbstractTableModel implements Observer<Server> {

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
	public synchronized void update(Server s) {
		files.clear();
		files.addAll(s.getFiles());
		fireTableStructureChanged();
	}

}
