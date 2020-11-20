package ui.gui;

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
    private LinkedList<String> arr;
    private String searchTerm = "";
    private String year = "2020W";
    private List<String> deptArr;
    private Overview overview;
    private JTextArea courseListField;
    private Set<String> courseList;

    // constructor
    public CoursePanel(SchedulerApp app) {
        this.app = app;
        setPreferredSize(new Dimension(SchedulerApp.WIDTH, SchedulerApp.HEIGHT));

        init();
    }

    // EFFECT: Initializes course choosing panel.
    private void init() {
        arr = new LinkedList<>(Arrays.asList(new String[]{"  MORNING", "AFTERNOON", "  EVENING", "N/A"}));
        courseList = new HashSet<>();
        readOverview();

        add(createSettingsPanel());
        add(courseSearchMasterPanel());
        add(courseSelectMasterPanel());
        add(coursesSoFarPanel());

        validate();
        repaint();
    }

    // EFFECT: getter
    public JTextArea getCourseListField() {
        return courseListField;
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
                updateCourseListPanel(courseListField);
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
                arr.set(i, s);
                updateFeedBackText(textArea, arr, 3);
            }
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
    private void updateFeedBackText(JTextArea textArea, List array, int loop) {
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
        master.add(searchBarPanel("Search For A Course"));

        // Scrollable text area with the list of departments, or the list of courses from the searched department.
        courseListField = new JTextArea(20, 10);
        JScrollPane scrollPane = new JScrollPane(courseListField);
        updateCourseListPanel(courseListField);

        master.add(scrollPane);
        return master;
    }

    // EFFECT: Creates a search bar.
    private JPanel searchBarPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JLabel("Search (PRESS ENTER)"));

        JTextField field = new JTextField();

        field.setColumns(10);
        field.getDocument().addDocumentListener(new SearchBarListener(this));
        field.addKeyListener(new SearchBarKeyListener(deptArr, courseList));

        panel.add(field);
        return panel;
    }

    // EFFECT: Updates and populates supplied JTextArea with courses or department, depending on the searched course.
    public void updateCourseListPanel(JTextArea field) {
        String fieldStr = "";
        deptArr = createOverviewList();
        for (String dept : deptArr) {
            fieldStr += (dept + "\n");
        }
        field.setText(fieldStr);
    }

    private List<String> createOverviewList() {
        ArrayList<String> arr = overview.getDepArr();
        String dept;
        if (searchTerm.contains(" ")) {
            int i = searchTerm.indexOf(" ");
            dept = searchTerm.substring(0, i);
        } else {
            dept = searchTerm;
        }

        List<String> newArr = arr.stream().filter(s -> s.contains(dept)).collect(Collectors.toList());

        if (newArr.size() == 1) {
            newArr = overview.getCourses(dept);
            if (searchTerm.contains(" ")) {
                newArr = newArr.stream().filter(s -> s.contains(searchTerm)).collect(Collectors.toList());
            }

        }

        return newArr;
    }

    // ================================================================================================================
    //     EFFECT: Panel to allow users to select courses instead of search for them.
    // referenced: https://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html
    //             for combo box
    private JPanel courseSelectMasterPanel() {
        List<String> potentialSelections = new ArrayList<>();

        JPanel master = new JPanel();
        master.setLayout(new BoxLayout(master, BoxLayout.Y_AXIS));

        DefaultListModel model = new DefaultListModel();

        JList courses = new JList(model);
        courses.setVisibleRowCount(18);
        courses.addListSelectionListener(e -> {
            potentialSelections.addAll(courses.getSelectedValuesList());
        });

        master.add(deptComboBox(model));
        master.add(new JScrollPane(courses));
        master.add(confirmButton(potentialSelections));

        return master;
    }

    //   EFFECT: Create and returns a panel to select department from a drop down menu.
    private JPanel deptComboBox(DefaultListModel model) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("OR Select Course(s) From a Department"));

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
        });

        return confirmBtn;
    }

    // ================================================================================================================
    // EFFECT: Displays all added courses.
    private JPanel coursesSoFarPanel() {
        JPanel panel = new JPanel();
        JTextArea text = createTextArea();
        updateFeedBackText(text, Arrays.asList(courseList.toArray()), courseList.size());
        panel.add(text);
        return panel;
    }
}
