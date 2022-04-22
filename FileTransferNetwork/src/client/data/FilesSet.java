package client.data;

import java.util.HashSet;
import java.util.Set;

import common.Monitor;

public class FilesSet extends Monitor {
	private Set<String> fileList;
	
	public FilesSet() {
		fileList = new HashSet<String>();
	}
	
	public synchronized void setFiles(Set<String> list) {
		startWrite();
		fileList = list;
		notify();
	}
	
	public Set<String> getFiles() {
		startRead();
		Set<String> list = new HashSet<String>();
		for (String f: fileList) 
			list.add(f);
		endRead();
		return list;
	}
}
