package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SectionTest {
    public TimeTable timeTable;
    public Course bioCourse;
    public ArrayList<Section> bioAllSections;

    public Course cpscCourse;
    public ArrayList<Section> cpscAllSections;
    public String[] timeTableTimeArr = {"Afternoon", "Evening", "Morning"};

    @BeforeEach
    public void setup() {
        timeTable = new TimeTable(2020, 0);
        timeTable.setTimePref(timeTableTimeArr);
        try {
            timeTable.addCourse("BIOL", "112");
            bioCourse = timeTable.getCourseList().get(0);
            bioCourse.addAllSections();
            bioAllSections = bioCourse.getAllSection();

            timeTable.addCourse("CPSC", "110");
            cpscCourse = timeTable.getCourseList().get(1);
            cpscCourse.addAllSections();
            cpscAllSections = cpscCourse.getAllSection();

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetTimeSlotMorning() {
        try {
            String timeSlot = bioAllSections.get(0).getTimeSlot();
            assertEquals("Morning", timeSlot);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetTimeSlot11AM() {
        try {
            String timeSlot = bioAllSections.get(3).getTimeSlot();
            assertEquals("Morning", timeSlot);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetTimeSlotAfternoon() {
        try {
            String timeSlot = bioAllSections.get(5).getTimeSlot();
            assertEquals("Afternoon", timeSlot);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetTimeSlotNight() {
        try {
            String timeSlot = bioAllSections.get(15).getTimeSlot();
            assertEquals("Evening", timeSlot);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testIsOverlappingFalse() {
        ArrayList<Section> webCourseSet = bioCourse.getAllActivities().get("Web-Oriented Course").get("Morning");
        Section sec1 = webCourseSet.get(0);
        Section sec2 = webCourseSet.get(1);
        assertFalse(sec1.isOverlapping(sec2));
    }

    @Test
    public void testIsOverlappingFalseReversed() {
        ArrayList<Section> webCourseSet = bioCourse.getAllActivities().get("Web-Oriented Course").get("Morning");
        Section sec1 = webCourseSet.get(0);
        Section sec2 = webCourseSet.get(1);
        assertFalse(sec2.isOverlapping(sec1));

    }

    @Test
    public void testIsOverlappingTrue() {
        Section sec1 = cpscAllSections.get(2);
        Section sec2 = cpscAllSections.get(18);
        assertTrue(sec1.isOverlapping(sec2));
    }

    @Test
    public void testIsOverlappingTrueReversed() {
        Section sec1 = cpscAllSections.get(2);
        Section sec2 = cpscAllSections.get(18);
        assertTrue(sec2.isOverlapping(sec1));
    }

    @Test
    public void testIsOverlappingListTrue() {
        assertTrue(bioAllSections.get(0).isOverlapping(cpscAllSections));
    }

    @Test
    public void testConstructor() {
        Section section = new Section("Full", "BIOL 112 101", "9:00", "10:00",
                "Web-Oriented Course", "1", " Mon Wed Fri");

        assertEquals("BIOL 112 101", section.getSection());
        assertEquals("9:00", section.getStart());
        assertEquals("10:00", section.getEnd());
        assertEquals(" Mon Wed Fri", section.getDays());
    }

    @Test
    public void testSummerT1() {
        timeTable.setWinterOrSummer(1);
        Section section = new Section("Full", "BIOL 112 101", "9:00", "10:00",
                "Web-Oriented Course", "1", " Mon Wed Fri");
        section.setTimeTable(timeTable);
        section.setCrucialFieldsBlank(false);
        section.formatDatesAndTime();

        assertEquals(5, section.getTimeSpans().get(0).getStart().getMonthValue());
    }

    @Test
    public void testSummerT2() {
        timeTable.setWinterOrSummer(1);
        Section section = new Section("Full", "BIOL 112 101", "9:00", "10:00",
                "Web-Oriented Course", "2", " Mon Wed Fri");
        section.setTimeTable(timeTable);
        section.setCrucialFieldsBlank(false);
        section.formatDatesAndTime();

        assertEquals(7, section.getTimeSpans().get(0).getStart().getMonthValue());
    }
}
