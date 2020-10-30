package exceptions;

public class NoTimeSpamAdded extends Exception {
    private final String section;
    private String term;
    private int winterOrSummer;

    public NoTimeSpamAdded(String term, int wos, String section) {
        this.term = term;
        this.winterOrSummer = wos;
        this.section = section;
    }

    public void printTerm() {
        System.out.println("Section: " + section);
        System.out.println("Term: " + term);
        System.out.println("WOS: " + winterOrSummer);

    }
}
