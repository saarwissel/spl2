package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    int time;
    LinkedList<DetectedObject> Dobjects;
    public StampedDetectedObjects(){
        this.time =(int) System.currentTimeMillis();
        this.Dobjects = new LinkedList<DetectedObject>();
    }

    public int getTime() {
        return time;
    }

    public LinkedList<DetectedObject> getDobjects() {
        return Dobjects;
    }
}
