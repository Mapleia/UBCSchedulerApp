package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class TimeSpanTest {
    public TimeTable timeTable;
    public String[] timeTableTimeArr = {"Afternoon", "Evening", "Morning"};
    public Course b112;
    public TimeSpan timeSpan112;

    @BeforeEach
    public void setup() {
        timeTable = new TimeTable(2020, true, timeTableTimeArr);
         try {
            b112 = Course.createCourse("BIOL","112", timeTable);
            timeSpan112 = b112.get("BIOL 112 101").getTimeSpans().get(0);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testConstructor() {
        LocalDate b112D = LocalDate.of(2020, 9, 7);
        ZonedDateTime b112z1 = ZonedDateTime.of(b112D, LocalTime.of(9, 0), ZoneId.of(TimeSpan.TIMEZONE));
        ZonedDateTime b112z2 = ZonedDateTime.of(b112D, LocalTime.of(10, 0), ZoneId.of(TimeSpan.TIMEZONE));

        assertEquals("Morning", timeSpan112.getTimeSlot());
        assertEquals(b112z2, timeSpan112.getEnd());
        assertEquals(b112z1, timeSpan112.getStart());
    }

    @Test
    public void testConvertStrTime() {
        assertTrue(timeSpan112.getStart()
                .equals(TimeSpan.convertStrTime("09:00", 2020, 9, "MONDAY")));
    }

    @Test
    public void testOverlappingStart() {
        TimeSpan timeSpan910 = new TimeSpan("9:00", "10:00", "Mon", 2020, 9);
        TimeSpan timeSpan911 = new TimeSpan("9:00", "11:00", "Mon", 2020, 9);
        TimeSpan timeSpan993 = new TimeSpan("9:00", "9:30", "Mon", 2020, 9);
        TimeSpan timeSpan893 = new TimeSpan("8:00", "9:30", "Mon", 2020, 9);

        assertTrue(TimeSpan.isOverlapping(timeSpan112, timeSpan910));
        assertTrue(TimeSpan.isOverlapping(timeSpan112, timeSpan911));
        assertTrue(TimeSpan.isOverlapping(timeSpan112, timeSpan993));
        assertTrue(TimeSpan.isOverlapping(timeSpan112, timeSpan893));

    }

    @Test
    public void testOverlappingEnd() {
        TimeSpan timeSpan810 = new TimeSpan("8:00", "10:00", "Mon", 2020, 9);
        TimeSpan timeSpan931 = new TimeSpan("9:30", "10:00", "Mon", 2020, 9);
        TimeSpan timeSpan9313 = new TimeSpan("9:30", "11:00", "Mon", 2020, 9);

        assertTrue(TimeSpan.isOverlapping(timeSpan112, timeSpan810));
        assertTrue(TimeSpan.isOverlapping(timeSpan112, timeSpan931));
        assertTrue(TimeSpan.isOverlapping(timeSpan112, timeSpan9313));

    }

    @Test
    public void testOverlappingOver() {
        TimeSpan timeSpan712 = new TimeSpan("7:00", "12:00", "Mon", 2020, 9);
        TimeSpan timeSpan93945 = new TimeSpan("9:30", "9:45", "Mon", 2020, 9);

        assertTrue(TimeSpan.isOverlapping(timeSpan112, timeSpan712));
        assertTrue(TimeSpan.isOverlapping(timeSpan112, timeSpan93945));
    }


    @Test
    public void testOverlappingFalse() {
        TimeSpan timeSpan = new TimeSpan("", "12:00", "SATURDAY", 2020, 9);
        TimeSpan timeSpanSat = new TimeSpan("", "12:00", "Sat", 2020, 9);
        TimeSpan timeSpanSun = new TimeSpan("", "12:00", "Sun", 2020, 9);
        TimeSpan timeSpanMon = new TimeSpan("10:01", "11:00", "Mon", 2020, 9);

        assertFalse(TimeSpan.isOverlapping(timeSpan112, timeSpan));
        assertFalse(TimeSpan.isOverlapping(timeSpan112, timeSpanSat));
        assertFalse(TimeSpan.isOverlapping(timeSpan112, timeSpanSun));
        assertFalse(TimeSpan.isOverlapping(timeSpan112, timeSpanMon));


    }

}
