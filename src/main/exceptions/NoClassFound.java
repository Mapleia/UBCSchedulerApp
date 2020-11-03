package exceptions;

import java.util.ArrayList;

abstract class NoClassFound extends Exception {
    private final ArrayList<String> classes;

    public NoClassFound() {
        classes = new ArrayList<>();
    }

    public void printClasses() {
        for (String c: classes) {
            System.out.println(c);
        }
    }

    public void addClasses(String input) {
        classes.add(input);
    }

    public int size() {
        return classes.size();
    }

}
