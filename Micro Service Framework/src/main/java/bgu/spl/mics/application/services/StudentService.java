package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.objects.Student.Degree;

import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private Degree status;
    private String department;
    private Future<Model> futureEvent;
    private List<Model> model;
    private Student stu;
    private CountDownLatch latch_time;
    private Cluster cluster = Cluster.getInstance();
    private int ticktime;
    private int terminatetick;
    private boolean terminate;

    public StudentService(String name, String department, Degree status, List<Model> model, CountDownLatch latch) {
        super(name);
        terminate = false;
        this.status = status;
        this.department = department;
        this.model = model;
        this.stu = new Student(name, status, department);
        this.latch_time = latch;
    }

    @Override
    protected void initialize() {
        //af7s ano atha future rg3ly null atha lazm awde ano ywkf alto5nett l2no lazm train test publish
        try {
            subscribeBroadcast(TerminateMessage.class, msg -> {
                terminate();
                System.out.println(getName() + " received termination");
                terminate = true;
                System.out.println("Test 1");
            });
            subscribeBroadcast(PublishConfrenceBroadcast.class, msg -> {
                stu.setPapersRead(1);
            });
            latch_time.await();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Model mod : model) {
            if (MessageBusImpl.getInstance().terminated) {
                terminate();
            }
            // if (mod.getStat() != Model.status.Tested) {
            futureEvent = sendEvent(new TrainModelEvent(mod));
            mod.setStat(Model.status.Training);
            if (!MessageBusImpl.getInstance().terminated && futureEvent != null && futureEvent.get() != null) {
                //cluster.getStatics().addmodel(mod);
                mod.setStat(Model.status.Trained);
                futureEvent = sendEvent(new TestModelEvent(mod, this.status));
                if (!MessageBusImpl.getInstance().terminated && futureEvent != null && futureEvent.get() != null) {
                    stu.addTrainedModel(mod);
                    mod.setStat(Model.status.Tested);
                    // if the model result good i collect to my array
                    if (!MessageBusImpl.getInstance().terminated && futureEvent != null && futureEvent.get().getRes() == Model.results.Good) {
                        sendEvent(new PublishResultsEvent(futureEvent.get(),this.getStudent()));

                    }
                }
            } else {
                terminate();
                break;
            }
        }
    }

    public void setLatch_time(CountDownLatch latch) {
        this.latch_time = latch;
    }

    public void setTicktime(int ticktime) {
        this.ticktime = ticktime;
    }

    public void setTerminatetick(int terminatetick) {
        this.terminatetick = terminatetick;
    }

    public Student getStudent() {
        return stu;
    }
}
