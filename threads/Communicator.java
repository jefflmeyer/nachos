package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>, and multiple
 * threads can be waiting to <i>listen</i>. But there should never be a time
 * when both a speaker and a listener are waiting, because the two threads can
 * be paired off at this point.
 */
public class Communicator {
	/**
	 * Allocate a new communicator.
	 */
	public Communicator() {
		this.flag = false;
		this.listening = 0;
		this.lock = new Lock();
		this.listener = new Condition2(lock);
		this.speaker = new Condition2(lock);
	}

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void (int word) {
    }
	/**
	 * Wait for a thread to listen through this communicator, and then transfer
	 * <i>word</i> to the listener.
	 *
	 * <p>
	 * Does not return until this thread is paired up with a listening thread.
	 * Exactly one listener should receive <i>word</i>.
	 *
	 * @param word
	 *            the integer to transfer.
	 */
	public void speak(int word) {
		lock.acquire();
		while(flag == true || listening == 0) {
			speaker.sleep();
		}
		this.word = new Integer(word);
		flag = true;
		listener.wakeAll();
		lock.release();
	}

	/**
	 * Wait for a thread to speak through this communicator, and then return the
	 * <i>word</i> that thread passed to <tt>speak()</tt>.
	 *
	 * @return the integer transferred.
	 */
	public int listen() {
		lock.acquire();
		listening++;
		while (flag == false) {
			speaker.wake();
			listener.sleep();
		}
		int message = word.intValue();
		word = null;
		flag = false;
		listening--;
		lock.release();
		return message;
	}
	
	private Lock lock;
	private boolean flag;
	private Integer word = null;
	private int listening;
	private Condition2 listener;
	private Condition2 speaker;

}
