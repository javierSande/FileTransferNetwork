/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package client.console;

import java.time.Instant;

import common.locks.Lock;
import common.locks.LockRompeEmpate;

public class ClientConsole {
	
	public enum Writer { CLIENT, LISTENER }
	
	private static final Lock lock = new LockRompeEmpate(2);

	public static void print(Writer w, String msg) {
		lock.takeLock(w.ordinal());
		System.out.println(String.format("[%s]: %s",Instant.now().toString(), msg));
		lock.releaseLock(w.ordinal());
	}
	
}
