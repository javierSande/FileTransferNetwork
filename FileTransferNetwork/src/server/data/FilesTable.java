package server.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import common.User;

public class FilesTable {
	private Map<String, Set<User>> fileMap;
	
	public FilesTable() {
		fileMap = new HashMap<String, Set<User>>();
	}
	
	public synchronized Set<String> getFiles() {
		Set<String> list = new HashSet<String>();
		for (String f: fileMap.keySet()) 
			list.add(f);
		return list;
	}
	
	public synchronized User getSender(String file) {
		return fileMap.get(file).iterator().next();
	}
	
	public synchronized void addUserFiles(User u) {
		for (String f: u.getSharedData()) {
			if (!fileMap.containsKey(f))
				fileMap.put(f, new HashSet<User>());
			fileMap.get(f).add(u);
		}
	}
	
	public synchronized void removeUserFiles(User u) {
		for (String f: u.getSharedData()) {
			fileMap.get(f).remove(u);
			if (fileMap.get(f).isEmpty())
				fileMap.remove(f);
		}
	}
}
