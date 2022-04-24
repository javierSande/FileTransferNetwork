/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common;

public abstract class Monitor {
	
	/* Monitor
	 * 
	 * Abstract class to be extended by those classes that have the readers-writers
	 * problem.
	 * A reading method should start calling startRead() and end calling endRead()
	 * and a writing method should start calling startWrite() and end calling
	 * notify().
	 */
	
	public int nReaders = 0;
	
	protected synchronized void startRead() {
		nReaders++;
	}
	
	protected synchronized void endRead() {
		nReaders--;
		if (nReaders == 0)
			notify();
	}
	
	protected void startWrite() {
		while (nReaders > 0)
			try { wait(); } catch (InterruptedException e) { return; }
	}
}
