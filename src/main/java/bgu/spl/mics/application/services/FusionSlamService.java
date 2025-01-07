package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvents;
import bgu.spl.mics.application.objects.*;

import java.util.ArrayList;
import java.util.List;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 *
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    boolean isLidarT;
    boolean isPoseT;
    List<TrackedObject> waitingList;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("The Fusion Slam",100);
        this.isPoseT = false;
        this.isLidarT = false;
        this.waitingList = new ArrayList<>();
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {

                    if (terminated.getService() == "fusion") {
                        StatisticalFolder.getInstance().setSystemRuntime(FusionSlam.getInstance().getPoses().size());
                        this.terminate();
                    }
                    if (terminated.getService() == "time") {
                        if(StatisticalFolder.getInstance().getSystemRuntime().get() == 0)
                        {
                            StatisticalFolder.getInstance().setSystemRuntime(FusionSlam.getInstance().getPoses().size());
                        }
                        this.terminate();
                    }
                    if (terminated.getService() == "lidar") {
                        this.isLidarT = true;
                        if(this.isPoseT == true)
                        {
                            sendBroadcast(new TerminatedBroadcast("fusion"));
                            this.terminate();
                        }
                    }
                    if (terminated.getService() == "pose") {
                        this.isPoseT = true;
                        if(this.isLidarT == true)
                        {
                            sendBroadcast(new TerminatedBroadcast("fusion"));
                            this.terminate();
                        }
                    }
                }

        );
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
                    terminate();
                }
        );


        subscribeEvent(TrackedObjectsEvents.class, (TrackedObjectsEvents t) -> {
            // Update the global map with the new tracked object

            if(FusionSlam.getInstance().getPoses().get(t.getDetectionTime()) == null)
            {
                this.waitingList.addAll(t.getTrackedObjects());
            }
            else if (FusionSlam.getInstance().getLandMarks().size() == 0){
                for (TrackedObject trackedObject : t.getTrackedObjects()) {
                    LandMark landy=new LandMark(trackedObject.getId(), trackedObject.getDescription(), trackedObject.getCloudPoints(), FusionSlam.getInstance().getPoses().get(t.getDetectionTime()));

                    FusionSlam.getInstance().getLandMarks().add(landy);
                }
                int sumLandMarks = FusionSlam.getInstance().getLandMarks().size();
                StatisticalFolder.getInstance().setNumLandmarks(sumLandMarks);
                complete(t, true);
            }

            else {
                for (TrackedObject trackedObject : t.getTrackedObjects()) {
                    boolean found = false;
                    for (LandMark landMark : FusionSlam.getInstance().getLandMarks()) {
                        if (landMark.getId().equals(trackedObject.getId())) {
                            landMark.update(trackedObject.getCloudPoints(), FusionSlam.getInstance().getPoses().get(trackedObject.getDetectionTime()));
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        LandMark land= new LandMark(trackedObject.getId(), trackedObject.getDescription(), trackedObject.getCloudPoints(), FusionSlam.getInstance().getPoses().get(t.getDetectionTime()));
                        FusionSlam.getInstance().getLandMarks().add(land);
                        System.out.println(land.toString());
                    }
                }
                int sumLandMarks = FusionSlam.getInstance().getLandMarks().size();
                StatisticalFolder.getInstance().setNumLandmarks(sumLandMarks);
            }

            complete(t, true);
        });
        subscribeEvent(PoseEvent.class, (PoseEvent pose) -> {
            System.out.println("send a pose event");
            FusionSlam.getInstance().getPoses().add(pose.getPose());
            if(this.waitingList.size() > 0)
            {
                for (TrackedObject trackedObject : this.waitingList) {
                    if(trackedObject.getDetectionTime()== pose.getTime())
                    {
                        FusionSlam.getInstance().check(trackedObject, pose.getPose());
                        waitingList.remove(trackedObject);
                    }

                }

            }
            complete(pose, true);
        });
        System.out.println("finish init"+ this.getName());
        SystemServicesCountDownLatch.getInstance().getCountDownLatch().countDown();



    }
}
