package ui;

import model.Course;
import model.ScheduleMaker;
import model.Section;
import model.TimeTable;

import java.util.ArrayList;
import java.util.Scanner;

public class SchedulerApp {
    private static Scanner input;
    private static TimeTable timeTable;

    private static String userName;

    // constructor
    public SchedulerApp() {
        input = new Scanner(System.in);
        timeTable = new TimeTable();

        askName();
        askTimePreference();
        askClassSpread();
        askCourses();
        printTimeTable();
    }

    // MODIFIES: this.
    // EFFECTS: Gathers user's name.
    public static void askName() {
        System.out.println("Welcome to UBC Course Scheduler. What is your name?");
        userName = input.nextLine();
    }

    // MODIFIES: timeTable, this.
    // EFFECT: Ask user their class time preferences in PST. (Morning / Afternoon / Evening).
    public static void askTimePreference() {
        String[] preferenceInput;

        System.out.println("Hi " + userName
                + "! Please list your preference in order of preferred timeslots.");
        System.out.println("EX: Afternoon, Evening, Morning");
        System.out.println("Mornings: 7:00 PST ~ 12:00 PST");
        System.out.println("Afternoon: 12:00 PST ~ 17:00 PST");
        System.out.println("Evening: 17:00 ~ 21:00 PST");

        preferenceInput = input.nextLine().trim().split(",");
        timeTable.setTimePref(preferenceInput);
    }

    // MODIFIES: timeTable, this.
    // EFFECT: Ask if they'd rather have their class spread out or concentrated on a couple of days.
    public static void askClassSpread() {
        String preference;
        System.out.println("Would you like to have your classes spread out over the week?");
        System.out.println("If no, schedule will concentrate classes on either MON/WED/FRI or TUES/THURS.");
        preference = input.nextLine();
        timeTable.setSpreadClasses(preference.equalsIgnoreCase("yes"));
    }

    // MODIFIES: timeTable, this.
    // EFFECT: Ask user to input courses to be added to their schedule.
    public static void askCourses() {
        String wouldAddMoreCourses;
        boolean moreCourse = true;

        String[] courseSplit;
        String course;
        System.out.println("Please enter the courses that you'd like to be have in your schedule.");
        System.out.println("Please format the course in course code (XXXX) hyphen (-) then number (###). XXXX-###");

        while (moreCourse) {
            course = input.next();
            courseSplit = course.split("-");
            try {
                timeTable.addCourse(courseSplit[0], courseSplit[1]);
            } catch (Exception e) {
                System.out.println(course + " not found.");
            }

            System.out.println("Do you have any more courses to add? (yes / no)");
            wouldAddMoreCourses = input.nextLine();
            if (wouldAddMoreCourses.equalsIgnoreCase("no")) {
                moreCourse = false;
            }
        }
        confirmCourses();

    }

    // EFFECT: Prints out and confirm list of courses to the user.
    private static void confirmCourses() {
        System.out.println("Here are all your courses.");
        ArrayList<Course> allCourses = timeTable.getCourseList();
        for (Course c : allCourses) {
            System.out.println(c.getSubjectCode() + "-" + c.getCourseNum());
        }
    }

    // REQUIRES: course list size > 0.
    // EFFECT: Print out finished timetable.
    public static void printTimeTable() {
        ScheduleMaker scheduleMaker = new ScheduleMaker(timeTable);

        System.out.println("Creating your sample timetable. Please wait.");
        try {
            for (Section s : scheduleMaker.makeTimeTable()) {
                System.out.println(s.getSection());
            }
        } catch (Exception e) {
            System.out.println("Error in making your schedule.");
            e.printStackTrace();
        }

    }
}
