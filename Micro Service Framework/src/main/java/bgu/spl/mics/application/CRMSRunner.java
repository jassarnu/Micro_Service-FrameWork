package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        JsonParser parser = new JsonParser();
        try (FileReader fileReader = new FileReader(args[0])) {
            Object obj = parser.parse(fileReader);
            JsonObject jsonobject = (JsonObject) obj;
            List<Thread> threads = new ArrayList<>();
            CountDownLatch latch_time = new CountDownLatch(1);
            //Students
            JsonArray studentsArray = jsonobject.get("Students").getAsJsonArray();
            List<StudentService> students = Students_data(studentsArray, latch_time);
            //gpus
            JsonArray GPUSArray = jsonobject.get("GPUS").getAsJsonArray();
            List<GPUService> gpus = GPUS_data(GPUSArray);
            for (GPUService gpu : gpus) {
                threads.add(new Thread(gpu));
            }

            //forCpus
            JsonArray CPUSArray = jsonobject.get("CPUS").getAsJsonArray();
            List<CPUService> cpus = CPUS_data(CPUSArray);
            for (CPUService cpu : cpus) {
                threads.add(new Thread(cpu));
            }

            //conferences
            JsonArray ConArray = jsonobject.get("Conferences").getAsJsonArray();
            List<ConferenceService> confs = conferences_data(ConArray);
            for (ConferenceService conf : confs) {
                threads.add(new Thread(conf));
            }

            //TimeService
            int ticktime = jsonobject.get("TickTime").getAsInt();
            int duration = jsonobject.get("Duration").getAsInt();

            for (GPUService gpu: gpus) {
                gpu.setTicktime(ticktime);
                gpu.setTerminatetick(duration);
            }

            for (StudentService student: students) {
                student.setTicktime(ticktime);
                student.setTerminatetick(duration);
            }

            TimeService time = new TimeService(ticktime, duration, latch_time);

            //threads runnig
            CountDownLatch latch_rest_threads = new CountDownLatch(gpus.size()+cpus.size()+confs.size());
            for (GPUService gpu: gpus) {
                gpu.setlatch(latch_rest_threads);
            }
            for (CPUService cpu: cpus) {
                cpu.setlatch(latch_rest_threads);
            }
            for (ConferenceService con: confs) {
                con.setlatch(latch_rest_threads);
            }
            for (StudentService ss : students) {
                ss.setLatch_time(latch_time);
            }

            //building cluster
            ConcurrentLinkedQueue<GPU> convert_gpu = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<CPU> convert_cpu = new ConcurrentLinkedQueue<>();
            for (int i = 0; i < gpus.size(); i++) {
                convert_gpu.add(gpus.get(i).convert());
            }
            for (int i = 0; i < cpus.size(); i++) {
                convert_cpu.add(cpus.get(i).convert());
            }
            Cluster.getInstance().setCPUS(convert_cpu);
            Cluster.getInstance().setGPUS(convert_gpu);

            CountDownLatch latch_finish = new CountDownLatch(threads.size()+students.size()+1);
            for (CPUService cpu:cpus) {
                cpu.setLatch_finish(latch_finish);
            }
            for (GPUService gpu:gpus) {
                gpu.setLatch_finish(latch_finish);
            }
            for (ConferenceService con:confs) {
                con.setLatch_finish(latch_finish);
            }
            for (StudentService student:students) {
                student.setLatch_finish(latch_finish);
            }
            time.setLatch_finish(latch_finish);
            for (Thread thread : threads) {
                thread.start();
            }
            latch_rest_threads.await();
            for (StudentService ss : students) {
                new Thread(ss).start();
            }
            new Thread(time).start();
            latch_finish.await();
            for(StudentService st: students) {
                st.getStudent().resetModels();
                Statics.getInstance().addStudent(st.getStudent());
            }
            for(ConferenceService con: confs){
                Statics.getInstance().addConference(con.getConference());
            }
            Cluster.getInstance().getStatics().printToFile("output.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    //==================================================================================================================


    private static List<StudentService> Students_data(JsonArray studentsArray, CountDownLatch latch) {
        List<StudentService> students = new LinkedList<>();
        //iterator students array
        Iterator<JsonElement> iterator = studentsArray.iterator();
        //loop on students array
        while (iterator.hasNext()) {
            JsonObject jsoniter = iterator.next().getAsJsonObject();
            String name = jsoniter.get("name").getAsString();
            String department = jsoniter.get("department").getAsString();
            Student.Degree status;
            switch (jsoniter.get("status").getAsString()) {
                case "MSc":
                    status = Student.Degree.MSc;
                    break;
                default:
                    status = Student.Degree.PhD;
                    break;
            }
            //for models
            JsonArray modelsArray = jsoniter.get("models").getAsJsonArray();
            List<Model> models = Models_data(modelsArray);
            students.add(new StudentService(name, department, status, models, latch));
        }

        return students;
    }

    private static List<Model> Models_data(JsonArray ModelArray) {
        Iterator<JsonElement> modelIter = ModelArray.iterator();
        Model mod = null;
        List<Model> m = new ArrayList<>();
        while (modelIter.hasNext()) {
            JsonObject model = modelIter.next().getAsJsonObject();
            Data data;
            String namemodel = model.get("name").getAsString();
            String typemodel = model.get("type").getAsString();
            int sizemodel = model.get("size").getAsInt();
            if (typemodel.equals("Images")) {
                data = new Data(Data.Type.Images, sizemodel);
            }
            else if (typemodel.equals("Text")) {
                data = new Data(Data.Type.Text, sizemodel);
            }
            else {
                data = new Data(Data.Type.Tabular, sizemodel);
            }
            mod = new Model(namemodel, data);
            m.add(mod);
        }
        return m;
    }


    //==================================================================================================================


    private static List<GPUService> GPUS_data(JsonArray GPUSArray) {
        List<GPUService> output = new LinkedList<>();
        GPUService gpu = null;
        int i = 0;
        Iterator<JsonElement> GPUSiter = GPUSArray.iterator();
        while (GPUSiter.hasNext()) {
            String gputype = GPUSiter.next().getAsString();
            if (gputype.equals("RTX3090")) {
                gpu = new GPUService("GPU" + i, GPU.Type.RTX3090, new GPU("GPU" + i, GPU.Type.RTX3090));
            } else if (gputype.equals("RTX2080") ) {
                gpu = new GPUService("GPU" + i, GPU.Type.RTX2080, new GPU("GPU" + i, GPU.Type.RTX2080));
            } else {
                gpu = new GPUService("GPU" + i, GPU.Type.GTX1080, new GPU("GPU" + i, GPU.Type.GTX1080));
            }
            i++;
            output.add(gpu);
        }
        return output;
    }


    //==================================================================================================================


    private static List<CPUService> CPUS_data(JsonArray CPUSArray) {
        List<CPUService> output = new LinkedList<>();
        int i = 1;
        Iterator<JsonElement> CBUSiter = CPUSArray.iterator();
        while (CBUSiter.hasNext()) {
            int cores = CBUSiter.next().getAsInt();
            output.add(new CPUService("CPU" + i, cores, new CPU("CPU" + i,cores)));
            i++;
        }
        return output;
    }


    //==================================================================================================================


    private static List<ConferenceService> conferences_data(JsonArray ConArray) {
        List<ConferenceService> output = new LinkedList<>();
        Iterator Coniter = ConArray.iterator();
        while (Coniter.hasNext()) {
            JsonObject confiter = (JsonObject) Coniter.next();
            String name = confiter.get("name").getAsString();
            int date = confiter.get("date").getAsInt();
            output.add(new ConferenceService(name, new ConfrenceInformation(name, date)));
        }
        return output;
    }
}
