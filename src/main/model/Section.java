package model;

import java.util.*;

public class Section {
    private final String status;
    private final String section;
    private String activity;
    private final String term;
    private final String days;
    private final String start;
    private final String end;

    private List<TimeSpan> timeSpans;
    private TimeTable timeTable;
    private boolean crucialFieldsBlank = false;

    // constructor
    public Section(String status, String section, String start, String end, String activity, String term, String days) {
        this.status = status;
        this.section = section;
        this.start = start;
        this.end = end;
        this.activity = activity;
        this.term = term;
        this.days = days;
    }

    // getters
    public String getStatus() {
        return status;
    }

    // getters
    public String getSection() {
        return section;
    }

    // getters
    public String getStart() {
        return start;
    }

    // getters
    public String getEnd() {
        return end;
    }

    // getters
    public String getActivity() {
        return activity;
    }

    // getters
    public String getTerm() {
        return term;
    }

    // getters
    public String getDays() {
        return days;
    }

    // getters
    public String getTimeSlot() {
        return timeSpans.get(0).getTimeSlot();
    }

    // getters
    public List<TimeSpan> getTimeSpans() {
        return timeSpans;
    }

    // getters
    public boolean isCrucialFieldsBlank() {
        return crucialFieldsBlank;
    }

    // setters
    public void setTimeTable(TimeTable timeTable) {
        this.timeTable = timeTable;
    }

    // setters
    public void setCrucialFieldsBlank(boolean crucialFieldsBlank) {
        this.crucialFieldsBlank = crucialFieldsBlank;
    }

    // REQUIRES: crucialFieldsBlank to be already set.
    // MODIFIES: this.
    // EFFECT: Add to list of timeSpans to set up timeSpan for all of the days the section is scheduled for.
    public void formatDatesAndTime() {
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
    public void crucialFieldsBlank() {
        crucialFieldsBlank = (start.trim().equals("") || end.trim().equals("") || days.trim().equals(""));
    }

    // MODIFIES: this
    // EFFECT: sets the activity type to required if any of the crucial fields are blank.
    // (* usually indicates that it's a mandatory section everyone has to register in. )
    public void checkRequired() {
        if (crucialFieldsBlank) {
            if (activity.equalsIgnoreCase("Web-Oriented Course")
                    || activity.equalsIgnoreCase("Lecture")) {
                activity = "Required";
            }
        }
    }
}
