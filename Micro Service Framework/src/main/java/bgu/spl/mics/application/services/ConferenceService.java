package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConfrenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TerminateMessage;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private int current_tick;
    private List<Model> modelsgood;
    private CountDownLatch latch;
    private ConfrenceInformation conference;

    public ConferenceService(String name, ConfrenceInformation conference) {
        super(name);
        this.conference = conference;
        modelsgood =new ArrayList<>();
    }

    public void setlatch(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, msg -> this.current_tick = msg.getTime());
        subscribeEvent(PublishResultsEvent.class,msg->{
            modelsgood.add(msg.getGoodModel());
            conference.addModel(msg.getGoodModel());
            msg.getStudent().setPublications(1);
        });
        sendBroadcast(new PublishConfrenceBroadcast(modelsgood));
        subscribeBroadcast(TerminateMessage.class, msg -> terminate());
        latch.countDown();
    }

    public ConfrenceInformation getConference() {
        return conference;
    }
}
