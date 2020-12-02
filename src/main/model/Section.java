package model;

import org.json.JSONObject;
import persistence.Writable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


// Represents a Section of a course.
public class Section implements Writable {
    private final String status;
    private final String section;
    private final String course;
    private final String activity;
    private final String term;
    private final List<String> days;
    private final LocalTime start;
    private final LocalTime end;

    private final String termYear;
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
        firstWeekList = new ArrayList<>();
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

    @Override
    public int hashCode() {
        return 31 * course.hashCode() * activity.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        Section other = (Section) obj;
        return course.equals(other.getCourse()) && activity.equals(other.getActivity());
    }
}
