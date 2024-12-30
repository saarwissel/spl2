package bgu.spl.mics.application.objects;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    String id;
    int time;
    String description;
    CloudPoint[] cloudPoints;

    public TrackedObject(String id, int time, String description, CloudPoint[] cloudPoints) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.cloudPoints = cloudPoints;
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

    public CloudPoint[] getCloudPoints() {
        return cloudPoints;
    }
}
