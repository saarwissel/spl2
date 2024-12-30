package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    enum Status {
        UP,
        DOWN,
        ERROR
    }
    int currentTick;
    Status status;
    LinkedList<Pose> poseList;

    public GPSIMU() {
        this.currentTick = (int)System.currentTimeMillis();
        this.status = Status.DOWN;
        this.poseList = new LinkedList<Pose>();
    }
}
