package ui;

import exceptions.NoCourseFound;
import model.Section;
import model.User;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

// Referenced TellerApp.
public class SchedulerApp {
    private final Scanner input;
    private User user;
    public static final String[] AVAILABLE_TERMS = new String[]{"2020W", "2021S"};

    public SchedulerApp() {
        input = new Scanner(System.in);
        //user = new User();

        runApp();
    }

    private void runApp() {
        boolean keepGoing = true;

        welcome();
        while (keepGoing) {
            showOptions();
            String command = input.next().toUpperCase();
            if (command.equals("E")) {
                keepGoing = false;
            } else {
                processCommands(command);
            }
        }
        System.out.println("Thank you for using the app! \n Goodbye!");
    }

    private void welcome() {
        System.out.println("Welcome to the UBC Course Scheduler app!");
        System.out.println("What would you like to do today?");
    }

    private void showOptions() {
        System.out.println("~~~~~~~~~~~~~~~~ OPTIONS ~~~~~~~~~~~~~~~~");
        System.out.println("\tE - to exit.");
        System.out.println("\tL - to load from a previously saved file.");
        System.out.println("\tN - to make a new timetable.");
        System.out.println("\tP - to print out your timetable file.");
        System.out.println("\tH - to show the options menu again.");
        System.out.println("~~~~~~~~~~~~~~~~ ======= ~~~~~~~~~~~~~~~~");

    }

    private void processCommands(String command) {
        switch (command) {
            case "N":
                createSchedule();
                break;
            case "S":
                saveFile();
                break;
            case "P":
                printSchedule();
                break;
            case "L":
                loadUser();
                break;
            case "H":
                showOptions();
                break;
            default:
                System.out.println("No valid command found.");
                break;
        }
    }

    // FOR LOADING PREVIOUS SAVE FILE =================================================================================
    private void loadUser() {
        System.out.println("Reading file from ./data/timetables/ ...");
        System.out.println("Please enter your file name (without .json file extension.)");
        String fileName = input.next();
        String filePath = "./data/timetables/" + fileName + ".json";
        JsonReader reader = new JsonReader(filePath);
        try {
            user = reader.readUser();
            System.out.println("Loaded " + fileName + "from ./data/timetables/ .");
        } catch (IOException e) {
            System.out.println("Unable to load your save file.");
        }
    }

    // FOR ADDING TO SCHEDULE =========================================================================================
    private void createSchedule() {
        setTerm();
        setPreferences();

        boolean tryMore = true;
        String command;

        while (tryMore) {
            try {
                addCourses();
                System.out.println("Do you have anymore to add?");
                command = input.next().toUpperCase();
                if (command.equals("NO")) {
                    tryMore = false;
                }
            } catch (NoCourseFound n) {
                System.out.println("These course(s) were not valid.");
                n.printClasses();
                System.out.println("Would you like to retry? (YES / NO)");
                command = input.next().toUpperCase();
                if (command.equals("NO")) {
                    tryMore = false;
                }
            }
        }
    }

    private void setTerm() {
        System.out.println("Here are the terms available that you can select from. Please enter one.");
        System.out.println("W = Both fall and spring terms.");
        System.out.println("S = Both summer term 1 and summer term 2.");

        for (String term : AVAILABLE_TERMS) {
            System.out.println("\t" + term);
        }
        String command = input.next().toUpperCase();
        user = new User(command);
    }

    private void setPreferences() {
        System.out.println("Please list your time preferences in order of preference (whether you'd like to keep your "
                + "courses during the morning, afternoon or evening.");
        System.out.println("Afternoon, Morning, Evening");
        String command = input.nextLine().toUpperCase();
        List<String> preferencesArr = new ArrayList<>();
        for (String time : command.trim().split(",")) {
            preferencesArr.add(time.trim());
        }
        user.setPreferences(preferencesArr);
    }

    private void addCourses() throws NoCourseFound {

        System.out.println("Please enter your courses. If you have multiple, please divide by a \",\".");
        System.out.println("Please format your course with [SUBJECT_CODE] [COURSE_NUM].\nXXXX ###.");

        input.nextLine();
        String coursesStr = input.nextLine();
        String[] arr = coursesStr.split(",");
        List<String> courses = Arrays.stream(arr).map(String::trim).collect(Collectors.toList());
        user.addCourses(courses);

    }

    // FOR PRINTING OUT SCHEDULE ======================================================================================
    private void printSchedule() {
        boolean isSuccessful = user.createTimeTable();
        if (!isSuccessful) {
            System.out.println("The scheduler could not find a time for these sections.");
            for (String missed : user.getErrorLog()) {
                System.out.println(missed);
            }
        } else {
            System.out.println("======================= TIMETABLE =======================");
            printTerms("TERM 1");
            printTerms("TERM 2");
            System.out.println("=========================================================");
        }
    }

    private void printTerms(String term) {
        System.out.println(term);
        for (Section sec : user.getFinalTimeTable().get(term)) {
            System.out.println("\tSECTION: " + sec.getSection());
            System.out.println("\tStart: " + sec.getStart());
            System.out.println("\tDays: " + sec.getDays());
        }
    }

    // FOR SAVING FILE ================================================================================================
    private void saveFile() {
        JsonWriter writer;
        System.out.println("File will be saved to ./data/timetables/ directory.");
        System.out.println("Please name your file.");
        String fileName = input.next();
        try {
            writer = new JsonWriter(fileName);
            writer.open();
            writer.write(user);
            writer.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error was found writing your file.");
        }
    }


}
