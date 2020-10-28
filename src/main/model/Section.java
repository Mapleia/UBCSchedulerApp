package model;

import java.util.*;

public class Section {
    private String status;
    private final String section;
    private String activity;
    private String term;
    private String days;
    private String start;
    private String end;

    private List<TimeSpan> timeSpans;
    private TimeTable timeTable;
    private boolean crucialFieldsBlank = false;

    // constructor
    public Section(String status, String section, String start, String end, String activity, String term, String days,
                   TimeTable timeTable) {
        this.status = status;
        this.section = section;
        this.start = start;
        this.end = end;
        this.activity = activity;
        this.term = term;
        this.days = days;
        this.timeTable = timeTable;

        this.crucialFieldsBlank();
        this.checkRequired();
        this.formatDatesAndTime();
    }

    public Section(String section, String activity) {
        this.section = section;
        this.activity = activity;
    }

    // getters
    public String getStatus() {
        return status;
    }

    public String getSection() {
        return section;
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

    public String getDays() {
        return days;
    }

    public String getTimeSlot() {
        return timeSpans.get(0).getTimeSlot();
    }

    public List<TimeSpan> getTimeSpans() {
        return timeSpans;
    }

    public boolean isCrucialFieldsBlank() {
        return crucialFieldsBlank;
    }

    @Override
    public boolean equals(Object section) {
        // referenced: https://stackoverflow.com/questions/24957813/indexof-will-not-find-a-custom-object-type
        if (!(section instanceof Section)) {
            return false;
        }
        Section s = (Section) section;
        return (this.section.equals(s.section) && this.activity.equals(s.activity));
    }

    @Override
    public int hashCode() {
        // referenced: https://www.baeldung.com/java-hashcode
        return section.hashCode() * activity.hashCode();
    }

    // REQUIRES: valid section
    // EFFECT: returns true if sections are overlapping in time.
    public boolean isOverlapping(Section section) {
        boolean result = false;
        List<TimeSpan> map1;
        List<TimeSpan> map2;
        List<TimeSpan> otherSectionMap = section.getTimeSpans();

        if (timeSpans.size() > otherSectionMap.size()) {
            map1 = timeSpans;
            map2 = otherSectionMap;
        } else {
            map1 = otherSectionMap;
            map2 = timeSpans;
        }

        search: {
            for (TimeSpan entry : map1) {
                for (TimeSpan entry2 : map2) {
                    if (entry2.isOverlapping(entry)) {
                        result = true;
                        break search;
                    }
                }
            }
        }

        return result;
    }

    // requires: list of sections
    // EFFECT: checks if there are any overlaps with the list of section and the this.section.
    public boolean isOverlapping(List<Section> sections) {
        boolean result = false;
        for (Section section : sections) {
            if (isOverlapping(section)) {
                result = true;
                break;
            }
        }

        return result;
    }

    // REQUIRES: start, end and days to be string.
    // MODIFIES: this.
    // EFFECT: sets crucialFieldsBlank to true if start (time in str) end or days is blank.
    private void crucialFieldsBlank() {
        crucialFieldsBlank = (start.trim().equals("") || end.trim().equals("") || days.trim().equals(""));
    }

    // MODIFIES: this
    // EFFECT: sets the activity type to required if any of the crucial fields are blank.
    // (* usually indicates that it's a mandatory section everyone has to register in. )
    private void checkRequired() {
        if (crucialFieldsBlank) {
            if (activity.equalsIgnoreCase("Web-Oriented Course")
                    || activity.equalsIgnoreCase("Lecture")) {
                activity = "Required";
            }
        }
    }

    // REQUIRES: crucialFieldsBlank to be already set.
    // MODIFIES: this.
    // EFFECT: Add to list of timeSpans to set up timeSpan for all of the days the section is scheduled for.
    private void formatDatesAndTime() {
        timeSpans = new ArrayList<>();
        if (!crucialFieldsBlank) {
            TimeSpan timeSpan;
            String[] daysArr = days.trim().split(" ");

            for (String s : daysArr) {
                if (timeTable.winterOrSummer == 0) {
                    if (term.equals("1")) {
                        timeSpan = new TimeSpan(start, end, s, timeTable.yearFall, TimeTable.TERM_FALL);
                    } else {
                        timeSpan = new TimeSpan(start, end, s, timeTable.yearSpring, TimeTable.TERM_SPRING);
                    }
                } else {
                    if (term.equals("1")) {
                        timeSpan = new TimeSpan(start, end, s, timeTable.yearSummer, TimeTable.TERM_SUMMER1);
                    } else {
                        timeSpan = new TimeSpan(start, end, s, timeTable.yearSummer, TimeTable.TERM_SUMMER2);
                    }
                }
                timeSpans.add(timeSpan);
            }
            Collections.sort(timeSpans);
        }
    }

}
