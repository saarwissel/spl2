package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;

public class TrackedObjectsEvents implements Event {
    int time;
    int detectionTime;
    List<TrackedObject> TO;

    public TrackedObjectsEvents(int time, List<TrackedObject> TO, int detectionTime) {
        this.time = time;
        this.TO=TO;
        this.detectionTime = detectionTime;
    }
    public int getTime() {
        return time;
    }

    public List<TrackedObject> getTrackedObjects() {
        return TO;
    }

    public int getDetectionTime() {
        return detectionTime;
    }
}
