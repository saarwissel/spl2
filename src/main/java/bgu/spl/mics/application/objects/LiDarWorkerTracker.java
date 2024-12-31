package bgu.spl.mics.application.objects;

import bgu.spl.mics.example.messages.DetectObjectsEvents;

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
        status= Status.DOWN;
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }
    public TrackedObject maketrack(DetectObjectsEvents events,DetectedObject t){//+++++++ checkwithgal
        return new TrackedObject(t.getId(),events.getTime(),t.getDescription(), (List<CloudPoint>) LiDarDataBase.getInstance("C:\\Users\\saarw\\Downloads\\Skeleton\\example_input_2\\lidar_data.json").getCordinate(t.id));
    }
    public LinkedList<TrackedObject> getTrackObjects() {
        return TrackObjects;
    }
}
