package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SectionTest {
    public TimeTable timeTable;
    public Course bioCourse;
    public ArrayList<Section> bioAllSections;
    public ArrayList<Section> bioMorningWebCourse;

    public Course cpscCourse;
    public ArrayList<Section> cpscAllSections;
    public String[] timeTableTimeArr = {"Afternoon", "Evening", "Morning"};

    @BeforeEach
    public void setup() {
        timeTable = new TimeTable(2020, 0, timeTableTimeArr, true);
        try {
            timeTable.addCourse("BIOL", "112");
            bioCourse = timeTable.getCourseList().get(0);
            bioAllSections = bioCourse.getAllSection();
            bioMorningWebCourse = bioCourse.getAllActivities().get("Web-Oriented Course").get("Morning");

            timeTable.addCourse("CPSC", "110");
            cpscCourse = timeTable.getCourseList().get(1);
            cpscAllSections = cpscCourse.getAllSection();

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetTimeSlotMorning() {
        try {
            String timeSlot = bioAllSections.get(1).getTimeSlot();
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
            assertEquals("Afternoon", timeSlot);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetTimeSlotAfternoon() {
        try {
            String timeSlot = bioAllSections.get(0).getTimeSlot();
            assertEquals("Afternoon", timeSlot);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetTimeSlotNight() {
        try {
            String timeSlot = bioAllSections.get(4).getTimeSlot();
            assertEquals("Evening", timeSlot);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testIsOverlappingFalse() {
        Section sec1 = bioMorningWebCourse.get(0);
        Section sec2 = bioMorningWebCourse.get(1);
        assertFalse(sec1.isOverlapping(sec2));
    }

    @Test
    public void testIsOverlappingFalseReversed() {
        Section sec1 = bioMorningWebCourse.get(0);
        Section sec2 = bioMorningWebCourse.get(1);
        assertFalse(sec2.isOverlapping(sec1));

    }

    @Test
    public void testIsOverlappingTrue() {
        Section sec1 = cpscAllSections.get(0);
        Section sec2 = cpscAllSections.get(16);
        assertTrue(sec1.isOverlapping(sec2));
    }

    @Test
    public void testIsOverlappingTrueReversed() {
        Section sec1 = cpscAllSections.get(0);
        Section sec2 = cpscAllSections.get(16);
        assertTrue(sec2.isOverlapping(sec1));
    }

    @Test
    public void testIsOverlappingListTrue() {
        assertTrue(bioAllSections.get(44).isOverlapping(cpscAllSections));
    }

    @Test
    public void testConstructor() {
        Section section = new Section("Full", "BIOL 112 101", "9:00", "10:00",
                "Web-Oriented Course", "1", " Mon Wed Fri", timeTable);

        assertEquals("BIOL 112 101", section.getSection());
        assertEquals("9:00", section.getStart());
        assertEquals("10:00", section.getEnd());
        assertEquals(" Mon Wed Fri", section.getDays());
    }

    @Test
    public void testSummerT1() {
        TimeTable timeTable2 = new TimeTable(2020, 1, timeTableTimeArr, true);


        Section section = new Section("Full", "BIOL 112 101", "9:00", "10:00",
                "Web-Oriented Course", "1", " Mon Wed Fri", timeTable2);

        assertEquals(5, section.getTimeSpans().get(0).getStart().getMonthValue());
    }


    @Test
    public void testSummerT2() {
        TimeTable timeTable2 = new TimeTable(2020, 1, timeTableTimeArr, true);

        Section section = new Section("Full", "BIOL 112 101", "9:00", "10:00",
                "Web-Oriented Course", "2", " Mon Wed Fri", timeTable2);

        assertEquals(7, section.getTimeSpans().get(0).getStart().getMonthValue());
    }

    @Test
    public void testOtherConstructor() {
        assertEquals(bioAllSections.get(0), new Section("BIOL 112 T21", "Tutorial"));
    }

    @Test
    public void testCheckRequired() {
        Section sectionTest = new Section("Full", "BIOL 112 101", "", "",
                "Lecture", "2", "", timeTable);
        assertEquals("Required", sectionTest.getActivity());
    }
}
