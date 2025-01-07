package bgu.spl.mics.application.objects;

import java.util.ArrayList;
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
    List<Pose> poseList;


    public GPSIMU(int Tick) {
        this.currentTick = Tick;
        this.status = Status.UP;
        this.poseList = new ArrayList<>();
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public List<Pose> getPoseList() {
        return poseList;
    }

    public void setStatus(int i) {
        if (i == 0) {
            this.status = Status.UP;
        } else if (i == 1) {
            this.status = Status.DOWN;
        } else if(i == 2){
            this.status = Status.ERROR;
        }
        else {
            System.out.println("Plese enter right status ");
        }
    }
}
