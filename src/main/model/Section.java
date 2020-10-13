package model;

public class Section {
    private final String start;
    private final String end;
    private final String activity;
    private final String term;
    private final String days;

    public Section(String start, String end, String activity, String term, String days) {
        this.start = start;
        this.end = end;
        this.activity = activity;
        this.term = term;
        this.days = days;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getActivity() {
        return activity;
    }

    public String getTerm() {
        return term;
    }

    public String getDay() {
        return days;
    }

}
