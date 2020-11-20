package ui.gui;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SchedulerApp extends JFrame {
    public static final int WIDTH = 900;
    public static final int HEIGHT = 700;
    public static final String[] ACADEMIC_YEAR = {"2020W"};
    private File userFile = null;
    private List<String> timePrefArr = new ArrayList<>();
    private User user;

    private JPanel panel;

    public SchedulerApp() {
        super("UBC Schedule Maker");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.GRAY);

        panel = new StartPanel(this);
        add(panel);
        pack();
        setVisible(true);
    }

    public void setUserFile(File file) {
        userFile = file;
    }

    public void nextPanel(JPanel panel, String panelName) {
        this.panel = panel;
        Container contain = getContentPane();
        contain.removeAll();
        add(panel);
        contain.validate();
        contain.repaint();
    }

}
