package persistence;

import exceptions.NoCourseFound;
import model.Course;
import model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.json.*;

// Represents a reader that reads users and courses from JSON data stored in file.
// Referenced https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo.git
// The demo is an app that works with a "workroom" and "thingy"/"thingies".
// All methods have references to the JsonReader, but it was been reworked to work with Users and Courses.
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads source file as string and returns it
    public String readFile() throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }

        return contentBuilder.toString();
    }

    // ====================== FOR USER OBJECT =========================================================================

    // EFFECTS: reads user from file and returns it;
    // throws IOException if an error occurs reading data from file
    public User readUser() throws IOException {
        try {
            String jsonData = readFile();                     // IOException
            JSONObject jsonObject = new JSONObject(jsonData); // JSONException
            return parseUser(jsonObject);                     // JSONException, NoCourseFound
        } catch (Exception e) {
            throw new IOException();
        }
    }

    // EFFECTS: parses User from JSON object and returns it
    // Throws NoCourseFound exception from addCourses
    // Throws JSONException exception if getString encounters an error.
    private User parseUser(JSONObject jsonObject) throws JSONException, NoCourseFound {
        String term = jsonObject.getString("Term");
        JSONArray arr = jsonObject.getJSONArray("Course List");
        JSONObject schedule = jsonObject.getJSONObject("Schedule");
        List<String> courses = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            String str = arr.getString(i);
            courses.add(str);
        }

        User user = new User(term);
        user.addSectionsToUser(schedule);
        user.addCourses(courses);
        return user;
    }

    // ======================= FOR COURSE OBJECT ======================================================================

    // REQUIRES: Sections in JSON files to be formatted correctly.
    // EFFECTS: reads course from file and returns it;
    // throws IOException if an error occurs reading data from file.
    public Course readCourse(String term, List<String> preferences) throws IOException {
        try {
            String jsonData = readFile();
            JSONObject jsonObject = new JSONObject(jsonData);
            return parseCourse(jsonObject, term, preferences);
        } catch (Exception e) {
            throw new IOException();
        }
    }

    // EFFECTS: parses Course from JSON object and returns it
    private Course parseCourse(JSONObject jsonObject, String term, List<String> preferences) throws JSONException {
        String courseName = jsonObject.getString("course_name");
        JSONArray sections = jsonObject.getJSONArray("sections");
        JSONArray terms = jsonObject.getJSONArray("terms");
        int credit = jsonObject.getInt("credits");

        List<String> termsList = new ArrayList<>();
        for (int i = 0; i < terms.length(); i++) {
            termsList.add(terms.getString(i));
        }
        JSONArray activitiesJson = jsonObject.getJSONArray("activities");
        List<String> activities = new ArrayList<>();
        for (int j = 0; j < activitiesJson.length(); j++) {
            activities.add(activitiesJson.getString(j));
        }
        try {
            return new Course(courseName, termsList, activities, sections, term, credit, preferences);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JSONException("Error in parsing " + courseName);
        }
    }

}
