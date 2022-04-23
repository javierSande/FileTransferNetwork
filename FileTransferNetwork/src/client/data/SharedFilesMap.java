package client.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import common.Monitor;

public class SharedFilesMap  extends Monitor {
	private Map<String,String> filesMap;

	public SharedFilesMap() {
		filesMap = new HashMap<String,String>();
	}
	
	public String getFilePath(String file) {
		startRead();
		String path = filesMap.get(file);
		endRead();
		return path;
	}
	
	public Set<String> getFiles() {
		startRead();
		Set<String> files = new HashSet<String>(filesMap.keySet());
		endRead();
		return files;
	}
	
	public synchronized void addFiles(Map<String, String> files) {
		startWrite();
		filesMap.putAll(files);
		notify();
	}
	
	public synchronized void addFile(String file, String path) {
		startWrite();
		filesMap.put(file, path);
		notify();
	}
	
	public synchronized void deleteFiles(List<String> files) {
		startWrite();
		for (String f: files)
			filesMap.remove(f);
		notify();
	}
	
	public synchronized void deleteFile(String file) {
		startWrite();
		filesMap.remove(file);
		notify();
	}
}
