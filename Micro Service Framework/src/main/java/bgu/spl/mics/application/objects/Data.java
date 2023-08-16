package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    public Type getType() {
        return type;
    }

    public enum Type {//type of data
        Images, Text, Tabular
    }
    private Type type;
    private int processed;// Number of samples which the GPU has processed for training
    private int size;// number of samples in the data
    public Data(Type type ,int size){
        this.processed=0;
        this.size=size;
        this.type=type;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }
    public int getProcessed() {
        return processed;
    }

    public int getSize() {
        return size;
    }
}
