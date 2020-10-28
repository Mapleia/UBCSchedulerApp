package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

// Represents the time span of which each section takes on a particular day.
public class TimeSpan implements Comparable<TimeSpan> {
    public static final String TIMEZONE = "America/Vancouver";

    private final String startTime;
    private final String endTime;
    private final int year;
    private final int month;
    private int day = -1;
    private final String dayOfWeek;

    private ZonedDateTime start;
    private ZonedDateTime end;
    private final ArrayList<ZonedDateTime> startTimes = new ArrayList<>();
    private String timeSlot;

    // constructor
    public TimeSpan(String startTime, String endTime, String dayOfWeek, int year, int month) {
        this.startTime = properStrTimeFormat(startTime);
        this.endTime = properStrTimeFormat(endTime);
        this.year = year;
        this.month = month;
        this.dayOfWeek = properDayOfWeekFormat(dayOfWeek);

        formatTimeDivision();
        setStartAndEnd();
        whatIsTimeSlot();
    }

    // constructor, with int day
    public TimeSpan(String startTime, String endTime, String dayOfWeek, int year, int month, int day) {
        this.startTime = properStrTimeFormat(startTime);
        this.endTime = properStrTimeFormat(endTime);
        this.year = year;
        this.month = month;
        this.day = day;
        this.dayOfWeek = properDayOfWeekFormat(dayOfWeek);

        formatTimeDivision();
        setStartAndEnd();
        whatIsTimeSlot();
    }

    // getters
    public String getTimeSlot() {
        return timeSlot;
    }

    // getters
    public ZonedDateTime getStart() {
        return start;
    }

    // getters
    public ZonedDateTime getEnd() {
        return end;
    }

    // getters
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    // MODIFIES: this
    // EFFECT: Initiates start and end fields.
    private void setStartAndEnd() {
        start = convertStrTime(startTime);
        end = convertStrTime(endTime);
    }

    // REQUIRES: time to be in 24hr HH:mm format.
    // EFFECT: Convert string time (like 19:01) to ZonedDateTime object.
    public ZonedDateTime convertStrTime(String time) {
        ZonedDateTime zonedDateTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        ZoneId zoneId = ZoneId.of(TIMEZONE);

        if (day == -1) {
            HashMap<String, LocalDate> firstWeek = makeDaysOfTheWeekMap();
            LocalDate dateFromMap = firstWeek.get(dayOfWeek);
            LocalTime timeParsed = LocalTime.parse(time, formatter);
            zonedDateTime = ZonedDateTime.of(dateFromMap, timeParsed, zoneId);
        } else {
            zonedDateTime = ZonedDateTime.of(LocalDate.of(year, month, day), LocalTime.parse(time, formatter), zoneId);
        }

        return zonedDateTime;
    }

    // MODIFIES: this
    // EFFECT: Converts 3 char of of the week strings to ALL CAPITAL FULL NAME.
    public String properDayOfWeekFormat(String day) {
        switch (day) {
            case "Sun":
                return "SUNDAY";
            case "Mon":
                return "MONDAY";
            case "Tue":
                return "TUESDAY";
            case "Wed":
                return "WEDNESDAY";
            case "Thu":
                return "THURSDAY";
            case "Fri":
                return "FRIDAY";
            case "Sat":
                return "SATURDAY";
            default:
                return day;
        }
    }

    // EFFECT: Returns true if this time span overlaps with the given time span.
    public boolean isOverlapping(TimeSpan timeSpan) {
        ZonedDateTime start2 = timeSpan.getStart();
        ZonedDateTime end2 = timeSpan.getEnd();

        if (start.isBefore(start2) && start2.isBefore(end)) {
            return true;
        } else {
            return start.isBefore(end2) && end2.isBefore(end);
        }
    }

    // MODIFIES: this
    // EFFECT: Converts time string in 24Hr format to conform to HH:mm standards.
    private String properStrTimeFormat(String time) {
        String str = time;
        str = str.trim();

        if (str.equals("")) {
            str = "08:00";
        }
        if (str.length() == 4) {
            str = "0" + str;
        }

        return str;
    }

    // EFFECT: returns a hashmap of "WEEK DAY NAME": date of the first week of the month.
    private HashMap<String, LocalDate> makeDaysOfTheWeekMap() {
        HashMap<String, LocalDate> map = new HashMap<>();
        int day = 1;

        LocalDate date = LocalDate.of(year, month, day);
        while (!(date.getDayOfWeek().getValue() == 7)) {
            day++;
            date = LocalDate.of(year, month, day);
        }
        map.put(date.getDayOfWeek().toString(), date);

        for (int i = 0; i < 6; i++) {
            day++;
            date = LocalDate.of(year, month, day);
            map.put(date.getDayOfWeek().toString(), date);
        }

        return map;
    }

    // REQUIRES: start and end to not be null
    // MODIFIES: this
    // EFFECT: Returns the section's time slot (morning / afternoon / evening).
    private void whatIsTimeSlot() {
        for (int i = 0; i < 3; i++) {
            if (start.isAfter(startTimes.get(i))) {
                if (i == 0) {
                    timeSlot = "Morning";
                }
                if (i == 1) {
                    timeSlot = "Afternoon";
                }
                if (i == 2) {
                    timeSlot = "Evening";
                }
            }
        }
    }

    // MODIFIES: this
    // EFFECT: add times to startTime array.
    private void formatTimeDivision() {
        startTimes.add(convertStrTime("07:59"));
        startTimes.add(convertStrTime("11:59"));
        startTimes.add(convertStrTime("16:59"));
    }

    // EFFECT: used to sort list of Timespan.
    @Override
    public int compareTo(TimeSpan o) {
        // referenced: https://stackoverflow.com/questions/4353572/
        // how-can-i-create-a-sorted-list-of-integer-and-string-pairs
        return start.compareTo(o.getStart());
    }
}
