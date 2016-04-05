package CoursePicker.ScheduleMaker;

/**
 * Created by Juliang on 4/4/16.
 */
import java.util.*;
import java.time.DayOfWeek;

class TimeInterval{
    private int startHour,startMinute, endHour,endMinute;
    private List<DayOfWeek> days;
    TimeInterval(int sh,int sm,int eh,int em,List<DayOfWeek> days){
        startHour = sh;
        startMinute = sm;
        endHour = eh;
        endMinute = em;
        this.days = days;
    }
    @Override
    public String toString(){
        String output = "";
        for (DayOfWeek d : days){
            output += (d + " ");
        }
        output += "\n" + startHour +":"+ startMinute + " - " + endHour +":" + endMinute;
        return output;
    }
}

public class FutureCourse {
    private String instructor, course, number, section, term;
    private int credits;
    private List<TimeInterval> time;

    public FutureCourse(String instructor,String courseName,String number, String section,String term,int credits,
                        List<TimeInterval> time){
        this.instructor = instructor;
        this.course = courseName;
        this.number = number;
        this.section = section;
        this.term = term;
        this.credits = credits;
        this.time = time;
    }
    @Override
    public String toString(){
        String output = term + "\n" + course + "-" + number + " "+ section + " " +credits +" hours \n" + instructor;
        for (TimeInterval ti: time)
            output += (ti +"\n");
        return output;
    }
}
