package ui.gui;

import ui.ConsoleSchedulerApp;

import javax.swing.*;
import java.awt.*;

public class SchedulerApp extends JFrame {
    public static final int WIDTH = 900;
    public static final int HEIGHT = 700;

    private StartPanel startPanel;

    public SchedulerApp() {
        super("UBC Schedule Maker");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.GRAY);

        startPanel = new StartPanel();
        add(startPanel);
        pack();
        setVisible(true);
    }
}
