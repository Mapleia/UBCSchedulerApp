package ui;

import model.Course;
import model.Scheduler;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static Scanner input;
    private static Scheduler scheduler;

    private static String userName;

    public static void main(String[] args) {
        input = new Scanner(System.in);
        scheduler = new Scheduler();

        askName();
        askCourses();
        askTimePreference();
        askClassSpread();
    }

    // MODIFIES: scheduler.
    // EFFECTS: Gathers user's name.
    public static void askName() {
        System.out.println("Welcome to UBC Course Scheduler. What is your name?");
        userName = input.nextLine();
    }

    // MODIFIES: scheduler.
    // EFFECT: Ask user to input courses to be added to their schedule.
    public static void askCourses() {
        String wouldAddMoreCourses;
        boolean moreCourse = true;

        String[] courseSplit;

        System.out.println("Hi " + userName
                + "! Please enter the courses that you'd like to be have in your schedule.");
        System.out.println("Please format the course in course code (XXXX) hyphen (-) then number (###). XXXX-###");

        while (moreCourse) {
            courseSplit = input.nextLine().split("-");
            scheduler.addCourse(courseSplit[0], courseSplit[1]);

            System.out.println("Do you have any more courses to add? (yes / no)");
            wouldAddMoreCourses = input.nextLine();
            if (wouldAddMoreCourses.equalsIgnoreCase("no")) {
                moreCourse = false;
            }
        }

        System.out.println("Here are all your courses.");
        ArrayList<Course> allCourses = scheduler.getCourseList();
        for (Course c : allCourses) {
            System.out.println(c.getSubjectCode() + "-" + c.getCourseNum());
        }
    }

    // MODIFIES: scheduler.
    // EFFECT: Ask user their class time preferences in PST. (Morning / Afternoon / Evening).
    public static void askTimePreference() {
        String[] preferenceInput;

        System.out.println("Please list your preference in order of preferred timeslots.");
        System.out.println("EX: Afternoon, Evening, Morning");
        System.out.println("Mornings: 8:00 PST ~ 11:59 PST");
        System.out.println("Afternoon: 12:00 PST ~ 16:59 PST");
        System.out.println("Evening: 17:00 ~ 20:59 PST");

        preferenceInput = input.nextLine().trim().split(",");
        scheduler.setPrimaryTimePref(preferenceInput[0]);
        scheduler.setSecondaryTimePref(preferenceInput[1]);
        scheduler.setTertiaryTimePref(preferenceInput[2]);
    }

    // MODIFIES: scheduler.
    // EFFECT: Ask if they'd rather have their class spread out or concentrated on a couple of days.
    public static void askClassSpread() {
        String preference;
        System.out.println("Would you like to have your classes spread out over the week?");
        System.out.println("If no, schedule will concentrate classes on either MON/WED/FRI or TUES/THURS.");
        preference = input.nextLine();
        scheduler.setSpreadClasses(preference.equalsIgnoreCase("yes"));
    }

}
