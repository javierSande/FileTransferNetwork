package client.data;

import java.util.HashSet;
import java.util.Set;

public class FilesSet {
	private Set<String> fileList;
	private int nReaders;
	
	public FilesSet() {
		fileList = new HashSet<String>();
		nReaders = 0;
	}
	
	private synchronized void startRead() {
		nReaders++;
	}
	
	private synchronized void endRead() {
		nReaders--;
		if (nReaders == 0)
			notify();
	}
	
	public synchronized void setFiles(Set<String> list) {
		if (nReaders > 0)
			try { wait(); } catch (InterruptedException e) { return; }
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
