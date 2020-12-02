package model;

import exceptions.NoCourseFound;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import persistence.JsonReader;
import persistence.Writable;

import java.util.*;
import java.util.stream.Collectors;

// Represents a user of the program, makes their timetable and stores information about them.
public class User implements Writable {
    public static final String[] TERMS = new String[] {"1", "2", "1-2"};
    private String termYear;
    private Set<String> courseNames;
    private Set<Course> courseSet;
    private HashMap<String, HashSet<Section>> finalTimeTable;
    private List<String> errorLog; //strings of missed sections
    private LinkedList<String> preferencesArr;

    private HashMap<String, TreeSet<Course>> result;

    // constructs user and initializes it.
    public User(String termYear) {
        this.termYear = termYear;
        init();
    }

    // initializes all of the fields.
    private void init() {
        courseNames = new HashSet<>();
        courseSet = new HashSet<>();
        errorLog = new ArrayList<>();
        finalTimeTable = new HashMap<>();

        result = new HashMap<>();
        result.put("1", new TreeSet<>());
        result.put("2", new TreeSet<>());
        result.put("1-2", new TreeSet<>());
    }

    // getters & setters ==============================================================================================

    public HashMap<String, HashSet<Section>> getFinalTimeTable() {
        return finalTimeTable;
    }

    public Set<Course> getCourseSet() {
        return courseSet;
    }

    public Set<String> getCourseNames() {
        return courseNames;
    }

    public List<String> getErrorLog() {
        return errorLog;
    }

    public HashMap<String, TreeSet<Course>> getResult() {
        return result;
    }

    public void setPreferences(LinkedList<String> preferencesArr) {
        this.preferencesArr = preferencesArr;
    }

    public void addCourseSet(Set<String> set) {
        this.courseNames.addAll(set);
    }

    public void setYear(String year) {
        termYear = year;
    }

    public LinkedList<String> getTimePref() {
        return preferencesArr;
    }

    // SETTER: sets to blank
    public void clearTimetable() {
        finalTimeTable = new HashMap<>();
        errorLog = new ArrayList<>();

        result = new HashMap<>();
        result.put("1", new TreeSet<>());
        result.put("2", new TreeSet<>());
        result.put("1-2", new TreeSet<>());
    }
    // ================================================================================================================

    // MODIFIES: this
    // EFFECTS: Loops through courseList and adds (Course) courses.
    // throws NoCourseFound if it encounters an not successful addition of course during the loop.
    public void addCourses() throws NoCourseFound {
        NoCourseFound error = new NoCourseFound();
        courseSet = new HashSet<>();
        for (String course : courseNames) {
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
    // Returns false if finds an exception / cannot find the course.
    // MADE SO THAT A LIST OF ERROR COURSE CAN BE MADE
    public boolean addCourse(String input) {
        String[] split = input.split(" ");
        String path = "./data/" + termYear + "/" + split[0].toUpperCase() + "/" + input.toUpperCase() + ".json";
        try {
            JsonReader jsonReader = new JsonReader(path);
            Course course = jsonReader.readCourse(termYear, preferencesArr);
            courseSet.add(course);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // EFFECT: Removes a list of courses from the user.
    public void removeCourses(List<String> courses) throws NoCourseFound {
        NoCourseFound error = new NoCourseFound();

        for (String course : courses) {
            boolean isSuccessful = removeCourse(course);

            if (!isSuccessful) {
                error.addClasses(course);
            }
        }

        if (error.size() > 0) {
            throw error;
        }
    }

    // EFFECT: Removes a course from the user, returns true if successful, false if not.
    private boolean removeCourse(String course) {
        if (courseNames.contains(course)) {
            courseNames.remove(course);
            return true;
        } else {
            return false;
        }
    }

    // ================================================================================================================

    // REQUIRES: courses already added and sorted into term 1 or term 2 (or neither, in the case of all year).
    // MODIFIES: this
    // EFFECTS: Creates a timetable based on the courses added, the time preference stated, and the available sections.
    public void createTimeTable() {
        Sorter.sortCourses(this);

        for (String t : TERMS) {
            finalTimeTable.putIfAbsent(t, new HashSet<>());
            for (Course c : result.get(t)) {
                for (String p : preferencesArr) {
                    for (Section s : c.getSortSections().get(p)) {
                        if (s.getActivity().equals("Waiting List")) {
                            continue;
                        }
                        if (!finalTimeTable.get(t).contains(s)
                                && !OverlapChecker.isOverlapping(s, finalTimeTable.get(t))) {
                            finalTimeTable.get(t).add(s);
                        }
                    }
                }

                ArrayList<String> errorResult = canAddToErrorLog(c, finalTimeTable.get(t));
                if (!errorResult.isEmpty()) {
                    errorLog.add(c.getCourseName() + ": " + errorResult.toString());
                }
            }
        }
    }

    private ArrayList<String> canAddToErrorLog(Course course, HashSet<Section> sections) {
        ArrayList<String> actList = new ArrayList<>();
        Set<Section> filtered = sections.stream()
                .filter(s -> s.getCourse().equals(course.getCourseName()))
                .collect(Collectors.toSet());

        for (String item : course.getActivitiesList()) {
            if (item.equals("Waiting List")) {
                continue;
            }
            Set<Section> actFilter = filtered.stream()
                    .filter(section -> section.getActivity().equals(item))
                    .collect(Collectors.toSet());
            if (actFilter.isEmpty()) {
                actList.add(item);
            }
        }
        return actList;
    }

    // ================================================================================================================

    @Override
    // EFFECTS: parses User object from Java object to json and returns a JSONObject.
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("Course List", new JSONArray(courseNames));
        json.put("Preferences", new JSONArray(preferencesArr));
        json.put("Term", termYear);
        json.put("TimePref", new JSONArray(preferencesArr));
        json.put("ErrorLog", new JSONArray(errorLog));

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

}
