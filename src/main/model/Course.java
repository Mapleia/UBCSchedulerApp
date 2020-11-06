package model;


import java.util.*;

// Represents a course at UBC.
public class Course {
    private String courseName;
    private final List<String> termsList;
    private final List<String> activities;
    private final List<Section> sections;
    private String term;
    private int credit;
    private final List<String> preferences;

    private HashMap<String, Section> sectionsMap;
    private HashMap<String, HashMap<String, ArrayList<Section>>> sortSections;

    // constructor for course
    public Course(String courseName, List<String> termsList, List<String> activities, List<Section> sections,
                  String term, int credit, List<String> preferences)  {
        this.courseName = courseName;
        this.termsList = termsList;
        this.activities = activities;
        this.sections = sections;
        this.term = term;
        this.credit = credit;
        this.preferences = new LinkedList<>(preferences);
        init();
    }

    // EFFECTS: initializes all of the fields, adds, then sorts sections.
    private void init()  {
        preferences.add("N/A");
        sectionsMap = new HashMap<>();
        mapSections();
        sortSections = new HashMap<>();
        sortSections();
    }

    // getters ========================================================================================================
    public List<String> getTerms() {
        return termsList;
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

    public List<String> getActivities() {
        return activities;
    }

    public String getCourseName() {
        return courseName;
    }
    // ================================================================================================================

    // MODIFIES: course
    // EFFECTS: parses sections from JSON object and adds them to the course.
    private void mapSections() {
        for (Section sec : sections) {
            if (sec.isRequired() && !activities.contains("Required")) {
                activities.add("Required");
            }
            sectionsMap.put(sec.getSection(), sec);
        }
    }

    // MODIFIES: this
    // EFFECTS: sorts sections by activity, then by timespan (morning/afternoon/evening)
    private void sortSections()  {
        Iterator<Section> itr = sections.iterator();
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

}
