package persistence;

import model.ScheduleMaker;
import model.TimeTable;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonReaderTest {
    TimeTable timeTable;
    String[] timeTableTimeArr;
    @BeforeEach
    public void setup() {
        timeTableTimeArr =  new String[]{"Afternoon", "Evening", "Morning"};
        timeTable = new TimeTable(2020, true, timeTableTimeArr);
    }


    @Test
    public void testFindCourseFile() {
        try {
            assertEquals("CPSC 210", JsonReader.findCourseFile("CPSC", "210", timeTable)
                    .getString("course_name"));
        } catch (Exception e) {
            fail();
            e.printStackTrace();
        }
    }


    @Test
    public void testFindCourseFileException() {
        timeTable = new TimeTable(2020, false, timeTableTimeArr);
        try {
            assertEquals("CPSC 210", JsonReader.findCourseFile("CPSC", "210", timeTable)
                    .getString("course_name"));
            fail();
        } catch (Exception e) {
            System.out.println("Success!");
        }
    }

    @Test
    public void testReadFile() {
        try {
            timeTable.addCourse("CPSC 210");
            timeTable.addCourse("BIOL 112");
            timeTable.addCourse("BIOL 200");
        } catch (Exception e) {
            fail();
        }


        ScheduleMaker scheduleMaker = new ScheduleMaker(timeTable, "user");

        try {
            JsonWriter.saveFile(scheduleMaker, "FIND_ME");
            JSONObject obj = JsonReader.findSavedFile("FIND_ME");
            assertEquals("user", obj.getString("username"));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testReadFileException() {

        ScheduleMaker scheduleMaker = new ScheduleMaker(timeTable, "user");

        try {
            JsonWriter.saveFile(scheduleMaker, "FIND_ME2");
            JsonReader.findSavedFile("FIND_ME4");
            fail();
        } catch (Exception e) {
            System.out.println("Success!");

        }


    }
}
