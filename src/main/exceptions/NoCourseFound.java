package exceptions;

public class NoCourseFound extends Exception {
    private final String code;
    private final String number;

    public NoCourseFound(String code, String number) {
        this.code = code;
        this.number = number;
    }

    public void printCourse() {
        System.out.println("Culprit: " + code + " " + number);
    }
}
