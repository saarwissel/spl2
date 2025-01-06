package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    int TickTime; //  מגדיר כמה זמן זו יחידת זמן במיליסקונדס
    int Duration; // כמה יחידות זמן יש לנו
    boolean crashed;
    public TimeService(int TickTime, int Duration) {
        super("timeGuy",100);
        this.TickTime = TickTime;
        this.Duration = Duration;
        this.crashed=false;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        long startTime = System.currentTimeMillis();
        long totalTime = this.TickTime * this.Duration;
        int tickCounter = 0;
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast c) -> {
            this.crashed = true;
            terminate();
        });
        while (System.currentTimeMillis() - startTime < totalTime && !this.crashed) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            long remTime = Math.max(0, this.TickTime - (elapsedTime % this.TickTime)); // מניעת ערכים שליליים

            if (elapsedTime / this.TickTime > tickCounter) { // וידוא שהטיק הנוכחי לא שודר כבר
                tickCounter++;
                sendBroadcast(new TickBroadcast(tickCounter));
            }

            try {
                Thread.sleep(remTime); // מחכה בצורה מדויקת בין טיקים
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
        if (!this.crashed) {
            sendBroadcast(new TerminatedBroadcast("time"));
            this.terminate();
        }
    }

}
