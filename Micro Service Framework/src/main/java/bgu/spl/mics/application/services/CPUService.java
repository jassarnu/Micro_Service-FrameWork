package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateMessage;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;

import java.util.concurrent.CountDownLatch;
//// CPU service is responsible for handling the {@link DataPreProcessEvent}.
/**
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private int cores;
    private Cluster cluster = Cluster.getInstance();
    private CPU cpu;
    private CountDownLatch latch;

    public CPUService(String name, int cores, CPU cpu) {
        super(name);
        this.cores = cores;
        this.cpu = cpu;
    }

    public CPU convert(){
        return cpu;
    }

    public void setlatch(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, msg -> {
            this.cpu.setTick(msg.getTick());
            this.cpu.getDataOrFinishData();
        });
        subscribeBroadcast(TerminateMessage.class, msg -> {
            terminate();
            cluster.terminate();
            cluster.updateCpuTime(cpu);
        });
        latch.countDown();
    }

    public int getCores() {
        return cores;
    }

}
