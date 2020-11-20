package ui.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Set;

// referenced: https://stackoverflow.com/questions/15169674/jtextfield-with-both-actionlistener-and-documentlistener
// for keyPressed method
public class SearchBarKeyListener implements KeyListener {
    private Set<String> courseList;
    private List<String> deptArr;

    public SearchBarKeyListener(List<String> deptArr, Set<String> courseList) {
        this.deptArr = deptArr;
        this.courseList = courseList;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            if (deptArr != null && deptArr.size() == 1) {
                courseList.add(deptArr.get(0));
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
