package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimeTableTest {
    public TimeTable timeTable;
    public String[] timeTableTimeArr = {"Afternoon", "Evening", "Morning"};

    @BeforeEach
    public void setup() {
        timeTable = new TimeTable(2020, 0, timeTableTimeArr, true);
        try {
            timeTable.addCourse("CPSC", "210");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddCourseValid() {
        Course courseFromTT = timeTable.getCourseList().get(0);
        assertEquals("210", courseFromTT.getCourseNum());
        assertEquals("CPSC", courseFromTT.getSubjectCode());
        assertEquals("CPSC 210 L1F", courseFromTT.getAllSection().get(0).getSection());
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
            timeTable.addCourse("CPSS", "210");
            fail();
        } catch (Exception e){
            System.out.println("No course found.");
        }
    }

    @Test
    public void testSetters() {
        timeTable.setSpreadClasses(true);
        assertTrue(timeTable.getSpreadClasses());
    }

    @Test
    public void testSummerInValid() {
        TimeTable timeTable1 = new TimeTable(2021, 1, timeTableTimeArr, true);

        try {
            timeTable1.addCourse("BIOL", "112");
            fail();
        } catch (Exception e) {
            System.out.println("No summer courses in data.");
        }
    }

}
