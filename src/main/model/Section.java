package model;

import exceptions.NoTimeSpan;
import org.json.JSONObject;
import persistence.Writable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Section implements Writable {
    private JSONObject jsonObject;
    private String status;
    private String section;
    private String activity;
    private String term;
    private List<String> days;
    private LocalTime start;
    private LocalTime end;

    private String termYear;
    private List<Integer> daysInt;
    private List<LocalDate> localDateList;


    // constructor for section.
    public Section(JSONObject jsonObject, String termYear) {
        this.jsonObject = jsonObject;
        this.termYear = termYear;
        init();
    }

    //TODO: write test cases.
    /* test cases:
    this start is null
    section start is null
    this end is null
    section end is null
    this has more days than section, isOverlapping true;
    this has more days than section, isOverlapping false;
    section has more days than this, isOverlapping true;
    section has more days than this, isOverlapping false;
    */
    public boolean isOverlapping(Section section) {
        if (section.getStart() == null || start == null) {
            return false;
        } else if (section.getEnd() == null || start == null) {
            return false;
        } else {
            if (section.getLocalDateList().size() > localDateList.size()) {
                return isOverlapping(section, this);

            } else {
                return isOverlapping(this, section);
            }
        }
    }

    private boolean isOverlapping(Section section1, Section section2) {
        boolean result = true;
        for (LocalDate date : section1.getLocalDateList()) {
            for (LocalDate date2 : section2.getLocalDateList()) {
                LocalTime start1 = section1.getStart();
                LocalTime start2 = section2.getStart();
                LocalTime end1 = section1.getEnd();
                LocalTime end2 = section2.getEnd();

                if (LocalDateTime.of(date, start1).isEqual(LocalDateTime.of(date2, start2))
                        || LocalDateTime.of(date, end1).isEqual(LocalDateTime.of(date2, end2))) {
                    result = true;
                } else {
                    result = LocalDateTime.of(date, start1).isBefore(LocalDateTime.of(date2, end2))
                            && LocalDateTime.of(date2, start2).isBefore(LocalDateTime.of(date, end1));
                }
            }
        }
        return result;
    }

    private LocalTime getEnd() {
        return end;
    }

    private void init() {
        localDateList = new ArrayList<>();

        days = new ArrayList<>();
        createSection();

        daysInt = new ArrayList<>();
        for (String day : days) {
            convertDaysToInt(day);
        }

        createZonedDateTimes();
    }

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

    private void createZonedDateTimes() {
        int year = Integer.parseInt(termYear.substring(0, 4));
        LocalDate date1;
        LocalDate date2;

        for (int dayInt : daysInt) {
            date1 = LocalDate.of(year, 9, 1);
            date1 = date1.with(TemporalAdjusters.firstInMonth(DayOfWeek.of(dayInt)));
            date2 = LocalDate.of(year, 1, 1);
            date2 = date2.with(TemporalAdjusters.firstInMonth(DayOfWeek.of(dayInt)));

            switch (term) {
                case "1":
                    localDateList.add(date1);
                    break;
                case "2":
                    localDateList.add(date2);
                    break;
                case "1-2":
                    localDateList.add(date1);
                    localDateList.add(date2);
                    break;
            }
        }

        localDateList = localDateList.stream().sorted().collect(Collectors.toList());
    }

    // EFFECT: serializes section object to a JSONObject.
    @Override
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("status", status);
        obj.put("section", section);
        obj.put("activity", activity);
        obj.put("term", term);
        obj.put("days", days);
        obj.put("start", start.toString());
        obj.put("end", end.toString());

        return obj;
    }

    // getter for section
    public String getSection() {
        return section;
    }

    // getter for start
    public LocalTime getStart() {
        return start;
    }

    // getter for days
    public List<String> getDays() {
        return days;
    }

    public List<LocalDate> getLocalDateList() {
        return localDateList;
    }

    public String getActivity() {
        return activity;
    }
    
    
    // EFFECTS: parses section from JSON object.
    private void createSection() {
        status = jsonObject.getString("status");
        section = jsonObject.getString("section");
        activity = jsonObject.getString("activity");
        term = jsonObject.getString("term");

        for (int i = 0; i < jsonObject.getJSONArray("days").length(); i++) {
            days.add(jsonObject.getJSONArray("days").getString(i));
        }

        if (jsonObject.getString("start").trim().equals("")
                || jsonObject.getString("end").trim().equals("")) {
            start = null;
            end = null;
        } else {
            start = LocalTime.parse(jsonObject.getString("start"), DateTimeFormatter.ofPattern("HH:mm"));
            end = LocalTime.parse(jsonObject.getString("end"), DateTimeFormatter.ofPattern("HH:mm"))
                    .minusMinutes(10);
        }
    }

    public String getTimeSpan() throws NoTimeSpan {
        if (start.isAfter(LocalTime.of(17, 59))) {
            return "EVENING";
        } else if (start.isAfter(LocalTime.of(11, 59))) {
            return "AFTERNOON";
        } else if (start.isBefore(LocalTime.of(12, 0))) {
            return "MORNING";
        } else {
            throw new NoTimeSpan();
        }
    }
}
