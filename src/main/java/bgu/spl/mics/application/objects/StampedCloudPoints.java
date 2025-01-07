package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    String id;
    int time;
    private List<List<CloudPoint>> cloudPoints;
    public StampedCloudPoints(String id) {
        this.id = id;
        this.time = (int) System.currentTimeMillis();
        this.cloudPoints = new ArrayList<>();
    }

    public int getTime() {
        return time;
    }

    public List<CloudPoint>getCpoints(int i) {
        return cloudPoints.get(i);
    }

    public String getId() {
        return this.id;
    }

    public int getLidarId() {
        return Integer.parseInt(this.id);
    }
}
