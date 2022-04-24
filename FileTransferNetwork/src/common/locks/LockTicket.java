/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common.locks;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class LockTicket implements Lock {
	
	private AtomicIntegerArray turn;
	private int next;
	private AtomicInteger number;

	public LockTicket(int n) {
		this.number = new AtomicInteger(1);
		this.next = 1;
		this.turn = new AtomicIntegerArray(n+1);
		
		for (int k = 0; k <= n; k++) {
			turn.set(k, 0);
		}
	}

	@Override
	public void takeLock(int i) {
		turn.set(i, number.getAndIncrement());
		while(turn.get(i) != next);
	}

	@Override
	public void releaseLock(int i) {
		next++;
	}

}
