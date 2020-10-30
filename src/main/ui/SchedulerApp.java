package ui;

import exceptions.NoCourseFound;
import exceptions.NoTimeSpamAdded;
import model.ScheduleMaker;
import model.Section;
import model.TimeTable;
import org.json.JSONObject;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class SchedulerApp {
    private static final Scanner input = new Scanner(System.in);
    private static TimeTable timeTable;
    private static ScheduleMaker scheduleMaker;

    private static String userName;
    private static final int year = LocalDateTime.now().getYear();
    private static int winterOrSummer;
    private static String[] timePreferences;

    private static boolean hasFile = false;
    private static JSONObject savedFile;

    // constructor
    public SchedulerApp() {
        loadFromSaveFile();
        askName();
        askTerm();
        askTimePreference();


        askCourses();
        confirmCourses();
        askRemoveCourses();
        printTimeTable();
        saveSession();
    }

    public static void loadFromSaveFile() {
        System.out.println("Welcome! Do you have a saved file to work from?");
        if (input.nextLine().equalsIgnoreCase("yes")) {
            hasFile = true;
            System.out.println("Please enter your file name (no file extension needed).");
            try {
                savedFile = JsonReader.findSavedFile(input.nextLine());
                userName = savedFile.get("username").toString();

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("No file found.");
                System.exit(1);
            }

        }
    }


    // MODIFIES: this.
    // EFFECTS: Gathers user's name.
    public static void askName() {
        if (hasFile) {
            System.out.println("Welcome back " + userName + "!");
        } else {
            System.out.println("Welcome to UBC Course Scheduler for " + year +  ". What is your name?");
            userName = input.nextLine();
        }
    }

    // MODIFIES: timeTable and this.
    // EFFECTS: Gathers the term that the user is looking for.
    public static void askTerm() {
        System.out.println("Is this a winter terms or summer terms? (w / s)");
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
    // EFFECT: Ask user to input courses to be added to their schedule.
    public static void askCourses() {
        timeTable = new TimeTable(year, winterOrSummer, timePreferences);

        boolean moreCourse = true;
        System.out.println("Please enter the courses that you'd like to be have in your schedule.");
        System.out.println("Please format the course in course code (XXXX) space ' ' then number (###). XXXX ###");
        System.out.println("If you have multiple courses to add, please list them and differentiate using"
                + " (, )");
        System.out.println("EX: CPSC 210, BIOL 200, CHEM 233");
        System.out.println("If you have no more courses to add, please enter 'skip'. ");
        hasFileAddCourses();

        while (moreCourse) {
            if (input.nextLine().equalsIgnoreCase("skip")) {
                moreCourse = false;
                break;
            }
            String course = input.nextLine();
            if (course.length() > 8) {
                System.out.println("List detected.");
                String[] courseListString = course.split(",");
                String[] arr = Arrays.stream(courseListString).map(String::trim).toArray(String[]::new);
                for (String str : arr) {
                    courseAddTry(str);
                }
            } else {
                courseAddTry(course);
            }
            System.out.println("Do you have any more courses to add? (yes / no)");
            moreCourse = input.nextLine().equalsIgnoreCase("yes");
        }

    }

    private static void hasFileAddCourses() {
        if (hasFile) {
            List<Object> courseObj = savedFile.getJSONArray("courses").toList();
            for (Object o : courseObj) {
                try {
                    timeTable.addCourse(o.toString());
                } catch (Exception e) {
                    System.out.println("Error found.");
                }
            }
        }
    }


    private static void courseAddTry(String str) {
        try {
            timeTable.addCourse(str);
        } catch (NoCourseFound noCourseFound) {
            noCourseFound.printCourse();
        } catch (NoTimeSpamAdded t) {
            t.printTerm();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(str + " added.");
    }

    // EFFECT: Prints out and confirm list of courses to the user.
    private static void confirmCourses() {
        System.out.println("Here are all your courses.");
        Set<String> allCourses = timeTable.getCourseList().keySet();
        for (String c : allCourses) {
            System.out.println(c);
        }
    }

    private static void askRemoveCourses() {
        boolean moreCourse = true;

        String course;
        System.out.println("Would you like to remove some courses? (yes / no)");
        if (input.nextLine().equalsIgnoreCase("yes")) {
            System.out.println("If so, please format the course in course code (XXXX) space ( ) then number (###). "
                    + "XXXX ###");

            while (moreCourse) {
                course = input.nextLine();
                try {
                    timeTable.removeCourse(course);
                } catch (NullPointerException e) {
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
        scheduleMaker = new ScheduleMaker(timeTable, userName);
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

    public static void saveSession() {
        System.out.println("Would you like to save your progress?");
        if (input.nextLine().equalsIgnoreCase("YES")) {
            System.out.println("Please name your file:");
            String fileName = input.nextLine();
            try {
                JsonWriter.saveFile(scheduleMaker, fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Thank you for using UBC Course Scheduler! Have a nice day.");
            System.exit(0);
        }

    }


}
