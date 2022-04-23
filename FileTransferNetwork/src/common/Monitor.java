/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common;

public abstract class Monitor {
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
		if (nReaders > 0)
			try { wait(); } catch (InterruptedException e) { return; }
	}
}
