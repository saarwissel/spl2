package bgu.spl.mics.example.messages;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;


public class TerminatedBroadcast implements Broadcast {

    public TerminatedBroadcast() {

    }
    public boolean run(){
        return true;
    }

}
