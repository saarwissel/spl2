package bgu.spl.mics.example.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;

public class TrackedObjectsEvents implements Event {
    int time;
    List<TrackedObject> TO;

    public TrackedObjectsEvents(int time, List<TrackedObject> TO) {
        this.time = time;
        this.TO=TO;
    }
    public int getTime() {
        return time;
    }
    public List<TrackedObject> getTrackedObject() {
        return TO;
    }
}
