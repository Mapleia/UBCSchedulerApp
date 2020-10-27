package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class TimeSpanTest {
    public TimeTable timeTable;
    public String[] timeTableTimeArr = {"Afternoon", "Evening", "Morning"};
    public Course course;
    public Course course2;
    public TimeSpan timeSpan;
    @BeforeEach
    public void setup() {
        timeTable = new TimeTable(2020, 0);
        timeTable.setTimePref(timeTableTimeArr);

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
        timeSpan = new TimeSpan("12:00", "13:00", "Mon", 2020, 9, 7);

        timeSpan.convertStrTime("12:00");
        assertEquals(timeSpan.getStart(), timeSpan.convertStrTime("12:00"));
    }

    @Test
    public void testProperStrTimeFormat() {
        timeSpan = new TimeSpan(" ", "13:00", "Mon", 2020, 9, 7);
        assertEquals(timeSpan.getStart(), timeSpan.convertStrTime("08:00"));
    }

    @Test
    public void testIsOverlappingTrue() {
        timeSpan = new TimeSpan("12:00", "13:00", "Mon", 2020, 9, 7);

        assertTrue(timeSpan.isOverlapping(new TimeSpan("12:30",
                "14:00", "Mon", 2020, 9, 7)));

    }

    @Test
    public void testIsOverlappingFalse() {
        timeSpan = new TimeSpan("17:00", "18:00", "Mon", 2020, 9, 7);

        assertFalse(timeSpan.isOverlapping(new TimeSpan("12:30",
                "14:00", "Mon", 2020, 9, 7)));

    }

    @Test
    public void testGetters() {
        timeSpan = new TimeSpan("12:00", "13:00", "Mon", 2020, 9, 7);

        assertEquals("Afternoon", timeSpan.getTimeSlot());
        assertEquals(timeSpan.convertStrTime("13:00"), timeSpan.getEnd());

    }
}
