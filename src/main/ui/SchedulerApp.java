package ui;

import exceptions.NoCourseFound;
import model.Course;
import model.ScheduleMaker;
import model.Section;
import model.TimeTable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

public class SchedulerApp {
    private static Scanner input;
    private static TimeTable timeTable;

    private static String userName;
    private static int year;

    // constructor
    public SchedulerApp() {
        input = new Scanner(System.in);
        year = LocalDateTime.now().getYear();
        timeTable = new TimeTable(year);

        askName();
        askTerm();
        askTimePreference();
        askClassSpread();
        askCourses();
        confirmCourses();
        printTimeTable();
    }

    // MODIFIES: this.
    // EFFECTS: Gathers user's name.
    public static void askName() {
        System.out.println("Welcome to UBC Course Scheduler for " + year +  ". What is your name?");
        userName = input.nextLine();

    }

    // MODIFIES: timeTable and this.
    // EFFECTS: Gathers the term that the user is looking for.
    public static void askTerm() {
        System.out.println("Hello " + userName + "! Is this a winter terms or summer terms? (w / s)");
        if (input.nextLine().equalsIgnoreCase("W")) {
            timeTable.setWinterOrSummer(0);
        } else {
            timeTable.setWinterOrSummer(1);
        }
    }

    // MODIFIES: timeTable, this.
    // EFFECT: Ask user their class time preferences in PST. (Morning / Afternoon / Evening).
    public static void askTimePreference() {
        String[] preferenceInput;

        System.out.println("Please list your preference in order of preferred timeslots.");
        System.out.println("EX: Afternoon, Evening, Morning");
        System.out.println("Mornings: 7:00 PST ~ 12:00 PST");
        System.out.println("Afternoon: 12:00 PST ~ 17:00 PST");
        System.out.println("Evening: 17:00 ~ 21:00 PST");
        System.out.println("If you like the example, please enter [D].");
        if (input.nextLine().equalsIgnoreCase("D")) {
            String[] modeDefault = {"Afternoon", "Evening", "Morning"};
            timeTable.setTimePref(modeDefault);
        } else {
            preferenceInput = input.nextLine().trim().split(",");
            timeTable.setTimePref(preferenceInput);
        }

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
            course = input.nextLine();
            courseSplit = course.split("-");
            try {
                timeTable.addCourse(courseSplit[0], courseSplit[1]);
            } catch (NoCourseFound e) {
                System.out.println(course + " not found.");
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Do you have any more courses to add? (yes / no)");
            wouldAddMoreCourses = input.nextLine();
            if (wouldAddMoreCourses.equalsIgnoreCase("no")) {
                moreCourse = false;
            }
        }

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
        System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");

        try {
            scheduleMaker.makeTimeTable();

            for (Section s : scheduleMaker.getFinalTimeTable()) {
                System.out.println(s.getSection());
                System.out.println("Days: " + s.getDays());
                System.out.println("Times: " + s.getStart() + " ~ " + s.getEnd());
                System.out.println("Type: " + s.getActivity());
                System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
            }

            Set<String> keySet = scheduleMaker.errorLog.keySet();
            if (!keySet.isEmpty()) {
                System.out.println("There has seem to be no possible sections found for the following:");
                for (String s: keySet) {
                    System.out.println(s + ": " + scheduleMaker.errorLog.get(s));
                }
            }

        } catch (Exception e) {
            System.out.println("Error in making your schedule. Please try again.");
            e.printStackTrace();
        }

    }
}
