package bgu.spl.mics;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;
/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	private ConcurrentHashMap<Event, LinkedBlockingQueue<MicroService>> eventSubscribers;
	private ConcurrentHashMap<Broadcast, LinkedBlockingQueue<MicroService>> broadcastSubscribers;
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> numMicro;
	private ConcurrentHashMap<MicroService, Future> micrtFuture;
	private ConcurrentHashMap<Event, MicroService> micrtComplete;//++++++++++++
	private ConcurrentHashMap<Event, MicroService> eActuall;

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
		eventSubscribers.get(type.getClass()).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		this.broadcastSubscribers.get(type.getClass()).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		micrtFuture.get(eActuall.get(e)).resolve(result);
		micrtComplete.put(e, eActuall.get(e));//++++++++++++
		eActuall.remove(e, eActuall.get(e));
		micrtFuture.remove(eActuall.get(e), micrtFuture.get(eActuall.get(e)));


	}

	@Override
	public void sendBroadcast(Broadcast b) {
		broadcastSubscribers.get(b.getClass()).forEach(m -> {
			numMicro.get(m).add(b);
		});

	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		try {
			MicroService m = eventSubscribers.get(e.getClass()).take();
			numMicro.get(m).add(e);
			eventSubscribers.get(e.getClass()).add(m);
			eActuall.put(e, m);
			Future<T> future = new Future<>();
			micrtFuture.put(m, future);
			return future;
		} catch (InterruptedException e1) {
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
		numMicro.remove(m);
		eventSubscribers.forEach((k, v) -> {
			if (v.contains(m)) {
				v.remove(m);
			}
		});
		broadcastSubscribers.forEach((k, v) -> {
			if (v.contains(m)) {
				v.remove(m);
			}
		});
		//++++++change the microservis that hundlle evant if it there is one

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {

		LinkedBlockingQueue<Message> queue = numMicro.get(m);
		//take of from all the messege queue
		while (queue.isEmpty()) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

		}
		return queue.take();
	}

	public static synchronized MessageBusImpl getInstance() {
		return SingletonHolder.INSTANCE;

	}
}