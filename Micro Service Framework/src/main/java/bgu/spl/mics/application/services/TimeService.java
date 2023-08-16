package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateMessage;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {
    private int current_time;
    private int speed;
    private int terminate_time;
    private CountDownLatch latch;

    public TimeService(int speed, int terminate_time, CountDownLatch latch) {
        super("TimeService");
        this.current_time = 0;
        this.speed = speed;
        this.terminate_time = terminate_time;
        this.latch = latch;
    }

    @Override
    protected void initialize() {
        boolean latchOn = false;
        try {
            while (current_time < terminate_time) {
                sendBroadcast(new TickBroadcast(current_time, current_time / speed));
                //System.out.println(current_time + "/" + terminate_time);
                if (!latchOn) {
                    latch.countDown();
                    latchOn = true;
                }
                TimeUnit.MILLISECONDS.sleep(speed);
                current_time = current_time + speed;
            }
            sendBroadcast(new TerminateMessage());//total time used
            terminate();
            MessageBusImpl.getInstance().terminated = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

