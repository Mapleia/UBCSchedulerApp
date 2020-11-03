package model;

import exceptions.NoCourseFound;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import persistence.JsonReader;

import java.util.*;

public class User {
    private String term = null;
    private List<String> courseList;
    private Set<Course> courseSet;
    private HashMap<String, ArrayList<Section>> finalTimeTable;
    private String[] errorLog; //strings of missed sections

    // constructs user and initializes it.
    public User() {
        init();
    }

    // initializes all of the fields.
    private void init() {
        courseList = new ArrayList<>();
        courseSet = new HashSet<>();
        errorLog = new String[]{};
        finalTimeTable = new HashMap<>();
    }

    // setter for term
    public void setTerm(String term) {
        this.term = term;
    }

    // getter for finalTimeTable
    public HashMap<String, ArrayList<Section>> getFinalTimeTable() {
        return finalTimeTable;
    }

    // getter for errorLog.
    public String[] getErrorLog() {
        return errorLog;
    }

    //TODO: finish this stub.
    public boolean createTimeTable() {
        return false;
    }

    // EFFECTS: parses User object from Java object to json and returns a JSONObject.
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("Course List", new JSONArray(courseList));
        json.put("Term", term);

        JSONObject schedule = new JSONObject();
        for (String term : finalTimeTable.keySet()) {
            JSONArray perTerm = new JSONArray();
            for (Section section : finalTimeTable.get(term)) {
                perTerm.put(section.toJson());
            }
            schedule.put(term, perTerm);
        }
        json.put("Schedule", schedule);

        return json;
    }

    // MODIFIES: this
    // EFFECTS: parses sections from User File "Schedule" and adds them to user.
    public void addSectionsToUser(JSONObject schedule) {
        Set<String> terms = schedule.keySet();

        for (String term : terms) {

            List<Object> perTerm = schedule.getJSONArray(term).toList();
            ArrayList<Section> list = new ArrayList<>();
            for (int i = 0; i < perTerm.size(); i++) {
                Section section = new Section(schedule.getJSONArray(term).getJSONObject(i));
                list.add(section);
            }

            finalTimeTable.put(term, list);
        }
    }

    // MODIFIES: this
    // EFFECTS: Loops through courseList and adds courses.
    // throws NoCourseFound if it encounters an Exception during the loop.
    public void addCourses(List<String> courses) throws NoCourseFound {
        NoCourseFound error = new NoCourseFound();

        for (String course : courses) {
            boolean isSuccessful = addCourse(course);

            if (!isSuccessful) {
                error.addClasses(course);
            }
        }

        if (error.size() > 0) {
            throw error;
        }
    }

    // MODIFIES: this
    // EFFECTS: adds to courseList and courseSet if a valid course is made and returns true.
    // Returns false if finds an exception / cannot find course.
    private boolean addCourse(String input) {
        String[] split = input.split(" ");

        String path = "./data/" + term + "/" + split[0] + "/" + input + ".json";

        try {
            JsonReader jsonReader = new JsonReader(path);
            Course course = jsonReader.readCourse();
            courseList.add(input);
            courseSet.add(course);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getCourseList() {
        return courseList;
    }

    public String getTerm() {
        return term;
    }
}
