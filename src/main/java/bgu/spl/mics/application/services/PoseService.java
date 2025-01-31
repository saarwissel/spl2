package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.PoseLoader;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.SystemServicesCountDownLatch;

import java.util.List;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    GPSIMU gpsimu;
    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("Pose_service",100);
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
                    gpsimu.setStatus(1);
                    terminate();
                }
        );
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {

                    if(terminated.getService() == "pose")
                    {
                        gpsimu.setStatus(1);
                        this.terminate();
                    }
                    if(terminated.getService() == "fusion")
                    {
                       gpsimu.setStatus(1);
                        this.terminate();
                    }
                    if(terminated.getService() == "time")
                    {
                        gpsimu.setStatus(1);
                        this.terminate();
                    }

                }
        );
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            int currentTick=tick.getTick();
            if(currentTick == 0)
            {
                System.out.println("getting started");
            }
            else if(currentTick <= gpsimu.getPoseList().size())
            {
                Pose t=gpsimu.getPoseList().get(currentTick-1);
                sendEvent(new PoseEvent(t,tick.getTick()));
            }
            else {
                sendBroadcast(new TerminatedBroadcast("pose"));
            }

        });
        System.out.println(this.gpsimu.toString());
        SystemServicesCountDownLatch.getInstance().getCountDownLatch().countDown();




    }
}
