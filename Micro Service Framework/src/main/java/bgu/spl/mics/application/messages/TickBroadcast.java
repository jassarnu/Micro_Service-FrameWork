package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private int time;
    private int tick;

    public TickBroadcast(int time, int tick){
        super();
        this.time = time;
        this.tick = tick;
    }

    public int getTick() {
        return tick;
    }

    public int getTime() {
        return time;
    }
}
