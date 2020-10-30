package persistence;

import model.ScheduleMaker;
import model.TimeTable;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class JsonWriterTest {
    TimeTable timeTable;
    String[] timeTableTimeArr;
    @BeforeEach
    public void setup() {
        timeTableTimeArr =  new String[]{"Afternoon", "Evening", "Morning"};
        timeTable = new TimeTable(2020, 0, timeTableTimeArr);
    }


    @Test
    public void testWriteFile() {
        try {
            timeTable.addCourse("CPSC 210");
            timeTable.addCourse("BIOL 112");
            timeTable.addCourse("BIOL 200");
        } catch (Exception e) {
            fail();
        }
        ScheduleMaker scheduleMaker = new ScheduleMaker(timeTable, "user");

        try {
            JsonWriter.saveFile(scheduleMaker, "FIND_ME20");
            JSONObject obj = JsonReader.findSavedFile("FIND_ME20");
            assertEquals("user", obj.getString("username"));
            assertEquals("[\"BIOL 112\",\"BIOL 200\",\"CPSC 210\"]", obj.getJSONArray("courses").toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testWriteFileFail() {
        try {
            timeTable.addCourse("CPSC 210");
            timeTable.addCourse("BIOL 112");
            timeTable.addCourse("BIOL 200");
        } catch (Exception e) {
            fail();
        }
        ScheduleMaker scheduleMaker = new ScheduleMaker(timeTable, "user");

        try {
            JsonWriter.saveFile(scheduleMaker, "TRYME");
            fail();
        } catch (Exception e) {
        }
    }
}
