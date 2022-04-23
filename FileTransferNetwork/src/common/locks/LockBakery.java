/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common.locks;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class LockBakery implements Lock {
	
	private AtomicIntegerArray turn;
	private int n;

	public LockBakery(int n) {
		this.n = n;
		this.turn = new AtomicIntegerArray(n+1);
		for (int i = 0; i <= n; i++) {
			turn.getAndSet(i, 0);
		}
	}

	@Override
	public void takeLock(int i) {
		turn.getAndSet(i, 1);
		int max = 1;
		for (int j = 1; j <= n; j++) {
			int aux = turn.get(j);
			if (aux > max) max = aux;
		}
		turn.getAndSet(i, max + 1);

		for (int j = 1; j <= n; j++) {
			if (j != i) {
				while (turn.get(j) != 0 && (turn.get(i) > turn.get(j) || (turn.get(i) == turn.get(j) && i > j)));
			}
		}
	}

	@Override
	public void releaseLock(int i) {
		turn.set(i, 0);
	}
}
