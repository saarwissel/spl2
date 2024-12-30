package bgu.spl.mics.application.objects;

import java.util.LinkedList;

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
        status= Status.DOWN;
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public LinkedList<TrackedObject> getTrackObjects() {
        return TrackObjects;
    }
}
