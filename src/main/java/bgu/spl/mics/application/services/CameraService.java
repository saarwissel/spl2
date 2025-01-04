package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.messages.DetectObjectsEvents;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.CrashedBroadcast;

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
        super(Integer.toString(camera.getId()),100);
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
        int addStat = camera.getStampedDetectedObjects().size();
        for(StampedDetectedObjects s:camera.getStampedDetectedObjects()){
            if(s.getTime()+camera.getFrequency()==currentTick){
                DetectObjectsEvents e=new DetectObjectsEvents(currentTick,this.camera.getFrequency(),(List<DetectedObject>) s,s.getTime());
                StatisticalFolder.getInstance().setNumDetectedObjects(addStat); // סטטיסטיקה סינגלטון סטטיסטי  נוסיף addStat
                sendEvent(e);
            }
        }
    }
    );
    subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {
        if(terminated.getService() != "pose") {
            camera.setStatus(1);
            terminate();
        }
    }
    );
    subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
        camera.setStatus(2);
        terminate();
    }
    );

    }

}
