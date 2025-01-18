import bgu.spl.mics.*;

import bgu.spl.mics.application.messages.DetectObjectsEvents;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvents;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {


    @Test
    public void testSendEvents() {
        MessageBusImpl bus = MessageBusImpl.getInstance();

        // יצירת MicroService לדוגמה
        MicroService service = new MicroService("TestService", 100) {
            @Override
            protected void initialize() {
                subscribeEvent(DetectObjectsEvents.class, (event) -> {
                    complete(event, "EventProcessed");
                    terminate(); // עצירת השירות מבפנים
                });
            }
        };

        // רישום והפעלת השירות
        bus.register(service);
        Thread serviceThread = new Thread(service::run);
        serviceThread.start();

        // יצירת אירוע ושליחתו
        DetectObjectsEvents event = new DetectObjectsEvents(1, 5, new ArrayList<>(), 10);
        Future<String> future = bus.sendEvent(event);

        // הפסקת ה-thread
        serviceThread.interrupt();
    }



    @Test
    public void testSubscribeEvent() {
        MessageBusImpl bus = MessageBusImpl.getInstance();

        // יצירת MicroService לדוגמה
        MicroService service = new MicroService("TestService", 100) {
            @Override
            protected void initialize() {
                subscribeEvent(DetectObjectsEvents.class, (event) -> {
                    System.out.println("Event received: " + event.getClass().getSimpleName());
                    complete(event, "EventHandled");
                    terminate(); // עצירת השירות
                });
            }
        };

        // רישום השירות
        bus.register(service);
        Thread serviceThread = new Thread(service::run);
        serviceThread.start();

        // יצירת אירוע ושליחתו
        DetectObjectsEvents event = new DetectObjectsEvents(1, 5, new ArrayList<>(), 10);
        Future<String> future = bus.sendEvent(event);

        // הפסקת השירות
        serviceThread.interrupt();
    }

    @Test
    public void testSendBroadcast() {
        MessageBusImpl bus = MessageBusImpl.getInstance();

        MicroService service1 = new MicroService("Service1", 100) {
            @Override
            protected void initialize() {}
        };
        MicroService service2 = new MicroService("Service2", 100) {
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



    // Classes for testing
    public static class TestBroadcast implements Broadcast {}
    public static class TestEvent implements Event<String> {}
}