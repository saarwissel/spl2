package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.DetectObjectsEvents;

import java.util.LinkedList;
import java.util.List;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */

public class LiDarWorkerTracker {

    enum Status {
        UP,
        DOWN,
        ERROR
    }

    int id;
    int frequency;
    LinkedList<TrackedObject> TrackObjects;
    Status status;

    public LiDarWorkerTracker(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.TrackObjects = new LinkedList<TrackedObject>();
        status= Status.UP;
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }
    public TrackedObject maketrack(DetectObjectsEvents events,DetectedObject t,List<CloudPoint> l){//+++++++ checkwithgal
        return new TrackedObject(t.getId(),events.getTime(),t.getDescription(),l,events.getDetectionTime());
    }
    public LinkedList<TrackedObject> getTrackObjects() {
        return TrackObjects;
    }

    public int getStatus(){
        if(status==Status.UP){
            return 0;
        }
        else if(status==Status.DOWN){
            return 1;
        }
        else{
            return 2;
        }
    }
}
