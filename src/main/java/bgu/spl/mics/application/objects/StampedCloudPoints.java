package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    String id;
    int time;
    LinkedList<CloudPoint> Cpoints;

    public StampedCloudPoints(String id) {
        this.id = id;
        this.time = (int) System.currentTimeMillis();
        this.Cpoints = new LinkedList<CloudPoint>();
    }

    public int getTime() {
        return time;
    }

    public LinkedList<CloudPoint> getCpoints() {
        return Cpoints;
    }

    public String getId() {
        return this.id;
    }
}
