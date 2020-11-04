package persistence;

import model.Course;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonReaderTest {
    private JsonReader jsonReader;
    public List<String> preferences = new ArrayList<>();

    @BeforeEach
    public void setup() {
        jsonReader = new JsonReader("./data/noSuchFile.json");
        preferences.add("Afternoon");
        preferences.add("Evening");
        preferences.add("Morning");
    }

    @Test
    public void testNonExistentFileUser() {
        try {
            User user = jsonReader.readUser();

            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    public void testNonExistentFileCourse() {
        try {
            Course course = jsonReader.readCourse("2020W", preferences);

            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyUser() {
        JsonReader reader = new JsonReader("./data/timetables/testUserWithCourses.json");
        try {
            User user = reader.readUser();
            assertEquals("CPSC 210", user.getCourseList().get(0));
            assertTrue(new HashMap<>().equals(user.getFinalTimeTable()));
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}
