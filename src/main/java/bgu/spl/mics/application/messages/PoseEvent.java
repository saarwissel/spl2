package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Pose;

public class PoseEvent implements Event {
    Pose pose;
    int time;

    public PoseEvent(Pose pose,int time) {
        this.pose=pose;
        this.time=time;
    }

    public Pose getPose() {
        return pose;
    }

    public int getTime() {
        return time;
    }
}
