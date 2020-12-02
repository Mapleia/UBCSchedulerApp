package ui.gui;

import javax.swing.*;
import java.awt.*;

public abstract class AppPanel extends JPanel {
    public SchedulerApp app;

    public AppPanel(SchedulerApp app) {
        this.app = app;
        setPreferredSize(new Dimension(SchedulerApp.WIDTH, SchedulerApp.HEIGHT));
    }

    public abstract void controlPanel();

}
