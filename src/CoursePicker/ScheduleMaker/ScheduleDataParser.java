package CoursePicker.ScheduleMaker;

import java.nio.charset.Charset;
import java.time.DayOfWeek;
import java.util.*;
import java.io.*;
import java.util.function.Predicate;

/**
 * Created by Juliang on 4/4/16.
 */
public class ScheduleDataParser {
    private ScheduleDataGetter dataGetter;
    private List<FutureCourse> courseList;

    private List<String> splitData(String source){
        String[] splitedSource = source.split("\\n{9}");
        LinkedList<String> courseDataList = new LinkedList<>(Arrays.asList(splitedSource));
        courseDataList.removeIf((String s) -> {
            return !s.contains("Associated Term:");
        });
        return courseDataList;
    }
    private List<FutureCourse> parseCourses(){

        /**
         * Parsing rules
         * 1. Lots of lines
         * 2. One line with subject name in it
         * 3. Lots of lines
         * 3. Line with "Instructor:"
         * 4. Instructor name
         * 5. Lots of lines
         * 6. Lines with "Credits" in it
         * 7. Lots of lines
         * 8. Line with time
         * 9. Line with days
         * 10. Line with location
         * 11. Lots of Lines
         * 12. If Line with time occurs again, go to 8, else go to 1.
         */
        List<String> dataList = this.splitData(this.dataGetter.getCleanData());
        List<FutureCourse> futureCourseList = new ArrayList<>();
        try{
            for (String data: dataList){
                if (!data.contains("Traditional"))
                    continue;
                String line;
                String instructor = "TBA";
                String course = null;
                String number = null;
                String section = null;
                String term = null;
                int credits = -1;
                List<TimeInterval> time = new ArrayList<>();
                InputStream stream = new ByteArrayInputStream(data.getBytes(Charset.forName("UTF-8")));
                Scanner in = new Scanner(stream);
                while (in.hasNextLine()){
                    line = in.nextLine();
                    if (line.contains(this.dataGetter.getSubject())){
                        course = this.dataGetter.getSubject();
                        number = line.substring(line.length()-9,line.length()-6);
                        section = line.substring(line.length()-3,line.length());
                        term = this.dataGetter.getTerm();
                    }else if (line.contains("Instructors:")){
                        instructor = in.next() + " " + in.next();
                    }else if (line.contains("Credit")){
                        credits = Integer.parseInt(line.substring(line.indexOf("Credits")-6,line.indexOf("Credits")-5));
                    }else if (line.contains(" am") || line.contains(" pm")){
                        String[] interval = line.split(" - ");
                        int startHour = Integer.parseInt(interval[0].substring(0,interval[0].indexOf(':')));
                        int startMinute = Integer.parseInt(interval[0].substring(interval[0].indexOf(':')+1,interval[0].indexOf(':')+3));
                        if (interval[0].contains("pm") && startHour != 12){
                            startHour += 12;
                        }
                        int endHour = Integer.parseInt(interval[1].substring(0,interval[1].indexOf(':')));
                        int endMinute = Integer.parseInt(interval[1].substring(interval[1].indexOf(':')+1,interval[1].indexOf(':')+3));
                        if (interval[1].contains("pm") && endHour != 12){
                            endHour += 12;
                        }
                        String dayString = in.nextLine();
                        List<DayOfWeek> days= new ArrayList<>();
                        if (dayString.indexOf('M') != -1){
                            days.add(DayOfWeek.MONDAY);
                        }
                        if (dayString.indexOf('T') != -1){
                            days.add(DayOfWeek.TUESDAY);
                        }
                        if (dayString.indexOf('W') != -1){
                            days.add(DayOfWeek.WEDNESDAY);
                        }
                        if (dayString.indexOf('R') != -1){
                            days.add(DayOfWeek.THURSDAY);
                        }
                        if (dayString.indexOf('F') != -1){
                            days.add(DayOfWeek.FRIDAY);
                        }
                        time.add(new TimeInterval(startHour,startMinute,endHour,endMinute,days));
                    }
                }
                if (instructor != null && course != null && number != null && section != null && term != null
                        && credits != -1 && !time.isEmpty())
                    futureCourseList.add(new FutureCourse(instructor, course, number, section, term, credits, time));
            }
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
        return futureCourseList;
    }
    public ScheduleDataParser(ScheduleDataGetter getter){
        this.dataGetter = getter;
        this.courseList = parseCourses();
    }

    public List<FutureCourse> getCourseList() throws Exception{
        if (this.courseList == null || this.courseList.isEmpty())
            throw new Exception("Cannot parse course list correctly\n");
        return courseList;
    }
}
