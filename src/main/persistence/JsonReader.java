package persistence;

import exceptions.NoCourseFound;
import model.Course;
import model.Section;
import model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
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
    private User parseUser(JSONObject jsonObject) throws JSONException, NoCourseFound, IOException {
        String term = jsonObject.getString("Term");
        JSONArray arr = jsonObject.getJSONArray("Course List");
        JSONObject schedule = jsonObject.getJSONObject("Schedule");
        List<String> courses = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            courses.add(arr.getString(i));
        }

        JSONArray prefArr = jsonObject.getJSONArray("Preferences");
        List<String> preferences = new LinkedList<>();
        for (int j = 0; j < prefArr.length(); j++) {
            preferences.add(prefArr.getString(j));
        }

        User user = new User(term);
        user.setPreferences(preferences);
        user.addSectionsFromTimeTable(schedule);
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
    private Course parseCourse(JSONObject jsonObject, String thisTerm, List<String> preferences) throws JSONException {
        String courseName = jsonObject.getString("course_name");
        JSONArray sections = jsonObject.getJSONArray("sections");
        JSONArray terms = jsonObject.getJSONArray("terms");
        JSONArray activities = jsonObject.getJSONArray("activities");
        int credit = jsonObject.getInt("credits");

        List<Section> sectionList = new ArrayList<>();
        for (int k = 0; k < sections.length(); k++) {
            Section sec = parseSection(sections.getJSONObject(k), thisTerm);
            if (!sec.getStatus().equals("STT")) {
                sectionList.add(sec);
            }
        }

        List<String> termsList = new ArrayList<>();
        for (int i = 0; i < terms.length(); i++) {
            termsList.add(terms.getString(i));
        }

        List<String> activitiesList = new ArrayList<>();
        for (int j = 0; j < activities.length(); j++) {
            activitiesList.add(activities.getString(j));
        }

        return new Course(courseName, termsList, activitiesList, sectionList, credit, preferences);

    }

    // ======================= FOR SECTION OBJECT =====================================================================

    // REQUIRES: Course to exist.
    // EFFECTS: Reads section from file and returns it;
    // throws IOException if an error occurs reading data from file.
    public Section readSection(String term, String section, List<String> preferences) throws IOException {
        try {
            String jsonData = readFile();
            JSONObject jsonObject = new JSONObject(jsonData);
            Course course = parseCourse(jsonObject, term, preferences);
            if (course.getSectionsMap().get(section) == null) {
                throw new IOException();
            } else {
                return course.getSectionsMap().get(section);
            }
        } catch (Exception e) {
            throw new IOException();
        }
    }

    // EFFECT: parses section from JSON object.
    private Section parseSection(JSONObject jsonObject, String thisTerm) {
        String status = jsonObject.getString("status");
        String section = jsonObject.getString("section");
        String activity = jsonObject.getString("activity");
        String term = jsonObject.getString("term");
        String course = jsonObject.getString("subject_code") + " " + jsonObject.getString("course_number");

        LocalTime start = null;
        LocalTime end = null;

        List<String> days = new ArrayList<>();
        for (int i = 0; i < jsonObject.getJSONArray("days").length(); i++) {
            days.add(jsonObject.getJSONArray("days").getString(i));
        }

        if (jsonObject.getString("start").trim().equals("") && !activity.equals("Waiting List")) {
            activity = "Required";
        } else if (!jsonObject.getString("start").trim().equals("")) {
            start = LocalTime.parse(jsonObject.getString("start"), DateTimeFormatter.ofPattern("HH:mm"));
            end = LocalTime.parse(jsonObject.getString("end"), DateTimeFormatter.ofPattern("HH:mm"))
                    .minusMinutes(10);
        }

        return new Section(status, section, course, activity, term, days,
                            start, end, thisTerm);
    }


}
