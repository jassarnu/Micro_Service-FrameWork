package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;//the name of the conference.
    private int date;//the time of the conference.
    private List<Model> publications;
    public ConfrenceInformation(String name,int date){
        this.name=name;
        this.date =date;
        publications = new ArrayList<>();
    }
    public void addModel(Model m){
        if(publications == null) new ArrayList<>();
        publications.add(m);
    }

    public String getName() {
        return name;
    }

    public int getDate() {
        return date;
    }
}
