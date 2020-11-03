package persistence;

import model.User;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class JsonWriterTest {

    @Test
    void testWriterInvalidFile() {
        try {
            User user = new User();
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
            User user = new User();
            user.setTerm("2020W");
            JsonWriter writer = new JsonWriter("testWriterEmptyUser");
            writer.open();
            writer.write(user);
            writer.close();

            JsonReader reader = new JsonReader("./data/timetables/testWriterEmptyUser.json");
            user = reader.readUser();
            assertEquals("2020W", user.getTerm());
            assertTrue(new HashMap<>().equals(user.getFinalTimeTable()));
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralUser() {
        try {
            User user = new User();
            user.setTerm("2020W");
            JsonWriter writer = new JsonWriter("testUserWithSections");

            JsonReader jsonReader = new JsonReader("./data/timetables/testUserWithSchedule.json");
            JSONObject jsonObject = new JSONObject(jsonReader.readFile());
            user.addSectionsToUser(jsonObject.getJSONObject("Schedule"));

            writer.open();
            writer.write(user);
            writer.close();

            JsonReader reader = new JsonReader("./data/timetables/testUserWithSections.json");
            user = reader.readUser();
            assertEquals("2020W", user.getTerm());
            assertEquals("BIOL 112 101",
                    user.getFinalTimeTable().get("Term1").get(0).getSection());
            assertEquals("BIOL 112 T01",
                    user.getFinalTimeTable().get("Term1").get(1).getSection());
            assertEquals("CPSC 210 202",
                    user.getFinalTimeTable().get("Term2").get(0).getSection());
            assertEquals("CPSC 210 L2H",
                    user.getFinalTimeTable().get("Term2").get(1).getSection());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}
