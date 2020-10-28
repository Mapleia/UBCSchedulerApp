package ui;

import exceptions.NoCourseFound;
import model.Course;
import model.ScheduleMaker;
import model.Section;
import model.TimeTable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class SchedulerApp {
    private static final Scanner input = new Scanner(System.in);
    private static TimeTable timeTable;

    private static String userName;
    private static final int year = LocalDateTime.now().getYear();
    private static int winterOrSummer;
    private static String[] timePreferences;
    private static boolean spreadClasses = true;

    // constructor
    public SchedulerApp() {

        askName();
        askTerm();
        askTimePreference();
        // askClassSpread();
        timeTable = new TimeTable(year, winterOrSummer, timePreferences, spreadClasses);
        askCourses();
        confirmCourses();
        askRemoveCourses();
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
            winterOrSummer = 0;
        } else {
            winterOrSummer = 1;
        }
    }

    // MODIFIES: timeTable, this.
    // EFFECT: Ask user their class time preferences in PST. (Morning / Afternoon / Evening).
    public static void askTimePreference() {

        System.out.println("Please list your preference in order of preferred timeslots.");
        System.out.println("EX: Afternoon, Evening, Morning");
        System.out.println("Mornings: 7:00 PST ~ 12:00 PST");
        System.out.println("Afternoon: 12:00 PST ~ 17:00 PST");
        System.out.println("Evening: 17:00 ~ 21:00 PST");
        System.out.println("If you like the example, please enter [D].");
        if (input.nextLine().equalsIgnoreCase("D")) {
            timePreferences = new String[]{"Afternoon", "Evening", "Morning"};
        } else {
            timePreferences = input.nextLine().trim().split(",");
        }

    }

    // MODIFIES: timeTable, this.
    // EFFECT: Ask if they'd rather have their class spread out or concentrated on a couple of days.
    public static void askClassSpread() {
        String preference;
        System.out.println("Would you like to have your classes spread out over the week?");
        System.out.println("If no, schedule will concentrate classes on either MON/WED/FRI or TUES/THURS.");
        preference = input.nextLine();
        spreadClasses = preference.equalsIgnoreCase("yes");
    }

    // MODIFIES: timeTable, this.
    // EFFECT: Ask user to input courses to be added to their schedule.
    public static void askCourses() {
        boolean moreCourse = true;
        System.out.println("Please enter the courses that you'd like to be have in your schedule.");
        System.out.println("Please format the course in course code (XXXX) hyphen (-) then number (###). XXXX-###");
        System.out.println("If you have multiple courses to add, please list them and differentiate using"
                + " {, }");
        System.out.println("EX: CPSC-210, BIOL-200, CHEM-233");
        while (moreCourse) {
            String course = input.nextLine();
            if (course.length() > 8) {
                System.out.println("List detected.");
                String[] courseListString = course.split(", ");
                for (String str : courseListString) {
                    courseAddTry(str);
                }
                System.out.println("All courses added.");
            } else {
                courseAddTry(course);
            }
            System.out.println("Do you have any more courses to add? (yes / no)");
            moreCourse = input.nextLine().equalsIgnoreCase("yes");
        }

    }

    private static void courseAddTry(String str) {
        String[] courseSplit = str.split("-");
        try {
            timeTable.addCourse(courseSplit[0], courseSplit[1]);
            System.out.println(str + " added.");
        } catch (NoCourseFound e) {
            System.out.println(str + " not found.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // EFFECT: Prints out and confirm list of courses to the user.
    private static void confirmCourses() {
        System.out.println("Here are all your courses.");
        ArrayList<Course> allCourses = timeTable.getCourseList();
        for (Course c : allCourses) {
            System.out.println(c.getSubjectCode() + "-" + c.getCourseNum());
        }
        System.out.println("Would you like to add more?");
        if (input.nextLine().equalsIgnoreCase("yes")) {
            askCourses();
        }
    }

    private static void askRemoveCourses() {
        boolean moreCourse = true;

        String[] courseSplit;
        String course;
        System.out.println("Would you like to remove some courses? (yes / no)");
        if (input.nextLine().equalsIgnoreCase("yes")) {
            System.out.println("If so, please format the course in course code (XXXX) hyphen (-) then number (###). "
                    + "XXXX-###");

            while (moreCourse) {
                course = input.nextLine();
                courseSplit = course.split("-");
                try {
                    timeTable.removeCourse(courseSplit[0], courseSplit[1]);
                } catch (NoCourseFound e) {
                    System.out.println(course + " not found.");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("Do you have any more courses to remove? (yes / no)");
                moreCourse = input.nextLine().equalsIgnoreCase("yes");
            }
        }
    }

    // REQUIRES: course list size > 0.
    // EFFECT: Print out finished timetable.
    private static void printTimeTable() {
        ScheduleMaker scheduleMaker = new ScheduleMaker(timeTable);

        System.out.println("Creating your sample timetable. Please wait.");
        System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");

        List<Section> schedule = scheduleMaker.getFinalTimeTable();
        try {
            for (Section s : schedule) {
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
