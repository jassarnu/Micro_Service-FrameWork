package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    public String getDepartment() {
        return department;
    }

    public Degree getStatus() {
        return status;
    }

    public enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private List<Model> modelslist;
    private List<Model> trainedModels;

    public Student(String name, Degree status, String department) {
        this.name = name;
        this.department = department;
        this.status = status;
        this.publications = 0;
        this.papersRead = 0;
        modelslist = new ArrayList<>();
        trainedModels = new ArrayList<>();
    }

    public void addTrainedModel(Model m){
        if (trainedModels == null) trainedModels = new ArrayList<>();
        trainedModels.add(m);
    }
    public void resetModels(){
        modelslist = null;
    }
    public String getName() {
        return name;
    }
    public int getPapersRead() {
        return papersRead;
    }

    public void setPapersRead(int papersRead) {
        this.papersRead += papersRead;
    }

    public int getPublications() {
        return publications;
    }

    public void setPublications(int publications) {
        this.publications += publications;
    }

}

