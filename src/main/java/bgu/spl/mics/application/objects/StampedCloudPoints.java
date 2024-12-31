package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    String id;
    int time;
    List<CloudPoint> Cpoints;

    public StampedCloudPoints(String id) {
        this.id = id;
        this.time = (int) System.currentTimeMillis();
        this.Cpoints = new ArrayList<>();
    }

    public int getTime() {
        return time;
    }

    public List<CloudPoint> getCpoints() {
        return Cpoints;
    }

    public String getId() {
        return this.id;
    }
}
