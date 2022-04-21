package common.locks;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class LockTicket implements Lock {
	
	private AtomicIntegerArray turn;
	private int next;
	private AtomicInteger number;
	
	

	public LockTicket(int n) {
		turn = new AtomicIntegerArray(n+1);
		this.next = 0;
		this.number = new AtomicInteger(0);
		
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
