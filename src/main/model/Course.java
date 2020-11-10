package model;


import java.util.*;

// Represents a course at UBC.
public class Course {
    private final String courseName;
    private final List<String> termsList;
    private List<String> activitiesList;
    private List<Section> sectionList;
    private final int credit;
    private final List<String> preferences;

    private HashMap<String, Section> sectionsMap;
    private HashMap<String, HashMap<String, ArrayList<Section>>> sortSections;

    // constructor for course
    public Course(String courseName, List<String> termsList, List<String> activitiesList, List<Section> sectionList,
                  int credit, List<String> preferences)  {
        this.courseName = courseName;
        this.termsList = termsList;
        this.activitiesList = activitiesList;
        this.sectionList = sectionList;
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

    public List<String> getActivitiesList() {
        return activitiesList;
    }

    public String getCourseName() {
        return courseName;
    }
    // ================================================================================================================

    // MODIFIES: course
    // EFFECTS: parses sections from JSON object and adds them to the course.
    private void mapSections() {
        for (Section sec : sectionList) {
            if (sec.isRequired() && !activitiesList.contains("Required")) {
                activitiesList.add("Required");
            }
            sectionsMap.put(sec.getSection(), sec);
        }
    }

    public void filterForTerm(String term) {
        List<String> filteredActivity = new ArrayList<>();
        List<Section> filteredSections = new ArrayList<>();
        for (Section section : sectionList) {
            if (section.getTerm().equals(term)) {
                if (!filteredActivity.contains(section.getActivity())) {
                    filteredActivity.add(section.getActivity());
                }
                filteredSections.add(section);
            }
        }
        sectionList = filteredSections;
        activitiesList = filteredActivity;
    }

    // MODIFIES: this
    // EFFECTS: sorts sections by activity, then by timespan (morning/afternoon/evening)
    public void sortSections() {
        Iterator<Section> itr = sectionList.iterator();
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
