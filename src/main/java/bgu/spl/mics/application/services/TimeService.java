package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.SystemServicesCountDownLatch;

import java.util.concurrent.atomic.AtomicInteger;

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
    int currentTick;
    public TimeService(int TickTime, int Duration) {
        super("timeGuy",100);
        this.TickTime = TickTime;
        this.Duration = Duration;
        this.crashed=false;
        this.currentTick = 0;
    }

    public int getTickTime() {
        return TickTime;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast c) -> {

            this.crashed = true;
            terminate();
        });
        subscribeBroadcast(TerminatedBroadcast.class , (TerminatedBroadcast t) ->{
            if(t.getService() == "time")
            {
                this.terminate();
            }
            if(t.getService() == "fusion")
            {
                this.terminate();
            }

        });
        try {SystemServicesCountDownLatch.getInstance().getCountDownLatch().await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        while(this.currentTick < this.Duration && !this.crashed)
        {
            TickBroadcast Tsend = new TickBroadcast(this.currentTick);
            sendBroadcast(Tsend);
            try {
                Thread.sleep(this.TickTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("sent tick"+this.currentTick);
            this.currentTick++;

        }
        sendBroadcast(new TerminatedBroadcast("time"));
        this.terminate();
        System.out.println("finish init"+ this.getName());

    }

}
