package client.data;

import java.util.HashSet;
import java.util.Set;

public class FilesTable {
	private Set<String> fileList;
	
	public FilesTable() {
		fileList = new HashSet<String>();
	}
	
	public synchronized void setFiles(Set<String> list) {
		fileList = list;
	}
	
	public synchronized Set<String> getFiles() {
		Set<String> list = new HashSet<String>();
		for (String f: fileList) 
			list.add(f);
		return list;
	}
}
