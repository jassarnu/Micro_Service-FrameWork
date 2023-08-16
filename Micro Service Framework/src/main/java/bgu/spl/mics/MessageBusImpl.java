package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> microanditsmassages = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Event, Future> future_events = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventanditsmicros = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadanditsmicros = new ConcurrentHashMap<>();
    public boolean terminated = false;
    private static class MessageBusImplHolder {
        private static MessageBusImpl busInstance = new MessageBusImpl();
    }

    public static MessageBusImpl getInstance() {
        return MessageBusImplHolder.busInstance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        synchronized (eventanditsmicros) {
            eventanditsmicros.putIfAbsent(type, new ConcurrentLinkedQueue<>());
            if (!eventanditsmicros.get(type).contains(m)) eventanditsmicros.get(type).add(m);
        }
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        synchronized (broadanditsmicros) {
            broadanditsmicros.putIfAbsent(type, new ConcurrentLinkedQueue<>());
            if (broadanditsmicros.get(type) != null && !broadanditsmicros.get(type).contains(m))
                broadanditsmicros.get(type).add(m);

        }

    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        future_events.get(e).resolve(result);
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        synchronized (broadanditsmicros) {
            if (broadanditsmicros.containsKey(b.getClass())) {
                ConcurrentLinkedQueue<MicroService> broad = broadanditsmicros.get(b.getClass());
                for (MicroService m : broad) {
                    if (microanditsmassages.get(m) != null) microanditsmassages.get(m).add(b);
                }
            }
        }

    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        synchronized (eventanditsmicros) {
            Future<T> future = new Future<>();
            if (!eventanditsmicros.containsKey(e.getClass())) return null;
            ConcurrentLinkedQueue<MicroService> eventMicros = eventanditsmicros.get(e.getClass());
            MicroService ourTarget = eventMicros.poll();
            if (ourTarget == null) {
                return null;
            } else {
                microanditsmassages.get(ourTarget).add(e);
                eventanditsmicros.get(e.getClass()).add(ourTarget);
                future_events.put(e, future);
            }
            return future;
        }

    }

    @Override
    public void register(MicroService m) {
        microanditsmassages.putIfAbsent(m, new LinkedBlockingQueue<Message>());

    }

    @Override
    public void unregister(MicroService m) {
        synchronized (eventanditsmicros) {
            for (ConcurrentLinkedQueue<MicroService> s : eventanditsmicros.values()) {
                s.remove(m);
            }
        }
        synchronized (broadanditsmicros) {
            for (ConcurrentLinkedQueue<MicroService> s : broadanditsmicros.values()) {
                s.remove(m);
            }
        }
        microanditsmassages.remove(m);

    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        return microanditsmassages.get(m).take();
    }
}
