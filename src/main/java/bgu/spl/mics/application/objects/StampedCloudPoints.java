package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * StampedCloudPoints represents a single data point in the LiDAR database.
 * It includes a timestamp, an identifier, and a list of cloud points.
 */
public class StampedCloudPoints {
    private int time;
    private String id;
    private List<List<Double>> cloudPoints;

    // Getters and Setters
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<List<Double>> getCloudPoints() {
        return cloudPoints;
    }



    @Override
    public String toString() {
        return "StampedCloudPoints{" +
                "time=" + time +
                ", id='" + id + '\'' +
                ", cloudPoints=" + cloudPoints +
                '}';
    }
}
