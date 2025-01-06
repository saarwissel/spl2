import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.CameraStatus;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CameraTest {

    @Test
    public void testCameraInitialization() {
        Camera camera = new Camera(1, 10);
        assertEquals(1, camera.getId(), "Camera ID should be 1");
        assertEquals(10, camera.getFrequency(), "Camera frequency should be 10");
        assertEquals(CameraStatus.DOWN, camera.getStatus(), "Camera status should initialize to DOWN");
    }

    @Test
    public void testSetStatus() {
        Camera camera = new Camera(1, 10);
        camera.setStatus(0);
        assertEquals(CameraStatus.UP, camera.getStatus(), "Camera status should update to UP");
        camera.setStatus(2);
        assertEquals(CameraStatus.ERROR, camera.getStatus(), "Camera status should update to ERROR");
    }

    @Test
    public void testGetDetectedObjectsAtTick() {
        Camera camera = new Camera(1, 10);
        StampedDetectedObjects sdo = new StampedDetectedObjects(5);
        sdo.getDobjects().add(new DetectedObject("obj1", "desc1"));

        camera.getStampedDetectedObjects().add(sdo);
        List<DetectedObject> result = camera.getDetectedObjectsAtTick(5);

        assertEquals(1, result.size(), "Should return one detected object");
        assertEquals("obj1", result.get(0).getId(), "Detected object ID should match");
    }
}
