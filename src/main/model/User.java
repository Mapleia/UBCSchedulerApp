package model;

import exceptions.NoCourseFound;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import persistence.JsonReader;
import persistence.Writable;

import java.io.IOException;
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
        result.put("1", new ArrayList<>());
        result.put("2", new ArrayList<>());
        result.put("1-2", new ArrayList<>());
        resultsCredits.put("1", 0);
        resultsCredits.put("2", 0);
    }

    // getters & setters ==============================================================================================
    public HashMap<String, ArrayList<Section>> getFinalTimeTable() {
        return finalTimeTable;
    }

    public List<String> getErrorLog() {
        return errorLog;
    }

    public void setPreferences(List<String> preferencesArr) {
        this.preferencesArr = preferencesArr;
    }
    // ================================================================================================================

    //TODO: finish this stub.
    public void createTimeTable() {
        sortCourses();
        for (String t : result.keySet()) {
            for (Course c : result.get(t)) {
                HashMap<String, Boolean> finalTimeTableContains = new HashMap<>();
                finalTimeTable.putIfAbsent(t, new ArrayList<>());
                // setup for finalTimeTableContains map
                for (String s : c.getActivitiesList()) {
                    finalTimeTableContains.put(s, false);
                }

                Iterator<String> itr = c.getActivitiesList().iterator();
                while (itr.hasNext()) {
                    String act = itr.next();
                    addIfPossible(act, finalTimeTableContains, itr, c, t);
                }

                addToErrorLog(finalTimeTableContains, c);

            }
        }
    }

    private void addToErrorLog(HashMap<String, Boolean> finalTimeTableContains, Course c) {
        if (finalTimeTableContains.containsValue(false)) {
            String sectionStr = " ";
            for (String s : finalTimeTableContains.keySet()) {
                if (finalTimeTableContains.get(s) == false) {
                    sectionStr = sectionStr.concat(s + ", ");
                }
            }
            errorLog.add(c.getCourseName() + ":\n\t" + sectionStr);
        }
    }

    // EFFECTS: Adds or skips activity based on what is already added.
    //          If W.O.C or Lecture is already added, skip Waiting List.
    //          If W.O.C is already added, skip Lecture.
    //          If Waiting List, skip.
    //          If at Waiting List, and the last one, and no Lecture or W.O.C, add.
    private void addIfPossible(String act, HashMap<String, Boolean> containsMap, Iterator<String> itr,
                               Course c, String t) {
        if (act.equals("Waiting List")) {
            containsMap.put("Waiting List", true);
            itr.remove();

        } else {
            for (String time : c.getSortSections().get(act).keySet()) {
                if (sectionIsAdded(c.getSortSections().get(act).get(time), t)) {
                    containsMap.put(act, true);
                    itr.remove();
                    break;
                }
            }
        }
    }

    // HELPER FOR addIfPossible
    // EFFECTS: Adds 1 section from a list of given sections to the final timetable if it fits.
    //          Returns false if no valid section could be added.
    //          Returns true if a section was added.
    private boolean sectionIsAdded(List<Section> sections, String term) {
        boolean success = false;
        potentialSections: {
            for (Section s1 : sections) {
                if (finalTimeTable.get(term).isEmpty()) {
                    success = true;
                    finalTimeTable.get(term).add(s1);
                    break potentialSections;
                } else {
                    boolean forThisSection = false;
                    for (Section s2 : finalTimeTable.get(term)) {
                        if (Section.isOverlapping(s1, s2)) {
                            forThisSection = true;
                        }
                    }
                    if (!forThisSection) {
                        success = true;
                        finalTimeTable.get(term).add(s1);
                        break potentialSections;
                    }
                }
            }
        }
        return success;
    }

    // EFFECTS: Sort courses into Term1, Term2 or Term1-2 ArrayList hashmaps.
    //          If the course is offered in term1 or 2, it puts the course into the term with the least
    //          amount of credits.
    private void sortCourses() {
        List<Course> copy = new ArrayList<>(courseSet);
        Iterator<Course> itr = copy.iterator();
        while (itr.hasNext()) {
            Course course = itr.next();
            if (course.getTerms().size() == 1 && course.getTerms().get(0).equals("1")) {
                addToTerm1Results(course);
                itr.remove();

            } else if (course.getTerms().size() == 1 && course.getTerms().get(0).equals("2")) {
                addToTerm2Results(course);
                itr.remove();

            } else if (course.getTerms().size() == 1 && course.getTerms().get(0).equals("1-2")) {
                course.filterForTerm("1-2");
                course.sortSections();
                result.get("1-2").add(course);
                itr.remove();

            } else if (resultsCredits.get("2") > resultsCredits.get("1")) {
                addToTerm1Results(course);
                itr.remove();

            } else {
                addToTerm2Results(course);
                itr.remove();
            }
        }
    }

    // HELPER FOR sortCourses
    // EFFECT: Adds Course to Term1, sort course sections according to term specified and removes from the to-do list.
    private void addToTerm1Results(Course c) {
        c.filterForTerm("1");
        c.sortSections();
        result.get("1").add(c);
        resultsCredits.put("1", resultsCredits.get("1") + c.getCredit());
    }

    // HELPER FOR sortCourses
    // EFFECT: Adds Course to Term2, sort course sections according to term specified and removes from the to-do list.
    private void addToTerm2Results(Course c) {
        c.filterForTerm("2");
        c.sortSections();
        result.get("2").add(c);
        resultsCredits.put("2", resultsCredits.get("2") + c.getCredit());
    }

    @Override
    // EFFECTS: parses User object from Java object to json and returns a JSONObject.
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("Course List", new JSONArray(courseList));
        json.put("Preferences", new JSONArray(preferencesArr));
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
    public void addSectionsFromTimeTable(JSONObject schedule) throws IOException {
        Set<String> terms = schedule.keySet();

        for (String term : terms) {

            List<Object> perTerm = schedule.getJSONArray(term).toList();
            ArrayList<Section> list = new ArrayList<>();
            for (int i = 0; i < perTerm.size(); i++) {
                JSONObject sectionJson = schedule.getJSONArray(term).getJSONObject(i);
                String p = "./data/" + termYear + "/" + sectionJson.getString("course").split(" ")[0].trim()
                        + "/" + sectionJson.getString("course") + ".json";
                JsonReader reader = new JsonReader(p);

                Section section = reader.readSection(termYear, sectionJson.getString("section"), preferencesArr);
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

        String path = "./data/" + termYear + "/" + split[0].toUpperCase() + "/" + input.toUpperCase() + ".json";

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

    public void clearTimetable() {
        finalTimeTable = new HashMap<>();
    }
}
