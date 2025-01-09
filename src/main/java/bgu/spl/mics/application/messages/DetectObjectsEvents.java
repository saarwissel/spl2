package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;

import java.util.List;

public class DetectObjectsEvents implements Event {
    private int time;
    private int detectionTime;
    public int cameraFreq;
    private List<DetectedObject> detectedObjects;

    public DetectObjectsEvents(int time,int cameraFreq, List<DetectedObject>detectedObjects, int detectionTime) {
        this.time = time;
        this.cameraFreq = cameraFreq;
        this.detectedObjects = detectedObjects;
        this.detectionTime = detectionTime;
    }

    public int getTime() {
        return time;
    }

    public int getDetectionTime() {
        return detectionTime;
    }

    public int getCameraFreq() {
        return cameraFreq;
    }

    public List<DetectedObject> getDt() {
        return detectedObjects;
    }
}
