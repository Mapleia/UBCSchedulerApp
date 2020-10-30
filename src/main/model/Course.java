package model;

import java.io.IOException;
import java.util.*;

import exceptions.NoCourseFound;
import exceptions.NoSectionFound;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonReader;

// Represents the general course offered by the institution, and has a list of sections.
public class Course implements Comparable<Course> {
    private final String subjectCode;
    private final String courseNum;
    private final JSONObject jsonSection;

    private final HashMap<String, Section> allSections;
    private final Set<String> allKeys;
    private final HashMap<String, HashMap<String, HashSet<Section>>> allActivities;
    private final TimeTable timeTable;

    private boolean hasTerm1 = false;
    private boolean hasTerm2 = false;

    // Constructors
    public Course(String subjectCode, String courseNum, JSONObject json, TimeTable timeTable) {
        this.subjectCode = subjectCode;
        this.courseNum = courseNum;
        this.jsonSection = json;
        this.timeTable = timeTable;

        allSections = new HashMap<>();
        allActivities = new HashMap<>();
        allKeys = new HashSet<>();

        addAllSections();  // allSections
    }

    // getters
    public String getSubjectCode() {
        return subjectCode;
    }

    public String getCourseNum() {
        return courseNum;
    }

    public Set<String> getAllKeys() {
        return allKeys;
    }

    public HashMap<String, HashMap<String, HashSet<Section>>> getAllActivities() {
        return allActivities;
    }

    public boolean isHasTerm1() {
        return hasTerm1;
    }

    public boolean isHasTerm2() {
        return hasTerm2;
    }

    public boolean contains(String section) {
        return allSections.containsKey(section);
    }

    public int size() {
        return allSections.size();
    }

    public long preferenceSize(String time) {
        return allSections.values().stream().filter(section -> section.getTimeSlot().equals(time)).count();
    }

    public Section get(String str) throws NoSectionFound {
        Section section = allSections.get(str);
        if (section == null) {
            throw new NoSectionFound();
        } else {
            return section;
        }
    }

    public static Course createCourse(String c, String n, TimeTable t)
            throws NoCourseFound, IOException {
        JSONObject obj = JsonReader.findCourseFile(c, n, t);
        return parseFromJsonObject(obj, t);
    }

    private static Course parseFromJsonObject(JSONObject obj, TimeTable table) {
        return new Course(obj.getString("subject_code"),
                obj.getString("course_number"),
                obj.getJSONObject("sections"),
                table);
    }

    // MODIFIES: this
    // EFFECT: Create Section Java Object for every section from JSON data. Makes list based on available activities.
    private void addAllSections() {
        JSONArray allNames = jsonSection.names();

        for (int i = 0; i < allNames.length(); i++) {

            String name = allNames.getString(i);
            JSONObject obj = jsonSection.getJSONObject(name);

            Section sec = Section.createSection(obj, timeTable);
            allKeys.add(sec.getActivity());
            containsTerms(sec);
            if (!sec.getStatus().equalsIgnoreCase("STT")) {
                allSections.put(sec.getSection(), sec);
                mapActivity(sec);
            }


        }
    }

    // MODIFIES: this
    // EFFECT: sets fields to true (hasTerm1 & hasTerm2) if the term is 1 or 2 or both.
    private void containsTerms(Section section) {
        if (section.getTerm().trim().contains("1")) {
            hasTerm1 = true;
        }
        if (section.getTerm().trim().contains("2")) {
            hasTerm2 = true;
        }
    }

    // MODIFIES: this
    // EFFECT: Adds to lists divided by activity type and time preference.
    private void mapActivity(Section section) {
        String time;
        if (section.isCrucialFieldsBlank()) {
            time = timeTable.primaryTimePref;
        } else {
            time = section.getTimeSlot();
        }

        String typeName = section.getActivity();

        // ======== SETUP ========
        //      TYPE:
        //          PRIMARY:   ();
        //          SECONDARY: ();
        //          TERTIARY:  ();
        HashMap<String, HashSet<Section>> setOfActivity = new HashMap<>();
        setOfActivity.putIfAbsent(timeTable.primaryTimePref, new HashSet<>());
        setOfActivity.putIfAbsent(timeTable.secondaryTimePref, new HashSet<>());
        setOfActivity.putIfAbsent(timeTable.tertiaryTimePref, new HashSet<>());

        allActivities.putIfAbsent(typeName, setOfActivity);

        if (time.equalsIgnoreCase(timeTable.primaryTimePref)) {
            allActivities.get(typeName).get(timeTable.primaryTimePref).add(section);

        } else if (time.equalsIgnoreCase(timeTable.secondaryTimePref)) {
            allActivities.get(typeName).get(timeTable.secondaryTimePref).add(section);

        } else {
            allActivities.get(typeName).get(timeTable.tertiaryTimePref).add(section);
        }
    }

    // REQUIRES: Another valid course.
    // EFFECT: A compareTo used to sort list of courses.
    @Override
    public int compareTo(Course o) {
        return Long.compare(preferenceSize(timeTable.primaryTimePref), o.preferenceSize(timeTable.primaryTimePref));
    }

    @Override
    public boolean equals(Object course) {
        // referenced: https://stackoverflow.com/questions/24957813/indexof-will-not-find-a-custom-object-type
        if (!(course instanceof Course)) {
            return false;
        }
        Course c = (Course) course;
        return (this.subjectCode.equals(c.subjectCode) && this.courseNum.equals(c.courseNum));
    }

    @Override
    public int hashCode() {
        // referenced: https://www.baeldung.com/java-hashcode
        return subjectCode.hashCode() * courseNum.hashCode();
    }
}