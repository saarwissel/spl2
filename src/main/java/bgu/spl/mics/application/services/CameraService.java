package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.example.messages.DetectObjectsEvents;
import bgu.spl.mics.example.messages.TerminatedBroadcast;
import bgu.spl.mics.example.messages.TickBroadcast;
import bgu.spl.mics.example.messages.CrashedBroadcast;

import java.util.LinkedList;
import java.util.List;


/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    Camera camera;
    long currTime;
    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super(Integer.toString(camera.getId()),Integer.MAX_VALUE);
        this.camera=camera;
        currTime=  System.currentTimeMillis();
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
    camera.setStatus(0);
    subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
        int currentTick = tick.getTick();
        for(StampedDetectedObjects s:camera.getStampedDetectedObjects()){
            if(s.getTime()+camera.getFrequency()==currentTick){
                DetectObjectsEvents e=new DetectObjectsEvents(currentTick, (List<DetectedObject>) s);
                sendEvent(e);
            }
        }
    }
    );
    subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {
        camera.setStatus(1);
        terminate();
    }
    );
    subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
        camera.setStatus(2);
        terminate();
    }
    );

    }

}
