package ui.gui;

import exceptions.NoCourseFound;
import model.Overview;
import persistence.JsonReader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

// Panel to select courses and set settings.
public class CoursePanel extends JPanel {
    private final SchedulerApp app;
    private String searchTerm = "";
    private String year = "2020W";
    private List<String> deptArr;
    private Overview overview;
    private JTextArea searchSuggestion;
    private DefaultListModel<String> courseListModel;
    private DefaultListModel<String> timePrefModel;

    // constructor
    public CoursePanel(SchedulerApp app) {
        this.app = app;
        setPreferredSize(new Dimension(SchedulerApp.WIDTH, SchedulerApp.HEIGHT));

        init();
    }

    // EFFECT: Initializes course choosing panel.
    private void init() {
        courseListModel = new DefaultListModel();
        timePrefModel = new DefaultListModel();
        if (app.getUser().getTimePref() != null) {
            for (String item : app.getUser().getTimePref()) {
                timePrefModel.addElement(item);
            }
            timePrefModel.removeElement("N/A");
        } else {
            timePrefModel.addElement("MORNING");
            timePrefModel.addElement("AFTERNOON");
            timePrefModel.addElement("EVENING");
        }

        //courseList = new HashSet<>();
        readOverview();

        add(createSettingsPanel());
        add(courseSearchMasterPanel());
        add(courseSelectMasterPanel());
        add(coursesSoFarPanel());
        add(controlPanel());

        validate();
        repaint();
    }

    public List<String> getDeptArr() {
        return deptArr;
    }

    public void addToCourseModel(String item) {
        courseListModel.addElement(item);
    }

    public boolean modelContains(String item) {
        return courseListModel.contains(item);
    }

    // EFFECT: Prints the current added courses to the console. (More for testing purposes...)
    public void printCourses() {
        System.out.println("==============================");
        for (Object item : courseListModel.toArray()) {
            System.out.println(item);
        }
    }

    // MODIFIES: this
    //   EFFECT: setter
    public void setSearchValue(String value) {
        this.searchTerm = value.toUpperCase();
    }

    // EFFECT: Reads the year's overview JSON file.
    private void readOverview() {
        JsonReader reader = new JsonReader("./data/" + year + "/overview.json");
        try {
            overview = reader.readOverview();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error occurred while reading overview file. Please try again.");
        }
    }

    // EFFECT: Create and returns the setting panel.
    // (Contains availableYearPanel & timePrefPanel).
    private JPanel createSettingsPanel() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.add(timePrefPanel());
        settingsPanel.add(availableYearPanel());
        return settingsPanel;
    }

    // MODIFIES: this
    //   EFFECT: Creates and returns panel with available academic years as buttons.
    //           Updates the available course selected, as it is dependent on the year selected.
    private JPanel availableYearPanel() {
        JPanel panel = new JPanel();
        // https://docs.oracle.com/javase/tutorial/uiswing/components/border.html
        // to create a titled border
        panel.setBorder(BorderFactory.createTitledBorder("Select an Academic Year"));

        for (String yr : SchedulerApp.ACADEMIC_YEAR) {
            JButton btn = new JButton(yr);
            panel.add(btn);
            btn.addActionListener(e -> {
                year = yr;
                app.getUser().setYear(year);
                updateSearchSuggestionPanel();
            });
        }
        return panel;
    }

    // ================================================================================================================
    // EFFECT: Creates and returns panel to input time preference.
    private JPanel timePrefPanel() {
        JPanel timePanel = new JPanel();
        // https://www.tutorialspoint.com/how-to-set-vertical-alignment-for-a-component-in-java
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
        timePanel.setBorder(BorderFactory.createTitledBorder("Time Preference"));

        String instructions = "Please click and drag to order your time preference.";
        JList orderTimePref = new JList(timePrefModel);
        orderTimePref.setDragEnabled(true);
        orderTimePref.setDropMode(DropMode.INSERT);
        orderTimePref.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTimePref.setTransferHandler(new DragAndDrop(orderTimePref, timePrefModel));
        orderTimePref.setBorder(BorderFactory.createEtchedBorder());
        orderTimePref.setFixedCellHeight(30);
        orderTimePref.setFixedCellWidth(100);
        timePanel.add(makeWrapLabelText(instructions));
        timePanel.add(orderTimePref);

        return timePanel;
    }

    //     EFFECT: Create label looking like text that can wrap around.
    // referenced: https://stackoverflow.com/questions/26420428/how-to-word-wrap-text-in-jlabel
    private JTextArea makeWrapLabelText(String labelText) {
        JTextArea text = new JTextArea(2, 20);
        text.append(labelText);
        text.setWrapStyleWord(true);
        text.setLineWrap(true);
        text.setOpaque(false);
        text.setEditable(false);
        text.setFocusable(false);
        text.setBackground(UIManager.getColor("Label.background"));
        text.setFont(UIManager.getFont("Label.font"));
        text.setBorder(UIManager.getBorder("Label.border"));

        return text;
    }

    // ================================================================================================================
    // EFFECT: Creates and returns panel to search for and select courses.
    private JPanel courseSearchMasterPanel() {
        JPanel master = new JPanel();
        master.setLayout(new BoxLayout(master, BoxLayout.Y_AXIS));
        master.add(searchBarPanel());

        // Scrollable text area with the list of departments, or the list of courses from the searched department.
        searchSuggestion = new JTextArea(20, 10);
        JScrollPane scrollPane = new JScrollPane(searchSuggestion);
        updateSearchSuggestionPanel();

        master.add(scrollPane);
        return master;
    }

    // EFFECT: Creates a search bar.
    private JPanel searchBarPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Search For A Course"));
        panel.add(new JLabel("Search (PRESS ENTER)"));

        JTextField field = new JTextField();
        field.setColumns(10);
        // listener, updates the "searchTerm" value as it updates
        field.getDocument().addDocumentListener(new SearchBarListener(this));
        // listener, if enter is pressed, and only 1 course is displayed in the suggested course list, add to model
        field.addKeyListener(new SearchBarKeyListener(this));

        panel.add(field);
        return panel;
    }

    // EFFECT: Updates and populates supplied JTextArea with courses or department, depending on the searched course.
    public void updateSearchSuggestionPanel() {
        String fieldStr = "";
        deptArr = createOverviewList();
        if (deptArr != null) {
            for (String dept : deptArr) {
                fieldStr += (dept + "\n");
            }
        }
        searchSuggestion.setText(fieldStr);
    }

    // EFFECT: Creates a list of departments or courses depending on the search term input.
    private List<String> createOverviewList() {
        if (searchTerm.contains(" ")) {
            int i = searchTerm.indexOf(" ");
            String dept = searchTerm.substring(0, i);
            return overview.getCourses(dept).stream().filter(s -> s.contains(searchTerm)).collect(Collectors.toList());
        } else {
            return overview.getDepArr().stream().filter(s -> s.contains(searchTerm)).collect(Collectors.toList());
        }
    }

    // ================================================================================================================
    //     EFFECT: Panel to allow users to select courses instead of search for them.
    // referenced: https://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html
    //             for combo box
    private JPanel courseSelectMasterPanel() {
        List<String> potentialSelections = new ArrayList<>();

        JPanel master = new JPanel();
        master.setBorder(BorderFactory.createTitledBorder("OR Select Course(s) From a Department"));
        master.setLayout(new BoxLayout(master, BoxLayout.Y_AXIS));

        DefaultListModel model = new DefaultListModel();

        JList courses = new JList(model);
        courses.setVisibleRowCount(18);
        courses.addListSelectionListener(e -> potentialSelections.addAll(courses.getSelectedValuesList()));

        master.add(deptComboBox(model));
        master.add(new JScrollPane(courses));
        master.add(confirmButton(potentialSelections));

        return master;
    }

    //   EFFECT: Create and returns a panel to select department from a drop down menu by adding to the list model.
    private JPanel deptComboBox(DefaultListModel model) {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Select a Department"));

        JComboBox deptDrownDown = new JComboBox(overview.getDepArr().toArray());
        deptDrownDown.addActionListener(e -> {
            model.removeAllElements();
            for (String item :  overview.getCourses(deptDrownDown.getSelectedItem().toString())) {
                model.addElement(item);
            }
        });

        panel.add(deptDrownDown);
        return panel;
    }

    // MODIFIES: this
    //   EFFECT: Create and returns a button that confirms to add courses.
    private JButton confirmButton(List<String> potentialSelections) {
        JButton confirmBtn = new JButton("Confirm");
        confirmBtn.addActionListener(e -> {
            for (String item : potentialSelections) {
                if (!courseListModel.contains(item)) {
                    courseListModel.addElement(item);
                }
            }
            printCourses();
        });

        return confirmBtn;
    }

    // ================================================================================================================
    // MODIFIES: this
    //   EFFECT: Displays all added courses.
    private JPanel coursesSoFarPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Your Courses"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // get previously added courses
        for (String userItem : app.getUser().getCourseNames()) {
            courseListModel.addElement(userItem);
        }

        JList courses = new JList(courseListModel);
        courses.setVisibleRowCount(18);
        panel.add(new JScrollPane(courses));

        panel.add(removeButton(courses));

        return panel;
    }

    // MODIFIES: this
    //   EFFECT: Remove selected courses.
    private JButton removeButton(JList courses) {
        JButton removeBtn = new JButton("Remove");
        removeBtn.addActionListener(e -> {
            List<String> thingsToRemove = courses.getSelectedValuesList();
            // referenced https://stackoverflow.com/questions/22743922/how-to-remove-selected-items-from-jlist?rq=1
            for (String item : thingsToRemove) {
                courseListModel.removeElement(item);
            }
        });
        return removeBtn;
    }

    // ================================================================================================================
    // MODIFIES: SchedulerApp
    //   EFFECT: Create and return a panel with a title and the button to go to the next stage.
    //           Shows warning to user if time preferences are not added, or if courses cannot be found.
    private JPanel controlPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Create Your Timetable"));
        panel.setPreferredSize(new Dimension(170, 50));

        panel.add(app.backButton(new StartPanel(app)));
        panel.add(nextButton());

        return panel;
    }

    private JButton nextButton() {
        JButton nextBtn = new JButton("Next");
        nextBtn.setActionCommand("Next");
        nextBtn.addActionListener(e -> {
            try {
                setTimePref();
                HashSet<String> courseSet = new HashSet<>();
                for (Object item : courseListModel.toArray()) {
                    String c = (String) item;
                    courseSet.add(c);
                }
                app.getUser().addCourseSet(courseSet);
                app.getUser().addCourses();
                app.nextPanel(new SchedulePanel(app));
            } catch (NoCourseFound noCourseFound) {
                noCourseFound.printStackTrace();
                JOptionPane.showMessageDialog(null,
                           "These courses were not found: " + noCourseFound.stringCourses());
            }
        });
        return nextBtn;
    }

    private void setTimePref() {
        LinkedList<String> timePrefList = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            timePrefList.add(timePrefModel.get(i));
        }
        timePrefList.add("N/A");
        app.getUser().setPreferences(timePrefList);
    }
}
