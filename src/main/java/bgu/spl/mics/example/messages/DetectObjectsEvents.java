package bgu.spl.mics.example.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;

import java.util.List;

public class DetectObjectsEvents implements Event {
    private int time;
    private List<DetectedObject> dt;

    public DetectObjectsEvents(int time, List<DetectedObject>dt) {
        this.time = time;
        this.dt = dt;
    }

    public int getTime() {
        return time;
    }

    public List<DetectedObject> getDt() {
        return dt;
    }
}
