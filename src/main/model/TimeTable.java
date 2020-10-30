package model;


import exceptions.NoCourseFound;
import exceptions.NoTimeSpanAdded;

import java.io.IOException;
import java.util.TreeMap;

// Represents the general information about their schedule, and stores selected courses.
public class TimeTable {
    private final TreeMap<String, Course> courseList = new TreeMap<>();

    public String primaryTimePref;
    public String secondaryTimePref;
    public String tertiaryTimePref;

    public boolean isWinter; // 0 = Winter, 1 = Summer
    public int yearFall;
    public int yearSpring;
    public int yearSummer;

    public static final int TERM_FALL = 9;
    public static final int TERM_SPRING = 1;
    public static final int TERM_SUMMER1 = 5;
    public static final int TERM_SUMMER2 = 7;

    // constructor
    public TimeTable(int year, boolean isWinter, String[] preferenceArray) {
        this.yearFall = year;
        this.yearSpring = year + 1;
        this.yearSummer = year;
        this.isWinter = isWinter;

        this.primaryTimePref = preferenceArray[0];
        this.secondaryTimePref = preferenceArray[1];
        this.tertiaryTimePref = preferenceArray[2];
    }

    // getters
    public Course getCourse(String course) throws NoCourseFound {
        if (!courseList.containsKey(course)) {
            String[] arr = course.split(" ");
            throw new NoCourseFound(arr[0], arr[1]);
        } else {
            return courseList.get(course);
        }
    }

    public TreeMap<String, Course> getCourseList() {
        return courseList;
    }

    // REQUIRES: Valid course code, course number (both as a string) and format.
    // MODIFIES: this
    // EFFECT: Adds course to the list of courses.
    public void addCourse(String input) throws NoCourseFound, NoTimeSpanAdded, IOException {
        String[] inputArr = input.split(" ");
        courseList.putIfAbsent(input, Course.createCourse(inputArr[0], inputArr[1], this));
    }

    public void removeCourse(String course) throws NoCourseFound {
        if (!courseList.containsKey(course)) {
            String[] arr = course.split(" ");
            throw new NoCourseFound(arr[0], arr[1]);
        } else {
            courseList.remove(course);
        }
    }



}
