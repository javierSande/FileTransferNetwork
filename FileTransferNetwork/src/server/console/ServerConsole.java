/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package server.console;

import java.time.Instant;

public class ServerConsole {
	public static synchronized void print(String msg) {
		System.out.println(String.format("[%s]: %s",Instant.now().toString(), msg));
	}
}
