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

    private final String dayOfWeek;
    private final int year;
    private final int month;

    private final ZonedDateTime startTime;
    private final ZonedDateTime endTime;
    private final ArrayList<ZonedDateTime> startTimes = new ArrayList<>();
    private String timeSlot;

    // constructor
    public TimeSpan(String startTime, String endTime, String dayOfWeek, int year, int month) {
        this.dayOfWeek = properDayOfWeekFormat(dayOfWeek);
        this.year = year;
        this.month = month;

        this.startTime = convertStrTime(properStrTimeFormat(startTime), this.year, this.month, this.dayOfWeek);
        this.endTime = convertStrTime(properStrTimeFormat(endTime), this.year, this.month, this.dayOfWeek);
        this.startTimes.add(convertStrTime("07:59", this.year, this.month, this.dayOfWeek));
        this.startTimes.add(convertStrTime("11:59", this.year, this.month, this.dayOfWeek));
        this.startTimes.add(convertStrTime("16:59", this.year, this.month, this.dayOfWeek));

        whatIsTimeSlot();
    }

    // getters
    public String getTimeSlot() {
        return timeSlot;
    }

    public ZonedDateTime getStart() {
        return startTime;
    }

    public ZonedDateTime getEnd() {
        return endTime;
    }

    // REQUIRES: time to be in 24hr HH:mm format.
    // EFFECT: Convert string time (like 19:01) to ZonedDateTime object.
    public static ZonedDateTime convertStrTime(String time, int yearC, int monthC, String day) {
        ZonedDateTime zonedDateTime;
        LocalDate date;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        ZoneId zoneId = ZoneId.of(TIMEZONE);

        HashMap<String, LocalDate> firstWeek = new HashMap<>();
        int dayInt = 1;

        date = LocalDate.of(yearC, monthC, dayInt);

        while (!(date.getDayOfWeek().getValue() == 7)) {
            dayInt++;
            date = LocalDate.of(yearC, monthC, dayInt);
        }
        firstWeek.put(date.getDayOfWeek().toString(), date);

        for (int i = 0; i < 6; i++) {
            dayInt++;
            date = LocalDate.of(yearC, monthC, dayInt);
            firstWeek.put(date.getDayOfWeek().toString(), date);
        }

        LocalDate dateFromMap = firstWeek.get(day);
        LocalTime timeParsed = LocalTime.parse(time, formatter);
        zonedDateTime = ZonedDateTime.of(dateFromMap, timeParsed, zoneId);

        return zonedDateTime;
    }

    // EFFECT: Returns true if this time span overlaps with the given time span.
    public static boolean isOverlapping(TimeSpan t1, TimeSpan t2) {
        ZonedDateTime start1 = t1.getStart();
        ZonedDateTime end1 = t1.getEnd();
        ZonedDateTime start2 = t2.getStart();
        ZonedDateTime end2 = t2.getEnd();

        if (start1.isEqual(start2)) {
            return true;
        } else if (end1.isEqual(end2)) {
            return true;
        } else if (start1.isBefore(start2) && end1.isAfter(start2)) {
            return true;
        } else {
            return start1.isAfter(start2) && start1.isBefore(end2);
        }
    }

    // EFFECT: used to sort list of Timespan.
    @Override
    public int compareTo(TimeSpan o) {
        // referenced: https://stackoverflow.com/questions/4353572/
        // how-can-i-create-a-sorted-list-of-integer-and-string-pairs
        return startTime.compareTo(o.getStart());
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

    // MODIFIES: this
    // EFFECT: Converts 3 char of of the week strings to ALL CAPITAL FULL NAME.
    private String properDayOfWeekFormat(String day) {
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

    // REQUIRES: start and end to not be null
    // MODIFIES: this
    // EFFECT: Returns the section's time slot (morning / afternoon / evening).
    private void whatIsTimeSlot() {
        for (int i = 0; i < 3; i++) {
            if (startTime.isAfter(startTimes.get(i))) {
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

}