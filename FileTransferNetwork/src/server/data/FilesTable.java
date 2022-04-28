/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package server.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import common.User;
import common.monitors.MonitorSyn;

public class FilesTable extends MonitorSyn {
	
	/* FilesTable
	 * 
	 * This class represents the table of data that assigns each file name
	 * with a set of users that have that file.
	 * When a file is requested by a client, the sender of this file is chosen
	 * randomly among the users that have that file.
	 * As we have the readers-writers problem, each method that tries to read or
	 * write data should follow the protocol of the Monitor class.
	 **/
	
	private volatile Map<String, Set<User>> fileMap;
	
	public FilesTable() {
		fileMap = new HashMap<String, Set<User>>();
	}
	
	public Set<String> getFiles() {
		startRead();
		Set<String> list = new HashSet<String>();
		for (String f: fileMap.keySet()) 
			list.add(f);
		endRead();
		return list;
	}
	
	/* public synchronized User getSender(String file)
	 * 
	 * Returns a random sender for the requested file among the users that 
	 * have that file available.
	 **/
	
	public User getSender(String file) {
		startRead();
		User sender = null;
		if (fileMap.containsKey(file)) {
			Random rand = new Random(System.currentTimeMillis());
			int size = fileMap.get(file).size();
			sender = (User) fileMap.get(file).toArray()[rand.nextInt(size)];
		}
		
		endRead();
		return sender;
	}
	
	public synchronized void addUserFiles(User u) {
		startWrite();
		for (String f: u.getSharedData()) {
			if (!fileMap.containsKey(f))
				fileMap.put(f, new HashSet<User>());
			fileMap.get(f).add(u);
		}
		endWrite();
	}
	
	public synchronized void removeUserFiles(User u) {
		startWrite();
		for (String f: u.getSharedData()) {
			if (fileMap.containsKey(f)) {
				fileMap.get(f).remove(u);
				if (fileMap.get(f).isEmpty())
					fileMap.remove(f);
			}
		}
		endWrite();
	}
}
