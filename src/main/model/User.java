package model;

import exceptions.NoCourseFound;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import persistence.JsonReader;
import persistence.Writable;

import java.util.*;

// Represents a user of the program, makes their timetable and stores information about them.
public class User implements Writable {
    private String termYear;
    private List<String> courseList;
    private List<Course> courseSet;
    private HashMap<String, ArrayList<Section>> finalTimeTable;
    private List<String> errorLog; //strings of missed sections
    private List<String> preferencesArr;

    private HashMap<String, ArrayList<Course>> result;
    private HashMap<String, Integer> resultsCredits;

    // constructs user and initializes it.
    public User(String termYear) {
        this.termYear = termYear;
        init();
    }

    // initializes all of the fields.
    private void init() {
        courseList = new ArrayList<>();
        courseSet = new ArrayList<>();
        errorLog = new ArrayList<>();
        finalTimeTable = new HashMap<>();

        result = new HashMap<>();
        resultsCredits = new HashMap<>();
        result.put("Term1", new ArrayList<>());
        result.put("Term2", new ArrayList<>());
        result.put("Term12", new ArrayList<>());
        resultsCredits.put("Term1", 0);
        resultsCredits.put("Term2", 0);
    }

    // getters & setters ==============================================================================================
    public HashMap<String, ArrayList<Section>> getFinalTimeTable() {
        return finalTimeTable;
    }

    public List<String> getErrorLog() {
        return errorLog;
    }

    public List<String> getCourseList() {
        return courseList;
    }

    public String getTerm() {
        return termYear;
    }

    public List<String> getPreferences() {
        return preferencesArr;
    }

    public void setPreferences(List<String> preferencesArr) {
        this.preferencesArr = preferencesArr;
    }
    // ================================================================================================================

    //TODO: finish this stub.
    public boolean createTimeTable() {
        sortCourses();
        Set<String> termSet = result.keySet();
        for (String t : termSet) {
            for (Course c : result.get(t)) {

            }
        }

        return false;
    }

    // EFFECTS: Sort courses into Term1, Term2 or Term1-2 ArrayList hashmaps.
    //          If the course is offered in term1 or 2, it puts the course into the term with the least
    //          amount of credits.
    private void sortCourses() {
        List<Course> copy = new ArrayList<>();
        copy.addAll(courseSet);
        Iterator<Course> itr = copy.iterator();
        while (itr.hasNext()) {
            Course course = itr.next();
            if (course.getTerms().size() == 1) {
                if (course.getTerms().contains("1")) {
                    addToTerm1Results(itr, course);
                } else if (course.getTerms().contains("2")) {
                    addToTerm2Results(itr, course);
                } else {
                    result.get("Term12").add(course);
                    itr.remove();
                }
            } else if (resultsCredits.get("Term2") > resultsCredits.get("Term1")) {
                addToTerm1Results(itr, course);
            } else {
                addToTerm2Results(itr, course);
            }
        }
    }

    // EFFECT: Adds Course to Term1 and removes from the list.
    private void addToTerm1Results(Iterator<Course> itr, Course c) {
        result.get("Term1").add(c);
        resultsCredits.put("Term1", resultsCredits.get("Term1") + c.getCredit());
        itr.remove();
    }

    // EFFECT: Adds Course to Term2 and removes from the list.
    private void addToTerm2Results(Iterator<Course> itr, Course c) {
        result.get("Term2").add(c);
        resultsCredits.put("Term2", resultsCredits.get("Term2") + c.getCredit());
        itr.remove();
    }

    // EFFECTS: Adds 1 section from a list of given sections to the final timetable if it fits.
    //          Returns false if no valid section could be added.
    //          Returns true if a section was added.
    private boolean sectionIsAdded(List<Section> sections, String term) {
        finalTimeTable.putIfAbsent(term, new ArrayList<>());
        boolean success = false;

        potentialSections : {
            for (Section s1 : sections) {
                for (Section s2 : finalTimeTable.get(term)) {
                    if (!Section.isOverlapping(s1, s2)) {
                        success = true;
                        finalTimeTable.get(term).add(s1);
                        break potentialSections;
                    }
                }
            }
        }
        return success;
    }

    @Override
    // EFFECTS: parses User object from Java object to json and returns a JSONObject.
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("Course List", new JSONArray(courseList));
        json.put("Term", termYear);

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
                Section section = new Section(schedule.getJSONArray(term).getJSONObject(i), termYear);
                list.add(section);
            }

            finalTimeTable.put(term, list);
        }
    }

    // MODIFIES: this
    // EFFECTS: Loops through courseList and adds courses.
    // throws NoCourseFound if it encounters an not successful addition of course during the loop.
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
    // Returns false if finds an exception / cannot find the course.
    private boolean addCourse(String input) {
        String[] split = input.split(" ");

        String path = "./data/" + termYear + "/" + split[0] + "/" + input + ".json";

        try {
            JsonReader jsonReader = new JsonReader(path);
            Course course = jsonReader.readCourse(termYear, preferencesArr);
            courseList.add(input);
            courseSet.add(course);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
