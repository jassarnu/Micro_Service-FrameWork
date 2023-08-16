package bgu.spl.mics.application.objects;

import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Statics {
    private static Statics singleton = null;
    private List<Student> students;
    private List<ConfrenceInformation> conferences;
    private int cpuTimeUsed;
    private int gpuTimeUsed;
    private AtomicInteger batchesProcessed;

    public static Statics getInstance() {
        if (singleton == null)
            singleton = new Statics();
        return singleton;
    }
    public Statics() {
        this.students = new ArrayList<>();
        this.conferences = new ArrayList<>();
        this.batchesProcessed = new AtomicInteger(0);
        this.cpuTimeUsed = 0;
    }

    /*public void addmodel(Model modevent) {
        modevents.add(modevent);
    }
    */
    public void addStudent(Student st) {
        if(students == null) students = new ArrayList<>();
        students.add(st);
        System.out.println("added " + st.getName());
    }

    public void addConference(ConfrenceInformation con) {
        conferences.add(con);
    }


    public AtomicInteger getTotal() {
        return batchesProcessed;
    }

    public void setTotal() {
        this.batchesProcessed.addAndGet(1);
    }

    public void addTimeUnitUsedByCpu(int collected_time) {
        this.cpuTimeUsed = cpuTimeUsed + collected_time;
    }

    public int getTimeUnitUsedByCpu() {
        return cpuTimeUsed;
    }

    public void printToFile(String outputFile) {
        try (FileWriter file = new FileWriter(outputFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(this, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addTimeUnitUsedByGpu(int i) {
        this.gpuTimeUsed += i;
    }

    public int getTimeUnitUsedByGpu() {
        return gpuTimeUsed;
    }
}
