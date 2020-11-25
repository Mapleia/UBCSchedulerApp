package ui.gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class StartPanel extends JPanel {
    private File loadedFile;
    private final SchedulerApp app;

    // constructor
    public StartPanel(SchedulerApp app) {
        this.app = app;

        init();
    }

    // Initializes the StartPanel.
    public void init() {
        setPreferredSize(new Dimension(SchedulerApp.WIDTH, SchedulerApp.HEIGHT));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        ImageIcon icon = new ImageIcon("./data/logo.png");
        add(new JLabel("UBC Course Scheduler", icon, JLabel.CENTER));

        buttonPanel();
    }

    // EFFECT: Create button panel to load a file, or go to next menu.
    // referenced how to make buttons from:
    // https://www.codejava.net/java-se/swing/jbutton-basic-tutorial-and-examples
    // referenced how to use JFileChooser from:
    // https://www.codejava.net/java-se/swing/show-simple-open-file-dialog-using-jfilechooser
    private void buttonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());


        JButton loadBtn = new JButton("Load File");
        loadBtn.setActionCommand("Load");
        panel.add(loadBtn);
        loadBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("./data/timetables/"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                loadedFile = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + loadedFile.getAbsolutePath());
                app.setUser(loadedFile);
            }
        });

        JButton nextBtn = new JButton("Next");
        nextBtn.setActionCommand("Next");
        panel.add(nextBtn);

        nextBtn.addActionListener(e -> app.nextPanel(new CoursePanel(app)));

        add(panel);
    }
}
