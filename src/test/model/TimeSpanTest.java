package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class TimeSpanTest {
    public TimeTable timeTable;
    public String[] timeTableTimeArr = {"Afternoon", "Evening", "Morning"};

    public Course course;
    public Course course2;
    public TimeSpan timeSpan;
    public TimeSpan timeSpan1213Fall;

    @BeforeEach
    public void setup() {
        timeTable = new TimeTable(2020, 0, timeTableTimeArr, true);
        timeSpan1213Fall = new TimeSpan("12:00", "13:00", "Mon", 2020, 9, 7);

        try {
            timeTable.addCourse("BIOl", "112");
            timeTable.addCourse("BIOL", "200");
            course = timeTable.getCourseList().get(0);
            course2 = timeTable.getCourseList().get(1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }


    }

    @Test
    public void testWithACourse() {
        LocalDateTime localDateTime = LocalDateTime.of(2020, 9, 7, 12, 0);
        ZoneId zoneId = ZoneId.of(TimeSpan.TIMEZONE);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);

        Section section = course.getAllActivities().get("Web-Oriented Course").get(timeTable.primaryTimePref).get(0);

        assertEquals(section.getTimeSpans().get(0).getStart(), zonedDateTime);
    }

    @Test
    public void testConvertStrTime() {

        assertEquals(timeSpan1213Fall.getStart(), timeSpan1213Fall.convertStrTime("12:00"));
    }

    @Test
    public void testProperStrTimeFormat() {
        timeSpan = new TimeSpan(" ", "13:00", "Mon", 2020, 9, 7);
        assertEquals(timeSpan.getStart(), timeSpan.convertStrTime("08:00"));
    }

    @Test
    public void testIsOverlappingTrue() {
        assertTrue(timeSpan1213Fall.isOverlapping(new TimeSpan("12:30",
                "14:00", "Mon", 2020, 9, 7)));

        assertTrue(timeSpan1213Fall.isOverlapping(new TimeSpan("12:30",
                "12:45", "Mon", 2020, 9, 7)));

        assertTrue(timeSpan1213Fall.isOverlapping(new TimeSpan("11:30",
                "12:45", "Mon", 2020, 9, 7)));
    }

    @Test
    public void testIsOverlappingFalse() {
        TimeSpan timeSpan2 = new TimeSpan("17:00", "18:00", "Mon", 2020, 9, 7);

        assertFalse(timeSpan2.isOverlapping(new TimeSpan("12:30",
                "14:00", "Mon", 2020, 9, 7)));

    }

    @Test
    public void testGetters() {
        assertEquals("Afternoon", timeSpan1213Fall.getTimeSlot());
        assertEquals(timeSpan1213Fall.convertStrTime("13:00"), timeSpan1213Fall.getEnd());

    }

    @Test
    public void testDaysOfWeekOtherCases() {
        TimeSpan timeSpan = new TimeSpan("9:00", "10:00", "Sat", 2020, 9);
        assertEquals("SATURDAY", timeSpan.getDayOfWeek());

        TimeSpan timeSpan2 = new TimeSpan("9:00", "10:00", "Sun", 2020, 9);
        assertEquals("SUNDAY", timeSpan2.getDayOfWeek());

        TimeSpan timeSpan3 = new TimeSpan("9:00", "10:00", "MONDAY", 2020, 9);
        assertEquals("MONDAY", timeSpan3.getDayOfWeek());
    }

    @Test
    public void testThuFri() {
        TimeSpan timeSpan4 = new TimeSpan("9:00", "10:00", "Thu", 2020, 9);
        assertEquals("THURSDAY", timeSpan4.getDayOfWeek());

        TimeSpan timeSpan5 = new TimeSpan("9:00", "10:00", "Fri", 2020, 9);
        assertEquals("FRIDAY", timeSpan5.getDayOfWeek());
    }

}
