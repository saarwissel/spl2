package bgu.spl.mics.example.messages;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Broadcast;

public class CrashedBroadcast implements Broadcast {


    public CrashedBroadcast() {
    }

public boolean run() {
    return true;
    }
}
