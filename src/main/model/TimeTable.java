package model;

import com.google.gson.Gson;
import exceptions.NoCourseFound;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

public class TimeTable {
    private final ArrayList<Course> courseList;
    private boolean spreadClasses = false;

    public String primaryTimePref;
    public String secondaryTimePref;
    public String tertiaryTimePref;

    // constructor
    public TimeTable() {
        courseList = new ArrayList<>();
    }

    // getters
    public ArrayList<Course> getCourseList() {
        return courseList;
    }

    // setters
    public void setTimePref(String[] timePref) {
        primaryTimePref = timePref[0];
        secondaryTimePref = timePref[1];
        tertiaryTimePref = timePref[2];
    }

    // setters
    public void setSpreadClasses(Boolean choice) {
        spreadClasses = choice;
    }

//    // getters
//    public boolean getSpreadClasses() {
//        return spreadClasses;
//    }

    // REQUIRES: Valid course code, course number (both as a string) and format.
    // MODIFIES: this
    // EFFECT: Adds course to the list of courses.
    public void addCourse(String courseCode, String courseNum) throws Exception {
        String path = "data\\2020W\\" + courseCode + "\\" + courseCode + " " + courseNum + ".json";
        File file = new File(path);
        Gson gson = new Gson();
        if (file.exists()) {
            Reader readFile = new FileReader(file);
            Course course = gson.fromJson(readFile, Course.class);
            course.setPrimaryTimePref(primaryTimePref);
            course.setSecondaryTimePrefTimePref(secondaryTimePref);
            course.setTertiaryTimePrefTimePref(tertiaryTimePref);
            course.addAllSections();
            courseList.add(course);
        } else {
            throw new NoCourseFound();
        }
    }
}
