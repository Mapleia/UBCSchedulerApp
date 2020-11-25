package ui.gui;

import exceptions.NoCourseFound;
import model.Overview;
import persistence.JsonReader;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

// Panel to select courses and set settings.
public class CoursePanel extends JPanel {
    private final SchedulerApp app;
    private LinkedList<String> timePrefArr;
    private String searchTerm = "";
    private String year = "2020W";
    private List<String> deptArr;
    private Overview overview;
    private JTextArea searchSuggestion;
    private Set<String> courseList;
    private DefaultListModel courseListModel;

    // constructor
    public CoursePanel(SchedulerApp app) {
        this.app = app;
        setPreferredSize(new Dimension(SchedulerApp.WIDTH, SchedulerApp.HEIGHT));

        init();
    }

    // EFFECT: Initializes course choosing panel.
    private void init() {
        timePrefArr = new LinkedList<>(Arrays.asList("MORNING", "AFTERNOON", "EVENING", "N/A"));
        courseList = new HashSet<>();
        readOverview();

        add(createSettingsPanel());
        add(courseSearchMasterPanel());
        add(courseSelectMasterPanel());
        add(coursesSoFarPanel());
        add(nextButton());

        validate();
        repaint();
    }

    public List<String> getDeptArr() {
        return deptArr;
    }

    public void addCourseList(String item) {
        courseList.add(item);
    }

    public void addToCourseModel(String item) {
        courseListModel.addElement(item);
    }

    public boolean modelContains(String item) {
        return courseListModel.contains(item);
    }

    // Prints the current added courses to the console. (More for testing purposes...)
    public void printCourses() {
        System.out.println("==============================");
        for (String item : courseList) {
            System.out.println(item);;
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
            System.out.println("Error occurred while reading overview file. Please try again.");
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
                app.setYear(year);
                updateSearchSuggestionPanel();
            });
        }
        return panel;
    }

    // ================================================================================================================
    // EFFECT: Creates and returns panel to input time preference.
    private JPanel timePrefPanel() {
        JPanel timePanel = new JPanel();
        timePanel.setPreferredSize(new Dimension(200, 320));
        // https://www.tutorialspoint.com/how-to-set-vertical-alignment-for-a-component-in-java
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
        timePanel.setBorder(BorderFactory.createTitledBorder("Time Preference"));

        // the result text area
        JTextArea textArea = createTextArea();

        // the input area
        JTextField textField = createInputField(textArea);

        // add it all to the panel
        String instructions = "Please enter your time preferences, in order and separated by a comma.\n "
                + "EX: \"MORNING, AFTERNOON, EVENING\"";
        timePanel.add(makeWrappableLabelText(instructions));
        timePanel.add(textField);
        timePanel.add(textArea);

        return timePanel;
    }

    // EFFECT: Create and returns an input field for the time preferences, and update the text box with feedback.
    private JTextField createInputField(JTextArea textArea) {
        JTextField textField = new JTextField();
        textField.addActionListener(e -> {
            String entry = textField.getText();
            for (int i = 0; i < 3; i++) {
                String s = entry.split(",")[i].trim().toUpperCase();
                timePrefArr.set(i, s);
                updateFeedBackText(textArea, timePrefArr, 3);

            }
            app.setTimePref(timePrefArr);
        });
        return textField;
    }

    // EFFECT: Create and returns a non focusable textArea that contains result from the inputted time preference.
    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea(3, 7);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        textArea.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        return textArea;
    }

    //     EFFECT: Create label looking like text that can wrap around.
    // referenced: https://stackoverflow.com/questions/26420428/how-to-word-wrap-text-in-jlabel
    private JTextArea makeWrappableLabelText(String labelText) {
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

    // REQUIRES: loop =< array.size()
    // EFFECT: Updates feedback text box.
    public static void updateFeedBackText(JTextArea textArea, List<String> array, int loop) {
        textArea.selectAll();
        textArea.replaceSelection("");

        for (int i = 0; i < loop; i++) {
            textArea.append(array.get(i) + "\n");
        }
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
        field.getDocument().addDocumentListener(new SearchBarListener(this));
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

    //   EFFECT: Create and returns a panel to select department from a drop down menu.
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
            courseList.addAll(potentialSelections);
            courseListModel.removeAllElements();
            for (String item : courseList) {
                courseListModel.addElement(item);
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

        courseListModel = new DefaultListModel();
        for (String userItem : app.getUserCourses()) {
            courseListModel.addElement(userItem);
        }

        for (String item :  courseList) {
            courseListModel.addElement(item);
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
            for (Object o : thingsToRemove) {
                courseListModel.removeElement(o);
            }

            courseList.removeAll(thingsToRemove);
            try {
                app.getUser().removeCourses(thingsToRemove);
            } catch (NoCourseFound noCourseFound) {
                noCourseFound.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "These courses were not found: " + noCourseFound.stringCourses());
            }
        });
        return removeBtn;
    }

    // ================================================================================================================
    // MODIFIES: SchedulerApp
    //   EFFECT: Create and return a panel with a title and the button to go to the next stage.
    //           Shows warning to user if time preferences are not added, or if courses cannot be found.
    private JPanel nextButton() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Create Your Timetable"));
        panel.setPreferredSize(new Dimension(170, 50));
        JButton nextBtn = new JButton("Next");
        nextBtn.setActionCommand("Next");

        nextBtn.addActionListener(e -> {
            if (validTimePref()) {
                JOptionPane.showMessageDialog(null,
                        "You have not filled in your time preference.");
            } else {
                try {
                    app.addCourses(courseList);
                    app.nextPanel(new SchedulePanel(app));
                } catch (NoCourseFound noCourseFound) {
                    noCourseFound.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "These courses were not found: " + noCourseFound.stringCourses());
                }
            }

        });
        panel.add(nextBtn);

        return panel;
    }

    private boolean validTimePref() {
        return timePrefArr.contains("MORNING") && timePrefArr.contains("EVENING") && timePrefArr.contains("AFTERNOON");
    }
}
