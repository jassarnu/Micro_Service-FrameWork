package bgu.spl.mics.example;

import bgu.spl.mics.application.objects.DataBatch;
import bgu.spl.mics.application.objects.GPU;

public class Unprocessed {
    private DataBatch data;
    private GPU gpu;

    public Unprocessed(DataBatch data, GPU gpu){
        this.data = data;
        this.gpu = gpu;
    }

    public DataBatch getData(){
        return data;
    }

    public GPU getGpu(){
        return gpu;
    }

}
