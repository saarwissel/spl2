package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {

    private static class statHolder {
        private static final StatisticalFolder INSTANCE = new StatisticalFolder();

    }
    AtomicInteger systemRuntime;
    AtomicInteger numDetectedObjects;
    AtomicInteger numTrackedObjects;
    AtomicInteger numLandmarks;

    public StatisticalFolder() {
        this.systemRuntime = new AtomicInteger(0);
        this.numDetectedObjects =  new AtomicInteger(0);
        this.numTrackedObjects = new AtomicInteger(0);
        this.numLandmarks = new AtomicInteger(0);
    }

    public AtomicInteger getNumDetectedObjects() {
        return numDetectedObjects;
    }

    public AtomicInteger getNumLandmarks() {
        return numLandmarks;
    }

    public AtomicInteger getNumTrackedObjects() {
        return numTrackedObjects;
    }

    public AtomicInteger getSystemRuntime() {
        return systemRuntime;
    }

    public void setNumDetectedObjects(int numDetectedObjects) {

        this.numDetectedObjects.addAndGet(numDetectedObjects);
    }

    public void setNumLandmarks(int numLandmarks) {

        this.numLandmarks.addAndGet(numLandmarks);
    }

    public void setNumTrackedObjects(int numTrackedObjects) {

        this.numTrackedObjects.addAndGet(numTrackedObjects);
    }

    public void setSystemRuntime(int systemRuntime) {

        this.systemRuntime.set(systemRuntime);
    }

    public static synchronized StatisticalFolder getInstance() {
        return StatisticalFolder.statHolder.INSTANCE;

    }
}