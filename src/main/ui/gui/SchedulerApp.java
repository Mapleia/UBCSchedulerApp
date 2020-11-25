package ui.gui;

import model.Section;
import model.User;
import persistence.JsonReader;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

// GUI app of the schedule maker.
public class SchedulerApp extends JFrame {
    public static final int WIDTH = 900;
    public static final int HEIGHT = 700;
    public static final String DEFAULT_YEAR = "2020W";
    public static final String[] ACADEMIC_YEAR = {DEFAULT_YEAR};
    private User user;

    private JPanel panel;

    // constructor
    public SchedulerApp() {
        super("UBC Schedule Maker");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.GRAY);
        user = new User(DEFAULT_YEAR);
        panel = new StartPanel(this);
        add(panel);
        pack();
        setVisible(true);
    }

    // EFFECT: Set up a user object from file.
    public void setUser(File file) {
        JsonReader reader = new JsonReader(file.getAbsolutePath());
        try {
            user = reader.readUser();
            nextPanel(new CoursePanel(this));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot read user file.");
        }
    }

    // EFFECT: Change the panel and the contents to the next panel.
    public void nextPanel(JPanel panel) {
        this.panel = panel;
        Container contain = getContentPane();
        contain.removeAll();
        add(panel);
        contain.validate();
        contain.repaint();
    }

/*    // EFFECT: Set the time preference of the user.
    public void setTimePref(LinkedList<String> arr) {
        user.setPreferences(arr);
        for (String item: arr) {
            System.out.println(item);
        }
    }*/

    public User getUser() {
        return user;
    }

/*    public Set<String> getUserCourses() {
        return user.getCourseNames();
    }

    public void setYear(String year) {
        user.setYear(year);
    }*/

/*    public void addCourses(Set<String> courseList) throws NoCourseFound {
        user.addCourses(courseList);
    }*/

    // EFFECT: Creates a timetable and returns it.
    public HashMap<String, HashSet<Section>> getTimeTable() {
        user.clearTimetable();
        user.createTimeTable();
        return user.getFinalTimeTable();
    }

    public JButton backButton(JPanel panel) {
        JButton button = new JButton("Back");
        button.setActionCommand("Back");
        button.addActionListener(e -> nextPanel(panel));

        return button;
    }
}
