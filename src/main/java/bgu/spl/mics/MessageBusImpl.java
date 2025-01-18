package bgu.spl.mics;

import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * This class manages the subscription and dispatching of events and broadcasts between MicroServices.
 */
public class MessageBusImpl implements MessageBus {
	private final ConcurrentHashMap<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventSubscribers;
	private final ConcurrentHashMap<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> broadcastSubscribers;
	private final ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> microServiceQueues;
	private final ConcurrentHashMap<Event, Future> futureMap;
	private final ConcurrentHashMap<Event, MicroService> eventToMicroService;

	private static class SingletonHolder {
		private static final MessageBusImpl INSTANCE = new MessageBusImpl();
	}

	private MessageBusImpl() {
		eventSubscribers = new ConcurrentHashMap<>();
		broadcastSubscribers = new ConcurrentHashMap<>();
		microServiceQueues = new ConcurrentHashMap<>();
		futureMap = new ConcurrentHashMap<>();
		eventToMicroService = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance() {
		return SingletonHolder.INSTANCE;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (eventSubscribers) {
			eventSubscribers.computeIfAbsent(type, k -> new LinkedBlockingQueue<>()).add(m);
		}
		System.out.println(m.getName() + " subscribed to Event: " + type.getSimpleName());
	}


	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (broadcastSubscribers) {
			broadcastSubscribers.computeIfAbsent(type, k -> new LinkedBlockingQueue<>()).add(m);
		}
		System.out.println(m.getName() + " subscribed to Broadcast: " + type.getSimpleName());
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		if (e == null || result == null) {
			System.err.println("Error: Attempted to complete a null event or result.");
			return;
		}
		synchronized (futureMap) {
			if (result != null) {
				futureMap.remove(e);
				futureMap.put(e, new Future(result));
				eventToMicroService.remove(e);
				System.out.println("Completed Event: " + e + " with result: " + result);
			} else {
				System.err.println("Error: No Result found for event: " + e);
			}
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		LinkedBlockingQueue<MicroService> subscribers;
		synchronized (broadcastSubscribers) {
			subscribers = broadcastSubscribers.get(b.getClass());
		}
		if (subscribers != null) {
			for (MicroService m : subscribers) {
				LinkedBlockingQueue<Message> queue = microServiceQueues.get(m);
				if (queue != null) {
					synchronized (queue) {
						queue.add(b);
						queue.notifyAll();
					}
				}
			}
			System.out.println("Broadcast sent: " + b.getClass().getSimpleName());
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if (e == null) {
			return null;
		}
		LinkedBlockingQueue<MicroService> subscribers;
		synchronized (eventSubscribers) {
			subscribers = eventSubscribers.get(e.getClass());
		}
		if (subscribers == null || subscribers.isEmpty()) {
			return null;
		}
		MicroService m;
		synchronized (subscribers) {
			m = subscribers.poll();
			if (m != null) {
				subscribers.add(m); // Re-add for round-robin behavior
			}
		}
		if (m != null) {
			LinkedBlockingQueue<Message> queue = microServiceQueues.get(m);
			if (queue != null) {
				synchronized (queue) {
					queue.add(e);
					queue.notifyAll();
				}
				Future<T> future = new Future<>();
				synchronized (futureMap) {
					futureMap.put(e, future);
				}
				synchronized (eventToMicroService) {
					eventToMicroService.put(e, m);
				}
				System.out.println("Event sent: " + e.getClass().getSimpleName() + " to " + m.getName());
				return future;
			}
		}
		return null;
	}

	@Override
	public void register(MicroService m) {
		if (!microServiceQueues.containsKey(m)) {
			microServiceQueues.put(m, new LinkedBlockingQueue<>());
		}
		System.out.println(m.getName() + " registered.");
	}

	@Override
	public void unregister(MicroService m) {
		if (m == null) {
			return;
		}
		synchronized (eventSubscribers) {
			eventSubscribers.forEach((event, queue) -> queue.remove(m));
		}
		synchronized (broadcastSubscribers) {
			broadcastSubscribers.forEach((broadcast, queue) -> queue.remove(m));
		}
		microServiceQueues.remove(m);
		System.out.println(m.getName() + " unregistered.");
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		LinkedBlockingQueue<Message> queue = microServiceQueues.get(m);
		if (queue == null) {
			throw new IllegalStateException("MicroService " + m.getName() + " is not registered.");
		}
		synchronized (queue) {
			while (queue.isEmpty()) {
				queue.wait();
			}
			return queue.take();
		}
	}
}
