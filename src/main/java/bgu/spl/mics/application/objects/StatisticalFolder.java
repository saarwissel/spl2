package bgu.spl.mics.application.objects;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {

    private static class statHolder {
        private static final StatisticalFolder INSTANCE = new StatisticalFolder();

    }
    int systemRuntime;
    int numDetectedObjects;
    int numTrackedObjects;
    int numLandmarks;

    public StatisticalFolder() {
        this.systemRuntime = 0;
        this.numDetectedObjects = 0;
        this.numTrackedObjects = 0;
        this.numLandmarks = 0;
    }

    public int getNumDetectedObjects() {
        return numDetectedObjects;
    }

    public int getNumLandmarks() {
        return numLandmarks;
    }

    public int getNumTrackedObjects() {
        return numTrackedObjects;
    }

    public int getSystemRuntime() {
        return systemRuntime;
    }

    public void setNumDetectedObjects(int numDetectedObjects) {
        this.numDetectedObjects = this.numTrackedObjects+numDetectedObjects;
    }

    public void setNumLandmarks(int numLandmarks) {
        this.numLandmarks = this.numLandmarks+numLandmarks;
    }

    public void setNumTrackedObjects(int numTrackedObjects) {
        this.numTrackedObjects = this.numTrackedObjects+numTrackedObjects;
    }

    public void setSystemRuntime(int systemRuntime) {
        this.systemRuntime = systemRuntime;
    }

    public static synchronized StatisticalFolder getInstance() {
        return StatisticalFolder.statHolder.INSTANCE;

    }
}

