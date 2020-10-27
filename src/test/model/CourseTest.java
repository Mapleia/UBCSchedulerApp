package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {
    public Course course;
    public Course course2;
    public TimeTable timeTable;
    public String[] timeTableTimeArr = {"Afternoon", "Evening", "Morning"};


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
    public void testCountActivity() {
        assertEquals(4,  course.getActivitySize().get("Web-Oriented Course"));
        assertEquals(4,  course2.getActivitySize().get("Web-Oriented Course"));

    }

    @Test
    public void testAddAllSections() {
        assertEquals("BIOL 112 101", course.getAllSection().get(0).getSection());
        assertEquals("BIOL 200 101", course2.getAllSection().get(0).getSection());

    }

    @Test
    public void testMapActivity() {
        ArrayList<Section> sec = course.getAllActivities().get("Web-Oriented Course").get(timeTable.tertiaryTimePref);
        assertTrue(sec.contains(course.getAllSection().get(0)));

        ArrayList<Section> sec2 = course2.getAllActivities().get("Web-Oriented Course").get(timeTable.primaryTimePref);
        assertTrue(sec2.contains(course2.getAllSection().get(2)));


    }

    @Test
    public void testGetter() {
        assertEquals("BIOL", course.getSubjectCode());
        assertEquals("112", course.getCourseNum());

        assertTrue(course2.isHasTerm2() && course2.isHasTerm1());

        course2.countPrimary();
        assertEquals(27, course2.getPrimaryCounter());
    }
}