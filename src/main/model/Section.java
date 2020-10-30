package model;

import org.json.JSONObject;

import java.util.*;

public class Section {
    private final String status;
    private final String section;
    private String activity;
    private String term;
    private final String days;
    private final String start;
    private final String end;

    private final List<TimeSpan> timeSpans;
    private final TimeTable timeTable;
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

        crucialFieldsBlank();
        checkRequired();
        timeSpans = new ArrayList<>();
        formatDatesAndTime();
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
        if (!timeSpans.isEmpty()) {
            return timeSpans.get(0).getTimeSlot();
        }
        return "";
    }

    public List<TimeSpan> getTimeSpans() {
        return timeSpans;
    }

    public boolean isCrucialFieldsBlank() {
        return crucialFieldsBlank;
    }

    @Override
    public boolean equals(Object o) {
        // referenced: https://stackoverflow.com/questions/24957813/indexof-will-not-find-a-custom-object-type
        if (!(o instanceof Section)) {
            return false;
        }
        Section s = (Section) o;
        boolean boolSection = this.section.equals(s.getSection());
        boolean boolActivity = this.activity.equals(s.getActivity());
        return boolSection && boolActivity;
    }

    @Override
    public int hashCode() {
        // referenced: https://www.baeldung.com/java-hashcode
        return section.hashCode() * activity.hashCode();
    }

    public static Section createSection(JSONObject obj, TimeTable table) {
        String status = obj.getString("status");
        String section = obj.getString("section");
        String activity = obj.getString("activity");
        String term = obj.getString("term");
        String days = obj.getString("days");
        String start = obj.getString("start");
        String end = obj.getString("end");

        return new Section(status, section, start, end, activity, term, days, table);
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
                    if (TimeSpan.isOverlapping(entry, entry2) || TimeSpan.isOverlapping(entry2, entry)) {
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
        if (!crucialFieldsBlank) {
            String[] daysArr = days.trim().split(" ");

            for (String s : daysArr) {
                if (timeTable.isWinter) {
                    timeSpans.addAll(winterTimeSpan(s));
                } else {
                    timeSpans.addAll(summerTimeSpan(s));
                }
            }

            Collections.sort(timeSpans);
        }
    }

    private List<TimeSpan> winterTimeSpan(String s) {
        List<TimeSpan> list = new ArrayList<>();

        switch (term) {
            case "2":
                list.add(new TimeSpan(start, end, s, timeTable.yearSpring, TimeTable.TERM_SPRING));
                return list;
            case "1-2":
                list.add(new TimeSpan(start, end, s, timeTable.yearFall, TimeTable.TERM_FALL));
                list.add(new TimeSpan(start, end, s, timeTable.yearSpring, TimeTable.TERM_SPRING));
                return list;
            default: // term 1
                list.add(new TimeSpan(start, end, s, timeTable.yearFall, TimeTable.TERM_FALL));
                return list;
        }
    }

    private List<TimeSpan> summerTimeSpan(String s)  {
        List<TimeSpan> list = new ArrayList<>();
        switch (term) {
            case "2":
                list.add(new TimeSpan(start, end, s, timeTable.yearSummer, TimeTable.TERM_SUMMER2));
                return list;
            case "1-2":
                list.add(new TimeSpan(start, end, s, timeTable.yearSummer, TimeTable.TERM_SUMMER1));
                list.add(new TimeSpan(start, end, s, timeTable.yearSummer, TimeTable.TERM_SUMMER2));
                return list;
            default: // term 1
                list.add(new TimeSpan(start, end, s, timeTable.yearSummer, TimeTable.TERM_SUMMER1));
                return list;
        }
    }

}