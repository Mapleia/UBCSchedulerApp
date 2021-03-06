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

    private AppPanel panel;

    // constructor
    public SchedulerApp() {
        super("UBC Schedule Maker");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.GRAY);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

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
    public void nextPanel(AppPanel panel) {
        this.panel = panel;
        Container contain = getContentPane();
        contain.removeAll();
        add(panel);
        contain.validate();
        contain.repaint();
    }

    public User getUser() {
        return user;
    }

    // EFFECT: Creates a timetable and returns it.
    public HashMap<String, HashSet<Section>> getTimeTable() {
        user.clearTimetable();
        user.createTimeTable();
        return user.getFinalTimeTable();
    }

    public JButton backButton(AppPanel panel) {
        JButton button = new JButton("Back");
        button.setActionCommand("Back");
        button.addActionListener(e -> nextPanel(panel));

        return button;
    }
}
