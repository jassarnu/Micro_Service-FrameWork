package bgu.spl.mics.application.objects;


import bgu.spl.mics.example.Unprocessed;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class Cluster {
    private static Cluster singleton = null;
    private ConcurrentLinkedQueue<GPU> GPUS;
    private ConcurrentLinkedQueue<CPU> CPUS;
    private LinkedBlockingQueue<Unprocessed> unprocessed;
    private ConcurrentHashMap<GPU, LinkedBlockingQueue<DataBatch>> processed;
    private ConcurrentHashMap<CPU, GPU> CPUandGPU;
    private Statics statics;
    private boolean terminated;

    private Cluster() {
        this.statics = Statics.getInstance();
        GPUS = new ConcurrentLinkedQueue<>();
        CPUS = new ConcurrentLinkedQueue<>();
        unprocessed = new LinkedBlockingQueue<>();
        processed = new ConcurrentHashMap<>();
        CPUandGPU = new ConcurrentHashMap<>();
        terminated = false;
    }

    public static Cluster getInstance() {
        if (singleton == null)
            singleton = new Cluster();
        return singleton;
    }
/*
    public CPU getCPuTarget() {
        CPU target = CPUS.peek();
        for (int i = 0; i < CPUS.size(); i++) {
            if (target.canGetNow()) {
                target = CPUS.poll();
                CPUS.add(target);
                return target;
            } else {
                CPUS.poll();
            }
        }
        return null;
    }

*/
//
//    private void sendtocputarget(DataBatch databatch, int idxGPUSended) throws InterruptedException {
//        boolean foundCpuTarget = false;
//        while (!CPUS.isEmpty() & !foundCpuTarget) {
//            if (CPUS.peek().haveplace()) {
//                foundCpuTarget = true;
//                CPU CPUTarget = CPUS.poll();
//                if(!unprocessed.isEmpty()) {
//                    Unprocessed fixit = unprocessed.poll();
//                    CPUTarget.sendtoprocessandgetprocess(fixit.getData(), fixit.getGpu().getIdx());
//                }
//                sendtogputarget(processed.get(idxGPUSended).take(), idxGPUSended);
//                CPUS.add(CPUTarget);
//            } else
//                CPUS.poll();
//
//        }
//    }

/*
    private void sendtoclusterproceeddata(DataBatch processedDat, int idxGPUSended) throws InterruptedException {
        processed.putIfAbsent(idxGPUSended, new LinkedBlockingQueue<>());
        processed.get(idxGPUSended).put(processedDat);
        if (!processed.isEmpty()) {
            sendtogputarget(processed.get(idxGPUSended).take(), idxGPUSended);
        }
    }
*/

    public Statics getStatics() {
        return statics;
    }

    public void setCPUS(ConcurrentLinkedQueue<CPU> CPUS) {
        this.CPUS = CPUS;
    }

    public void setGPUS(ConcurrentLinkedQueue<GPU> GPUS) {
        this.GPUS = GPUS;
    }

    public void addUnprocessed(Unprocessed shouldAdd) {
        unprocessed.add(shouldAdd);
    }
/*
    private void sendtocpu(Type type) throws InterruptedException {
        CPU cputarget = getCPuTarget();
        cputarget.process(unprocessed.poll(), type);
    }*/

    public void addProcessed(DataBatch data, CPU cpu) {
        try {
            GPU g = CPUandGPU.remove(cpu);
            processed.putIfAbsent(g, new LinkedBlockingQueue<>());
            processed.get(g).put(data);
            getStatics().setTotal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
/*
    public void sendoclusterproceeddata(DataBatch data, GPU gpu) throws InterruptedException {
        processed.putIfAbsent(gpu, new LinkedBlockingQueue<>());
        processed.get(gpu).put(data);
        gpu.getTheProcessedData(processed.get(gpu).take());
    }
*/
    public void getUnprocessed(CPU cpu) {
        Unprocessed data = unprocessed.poll();
        if (data != null) {
            cpu.setData(data.getData());
            CPUandGPU.putIfAbsent(cpu, data.getGpu());
        }
    }

    public DataBatch getProcessedData(GPU gpu) {
        DataBatch data = null;
        try {
            if (processed.get(gpu) == null) processed.put(gpu, new LinkedBlockingQueue<>());
            while (!terminated & data == null) {
                data = processed.get(gpu).poll();
                Thread.sleep(1);
            }
        } catch(Exception  e) { e.printStackTrace();}
            return data;
    }

    public void updateCpuTime(CPU cpu) {
        statics.addTimeUnitUsedByCpu(cpu.getCollected_time());
    }

    public void terminate() {
        terminated = true;
    }
}

