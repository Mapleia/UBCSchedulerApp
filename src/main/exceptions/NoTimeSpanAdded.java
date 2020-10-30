package exceptions;

public class NoTimeSpanAdded extends Exception {
    private final String section;
    private String term;

    public NoTimeSpanAdded(String term, String section) {
        this.term = term;
        this.section = section;
    }

    public void printTerm() {
        System.out.println("Section: " + section);
        System.out.println("Term: " + term);

    }
}
