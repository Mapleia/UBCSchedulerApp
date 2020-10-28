package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {
    public Course course;
    public Course course2;
    public TimeTable timeTable;
    public String[] timeTableTimeArr = {"Afternoon", "Evening", "Morning"};

    @BeforeEach
    public void setup() {
        timeTable = new TimeTable(2020, 0, timeTableTimeArr, true);

        try {
            timeTable.addCourse("BIOL", "112");
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
        assertEquals("BIOL 112 T21", course.getAllSection().get(0).getSection());
        assertEquals("BIOL 200 T61", course2.getAllSection().get(0).getSection());

    }

    @Test
    public void testMapActivity() {
        ArrayList<Section> sec = course.getAllActivities().get("Web-Oriented Course").get(timeTable.tertiaryTimePref);
        assertTrue(sec.contains(course.getAllSection().get(44)));

        ArrayList<Section> sec2 = course2.getAllActivities().get("Web-Oriented Course").get(timeTable.primaryTimePref);
        assertTrue(sec2.contains(course2.getAllSection().get(35)));


    }

    @Test
    public void testGetter() {
        assertEquals("BIOL", course.getSubjectCode());
        assertEquals("112", course.getCourseNum());

        assertTrue(course2.isHasTerm2() && course2.isHasTerm1());

        assertEquals(27, course2.getPrimaryCounter());
    }

    @Test
    public void testSummer() {
        TimeTable timeTable2 = new TimeTable(2020, 1, timeTableTimeArr, true);

        try {
            timeTable2.addCourse("BIOL", "112");
            fail();
        } catch (Exception e) {
            System.out.println("No summer courses in data.");

        }
    }

    @Test
    public void testEqualsTrue() {
        assertEquals(new Course("BIOL", "112"), course);
    }

    @Test
    public void testEqualsFalse() {
        assertNotEquals(course2, course);
    }

    @Test
    public void testEqualsFalseNotCourse() {
        assertNotEquals(course2.getAllSection().get(0), course);
    }

    @Test
    public void testComparison() {
        List<Course> courseList = new ArrayList<>();
        courseList.add(course);
        courseList.add(course2);
        Collections.sort(courseList);

        List<Course> courseListT = timeTable.getCourseList();
        Collections.sort(courseListT);

        assertEquals(courseList.get(0).getSubjectCode(), courseList.get(0).getSubjectCode());

    }

    @Test
    public void testCountPrim() {
        assertEquals(19, course.getPrimaryCounter());
    }
}