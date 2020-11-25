package exceptions;

import java.util.ArrayList;

// NoCourseFound is thrown when an error is encountered when parsing / reading from course JSON file.
public class NoCourseFound extends Exception {
    ArrayList<String> list;

    // constructor
    public NoCourseFound() {
        list = new ArrayList<>();
    }

    // EFFECTS: adds culprit course to list.
    public void addClasses(String input) {
        list.add(input);
    }

    // EFFECTS: returns size of error list.
    public int size() {
        return list.size();
    }

    // EFFECTS: prints out the list of courses that were not found into UI.
    public void printClasses() {
        for (String s : list) {
            System.out.println(s);
        }
    }
}
