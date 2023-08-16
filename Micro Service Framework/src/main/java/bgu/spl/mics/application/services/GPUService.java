package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateMessage;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private GPU.Type type;
    private int current_tick;
    private Cluster cluster = Cluster.getInstance();
    private MessageBusImpl bus = MessageBusImpl.getInstance();
    private GPU gpu;
    private int idx;
    private CountDownLatch latch;
    private int ticktime;
    private int terminatetick;
    private int collected_time;

    public GPUService(String name, GPU.Type type, GPU gpu) {
        super(name);
        this.type = type;
        this.gpu = gpu;
        idx = Integer.parseInt(name.substring(3, 4));
        collected_time = 0;
    }

    public GPU convert() {
        return gpu;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateMessage.class, msg -> terminate());
        subscribeBroadcast(TickBroadcast.class, msg -> this.current_tick = msg.getTime());
        subscribeEvent(TrainModelEvent.class, msg -> {
            int first_tick = current_tick;
            gpu.setModel(msg.getModel());
            while(!gpu.isFinishedProcessing()) {
                gpu.process();
                if(!gpu.getData()){
                    complete(msg, null);
                    break;
                }
                gpu.trainData();
            }
            gpu.setFinishedProcessing(false);
            complete(msg, gpu.getModel());
            collected_time = current_tick - first_tick;

        });

        subscribeEvent(TestModelEvent.class, msg -> {
            Random rand = new Random();
            Double rand1 = rand.nextDouble();
            //hen bde ahy ale ygbly student 3shan ageb ho ayydegree
            // check this ?? <= or ==
            if ((rand1 <= 0.6 && msg.getStudentstaus() == Student.Degree.MSc) || (rand1 <= 0.8 && msg.getStudentstaus() == Student.Degree.PhD)) {
                msg.getModel().setRes(Model.results.Good);
            } else {
                msg.getModel().setRes(Model.results.Bad);
            }
            complete(msg, msg.getModel());
        });
        latch.countDown();
    }

    public GPU.Type getType() {
        return type;
    }

    public void setlatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public void setTicktime(int ticktime) {
        this.ticktime = ticktime;
    }

    public void setTerminatetick(int terminatetick) {
        this.terminatetick = terminatetick;
    }
}
