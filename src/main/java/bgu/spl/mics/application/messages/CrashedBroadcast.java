package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;

public class CrashedBroadcast implements Broadcast {


    public CrashedBroadcast() {
    }

public boolean run() {
    return true;
    }
}
