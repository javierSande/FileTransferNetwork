/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */
package common.monitors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class MonitorLocks implements Monitor {
	
	private int nr = 0, nw = 0;
	private ReentrantLock lock = new ReentrantLock();
	private Condition ok_to_read = lock.newCondition();
	private Condition ok_to_write = lock.newCondition();
	
	public void startRead() {
		lock.lock();
		while(nw > 0) {
			try {
				ok_to_read.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		nr++;
		lock.unlock();
	}
	
	public void endRead() {
		lock.lock();
		nr -= 1;
		if (nr == 0) {
			ok_to_write.signal();
		}
		lock.unlock();
	}
	
	public void startWrite() {
		lock.lock();
		while(nr > 0 || nw > 0) {
			try {
				ok_to_write.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		nw++;
		lock.unlock();
	}
	
	public void endWrite() {
		lock.lock();
		nw--;
		ok_to_write.signal();
		ok_to_read.signalAll();
		lock.unlock();
	}
}
