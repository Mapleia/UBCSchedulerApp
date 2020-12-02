package ui.gui;

import exceptions.NoCourseFound;
import model.Overview;
import persistence.JsonReader;
import ui.gui.coursepanel.SearchPanel;
import ui.gui.coursepanel.SelectPanel;
import ui.gui.coursepanel.TimePrefPanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

// Panel to select courses and set settings.
public class CoursePanel extends AppPanel implements GoNext {
    private String year = "2020W";
    private Overview overview;
    private DefaultListModel<String> courseListModel;
    private DefaultListModel<String> timePrefModel;

    private SearchPanel searchPanel;

    // constructor
    public CoursePanel(SchedulerApp app) {
        super(app);
        courseListModel = new DefaultListModel<>();
        timePrefModel = new DefaultListModel<>();
        timePrefInit();

        readOverview();

        searchPanel = new SearchPanel(overview, courseListModel);
        add(searchPanel);
        add(new SelectPanel(overview, courseListModel));
        add(coursesSoFarPanel());
        controlPanel();
    }

    // MODIFIES: this
    //   EFFECT: Adds a control panel with [back/next/TimePrefPanel/availableYear] settings.
    //           Shows warning to user if time preferences are not added, or if courses cannot be found.
    @Override
    public void controlPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(170, 300));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new TimePrefPanel(timePrefModel));
        panel.add(availableYearPanel());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(app.backButton(new StartPanel(app)));
        buttonPanel.add(nextButton());
        panel.add(buttonPanel);

        add(panel);
    }

    // EFFECT: Sets time preference, adds courses to user. If NoCourseFound is caught, popup message will show up.
    @Override
    public JButton nextButton() {
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

    // MODIFIES: app, user
    //   EFFECT: Set time preference to the user.
    private void setTimePref() {
        LinkedList<String> timePrefList = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            timePrefList.add(timePrefModel.get(i));
        }
        timePrefList.add("N/A");
        app.getUser().setPreferences(timePrefList);
    }

    // EFFECT: Initialize and populate timePrefModel
    private void timePrefInit() {
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
                readOverview();
                app.getUser().setYear(year);
                searchPanel.updateSearchSuggestionPanel();
            });
        }
        return panel;
    }

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

        JList<String> courses = new JList<>(courseListModel);
        courses.setVisibleRowCount(18);
        panel.add(new JScrollPane(courses));

        panel.add(removeButton(courses));

        return panel;
    }

    // MODIFIES: this
    //   EFFECT: Remove selected courses.
    private JButton removeButton(JList<String> courses) {
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

}
