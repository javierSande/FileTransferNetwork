/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common.locks;

public interface Lock {
	public void takeLock(int i);
	public void releaseLock(int i);
}
