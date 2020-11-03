package exceptions;

import java.util.ArrayList;

public class NoCourseFound extends Exception {
    ArrayList<String> list;

    public NoCourseFound() {
        list = new ArrayList<>();
    }

    public void addClasses(String input) {
        list.add(input);
    }

    public int size() {
        return list.size();
    }

    public void printClasses() {
        for (String s : list) {
            System.out.println(s);
        }
    }
}
