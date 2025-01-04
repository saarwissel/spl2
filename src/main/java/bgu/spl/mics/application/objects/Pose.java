package bgu.spl.mics.application.objects;

/**
 * Represents the robot's pose (position and orientation) in the environment.
 * Includes x, y coordinates and the yaw angle relative to a global coordinate system.
 */
public class Pose {
    float x;
    float y;
    float yaw;
    int time;
    public Pose(float x, float y, float yaw, int time){
        this.x=x;
        this.y=y;
        this.yaw=yaw;
        this.time=time;
    }

    public int getTime() {
        return time;
    }

    public float getX() {
        return x;
    }

    public float getYaw() {
        return yaw;
    }

    public float getY() {
        return y;
    }

    public void updatePose(Pose pose){
        this.x=pose.getX();
        this.y=pose.getY();
        this.yaw=pose.getYaw();
        this.time=pose.getTime();
    }

    public double getRadian(){
        double radians = Math.toRadians(this.yaw);
        return radians;
    }
}
