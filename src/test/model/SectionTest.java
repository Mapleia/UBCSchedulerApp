package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    public void testGetters() {
        try {
            assertEquals("BIOL 112 101", bioAllSections.get(0).getSection());

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
}
