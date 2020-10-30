package persistence;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import exceptions.NoCourseFound;
import model.TimeTable;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

// referenced JsonSerializationDemo.
public class JsonReader {

    public static JSONObject findCourseFile(String code, String number, TimeTable table)
            throws NoCourseFound, IOException {
        String path;

        if (table.isWinter) {
            path = "data\\" + table.yearFall + "W" + "\\" + code + "\\" + code + " " + number + ".json";
        } else {
            path = "data\\" + table.yearSummer + "S" + "\\" + code + "\\" + code + " " + number + ".json";
        }

        File file = new File(path);
        if (file.exists()) {
            String jsonCourseString = Files.asCharSource(file, Charsets.UTF_8).read();
            return new JSONObject(jsonCourseString);
        } else {
            throw new NoCourseFound(code, number);
        }
    }

    public static JSONObject findSavedFile(String fileName) throws IOException {
        String path = "./data/timetables/" + fileName + ".json";
        File file = new File(path);
        if (file.exists()) {
            String jsonCourseString = Files.asCharSource(file, Charsets.UTF_8).read();
            return new JSONObject(jsonCourseString);
        } else {
            throw new NoSuchFileException(path);
        }
    }
}
