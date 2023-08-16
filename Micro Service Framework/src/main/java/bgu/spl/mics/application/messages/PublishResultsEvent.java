package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class PublishResultsEvent implements Event {
    private Model goodModel;
    private Student student;
    public PublishResultsEvent(Model goodModel, Student student){
        this.student=student;
        this.goodModel = goodModel;
    }

    public Model getGoodModel() {
        return goodModel;
    }

    public Student getStudent() {
        return student;
    }
}
