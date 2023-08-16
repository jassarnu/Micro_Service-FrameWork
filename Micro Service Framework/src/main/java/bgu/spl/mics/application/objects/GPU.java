package bgu.spl.mics.application.objects;

import bgu.spl.mics.example.Unprocessed;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class GPU {
    private boolean finishedProcessing;
    private String name;
    private Model model; //the model that the GPU is currently working on. (null for none)
    private Type type;
    private LinkedBlockingQueue<DataBatch> processedDatas;
    private Cluster cluster = Cluster.getInstance();
    private int idx;
    private int processed;

    public void setFinishedProcessing(boolean b) {
        finishedProcessing = b;
    }


    public enum Type {RTX3090, RTX2080, GTX1080}

    public GPU(String name, Type type) {
        this.name = name;
        this.model = null;
        this.type = type;
        processedDatas = new LinkedBlockingQueue<>(numberOfBatces()); //bde a4yrr al7gm
        idx = Integer.parseInt(name.substring(3, 4));

    }

    public void trainData() {
        int multi = datasWait();
        cluster.getStatics().addTimeUnitUsedByGpu(multi*processedDatas.size());
        //System.out.println("clearing data");
        processedDatas.clear();
        //System.out.println("data was cleared");
    }//dtstna 1/2/4

    private int numberOfBatces() {
        if (type.equals(Type.RTX3090)) {
            return 32;
        } else if (type.equals(Type.GTX1080)) {
            return 8;
        } else {
            return 16;

        }
    }

    public Model getModel() {
        return model;
    }

//    public LinkedBlockingQueue<DataBatch> getGpuqueue() {
//        return GPUQueue;
//    }

    public Type getType() {
        return type;
    }

//    public boolean haveplace() {
//        return GPUQueue.size() < model.getData().getSize();
//    }

//    public void getprocededdata(DataBatch processedData) {
//        if (haveplace()) {
//            GPUQueue.add(processedData);
//        } else
//            GPUQueue.poll();
//    }

//    public int getIdx() {
//        return idx;
//    }

    public void setModel(Model mod) {
        this.model = mod;
    }



/*  public void getTheProcessedData(DataBatch data) throws InterruptedException {
        if (GPUQueue.hasPlace()) {
            GPUQueue.add(data);
            sleep(gpu_wait_time());
            GPUQueue.poll();
        } else {
            sleep(gpu_wait_time());
            GPUQueue.poll();
        }
    }
*/
    private int datasWait() {
        if (this.type == Type.GTX1080) {
            return 4;
        } else if (this.type == Type.RTX2080) {
            return 2;
        } else
            return 1;
    }

    public boolean isFinishedProcessing() {
        return finishedProcessing;
    }

    public void process() {
        int num = 0;
        processed = 0;
        Data data = model.getData();
        while (num < numberOfBatces() && data.getProcessed() < data.getSize()) { //tnsash sho dsawe bs alwhile false
            DataBatch databatch = new DataBatch(data, data.getProcessed(), data.getProcessed() + 1000, data.getType()); //checkkkk
            cluster.addUnprocessed(new Unprocessed(databatch, this));
            data.setProcessed(data.getProcessed() + 1000);
            num++;
            processed++;
            //System.out.println("sent data");
        }
    }

    public boolean getData() {
        int num = 0;
        try {
            while (num < processed) {
                DataBatch data = cluster.getProcessedData(this);
                if (data != null) {
                    processedDatas.put(data);
                    num++;
                } else return false;
            }
            if (model.getData().getProcessed() == model.getData().getSize())
                finishedProcessing = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }


}
