package ui.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class StartPanel extends JPanel {
    private JButton loadBtn;
    private File loadedFile;

    public StartPanel() {
        setPreferredSize(new Dimension(SchedulerApp.WIDTH, SchedulerApp.HEIGHT));
        setBackground(Color.GRAY);

        init();
    }


    public void init() {
        setupButtons();
    }

    // referenced how to make buttons from:
    // https://www.codejava.net/java-se/swing/jbutton-basic-tutorial-and-examples
    // referenced how to use JFileChooser from:
    // https://www.codejava.net/java-se/swing/show-simple-open-file-dialog-using-jfilechooser
    public void setupButtons() {
        loadBtn = new JButton("Load File");
        loadBtn.setActionCommand("Load");
        add(loadBtn);
        loadBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("./data/timetables/"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                loadedFile = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + loadedFile.getAbsolutePath());
            }
        });

    }
}
