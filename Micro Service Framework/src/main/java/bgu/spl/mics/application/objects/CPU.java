package bgu.spl.mics.application.objects;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int current_time;
    private int start_time;
    private String name;
    private int cores;//number of cores.
    private DataBatch data ;//the data the cpu currently processing. You can choose a container of your choice
    private LinkedBlockingQueue<DataBatch> cpuqueue; // we did this
    private int collected_time = 0;
    private Cluster cluster = Cluster.getInstance();

    public CPU(String name, int cores) {
        this.name = name;
        this.cores = cores;
        this.data = null;
        cpuqueue =new LinkedBlockingQueue<>(cores);
    }

//    public int getCores() {
//        return cores;
//    }
    /*
        public boolean canGetNow(){
            return data.isEmpty();
        }

        public void sendtoprocessandgetprocess(DataBatch databatch, int idx) throws InterruptedException {
            int tick_first = current_time;
            if(cpuqueue.size() < cores) {
                cpuqueue.add(databatch);
                data.add(databatch);
                collected_time = current_time - tick_first; //?
                cluster.getStatics().setTimeUnitUsedByCpu(collected_time);
                cluster.addProcessed(cpuqueue.poll(), idx);
                data.remove(databatch);
            }
        }

    */
    public void setTick(int time) {
        this.current_time = time;
    }
/*
    public void process(Unprocessed unprocessed, Data.Type type) throws InterruptedException {
        int time_to_wait = getTimetowait(type);
        sleep(time_to_wait);
        cluster.sendoclusterproceeddata(unprocessed.getData(),unprocessed.getGpu());
    }
*/
    private int getTimetowait(Data.Type type) {
        int res = (32/cores);
        if(type.equals( Data.Type.Images)) {
            return res * 4;
        }
        else if (type.equals( Data.Type.Text)){
            return res*2;
        }
        else
            return res;
    }

    public void getDataOrFinishData() {
        if(data == null) {
            //System.out.println("trying to get data at " + current_time);
            cluster.getUnprocessed(this);
            if(data != null){
                //System.out.println("received data at " + current_time);
            }
        } else if(current_time - start_time >= getTimetowait(data.getType())){
            //output
            cluster.addProcessed(data, this);
            //System.out.println("processed a batch, waited " + getTimetowait(data.getType()));
            this.collected_time += (current_time - start_time);
            data = null;
        }
    }

    public void setData(DataBatch data) {
        this.data = data;
        start_time = current_time;
    }

    public int getCollected_time() {
        return collected_time;
    }
}
