import bgu.spl.mics.*;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    @Test
    public void testRegisterMicroService() {
        MessageBusImpl bus = MessageBusImpl.getInstance();
        MicroService service = new MicroService("TestService",100) {
            @Override
            protected void initialize() {}
        };

        bus.register(service);
        assertDoesNotThrow(() -> bus.awaitMessage(service), "The service should be able to receive messages after registration.");
    }

    @Test
    public void testSendBroadcast() {
        MessageBusImpl bus = MessageBusImpl.getInstance();

        MicroService service1 = new MicroService("Service1",100) {
            @Override
            protected void initialize() {}
        };

        MicroService service2 = new MicroService("Service2",100) {
            @Override
            protected void initialize() {}
        };

        bus.register(service1);
        bus.register(service2);

        bus.subscribeBroadcast(TestBroadcast.class, service1);
        bus.subscribeBroadcast(TestBroadcast.class, service2);

        TestBroadcast broadcast = new TestBroadcast();
        bus.sendBroadcast(broadcast);

        assertDoesNotThrow(() -> {
            assertEquals(broadcast, bus.awaitMessage(service1), "Service1 should receive the broadcast.");
            assertEquals(broadcast, bus.awaitMessage(service2), "Service2 should receive the broadcast.");
        });
    }

    @Test
    public void testSendEventWithFutureFunctionality() throws InterruptedException {
        MessageBusImpl bus = MessageBusImpl.getInstance();

        MicroService service1 = new MicroService("Service1",100) {
            @Override
            protected void initialize() {}
        };

        MicroService service2 = new MicroService("Service2",100) {
            @Override
            protected void initialize() {}
        };

        bus.register(service1);
        bus.register(service2);

        bus.subscribeEvent(TestEvent.class, service1);

        TestEvent event = new TestEvent();
        Future<String> future = bus.sendEvent(event);

        assertNotNull(future, "The future object should not be null.");

        // בדיקת awaitMessage
        assertDoesNotThrow(() -> {
            Message message = bus.awaitMessage(service1);
            assertEquals(event, message, "Service1 should receive the event.");
        });

        // פתרון ה-Future
        future.resolve("Success");
        assertTrue(future.isDone(), "The future should be marked as done.");
        assertEquals("Success", future.get(), "The future should resolve to 'Success'.");

        // בדיקת get עם timeout
        Future<String> timeoutFuture = new Future<>();
        String result = timeoutFuture.get(500, TimeUnit.MILLISECONDS);
        assertNull(result, "The future should return null after timeout.");

        // פתרון נוסף לבדיקה
        timeoutFuture.resolve("Timeout Success");
        assertEquals("Timeout Success", timeoutFuture.get(), "The resolved result should be 'Timeout Success'.");
    }

    // Classes for testing
    public static class TestBroadcast implements Broadcast {}
    public static class TestEvent implements Event<String> {}
}