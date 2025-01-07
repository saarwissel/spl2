package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.MicroService;

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
        super("lidar "+LiDarWorkerTracker.getId(),100);
        this.LiDarWorkerTracker=LiDarWorkerTracker;
        this.readyToSend=new ArrayList<>();

    }
    public void clear(List<TrackedObject> trackedObjectList){
        for(TrackedObject t: trackedObjectList){
            trackedObjectList.remove(t);
        }
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
                if(terminated.getService()=="lidar"){
                    terminate();
                }
                if(terminated.getService()=="fusion"){
                    this.terminate();
                }
                if(terminated.getService()=="time"){
                    this.terminate();
                }
                }
        );
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
                    terminate();
                }
        );
        subscribeEvent(DetectObjectsEvents.class, (DetectObjectsEvents event) ->{//////with gall
            System.out.println("flag 1");
            int detectionTime = event.getTime();
            int i =0;
            int sumT = 0;
            StampedCloudPoints s;
            List<CloudPoint> l;
            for (DetectedObject t: event.getDt()) {
                System.out.println("flag 2");

                LiDarDataBase lidarDatabase = LiDarDataBase.getInstance();
                if(detectionTime - event.getCameraFreq() < lidarDatabase.getStumpedCloudPoints().size())
                {
                        s = lidarDatabase.getStumpedCloudPoints().get(detectionTime - event.getCameraFreq());
                        if (s.getId().equals("ERROR")) {
                          if (StatisticalFolder.getInstance().getSystemRuntime().get() == 0) {
                           StatisticalFolder.getInstance().setSystemRuntime(currentTick);

                          }
                         CrashedBroadcast e = new CrashedBroadcast();
                         this.LiDarWorkerTracker.setStatus(2);
                            sendBroadcast(e);
                            break;

                }
                        else {
                         System.out.println("flag 3");

                         l = s.getCpoints(i);

                            readyToSend.add((this.LiDarWorkerTracker.maketrack(event, t, l)));
                         i++;
                            sumT = sumT + readyToSend.size();
                            StatisticalFolder.getInstance().setNumTrackedObjects(sumT);// סטטיסטיקה סינגלטון סטטיסטי

                }}
                if (sumT == LiDarDataBase.getInstance().getStumpedCloudPoints().size()) {
                    sendBroadcast(new TerminatedBroadcast("lidar"));
                }

                if (currentTick >= detectionTime + this.LiDarWorkerTracker.getFrequency()) {
                    System.out.println("flag 4");

                    TrackedObjectsEvents e = new TrackedObjectsEvents(detectionTime + LiDarWorkerTracker.getFrequency(), readyToSend, event.getDetectionTime());
                    this.clear(readyToSend);
                    sendEvent(e);
                }
            }
            complete(event,true);
            });
        SystemServicesCountDownLatch.getInstance().getCountDownLatch().countDown();

        System.out.println("finish init"+ this.getName());

    }
}
