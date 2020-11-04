package model;

import exceptions.NoCourseFound;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private User user;
    List<String> preferences = new ArrayList<>();


    @BeforeEach
    public void setup() {
        user = new User("2020W");
        preferences.add("Afternoon");
        preferences.add("Evening");
        preferences.add("Morning");
        user.setPreferences(preferences);
    }

    @Test
    public void testToJson() {
        List<String> courses = new ArrayList<>();
        courses.add("CPSC 210");
        courses.add("BIOL 112");
        try {
            user.addCourses(courses);
        } catch (Exception e) {

            fail();
        }
        JsonReader reader = new JsonReader("./data/timetables/testUserWithCourses.json");
        try {
            JSONObject obj = new JSONObject(reader.readFile());
            assertTrue(obj.similar(user.toJson()));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreateTimeTable() {
        assertFalse(user.createTimeTable());
        assertTrue(new HashMap<String, HashMap<String, Section>>().equals(user.getFinalTimeTable()));
    }

    @Test
    public void testErrorLog() {
        assertTrue(new ArrayList<>().equals(user.getErrorLog()));
    }

    @Test
    public void testAddSectionsToUser() {
        JsonReader jsonReader = new JsonReader("./data/timetables/testUserWithSchedule.json");
        try {
            user = jsonReader.readUser();
        } catch (Exception e) {
            fail();
        }

        assertEquals("BIOL 112 101",
                user.getFinalTimeTable().get("Term1").get(0).getSection());
        assertEquals("BIOL 112 T01",
                user.getFinalTimeTable().get("Term1").get(1).getSection());
        assertEquals("CPSC 210 202",
                user.getFinalTimeTable().get("Term2").get(0).getSection());
        assertEquals("CPSC 210 L2H",
                user.getFinalTimeTable().get("Term2").get(1).getSection());
        List<String> list = new ArrayList<>();
        list.add("CPSC 210");
        list.add("BIOL 112");
        assertTrue(list.equals(user.getCourseList()));
        assertEquals("2020W", user.getTerm());
    }

    @Test
    public void testAddCoursesException() {
        List<String> list = new ArrayList<>();
        list.add("CPPS 110");
        list.add("BIOI 312");


        try {
            user.addCourses(list);
            fail();
        } catch (NoCourseFound noCourseFound) {
            noCourseFound.printClasses();
            System.out.println("It's fine!");
        }
    }

    @Test
    public void testToJsonWithSchedule() {
        JsonReader jsonReader = new JsonReader("./data/timetables/testUserWithSchedule.json");
        try {
            JSONObject jsonObject = new JSONObject(jsonReader.readFile());
            user.addSectionsToUser(jsonObject.getJSONObject("Schedule"));

        } catch (Exception e) {
            fail();
        }
        try {
            user.toJson();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testSortCourses() {
        List<String> coursesToAdd = new ArrayList<>();
        coursesToAdd.add("BIOL 155"); // TERM 1-2
        coursesToAdd.add("ASIA 100"); // TERM 1
        coursesToAdd.add("BIOL 112"); // TERM 1 AND 2
        coursesToAdd.add("ASIA 101"); // TERM 2

        try {
            user.addCourses(coursesToAdd);
        } catch (Exception e) {
            fail();
        }

        assertFalse(user.createTimeTable());

    }

    @Test
    public void testSortCoursesALot() {
        List<String> coursesToAdd = new ArrayList<>();
        coursesToAdd.add("BIOL 155");
        coursesToAdd.add("BIOL 200");
        coursesToAdd.add("BIOL 140");
        coursesToAdd.add("CHEM 233");
        coursesToAdd.add("ENGL 110");
        coursesToAdd.add("CPSC 210");

        try {
            user.addCourses(coursesToAdd);
        } catch (Exception e) {
            fail();
        }

        assertFalse(user.createTimeTable());

    }
}
