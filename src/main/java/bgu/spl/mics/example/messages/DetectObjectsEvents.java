package bgu.spl.mics.example.messages;

import bgu.spl.mics.Event;

public class DetectObjectsEvents implements Event {
    int time;

    public DetectObjectsEvents(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }
}
