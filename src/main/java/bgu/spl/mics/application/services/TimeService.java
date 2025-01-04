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
    public TimeService(int TickTime, int Duration) {
        super("timeGuy",1);
        this.TickTime = TickTime;
        this.Duration = Duration;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {

        long start = System.currentTimeMillis();
        long time = (this.TickTime/1000)*this.Duration;
        int c = 0;
        while(time > System.currentTimeMillis()-start){  //+++++++++++++++++++++++++
            if (System.currentTimeMillis()-start % this.TickTime == 0){
                c = c+1;
                StatisticalFolder.getInstance().setSystemRuntime(c);
                sendBroadcast(new TickBroadcast(c));

            }
            else {
                try {
                    wait(this.TickTime - (System.currentTimeMillis()-start % this.TickTime));
                } catch (InterruptedException e) {
                    e.printStackTrace(); //אמור לשמש לדיבאגינג אבל אחר כך נעיף
                }
            }

        }
        sendBroadcast(new CrashedBroadcast());
        sendBroadcast(new TerminatedBroadcast("time")); //צריך ליצור את השידור הזה
    }
}
