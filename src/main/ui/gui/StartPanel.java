package ui.gui;

import javax.swing.*;
import java.io.File;

public class StartPanel extends AppPanel implements GoNext {
    private File loadedFile;

    // constructor
    public StartPanel(SchedulerApp app) {
        super(app);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        ImageIcon icon = new ImageIcon("./data/logo.png");
        add(new JLabel("UBC Course Scheduler", icon, JLabel.CENTER));
        controlPanel();
    }

    // EFFECT: Create button panel to load a file, or go to next menu.
    // referenced how to make buttons from:
    // https://www.codejava.net/java-se/swing/jbutton-basic-tutorial-and-examples
    // referenced how to use JFileChooser from:
    // https://www.codejava.net/java-se/swing/show-simple-open-file-dialog-using-jfilechooser
    @Override
    public void controlPanel() {
        JPanel panel = new JPanel();
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

        JButton nextBtn = nextButton();

        panel.add(nextBtn);

        add(panel);
    }

    @Override
    public JButton nextButton() {
        JButton nextBtn = new JButton("Next");
        nextBtn.setActionCommand("Next");
        nextBtn.addActionListener(e -> app.nextPanel(new CoursePanel(app)));
        return nextBtn;
    }
}
