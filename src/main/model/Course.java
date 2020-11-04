package model;

import exceptions.NoCourseFound;
import exceptions.NoTimeSpan;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class Course {
    private final JSONArray sections;
    private final List<String> terms;
    private final List<String> activities;
    private final List<String> preferences;
    private String term;
    private String courseName;
    private HashMap<String, Section> sectionsMap;
    private int credit;
    private HashMap<String, HashMap<String, ArrayList<Section>>> sortSections;

    // constructor for course
    public Course(String courseName, List<String> termsList, List<String> activities, JSONArray sections,
                  String term, int credit, List<String> preferences) throws NoTimeSpan {
        this.courseName = courseName;
        this.terms = termsList;
        this.activities = activities;
        this.sections = sections;
        this.term = term;
        this.credit = credit;
        this.preferences = preferences;
        init();
    }

    // initializes all of the fields when necessary.
    private void init() throws NoTimeSpan {
        sectionsMap = new HashMap<>();
        addSections();
        sortSections = new HashMap<>();
        sortSections();
    }

    // MODIFIES: course
    // EFFECTS: parses sections from JSON object and adds them to the course.
    private void addSections() {
        for (Object o : sections) {
            JSONObject json = (JSONObject) o;
            Section section = new Section(json, term);
            sectionsMap.put(section.getSection(), section);
        }
    }

    private void sortSections() throws NoTimeSpan {
        List<Section> values = sectionsMap.values().stream().collect(Collectors.toList());
        Iterator<Section> itr = values.iterator();
        HashMap<String, ArrayList<Section>> timeSorted;

        while (itr.hasNext()) {
            Section section = itr.next();
            if (!sortSections.containsKey(section.getActivity())) {
                timeSorted = new HashMap<>();
                for (String time : preferences) {
                    timeSorted.put(time.toUpperCase(), new ArrayList<>());
                }
                sortSections.putIfAbsent(section.getActivity(), timeSorted);
            }

            sortSections.get(section.getActivity()).get(section.getTimeSpan()).add(section);
            itr.remove();
        }
    }

    public List<String> getTerms() {
        return terms;
    }

    public HashMap<String, Section> getSectionsMap() {
        return sectionsMap;
    }

    public int getCredit() {
        return credit;
    }

    public HashMap<String, HashMap<String, ArrayList<Section>>> getSortSections() {
        return sortSections;
    }
}
