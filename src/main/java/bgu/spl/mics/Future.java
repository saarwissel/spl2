package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	
	/**
	 * This should be  the only public constructor in this class.
	 */
	T result;
	boolean isDone;
	public Future() {
		result=null;
		isDone=false;
	}

	public Future(T result)
	{
		this.result = result;
		isDone = true;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */
	public T get() throws InterruptedException {
		while(!isDone){
			try {
				this.wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				continue;//++++++++
			}
		}
		//relese the thread
		return result;
	}
	
	/**
     * Resolves the result of this Future object.
     */
	public void resolve(T result) {
		synchronized (this) {
				this.result = result;
				this.isDone = true;
				this.notifyAll(); // Notify all waiting threads
		}
	}

	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isDone() {
		return  isDone;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public T get(long timeout, TimeUnit unit) {
		long gTime=unit.toMillis(timeout);
		long startTime = System.currentTimeMillis();
		synchronized (this){
			while (!isDone){
				long pasTime = System.currentTimeMillis() - startTime;
				long remTime = gTime - pasTime;
				if(gTime<pasTime){
					return null;
				}
                try {
                    this.wait(remTime);
                } catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					continue;//++++++++
                }
            }
		}

		return result;
	}
}
