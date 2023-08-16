package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private Data data ;//- the Data the batch belongs to
    private int start_index;//The index of the first sample in the batch.
    private int end_index;
    private Data.Type type;

    public DataBatch(Data data,int start_index,int end_index, Data.Type type) {
        this.data = data;
        this.start_index = start_index;
        this.end_index =end_index;
        this.type = type;
    }

    public int getStart_index() {
        return start_index;
    }

    public void setStart_index(int start_index) {
        this.start_index = start_index;
    }

    public int getEnd_index() {
        return end_index;
    }

    public void setEnd_index(int end_index) {
        this.end_index = end_index;
    }

    public Data getData() {
        return data;
    }

    public Data.Type getType() {
        return type;
    }
}
