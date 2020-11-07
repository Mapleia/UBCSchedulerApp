package model;

import org.json.JSONObject;
import persistence.Writable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


// Represents a Section of a course.
public class Section implements Writable {
    private String status;
    private String section;
    private String course;
    private String activity;
    private String term;
    private List<String> days;
    private LocalTime start;
    private LocalTime end;

    private String termYear;
    private List<Integer> daysInt;
    private List<LocalDate> firstWeekList;


    //constructor for testing
    public Section(String status, String section, String course, String activity, String term, List<String> days,
                   LocalTime start, LocalTime end, String termYear) {
        this.status = status;
        this.section = section;
        this.course = course;
        this.activity = activity;
        this.term = term;
        this.days = days;
        this.start = start;
        this.end = end;
        this.termYear = termYear;

        init();
    }

    // EFFECT: Initializes fields, and runs private methods to populate fields.
    private void init() {
        firstWeekList = new ArrayList<>();

        daysInt = new ArrayList<>();
        for (String day : days) {
            convertDaysToInt(day);
        }

        createFirstWeekOfTerm();
    }

    // getters ========================================================================================================
    public LocalTime getEnd() {
        return end;
    }

    public String getSection() {
        return section;
    }

    public LocalTime getStart() {
        return start;
    }

    public String getStartStr() {
        if (start == null) {
            return "N/A";
        } else {
            return start.toString();
        }
    }

    public String getEndStr() {
        if (end == null) {
            return "N/A";
        } else {
            return end.toString();
        }
    }

    public List<String> getDays() {
        return days;
    }

    public List<LocalDate> getFirstWeekList() {
        return firstWeekList;
    }

    public String getActivity() {
        return activity;
    }

    public String getCourse() {
        return course;
    }

    // EFFECT: Returns true if the activity is a "required" type.
    public boolean isRequired() {
        return activity.equals("Required");
    }

    public String getTerm() {
        return term;
    }

    public Object getStatus() {
        return status;
    }
    // ================================================================================================================

    // EFFECTS: Returns true if the sections are overlapping.
    public static boolean isOverlapping(Section section1, Section section2) {
        boolean results;
        if (section1.getStart() == null || section2.getStart() == null) {
            results = false;
        } else if (section1.getEnd() == null || section2.getEnd() == null) {
            results = false;
        } else if (section1.getFirstWeekList().size() > section2.getFirstWeekList().size()) {
            results = isOverlappingHelper(section1, section2);
        } else {
            results = isOverlappingHelper(section2, section1);
        }
        return results;
    }

    // REQUIRES: section1 to have more days then section2, start & end cannot be null.
    // EFFECTS: Returns true if sections are overlapping.
    private static boolean isOverlappingHelper(Section section1, Section section2) {
        boolean result = false;
        for (LocalDate date : section1.getFirstWeekList()) {
            for (LocalDate date2 : section2.getFirstWeekList()) {
                LocalTime start1 = section1.getStart();
                LocalTime start2 = section2.getStart();
                LocalTime end1 = section1.getEnd();
                LocalTime end2 = section2.getEnd();

                if (LocalDateTime.of(date, start1).isEqual(LocalDateTime.of(date2, start2))
                        || LocalDateTime.of(date, end1).isEqual(LocalDateTime.of(date2, end2))) {
                    result = true;
                } else {
                    boolean s1IsBeforeS2 = LocalDateTime.of(date, start1).isBefore(LocalDateTime.of(date2, end2));
                    boolean s2IsBeforeE1 = LocalDateTime.of(date2, start2).isBefore(LocalDateTime.of(date, end1));
                    if (s1IsBeforeS2 && s2IsBeforeE1) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    // EFFECT: serializes section object to a JSONObject.
    @Override
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("status", status);
        obj.put("section", section);
        obj.put("course", course);
        obj.put("activity", activity);
        obj.put("term", term);
        obj.put("days", days);
        obj.put("start", getStartStr());
        obj.put("end", getEndStr());

        return obj;
    }

    // EFFECT: From an array of days, convert a day of the week to an int value.
    //         1 is Monday and so on and so forth. 7 is Sunday.
    private void convertDaysToInt(String day) {
        switch (day) {
            case "MON":
                daysInt.add(1);
                break;
            case "TUE":
                daysInt.add(2);
                break;
            case "WED":
                daysInt.add(3);
                break;
            case "THU":
                daysInt.add(4);
                break;
            case "FRI":
                daysInt.add(5);
                break;
            case "SAT":
                daysInt.add(6);
                break;
            default:
                daysInt.add(7);
        }
    }

    // EFFECT: Populate "localDateList" field with LocalDate of dates (that the section runs on) and
    //         of the first week in the term.
    private void createFirstWeekOfTerm() {
        int year = Integer.parseInt(termYear.substring(0, 4));
        LocalDate date1;
        LocalDate date2;

        for (int dayInt : daysInt) {
            date1 = LocalDate.of(year, 9, 1);
            date1 = date1.with(TemporalAdjusters.firstInMonth(DayOfWeek.of(dayInt)));
            date2 = LocalDate.of(year, 1, 1);
            date2 = date2.with(TemporalAdjusters.firstInMonth(DayOfWeek.of(dayInt)));

            switch (term) {
                case "1-2":
                    firstWeekList.add(date1);
                    firstWeekList.add(date2);
                    break;
                case "2":
                    firstWeekList.add(date2);
                    break;
                default:
                    firstWeekList.add(date1);
                    break;
            }
        }

        firstWeekList = firstWeekList.stream().sorted().collect(Collectors.toList());
    }

    // EFFECT: Returns "MORNING" / "AFTERNOON" / "EVENING" based on the start time and end time of the section.
    //         Throws NoTimeSpan if a suitable one is not found.
    public String getTimeSpan() {
        if (start == null || end == null) {
            return "N/A";
        } else if (start.isAfter(LocalTime.of(17, 59))) {
            return "EVENING";
        } else if (start.isAfter(LocalTime.of(11, 59))) {
            return "AFTERNOON";
        } else {
            return "MORNING";
        }
    }
}
