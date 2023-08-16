package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class TestModelEvent implements Event <Model>{
    private Model model;
    private Student.Degree studentstaus;
    public TestModelEvent(Model model, Student.Degree studentstaus){
        this.model = model;
        this.studentstaus =studentstaus;
    }
    public Model getModel() {
        return model;
    }

    public Student.Degree getStudentstaus() {
        return studentstaus;
    }
}

