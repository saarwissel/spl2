import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.CloudPoint;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FusionSlamTest {

    @Test
    public void testAddLandmarksFromEmptyTrackedObjects() {
        FusionSlam fusionSlam = FusionSlam.getInstance();

        // Clear landmarks before the test
        fusionSlam.getLandMarks().clear();

        // Test with an empty list of tracked objects
        List<TrackedObject> trackedObjects = new ArrayList<>();
        Pose pose = new Pose(0, 0, 0, 0);
        trackedObjects.forEach(t -> fusionSlam.check(t, pose));

        assertTrue(fusionSlam.getLandMarks().isEmpty(), "Expected no landmarks to be created from an empty list.");
    }

    @Test
    public void testAddSingleLandmark() {
        FusionSlam fusionSlam = FusionSlam.getInstance();

        // Clear landmarks before the test
        fusionSlam.getLandMarks().clear();

        // Create a single tracked object
        List<CloudPoint> cloudPoints = Arrays.asList(new CloudPoint(1.0, 2.0));
        TrackedObject trackedObject = new TrackedObject("obj1", 1, "Object 1", cloudPoints, 1);
        Pose pose = new Pose(0, 0, 0, 0);

        fusionSlam.check(trackedObject, pose);

        // Validate the landmark
        List<LandMark> landmarks = fusionSlam.getLandMarks();
        assertEquals(1, landmarks.size(), "Expected exactly one landmark to be created.");
        assertEquals("obj1", landmarks.get(0).getId(), "Landmark ID should match the tracked object ID.");
    }

    @Test
    public void testUpdateExistingLandmark() {
        FusionSlam fusionSlam = FusionSlam.getInstance();

        // Clear landmarks before the test
        fusionSlam.getLandMarks().clear();

        // Create a tracked object
        List<CloudPoint> cloudPoints1 = Arrays.asList(new CloudPoint(1.0, 1.0));
        TrackedObject trackedObject1 = new TrackedObject("obj1", 1, "Object 1", cloudPoints1, 1);
        Pose pose = new Pose(0, 0, 0, 0);

        // Add the first tracked object
        fusionSlam.check(trackedObject1, pose);

        // Update the same tracked object with new data
        List<CloudPoint> cloudPoints2 = Arrays.asList(new CloudPoint(5.0, 4.0));
        TrackedObject trackedObject2 = new TrackedObject("obj1", 2, "Updated Object 1", cloudPoints2, 2);

        fusionSlam.check(trackedObject2, pose);

        // Validate the updated landmark
        List<LandMark> landmarks = fusionSlam.getLandMarks();
        assertEquals(1, landmarks.size(), "Expected exactly one landmark.");
        assertEquals(3.0, landmarks.get(0).getLandCloudPoints().get(0).getX(), 0.01, "Landmark cloud point X should be updated.");
    }
}
