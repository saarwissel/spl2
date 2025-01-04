package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    String id;
    int time;
    String description;
    List<CloudPoint> cloudPoints;
    int detectionTime;


    public TrackedObject(String id, int time, String description, List<CloudPoint> cloudPoints, int detectionTime) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.cloudPoints = cloudPoints;
        this.detectionTime = detectionTime;

    }

    public String getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public List<CloudPoint> getCloudPoints() {
        return cloudPoints;
    }

    public int getDetectionTime() {
        return detectionTime;
    }
}
