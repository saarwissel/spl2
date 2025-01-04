package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;


public class TerminatedBroadcast implements Broadcast {
    String service;
    

    public TerminatedBroadcast(String service) {
        this.service = service;
    }

    public String getService() {
        return service;
    }
}
