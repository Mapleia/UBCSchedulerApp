package model;

import exceptions.NoCourseFound;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
    }

    @Test
    public void testToJson() {
        List<String> courses = new ArrayList<>();
        courses.add("CPSC 210");
        courses.add("BIOL 112");
        user.setTerm("2020W");
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
        assertTrue(Arrays.equals(new String[]{}, user.getErrorLog()));
    }

    @Test
    public void testAddSectionsToUser() {
        JsonReader jsonReader = new JsonReader("./data/timetables/testUserWithSchedule.json");
        try {
            JSONObject jsonObject = new JSONObject(jsonReader.readFile());
            user.addSectionsToUser(jsonObject.getJSONObject("Schedule"));

        } catch (Exception e) {
            fail();
        }

        assertEquals("BIOL 112 101",
                user.getFinalTimeTable().get("Term1").get("BIOL 112 101").getSection());
        assertEquals("BIOL 112 T01",
                user.getFinalTimeTable().get("Term1").get("BIOL 112 T01").getSection());
        assertEquals("CPSC 210 202",
                user.getFinalTimeTable().get("Term2").get("CPSC 210 202").getSection());
        assertEquals("CPSC 210 L2H",
                user.getFinalTimeTable().get("Term2").get("CPSC 210 L2H").getSection());
    }

    @Test
    public void testAddCoursesException() {
        List<String> list = new ArrayList<>();
        list.add("CPPS 110");
        list.add("BIOI 312");
        try {
            user.setTerm("2020W");
            user.addCourses(list);
            fail();
        } catch (NoCourseFound noCourseFound) {
            System.out.println("It's fine!");
        }
    }

    @Test
    public void testToJsonWithSchedule() {
        JsonReader jsonReader = new JsonReader("./data/timetables/testUserWithSchedule.json");
        try {
            JSONObject jsonObject = new JSONObject(jsonReader.readFile());
            user.setTerm("2020W");
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
}
