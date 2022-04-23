/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common;

public interface Observable<T> {
	public void addObserver(Observer<T> o);

	public default void removeObserver(Observer<T> o) {}
}
