package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    int time;
    List<DetectedObject> Dobjects;
    public StampedDetectedObjects(int numOfTick){
        this.time = numOfTick;
        this.Dobjects = new ArrayList<>();
        }


    public int getTime() {
        return time;
    }


    public List<DetectedObject> getDobjects() {
        return Dobjects;
    }

}
