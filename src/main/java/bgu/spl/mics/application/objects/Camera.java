package bgu.spl.mics.application.objects;
import java.util.*;

import bgu.spl.mics.application.objects.StampedDetectedObjects;

public class Camera {
    // TODO: Define fields and methods.
    private int id; // מזהה המצלמה
    private int frequency; // פרק הזמן לשליחת אירועים
    private CameraStatus status; // סטטוס המצלמה
    private List<StampedDetectedObjects> detectedObjects; // רשימת אובייקטים שזוהו



    public Camera(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.status = CameraStatus.UP;
        this.detectedObjects = new ArrayList<>();
    }
    public Camera(int id, int frequency, List<StampedDetectedObjects> detectedObjects) {
        this.id = id;
        this.frequency = frequency;
        this.status = CameraStatus.UP;
        this.detectedObjects = detectedObjects;

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

    public void setStatus(int status) {
        if (status == 0) {
            this.status = CameraStatus.UP;
        } else if (status == 1) {
            this.status = CameraStatus.DOWN;
        } else {
            this.status = CameraStatus.ERROR;
        }
    }

    public List<StampedDetectedObjects> getStampedDetectedObjects() {
        return detectedObjects;
    }


    public List<DetectedObject> getDetectedObjectsAtTick(int tick) {
        // סינון הרשימה של StampedDetectedObjects לפי הזמן הנתון
        for (StampedDetectedObjects stampedObject : detectedObjects) {
            if (stampedObject.getTime() == tick) {
                return stampedObject.getDobjects(); // מחזיר את רשימת האובייקטים שהתגלו בטיק זה
            }
        }
        return new LinkedList<>(); // אם לא נמצאו אובייקטים בטיק הזה, מחזיר רשימה ריקה
    }
    @Override
    public String toString() {
        return "Camera{" +
                "id=" + id +
                ", frequency=" + frequency +
                ", status=" + status +
                ", detectedObjectsList=" + detectedObjects +
                '}';
    }

}
