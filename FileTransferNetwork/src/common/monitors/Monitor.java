/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */
package common.monitors;

public interface Monitor {
	
	public void startRead();
	public void endRead();
	public void startWrite();
	public void endWrite();
	
}
