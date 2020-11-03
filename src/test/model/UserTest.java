package model;

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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Schedule", "")
    }
}
