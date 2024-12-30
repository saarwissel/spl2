package bgu.spl.mics.application.objects;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
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
}

