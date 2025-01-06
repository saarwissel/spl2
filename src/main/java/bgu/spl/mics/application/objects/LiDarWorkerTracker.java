package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.DetectObjectsEvents;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */

public class LiDarWorkerTracker {

    public Object setStatus;

    enum Status {
        UP,
        DOWN,
        ERROR
    }

    int id;
    int frequency;
    ConcurrentLinkedQueue<TrackedObject> TrackObjects;
    Status status;

    public LiDarWorkerTracker(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.TrackObjects = new ConcurrentLinkedQueue<>();
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
    public ConcurrentLinkedQueue<TrackedObject> getTrackObjects() {
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
    public void setStatus(int status){
        if(status==0){
            this.status=Status.UP;
        }
        else if(status==1){
            this.status=Status.DOWN;
        }
        else{
            this.status=Status.ERROR;
        }
    }
}
