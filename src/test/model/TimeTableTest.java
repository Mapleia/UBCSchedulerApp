package model;

import exceptions.NoCourseFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimeTableTest {
    public TimeTable timeTable;
    public String[] timeTableTimeArr = {"Afternoon", "Evening", "Morning"};

    @BeforeEach
    public void setup() {
        timeTable = new TimeTable(2020, true, timeTableTimeArr);
        try {
            timeTable.addCourse("CPSC 210");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddCourseValid() {
        Course courseFromTT = null;
        try {
            courseFromTT = timeTable.getCourse("CPSC 210");
        } catch (NoCourseFound n) {
            fail();
            n.printStackTrace();
        }
        assertEquals("210", courseFromTT.getCourseNum());
        assertEquals("CPSC", courseFromTT.getSubjectCode());
        assertTrue(courseFromTT.contains("CPSC 210 L1F"));
    }

    @Test
    public void testSetTimePref() {
        assertEquals(timeTableTimeArr[0], timeTable.primaryTimePref);
        assertEquals(timeTableTimeArr[1], timeTable.secondaryTimePref);
        assertEquals(timeTableTimeArr[2], timeTable.tertiaryTimePref);
    }

    @Test
    public void testAddCourseException() {
        try {
            timeTable.addCourse("CPSS 210");
            Course course = timeTable.getCourse("CPSS 210");
            System.out.println(course.getAllKeys());
            fail();
        } catch (Exception e){
            System.out.println("No course found.");
        }
    }

    @Test
    public void testSummerInValid() {
        TimeTable timeTable1 = new TimeTable(2021, false, timeTableTimeArr);

        try {
            timeTable1.addCourse("BIOL 112");
            fail();
        } catch (Exception e) {
            System.out.println("No summer courses in data.");
        }
    }

    @Test
    public void testGetNonExistingCourse() {
        try {
            timeTable.getCourse("LFS 100");
            fail();
        } catch (Exception e) {
            System.out.println("Success!");

        }
    }

    @Test
    public void testGetCourseList() {
        try {
            assertTrue(timeTable.getCourseList().containsKey("CPSC 210"));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testRemoveNonExistingCourse() {
        try {
            timeTable.removeCourse("LFS 100");
            fail();
        } catch (Exception e) {
            System.out.println("Success!");

        }
    }

    @Test
    public void testRemoveExistingCourse() {
        try {
            timeTable.removeCourse("CPSC 210");
            assertFalse(timeTable.getCourseList().containsKey("CPSC 210"));
        } catch (Exception e) {
            fail();

        }
    }
}
