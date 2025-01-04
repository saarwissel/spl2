package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.PoseLoader;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

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
        super("Pose_service",Integer.MAX_VALUE);
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        String filePath = "C:\\Users\\saarw\\Downloads\\Skeleton\\example_input_2\\pose_data.json";

        List<Pose> poseList = PoseLoader.loadPoses(filePath);
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
            int currntTick=tick.getTick();
            Pose t=gpsimu.getPoseList().get(currntTick);
            PoseEvent e=new PoseEvent(t,currntTick);
            sendEvent(e);
        });

    }
}
