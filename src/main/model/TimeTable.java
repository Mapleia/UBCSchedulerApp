package model;

import com.google.gson.Gson;
import exceptions.NoCourseFound;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import java.util.ArrayList;

// Represents the general information about their schedule, and stores selected courses.
public class TimeTable {
    private final ArrayList<Course> courseList = new ArrayList<>();
    private boolean spreadClasses = false;

    public String primaryTimePref;
    public String secondaryTimePref;
    public String tertiaryTimePref;

    public int winterOrSummer; // 0 = Winter, 1 = Summer
    public int yearFall;
    public int yearSpring;
    public int yearSummer;

    public static final int TERM_FALL = 9;
    public static final int TERM_SPRING = 1;
    public static final int TERM_SUMMER1 = 5;
    public static final int TERM_SUMMER2 = 7;


    // constructor (for schedulerApp)
    public TimeTable(int year) {
        this.yearFall = year;
        this.yearSpring = year + 1;
        this.yearSummer = year;
    }

    // constructor
    public TimeTable(int year, int winterOrSummer) {
        this.yearFall = year;
        this.yearSpring = year + 1;
        this.yearSummer = year;
        this.winterOrSummer = winterOrSummer;
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

    // setters
    public void setWinterOrSummer(int winterOrSummer) {
        this.winterOrSummer = winterOrSummer;
    }

    // getters
    public boolean getSpreadClasses() {
        return spreadClasses;
    }

    // REQUIRES: Valid course code, course number (both as a string) and format.
    // MODIFIES: this
    // EFFECT: Adds course to the list of courses.
    public void addCourse(String courseCode, String courseNum) throws NoCourseFound, FileNotFoundException {
        // Gson code referenced http://tutorials.jenkov.com/java-json/gson.html.
        String path;

        if (winterOrSummer == 0) {
            path = "data\\" + yearFall + "W" + "\\" + courseCode + "\\" + courseCode + " " + courseNum + ".json";
        } else {
            path = "data\\" + yearSummer + "S" + "\\" + courseCode + "\\" + courseCode + " " + courseNum + ".json";
        }
        File file = new File(path);
        Gson gson = new Gson();

        if (file.exists()) {
            Reader readFile = new FileReader(file);
            Course course = gson.fromJson(readFile, Course.class);
            course.setTimeTable(this);
            course.addAllSections();
            course.countActivity();
            courseList.add(course);
        } else {
            throw new NoCourseFound();
        }
    }

    public void removeCourse(String courseCode, String courseNum) throws NoCourseFound {
        Course removedCourse = new Course(courseCode, courseNum);
        if (!courseList.contains(removedCourse)) {
            throw new NoCourseFound();
        } else {
            courseList.remove(removedCourse);
        }
    }

}
