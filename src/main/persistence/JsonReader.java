package persistence;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import model.TimeTable;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

// referenced JsonSerializationDemo.
public class JsonReader {

    public static JSONObject findCourseFile(String code, String number, TimeTable table)
            throws IOException {
        String path;

        if (table.isWinter) {
            path = "data\\" + table.yearFall + "W" + "\\" + code + "\\" + code + " " + number + ".json";
        } else {
            path = "data\\" + table.yearSummer + "S" + "\\" + code + "\\" + code + " " + number + ".json";
        }

        File file = new File(path);
        String jsonCourseString = Files.asCharSource(file, Charsets.UTF_8).read();
        return new JSONObject(jsonCourseString);
    }

    public static JSONObject findSavedFile(String fileName) throws IOException {
        String path = "./data/timetables/" + fileName + ".json";
        File file = new File(path);
        String jsonCourseString = Files.asCharSource(file, Charsets.UTF_8).read();
        return new JSONObject(jsonCourseString);
    }
}
