package model;

import exceptions.NoCourseFound;
import exceptions.NoSectionFound;
import exceptions.NoTimeSpanAdded;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class CourseTest {
    private static final String[] PREFERENCE_ARRAY = {"Afternoon", "Morning", "Evening"};
    private TimeTable timeTableW;
    private Course cpsc210;
    private Section cpsc210Section;
    private final String[] validCpsc210Section = new String[]{"CPSC 210 L1U", "Laboratory"};

    @BeforeEach
    public void setup() {
        timeTableW = new TimeTable(2020, true, PREFERENCE_ARRAY);

        try {
            cpsc210 = Course.createCourse("CPSC", "210", timeTableW);
            JSONObject obj = JsonReader.findCourseFile("CPSC", "210", timeTableW);
            cpsc210Section = Section.createSection(obj.getJSONObject("sections").getJSONObject("L1U"), timeTableW);

        } catch (Exception e) {
            fail("No course found, setup failed.");
            e.printStackTrace();
        }
    }

    @Test
    public void testValidCourse() {
        assertEquals("CPSC", cpsc210.getSubjectCode());
        assertEquals("210", cpsc210.getCourseNum());
        assertTrue(cpsc210.isHasTerm1());
        assertTrue(cpsc210.isHasTerm2());
    }

    @Test
    public void testAddAllSections() {
        assertTrue(cpsc210.size() > 0);

        assertTrue(cpsc210.contains(validCpsc210Section[0]));
    }

    @Test
    public void testAllActivities() {
        assertTrue(cpsc210.getAllActivities()
                .get("Laboratory")
                .get("Evening")
                .contains(cpsc210Section));
    }

    @Test
    public void testCountPrimary() {
        int counter = 0;
        for (String key : cpsc210.getAllKeys()) {
            counter += cpsc210.getAllActivities().get(key).get(PREFERENCE_ARRAY[0]).size();
        }

       assertEquals(counter, cpsc210.preferenceSize(PREFERENCE_ARRAY[0]));
    }

    @Test
    public void testEquals() {
        try {
            assertTrue(cpsc210.equals(Course.createCourse("CPSC", "210", timeTableW)));
            assertFalse(cpsc210.equals(Course.createCourse("CPSC", "110", timeTableW)));
            assertFalse(cpsc210.equals(cpsc210Section));
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void testHashCode() {
        try {
            assertEquals(cpsc210.hashCode(),
                    Course.createCourse("CPSC", "210", timeTableW).hashCode());
            assertNotEquals(cpsc210.hashCode(),
                    Course.createCourse("CPSC", "310", timeTableW).hashCode());

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCompareTo() {
        try {
            timeTableW.addCourse("BIOL 330");
            Course bioCourse = timeTableW.getCourse("BIOL 330");
            assertEquals(bioCourse, Course.createCourse("BIOL", "330", timeTableW));
            assertTrue(cpsc210.compareTo(bioCourse)> bioCourse.compareTo(cpsc210));

        } catch (NoCourseFound | NoTimeSpanAdded e) {
            fail("No bio course found.");
            e.printStackTrace();
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void testCrucialFieldBlank() {
        try {
            timeTableW.addCourse("BIOL 200");
            Course bioCourse = timeTableW.getCourse("BIOL 200");
            HashSet<Section> req = bioCourse.getAllActivities().get("Required").get(PREFERENCE_ARRAY[0]);

            JSONObject obj = JsonReader.findCourseFile("BIOL", "200", timeTableW);
            Section bioSection = Section.createSection(obj.getJSONObject("sections").getJSONObject("000"), timeTableW);


            assertTrue(req.contains(bioSection));

        } catch (Exception e) {
            fail("No bio course found.");
            e.printStackTrace();
        }
    }

    @Test
    public void testGetSection() {
        try {
            assertTrue(cpsc210Section.equals(cpsc210.get("CPSC 210 L1U")));

        } catch (NoSectionFound noSectionFound) {
            noSectionFound.printStackTrace();
        }
    }

    @Test
    public void testGetSectionNull() {
        try {
            assertTrue(cpsc210Section.equals(cpsc210.get("CPSC 210 ZFF")));
            fail();
        } catch (NoSectionFound noSectionFound) {
            System.out.println("Success!");
        }
    }
}
