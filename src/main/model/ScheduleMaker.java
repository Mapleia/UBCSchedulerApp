package model;

import java.util.ArrayList;

public class ScheduleMaker {
    private final TimeTable timeTable;
    private ArrayList<Section> finalTimeTable;
    ArrayList<Course> allCourses;

    // constructor
    public ScheduleMaker(TimeTable timeTable) {
        this.timeTable = timeTable;
        finalTimeTable = new ArrayList<>();
        allCourses = timeTable.getCourseList();

    }

    // REQUIRES: Course list size > 0.
    // EFFECT: Create a valid timetable.
    public ArrayList<Section> makeTimeTable() {
        //stub
        return finalTimeTable;
    }

}
