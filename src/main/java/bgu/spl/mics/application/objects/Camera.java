package bgu.spl.mics.application.objects;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
enum CameraStatus {
    UP,
    DOWN,
    ERROR
}
public class Camera {
    // TODO: Define fields and methods.
    private int id; // מזהה המצלמה
    private int frequency; // פרק הזמן לשליחת אירועים
    private CameraStatus status; // סטטוס המצלמה
    private LinkedList<StampedDetectedObjects> Tobjects; // רשימת אובייקטים שזוהו

    public Camera(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.status = CameraStatus.DOWN;
        this.Tobjects = new LinkedList<StampedDetectedObjects>();

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFrequency() {
        return frequency;
    }


    public CameraStatus getStatus() {
        return status;
    }

    public void setStatus(CameraStatus status) {
        this.status = status;
    }

    public LinkedList<StampedDetectedObjects> getStampedDetectedObjects() {
        return Tobjects;
    }
    @Override
    public String toString() {
        return "Camera{" +
                "id=" + id +
                ", frequency=" + frequency +
                ", status=" + status +
                ", detectedObjectsList=" + Tobjects +
                '}';
    }

}
