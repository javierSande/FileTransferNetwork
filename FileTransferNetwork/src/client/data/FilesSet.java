/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package client.data;

import java.util.HashSet;
import java.util.Set;

import common.monitors.MonitorSyn;

public class FilesSet extends MonitorSyn {
	
	/* FilesSet
	 * 
	 * This class represents a set of the files that a user has available.
	 * As we have the readers-writers problem to access these available files,
	 * each method that tries to read or write data should follow the protocol
	 * of the Monitor class.
	 */
	
	private volatile Set<String> fileList;
	
	public FilesSet() {
		fileList = new HashSet<String>();
	}
	
	public synchronized void setFiles(Set<String> list) {
		startWrite();
		fileList = list;
		endWrite();
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
