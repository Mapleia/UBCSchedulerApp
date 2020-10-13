package model;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

public class Scheduler {
    private ArrayList<Course> courseList;

    private Gson gson;

    private String primaryTimePref;
    private String secondaryTimePref;
    private String tertiaryTimePref;

    private boolean spreadClasses = false;

    // constructor
    public Scheduler() {
        courseList = new ArrayList<>();
        gson = new Gson();
    }

    // getters
    public ArrayList<Course> getCourseList() {
        return courseList;
    }

    // setters
    public void setPrimaryTimePref(String timeOfDay) {
        primaryTimePref = timeOfDay;
    }

    // setters
    public void setSecondaryTimePref(String timeOfDay) {
        secondaryTimePref = timeOfDay;
    }

    // setters
    public void setTertiaryTimePref(String timeOfDay) {
        tertiaryTimePref = timeOfDay;
    }

    // setters
    public void setSpreadClasses(Boolean choice) {
        spreadClasses = choice;
    }

    // REQUIRES: Valid course code, course number (both as a string) and format.
    // MODIFIES: this
    // EFFECT: Adds course to the list of courses.
    public void addCourse(String courseCode, String courseNum) {
        String path = "data\\2020W\\" + courseCode + "\\" + courseCode + " " + courseNum + ".json";
        File file = new File(path);

        if (file.exists()) {
            try {
                Reader readFile = new FileReader(file);
                Course course = gson.fromJson(readFile, Course.class);
                course.addAllSections();
                courseList.add(course);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException();
        }
    }


}
