package nachos.threads;

import nachos.machine.*;
import java.util.LinkedList;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
	/**
	 * Allocate a new Alarm. Set the machine's timer interrupt handler to this
	 * alarm's callback.
	 *
	 * <p>
	 * <b>Note</b>: Nachos will not function correctly with more than one alarm.
	 */
	public Alarm() {

		waitingList = new LinkedList<WaitingThread>();

		Machine.timer().setInterruptHandler(new Runnable() {
			public void run() {
				timerInterrupt();
			}
		});
	}

	/**
	 * The timer interrupt handler. This is called by the machine's timer
	 * periodically (approximately every 500 clock ticks). Causes the current
	 * thread to yield, forcing a context switch if there is another thread that
	 * should be run.
	 */
	public void timerInterrupt() {
		// KThread.currentThread().yield();
		long time = Machine.timer().getTime();
		if (waitingList.isEmpty())
			return;
		while(!waitingList.isEmpty() && waitingList.getFirst().wakeUpTime <= time) {
			WaitingThread next = waitingList.getFirst();
			next.threadPointer.ready();
			waitingList.remove(next);
		}
		
	}

	/**
	 * Put the current thread to sleep for at least <i>x</i> ticks, waking it up
	 * in the timer interrupt handler. The thread must be woken up (placed in
	 * the scheduler ready set) during the first timer interrupt where
	 *
	 * <p>
	 * <blockquote> (current time) >= (WaitUntil called time)+(x) </blockquote>
	 *
	 * @param x
	 *            the minimum number of clock ticks to wait.
	 *
	 * @see nachos.machine.Timer#getTime()
	 */
	public void waitUntil(long x) {
		// for now, cheat just to get something working (busy waiting is bad)
		long wakeTime = Machine.timer().getTime() + x;
		// while (wakeTime > Machine.timer().getTime()) KThread.yield();
		Machine.interrupt().disable();
		waitingList.add(new WaitingThread(wakeTime, KThread.currentThread()));
		KThread.sleep();
		Machine.interrupt().enable();
	}

	// New class WaitingThread to store wakeUpTime and threadPointer
	private class WaitingThread {
		private long wakeUpTime;
		private KThread threadPointer;

		WaitingThread(long myTime, KThread myThread) {
			this.wakeUpTime = myTime;
			this.threadPointer = myThread;
		}
	}

	// LinkedList to hold WaitingThreads
	private LinkedList<WaitingThread> waitingList;

}
