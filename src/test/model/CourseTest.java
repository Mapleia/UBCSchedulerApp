package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CourseTest {
    public Course cpsc210;
    public JsonReader reader;
    public List<String> preferences = new LinkedList<>();

    @BeforeEach
    public void setup() {
        reader = new JsonReader("./data/2020W/CPSC/CPSC 210.json");
        preferences.add("Afternoon");
        preferences.add("Evening");
        preferences.add("Morning");
    }

    @Test
    public void testSectionCreation() {
        try {
            cpsc210 = reader.readCourse("2020W", preferences);
        } catch (Exception e) {
            fail();
        }

        assertEquals("CPSC 210 L1U", cpsc210.getSectionsMap().get("CPSC 210 L1U").getSection());
        assertEquals("18:00", cpsc210.getSectionsMap().get("CPSC 210 L1U").getStart().toString());
        assertTrue(cpsc210.getTerms().contains("1"));
        assertEquals(4, cpsc210.getCredit());

    }

    @Test
    public void testSortedSections() {
        try {
            cpsc210 = reader.readCourse("2020W", preferences);
        } catch (Exception e) {
            fail();
        }
        assertEquals("CPSC 210 L1A", cpsc210.getSortSections().get("Laboratory").get("AFTERNOON").get(0)
                .getSection());
    }

    @Test
    public void testMapSectionsRequired() {
        Course japn200 = null;
        try {
            reader = new JsonReader("./data/2020W/JAPN/JAPN 200.json");
            japn200 = reader.readCourse("2020W", preferences);
        } catch (Exception e) {
            fail();
        }
        assertEquals("JAPN 200 007", japn200.getSortSections().get("Required").get("N/A").get(0).getSection());
        assertTrue(japn200.getActivitiesList().contains("Required"));
        assertEquals("JAPN 200", japn200.getCourseName());
    }
}
