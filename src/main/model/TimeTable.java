package model;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import exceptions.NoCourseFound;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

// Represents the general information about their schedule, and stores selected courses.
public class TimeTable {
    private final ArrayList<Course> courseList = new ArrayList<>();
    private boolean spreadClasses;

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

    // constructor
    public TimeTable(int year, int winterOrSummer, String[] preferenceArray, boolean spreadClasses) {
        this.yearFall = year;
        this.yearSpring = year + 1;
        this.yearSummer = year;
        this.winterOrSummer = winterOrSummer;

        this.primaryTimePref = preferenceArray[0];
        this.secondaryTimePref = preferenceArray[1];
        this.tertiaryTimePref = preferenceArray[2];

        this.spreadClasses = spreadClasses;
    }

    // getters
    public ArrayList<Course> getCourseList() {
        return courseList;
    }

    public boolean getSpreadClasses() {
        return spreadClasses;
    }

    // setters
    public void setSpreadClasses(Boolean choice) {
        spreadClasses = choice;
    }

    // REQUIRES: Valid course code, course number (both as a string) and format.
    // MODIFIES: this
    // EFFECT: Adds course to the list of courses.
    public void addCourse(String courseCode, String courseNum) throws Exception {
        String path;
        if (winterOrSummer == 0) {
            path = "data\\" + yearFall + "W" + "\\" + courseCode + "\\" + courseCode + " " + courseNum + ".json";
        } else {
            path = "data\\" + yearSummer + "S" + "\\" + courseCode + "\\" + courseCode + " " + courseNum + ".json";
        }
        File file = new File(path);
        if (file.exists()) {
            String jsonCourseString = Files.asCharSource(file, Charsets.UTF_8).read();
            JSONObject obj = new JSONObject(jsonCourseString);
            Course course = parseFromJsonObject(obj);
            courseList.add(course);

        } else {
            throw new NoCourseFound();
        }




    }

    // REQUIRES: Valid course code, course number (both as a string) and format.
    // MODIFIES: this
    // EFFECT: Adds course to the list of courses.
    public void addCourse(String input) throws Exception {
        String[] inputArr = input.split("-");
        String path;
        if (winterOrSummer == 0) {
            path = "data\\" + yearFall + "W" + "\\" + inputArr[0] + "\\" + inputArr[0] + " " + inputArr[1] + ".json";
        } else {
            path = "data\\" + yearSummer + "S" + "\\" + inputArr[0] + "\\" + inputArr[0] + " " + inputArr[1] + ".json";
        }
        File file = new File(path);
        if (file.exists()) {
            String jsonCourseString = Files.asCharSource(file, Charsets.UTF_8).read();
            JSONObject obj = new JSONObject(jsonCourseString);
            Course course = parseFromJsonObject(obj);
            courseList.add(course);

        } else {
            throw new NoCourseFound();
        }
    }

    private Course parseFromJsonObject(JSONObject obj) {
        return new Course(obj.getString("subject_code"),
                obj.getString("course_number"),
                obj.getJSONObject("sections"),
                this);
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
