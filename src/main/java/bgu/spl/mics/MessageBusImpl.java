
package bgu.spl.mics;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	private ConcurrentHashMap<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventSubscribers;
	private ConcurrentHashMap<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> broadcastSubscribers;
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> numMicro;
	private ConcurrentHashMap<MicroService, Future> micrtFuture;
	private ConcurrentHashMap<Event, MicroService> micrtComplete;//++++++++++++
	private ConcurrentHashMap<Event, MicroService> eActuall;
	private Object lock;
	ReentrantReadWriteLock locker=new ReentrantReadWriteLock();

	private static class SingletonHolder {
		private static final MessageBusImpl INSTANCE = new MessageBusImpl();
	}

	public MessageBusImpl() {
		this.eventSubscribers = new ConcurrentHashMap<>();
		this.broadcastSubscribers = new ConcurrentHashMap<>();
		this.numMicro = new ConcurrentHashMap<>();
		this.micrtFuture = new ConcurrentHashMap<>();
		this.micrtComplete = new ConcurrentHashMap<>();//++++++++++++
		this.eActuall = new ConcurrentHashMap<>();
		this.lock=new Object();

	}

	public MessageBusImpl(int eventSus, int brodSus, int numMicro, int micrtFuture, int micrtComplete, int eActuall) {
		this.eventSubscribers = new ConcurrentHashMap<>(eventSus);
		this.broadcastSubscribers = new ConcurrentHashMap<>(brodSus);
		this.numMicro = new ConcurrentHashMap<>(numMicro);
		this.micrtFuture = new ConcurrentHashMap<>(micrtFuture);
		this.micrtComplete = new ConcurrentHashMap<>(micrtComplete);//++++++++++++
		this.eActuall = new ConcurrentHashMap<>(eActuall);
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (eventSubscribers.computeIfAbsent(type, k -> new LinkedBlockingQueue<>())) {
			eventSubscribers.get(type).add(m);
		}


	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastSubscribers.computeIfAbsent(type, k -> new LinkedBlockingQueue<>()).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		if (e == null) {
			System.err.println("Error: Attempted to complete a null event.");
			return;
		}

		synchronized (eActuall) {
			MicroService m = eActuall.get(e);
			if (m == null) {
				System.err.println("Error: No MicroService found for event: " + e);
				return;
			}

			synchronized (micrtFuture) {
				Future<T> future = micrtFuture.get(m);
				if (future == null) {
					System.err.println("Error: No Future found for MicroService: " + m.getName());
					return;
				}

				synchronized (future) {
					System.out.println("Completing event: " + e + " for MicroService: " + m.getName());
					future.resolve(result);
				}

				micrtFuture.remove(m);
				eActuall.remove(e);
			}
		}
	}


	@Override
	public void sendBroadcast(Broadcast b) {
		LinkedBlockingQueue<MicroService> subs = broadcastSubscribers.get(b.getClass());
		if (subs != null) {
			subs.forEach(m -> {
				if(numMicro.get(m) != null && b != null)
				{
					synchronized (numMicro.get(m)) {
						numMicro.get(m).add(b);
						numMicro.get(m).notifyAll();
					}
				}

			});

		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		LinkedBlockingQueue<MicroService>subs;

		if(e == null)
		{
			return null;
		}
		synchronized (eventSubscribers)
		{
			subs=eventSubscribers.get(e.getClass());
			try {
				while(this.eventSubscribers == null)
				{
					this.wait();
				}
				if(subs==null||subs.isEmpty()){
					return null;
				}
				MicroService m = subs.poll();
				if(m != null) {
					synchronized (numMicro.get(m)) {
						numMicro.get(m).add(e);
						numMicro.get(m).notifyAll();

					}
					eventSubscribers.get(e.getClass()).add(m);
					eActuall.put(e, m);
					Future<T> future = new Future<>();
					micrtFuture.put(m, future);
					return future;

				}
			} catch (InterruptedException e1) {
				return null;
			}

			return null;

		}

	}

	@Override
	public void register(MicroService m) {
		LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<>();
		numMicro.put(m, queue);
	}

	@Override
	public void unregister(MicroService m) {
		if (m == null) {
			return;
		}

		synchronized (eventSubscribers) {
			synchronized (broadcastSubscribers) {
				numMicro.remove(m);
				eventSubscribers.forEach((k, v) -> v.remove(m));
				broadcastSubscribers.forEach((k, v) -> v.remove(m));
			}
		}
	}


	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(m==null){
			throw new InterruptedException("MicroService is null.");
		}
		else{
		LinkedBlockingQueue<Message> queue = numMicro.get(m);
		if (queue == null) {
			throw new IllegalStateException("MicroService " + m.getName() + " is not registered.");
		}
		synchronized (queue) { // ודא סינכרון על התור
			while (queue.isEmpty()) {
				queue.wait(); // ממתין עד שההודעה תגיע
			}
			return queue.take(); // מחזיר הודעה
		}
		}
	}

	public static synchronized MessageBusImpl getInstance() {
		return SingletonHolder.INSTANCE;

	}
}

