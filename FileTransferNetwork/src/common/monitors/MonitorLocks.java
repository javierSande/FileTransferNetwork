package common.monitors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class MonitorLocks implements Monitor {
	
	private int nr = 0, nw = 0;
	private ReentrantLock lock = new ReentrantLock();
	private Condition ok_to_read = lock.newCondition();
	private Condition ok_to_write = lock.newCondition();
	
	public synchronized void startRead() {
		while(nw > 0) {
			try {
				ok_to_read.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		nr++;
	}
	
	public synchronized void endRead() {
		nr -= 1;
		if (nr == 0) {
			ok_to_write.signal();
		}
	}
	
	public synchronized void startWrite() {
		while(nr > 0 || nw > 0) {
			try {
				ok_to_write.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		nw++;
	}
	
	public synchronized void endWrite() {
		nw--;
		ok_to_write.signal();
		ok_to_read.signalAll();
	}
}
