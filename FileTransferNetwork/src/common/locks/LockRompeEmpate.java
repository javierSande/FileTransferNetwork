/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common.locks;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class LockRompeEmpate implements Lock {
	
	private volatile AtomicIntegerArray in;
	private volatile AtomicIntegerArray last;
	private int n;
	
	public LockRompeEmpate(int n) {
		in = new AtomicIntegerArray(n+1);
		last = new AtomicIntegerArray(n+1);
		this.n = n;
		
		for (int k = 0; k <= n; k++) {
			in.getAndSet(k, 0);
			last.getAndSet(k, 0);
		}
	}

	@Override
	public void takeLock(int i) {
		for (int j = 1; j <= n; j++) {
			last.getAndSet(j, i);
			in.getAndSet(i, j);
			for (int k = 1; k <= n; k++) {
				if (i != k) {
					while(in.get(k) >= in.get(i) && last.get(j) == i);
				}
			}
		}
	}

	@Override
	public void releaseLock(int i) {
		in.getAndSet(i, 0);
	}

}
