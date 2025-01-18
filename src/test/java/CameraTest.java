import bgu.spl.mics.application.messages.DetectObjectsEvents;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.CameraService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CameraTest {

    @Test
    public void testTrackedObjectCreation() {
        LiDarWorkerTracker tracker = new LiDarWorkerTracker(1, 5);

        // יצירת נתונים לדוגמה
        List<List<Double>> cloudPoints = Arrays.asList(
                Arrays.asList(1.0, 2.0),
                Arrays.asList(3.0, 4.0)
        );
        DetectedObject detectedObject = new DetectedObject("obj1", "Test Object");
        DetectObjectsEvents event = new DetectObjectsEvents(1, 10, Arrays.asList(detectedObject), 5);

        // קריאה לפונקציה
        TrackedObject trackedObject = tracker.maketrack(event, detectedObject, cloudPoints);

        // בדיקת הערכים שנוצרו
        assertEquals("obj1", trackedObject.getId(), "The ID of the TrackedObject should match the DetectedObject.");
        assertEquals(1, trackedObject.getTime(), "The time of the TrackedObject should match the event time.");
        assertEquals(2, trackedObject.getCloudPoints().size(), "The number of CloudPoints should match the input size.");
        assertEquals(1.0, trackedObject.getCloudPoints().get(0).getX(), 0.01, "CloudPoint X should match.");
    }
    @Test
    public void testCameraInitialization() {
        Camera camera = new Camera(1, 10);
        assertEquals(1, camera.getId(), "Camera ID should be 1");
        assertEquals(10, camera.getFrequency(), "Camera frequency should be 10");
        assertEquals(CameraStatus.UP, camera.getStatus(), "Camera status should initialize to UP");
    }

    @Test
    public void testGetDetectedObjectsAtTick() {
        Camera camera = new Camera(1, 10);
        StampedDetectedObjects sdo = new StampedDetectedObjects(5, Arrays.asList(
                new DetectedObject("obj1", "desc1"),
                new DetectedObject("obj2", "desc2")
        ));
        camera.getStampedDetectedObjects().add(sdo);

        List<DetectedObject> detected = camera.getDetectedObjectsAtTick(5);

        assertEquals(2, detected.size(), "Expected two objects to be detected at tick 5.");
        assertEquals("obj1", detected.get(0).getId(), "First object ID should match.");
    }
}
