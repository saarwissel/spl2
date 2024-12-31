package bgu.spl.mics.application.services;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.example.messages.*;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarWorkerService extends MicroService {
    LiDarWorkerTracker LiDarWorkerTracker;
    private int currentTick=0;
    List<TrackedObject>readyToSend;

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarWorkerService(LiDarWorkerTracker LiDarWorkerTracker) {
        super(Integer.toString(LiDarWorkerTracker.getId()),Integer.MAX_VALUE);
        this.LiDarWorkerTracker=LiDarWorkerTracker;
        this.readyToSend=new ArrayList<>();

    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            currentTick = tick.getTick();
        });
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {
                    terminate();
                }
        );
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
                    terminate();
                }
        );
        subscribeEvent(DetectObjectsEvents.class, (event) ->{//////with gall
            int detectionTime = event.getTime();
            for (DetectedObject t: event.getDt()){
                    readyToSend.add((this.LiDarWorkerTracker.maketrack(event,t)));
            }
            if (currentTick >= detectionTime + this.LiDarWorkerTracker.getFrequency()) {

            }
                List<CloudPoint> cloudPoints = new ArrayList<>();
                for(CloudPoint c: LiDarDataBase.getInstance("").getCloudPoints().get(0).getCpoints()){
                    cloudPoints.add(c);
                }
                for(CloudPoint t:cloudPoints){
                    TrackedObjectsEvents
                }

            });
    }
}
