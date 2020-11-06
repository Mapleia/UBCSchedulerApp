package persistence;

import model.User;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonWriterTest {

    @Test
    void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./data/timetables/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyUser() {
        try {
            User user = new User("2020W");
            user.setPreferences(Arrays.asList("AFTERNOON", "MORNING", "EVENING", "N/A"));
            JsonWriter writer = new JsonWriter("testWriterEmptyUser");
            writer.open();
            writer.write(user);
            writer.close();

            JsonReader reader = new JsonReader("./data/timetables/testWriterEmptyUser.json");
            user = reader.readUser();
            assertEquals("2020W", user.getTerm());
            assertEquals(user.getFinalTimeTable(), new HashMap<>());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralUser() {
        User user = new User("2020W");
        try {
            List<String> courses = Arrays.asList("CPSC 210", "CPSC 110", "BIOL 112");
            user.setPreferences(Arrays.asList("AFTERNOON", "EVENING", "MORNING"));
            user.addCourses(courses);
            user.createTimeTable();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        try {
            JsonWriter writer = new JsonWriter("testUserWithSections");

            writer.open();
            writer.write(user);
            writer.close();

            JsonReader reader = new JsonReader("./data/timetables/testUserWithSections.json");
            user = reader.readUser();
            assertEquals("2020W", user.getTerm());
            assertEquals("CPSC 110 101",
                    user.getFinalTimeTable().get("1").get(0).getSection());
            assertEquals("CPSC 110 L12",
                    user.getFinalTimeTable().get("1").get(1).getSection());
            assertEquals("CPSC 110 101",
                    user.getFinalTimeTable().get("1").get(0).getSection());
            assertEquals("CPSC 210 L2A",
                    user.getFinalTimeTable().get("2").get(1).getSection());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception should not have been thrown");
        }
    }
}
