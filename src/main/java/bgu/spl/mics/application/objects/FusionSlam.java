package bgu.spl.mics.application.objects;

import bgu.spl.mics.MessageBusImpl;

import java.util.ArrayList;
import java.util.List;

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
    List<LandMark> landMarks;
    List<Pose> poses;


    public FusionSlam() {
            this.landMarks = new ArrayList<>();
            this.poses = new ArrayList<>();
    }

    public List<LandMark> getLandMarks() {
        return landMarks;
    }

    public List<Pose> getPoses() {
        return poses;
    }

    public static synchronized FusionSlam getInstance() {
        return FusionSlamHolder.INSTANCE;

    }

    public void check(TrackedObject t, Pose p){
        if (FusionSlam.getInstance().getLandMarks().size() == 0){
                FusionSlam.getInstance().getLandMarks().add(new LandMark(t.getId(), t.getDescription(), t.getCloudPoints(), p));
                StatisticalFolder.getInstance().setNumLandmarks(1);
            }
        else {
                boolean found = false;
                for (LandMark landMark : FusionSlam.getInstance().getLandMarks()) {
                    if (landMark.getId().equals(t.getId())) {
                        landMark.update(t.getCloudPoints());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    this.landMarks.add(new LandMark(t.getId(), t.getDescription(), t.getCloudPoints(), p));
                }
            }
            StatisticalFolder.getInstance().setNumLandmarks(1);
    }

    @Override
    public String toString() {
        String str ="";
        for (LandMark l:this.getLandMarks()){
            str=str+ l.id.toString();
        }
        return str;
    }
}


