package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    public enum status {PreTrained, Training, Trained, Tested}

    public enum results {None, Good, Bad}

    private String name;//name of the model
    private Data data;// the data the model should train on.
    private status status;
    private results results;

    public Model(String name, Data data) {
        this.name = name;
        this.data = data;
        this.status =status.PreTrained;
        this.results=results.None;

    }
    public Data getData() {
        return data;
    }

    public results getRes() {
        return results;
    }

    public status getStat() {
        return status;
    }
    public void setRes(results res) {
        this.results=res;
    }

    public void setStat(status stat) {
        this.status=stat;
    }

    public String getName() {
        return name;
    }
}
