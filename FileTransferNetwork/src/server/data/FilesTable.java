package server.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import common.User;

public class FilesTable {
	private Map<String, Set<User>> fileMap;
	
	private int nReaders;
	
	public FilesTable() {
		nReaders = 0;
		fileMap = new HashMap<String, Set<User>>();
	}
	
	private synchronized void startRead() {
		nReaders++;
	}
	
	private synchronized void endRead() {
		nReaders--;
		if (nReaders == 0)
			notify();
	}
	
	public synchronized Set<String> getFiles() {
		startRead();
		Set<String> list = new HashSet<String>();
		for (String f: fileMap.keySet()) 
			list.add(f);
		endRead();
		return list;
	}
	
	public synchronized User getSender(String file) {
		startRead();
		User sender = fileMap.get(file).iterator().next();
		endRead();
		return sender;
	}
	
	public synchronized void addUserFiles(User u) {
		if (nReaders > 0)
			try {
				wait();
			} catch (InterruptedException e) { return; }
		for (String f: u.getSharedData()) {
			if (!fileMap.containsKey(f))
				fileMap.put(f, new HashSet<User>());
			fileMap.get(f).add(u);
		}
	}
	
	public synchronized void removeUserFiles(User u) {
		if (nReaders > 0)
			try {
				wait();
			} catch (InterruptedException e) { return; }
		for (String f: u.getSharedData()) {
			if (fileMap.containsKey(f)) {
				fileMap.get(f).remove(u);
				if (fileMap.get(f).isEmpty())
					fileMap.remove(f);
			}
		}
		notify();
	}
}
