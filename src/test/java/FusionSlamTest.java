import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.TrackedObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FusionSlamTest {

    @Test
    public void testTransformTrackedObjectsToLandmarks_EmptyList() {
        FusionSlam fusionSlam = FusionSlam.getInstance();
        List<LandMark> result = fusionSlam.transformTrackedObjectsToLandmarks(List.of());
        assertTrue(result.isEmpty(), "Expected empty list when no tracked objects are provided");
    }

    @Test
    public void testTransformTrackedObjectsToLandmarks_SingleObject() {
        FusionSlam fusionSlam = FusionSlam.getInstance();
        TrackedObject trackedObject = new TrackedObject("id1", 10, 20);
        List<LandMark> result = fusionSlam.transformTrackedObjectsToLandmarks(List.of(trackedObject));

        assertEquals(1, result.size(), "Expected one landmark");
        assertEquals("id1", result.get(0).getId(), "Landmark ID should match tracked object ID");
    }

    @Test
    public void testTransformTrackedObjectsToLandmarks_MultipleObjects() {
        FusionSlam fusionSlam = FusionSlam.getInstance();
        List<TrackedObject> trackedObjects = List.of(
                new TrackedObject("id1", 10, 20),
                new TrackedObject("id2", 30, 40)
        );

        List<LandMark> result = fusionSlam.transformTrackedObjectsToLandmarks(trackedObjects);

        assertEquals(2, result.size(), "Expected two landmarks");
        assertEquals("id1", result.get(0).getId(), "First landmark ID should match");
        assertEquals("id2", result.get(1).getId(), "Second landmark ID should match");
    }
}
