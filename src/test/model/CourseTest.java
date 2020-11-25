package model;

import exceptions.NoCourseFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import java.io.IOException;
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

        try {
            cpsc210 = reader.readCourse("2020W", preferences);
            cpsc210.sortSections();
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void testSectionCreation() {
        assertEquals("CPSC 210 L1U", cpsc210.getSectionsMap().get("CPSC 210 L1U").getSection());
        assertEquals("18:00", cpsc210.getSectionsMap().get("CPSC 210 L1U").getStart().toString());
        assertTrue(cpsc210.getTerms().contains("1"));
        assertEquals(4, cpsc210.getCredit());

    }

    @Test
    public void testSortedSections() {
        assertEquals("CPSC 210 102", cpsc210.getSortSections().get("AFTERNOON").get(0)
                .getSection());
    }

    @Test
    public void testMapSectionsRequired() {
        Course japn200 = null;
        try {
            reader = new JsonReader("./data/2020W/JAPN/JAPN 200.json");
            japn200 = reader.readCourse("2020W", preferences);
            japn200.sortSections();
        } catch (IOException e) {
            fail();
        }
        assertEquals("JAPN 200 007", japn200.getSortSections().get("N/A").get(0).getSection());
        assertTrue(japn200.getActivitiesList().contains("Required"));
        assertEquals("JAPN 200", japn200.getCourseName());
    }

    @Test
    public void testEquals() {
        Course japn200 = null;
        Course biol200 = null;
        Course biol200v2 = null;
        try {
            reader = new JsonReader("./data/2020W/JAPN/JAPN 200.json");
            japn200 = reader.readCourse("2020W", preferences);
            japn200.sortSections();
            reader = new JsonReader("./data/2020W/BIOL/BIOL 200.json");
            biol200 = reader.readCourse("2020W", preferences);
            biol200.sortSections();
            reader = new JsonReader("./data/2020W/BIOL/BIOL 200.json");
            biol200v2 = reader.readCourse("2020W", preferences);
            biol200v2.sortSections();
        } catch (IOException e) {
            fail();
        }

        assertEquals(biol200, biol200v2);
        assertEquals(biol200, biol200);
        biol200v2 = null;
        assertNotEquals(biol200v2, biol200);
        assertNotEquals(biol200, japn200);
        assertNotEquals(japn200.getSectionsMap().get("JAPN 200 001"), biol200);
    }
}
