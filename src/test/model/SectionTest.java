package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SectionTest {
    public ArrayList<Section> allSections;
    public String[] timeTableTimeArr = {"Afternoon", "Evening", "Morning"};

    @BeforeEach
    public void setup() {
        TimeTable timeTable = new TimeTable();
        timeTable.setTimePref(timeTableTimeArr);
        try {
            timeTable.addCourse("BIOL", "112");
            Course course = timeTable.getCourseList().get(0);
            allSections = course.getAllSection();

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetTimeSlotMorning() {
        try {
            String timeSlot = allSections.get(0).getTimeSlot();
            assertEquals("Morning", timeSlot);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetTimeSlot11AM() {
        try {
            String timeSlot = allSections.get(3).getTimeSlot();
            assertEquals("Morning", timeSlot);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetTimeSlotAfternoon() {
        try {
            String timeSlot = allSections.get(5).getTimeSlot();
            assertEquals("Afternoon", timeSlot);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetTimeSlotNight() {
        try {
            String timeSlot = allSections.get(15).getTimeSlot();
            assertEquals("Evening", timeSlot);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetters() {
        try {
            assertEquals("BIOL 112 101", allSections.get(0).getSection());

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
