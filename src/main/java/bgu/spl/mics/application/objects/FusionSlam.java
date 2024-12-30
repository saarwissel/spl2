package bgu.spl.mics.application.objects;

import bgu.spl.mics.MessageBusImpl;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    // Singleton instance holder
    private static class FusionSlamHolder {
            private static final FusionSlam INSTANCE = new FusionSlam();

    }
    LandMark landMark;
    Pose poses;

    public FusionSlam() {
        this.landMark = new LandMark();
        this.poses = new Pose();
    }

    public LandMark getLandMark() {
        return landMark;
    }

    public Pose getPoses() {
        return poses;
    }
    public static synchronized FusionSlam getInstance() {
        return FusionSlamHolder.INSTANCE;

    }
}


