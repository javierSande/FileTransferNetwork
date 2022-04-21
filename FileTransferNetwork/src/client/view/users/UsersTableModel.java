package client.view.users;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import client.network.Client;
import common.User;
import server.view.Observer;

@SuppressWarnings("serial")
public class UsersTableModel extends AbstractTableModel implements Observer<Client> {

	private List<User> users;
	private final String[] columnNames = { "Id", "Name", "IP", "Nº Files" };
	
	public UsersTableModel() {
		users = new ArrayList<User>();
	}
	
	@Override
	public int getRowCount() {
		return users.size();
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
		User u = users.get(rowIndex);
		switch(columnIndex) {
		case 0:
			return u.getId();
		case 1:
			return u.getName();
		case 2:
			return u.getIp();
		case 3:
			return u.getSharedData().size();
		}
		return null;
	}
	
	@Override
	public void update(Client c) {
		users = c.getUserList();
		fireTableStructureChanged();
	}

}
