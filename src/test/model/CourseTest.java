package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {
    public Course course;
    public TimeTable timeTable;

    @BeforeEach
    public void setup() {
        timeTable = new TimeTable();
        try {
            timeTable.addCourse("BIOl", "112");
            course = timeTable.getCourseList().get(0);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testCountActivity() {
        assertEquals(4,  course.getActivitySize().get("Web-Oriented Course"));
    }

    @Test
    public void testAddAllSections() {
        assertEquals("BIOL 112 101", course.getAllSection().get(0).getSection());
    }

    @Test
    public void testMapActivity() {
        HashSet<Section> sec = course.getAllActivities().get("Web-Oriented Course").get(course.tertiaryTimePref);
        assertTrue(sec.contains(course.getAllSection().get(0)));
    }

    @Test
    public void testGetter() {
        assertEquals("BIOL", course.getSubjectCode());
        assertEquals("112", course.getCourseNum());
    }
}