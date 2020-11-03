package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;

public class Course {
    private final JSONArray sections;
    private String courseName;
    private HashMap<String, Section> sectionsMap;

    // constructor for course
    public Course(String courseName, JSONArray sections) {
        this.courseName = courseName;
        this.sections = sections;
        init();
    }

    // initializes all of the fields when necessary.
    private void init()  {
        sectionsMap = new HashMap<>();
        addSections();
    }

    public HashMap<String, Section> getSectionsMap() {
        return sectionsMap;
    }

    // MODIFIES: course
    // EFFECTS: parses sections from JSON object and adds them to the course.
    private void addSections() {
        for (Object o : sections) {
            JSONObject json = (JSONObject) o;
            Section section = new Section(json);
            sectionsMap.put(section.getSection(), section);
        }
    }
}
