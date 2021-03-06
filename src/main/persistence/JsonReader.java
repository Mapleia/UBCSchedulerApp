package persistence;

import exceptions.NoCourseFound;
import model.Course;
import model.Overview;
import model.Section;
import model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import org.json.*;

// Represents a reader that reads users and courses from JSON data stored in file.
// Referenced https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo.git
// The demo is an app that works with a "workroom" and "thingy"/"thingies".
// All methods have references to the JsonReader, but it was been reworked to work with Users and Courses.
public class JsonReader {
    private final String source;

    // EFFECTS: constructs reader to read from source file
    // source requires: full path
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
        HashSet<String> courses = new HashSet<>();
        for (int i = 0; i < arr.length(); i++) {
            courses.add(arr.getString(i));
        }

        JSONArray prefArr = jsonObject.getJSONArray("TimePref");
        LinkedList<String> preferences = new LinkedList<>();
        for (int j = 0; j < prefArr.length(); j++) {
            preferences.add(prefArr.getString(j));
        }

        User user = new User(term);
        user.setPreferences(preferences);
        addSectionsFromTimeTable(user, schedule, term, preferences);
        user.addCourseSet(courses);
        user.addCourses();
        return user;
    }

    // MODIFIES: this
    // EFFECTS: parses sections from User File "Schedule" and adds them to user.
    public void addSectionsFromTimeTable(User user, JSONObject schedule, String termYear, List<String> preferencesArr)
            throws IOException {
        Set<String> terms = schedule.keySet();

        for (String term : terms) {

            List<Object> perTerm = schedule.getJSONArray(term).toList();
            HashSet<Section> list = new HashSet<>();
            for (int i = 0; i < perTerm.size(); i++) {
                JSONObject sectionJson = schedule.getJSONArray(term).getJSONObject(i);
                String p = "./data/" + termYear + "/" + sectionJson.getString("course").split(" ")[0].trim()
                        + "/" + sectionJson.getString("course") + ".json";
                JsonReader reader = new JsonReader(p);

                Section section = reader.readSection(termYear, sectionJson.getString("section"), preferencesArr);
                list.add(section);
            }

            user.getFinalTimeTable().put(term, list);
        }
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

    // ======================= FOR OVERVIEW FILE ======================================================================
    // EFFECTS: reads overview file and returns it;
    // throws IOException if an error occurs reading data from file

    public Overview readOverview() throws IOException {
        try {
            String jsonData = readFile();                     // IOException
            JSONObject jsonObject = new JSONObject(jsonData); // JSONException
            return parseOverview(jsonObject);                 // JSONException
        } catch (Exception e) {
            throw new IOException();
        }
    }

    private Overview parseOverview(JSONObject jsonObject) {
        JSONArray departments = jsonObject.getJSONArray("DEPARTMENT");
        JSONObject courses = jsonObject.getJSONObject("COURSES");
        ArrayList<String> depArr = new ArrayList<>();
        HashMap<String, ArrayList<String>> courseMap = new HashMap<>();

        for (int i = 0; i < departments.length(); i++) {
            depArr.add(departments.getString(i));
        }

        Iterator<String> itr = courses.keys();

        while (itr.hasNext()) {
            String key = itr.next();
            ArrayList<String> courseList = new ArrayList<>();
            for (int j = 0; j < courses.getJSONArray(key).length(); j++) {
                courseList.add(courses.getJSONArray(key).getString(j));
            }
            courseMap.put(key, courseList);
        }

        return new Overview(depArr, courseMap);
    }

}
