package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

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
    AtomicInteger currentTick;
    public TimeService(int TickTime, int Duration) {
        super("timeGuy",100);
        this.TickTime = TickTime;
        this.Duration = Duration;
        this.crashed=false;
        this.currentTick = new AtomicInteger(0);
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
        currentTick.addAndGet(1);
        TickBroadcast Tsend=new TickBroadcast(currentTick.get());
        System.out.println("the tick is "+ currentTick.get() );
        sendBroadcast(Tsend);
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast c) -> {

            this.crashed = true;
            terminate();
        });
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast c) -> {
            currentTick.compareAndSet(currentTick.get(), c.getTick());
            System.out.println("the tick after cas is "+ currentTick.get() );
            if (currentTick.get() > this.Duration) {
                sendBroadcast(new TerminatedBroadcast("time"));
            }
            else{
                TickBroadcast tt=new TickBroadcast(currentTick.get()+1);
                sendBroadcast(tt);
            }
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

    }

}
