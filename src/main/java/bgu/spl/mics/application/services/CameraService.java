package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
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
        super("camera "+camera.getId(),100);
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
        int addStat;
        if(camera.getStampedDetectedObjects() != null)
        {
             addStat = camera.getStampedDetectedObjects().size();
        }
        else {
            addStat = 0;
        }
        if (camera.getStampedDetectedObjects() != null && !camera.getStampedDetectedObjects().isEmpty() )
        {

            for(DetectedObject s:camera.getStampedDetectedObjects().get(currentTick).getDobjects()){
                    if(s.getId()=="ERROR"){
                        if(StatisticalFolder.getInstance().getSystemRuntime().get()==0){
                            StatisticalFolder.getInstance().setSystemRuntime(currentTick);
                        }
                        CrashedBroadcast e=new CrashedBroadcast();
                        camera.setStatus(2);
                        sendBroadcast(e);
                    }
                    else {
                        DetectObjectsEvents e = new DetectObjectsEvents(currentTick + camera.getFrequency(), camera.getFrequency(), camera.getStampedDetectedObjects().get(currentTick).getDobjects(), currentTick);
                        StatisticalFolder.getInstance().setNumDetectedObjects(addStat);
                        System.out.println(e.toString()+ camera.getStampedDetectedObjects().get(currentTick).getDobjects().toString() +" was here");
                        sendEvent(e);
                    }
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
        terminate();
    }
    );
        System.out.println("finish init"+ this.getName());
        SystemServicesCountDownLatch.getInstance().getCountDownLatch().countDown();



    }

}
