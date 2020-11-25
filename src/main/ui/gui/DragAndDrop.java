package ui.gui;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

// handles list items being transferred around
// copied from https://stackoverflow.com/questions/16586562/reordering-jlist-with-drag-and-drop

public class DragAndDrop extends TransferHandler {
    private final JList<String> list;
    private final DefaultListModel<String> model;
    private int index;
    private boolean beforeIndex = false;
    //Start with `false` therefore if it is removed from or added to the list it still works

    public DragAndDrop(JList<String> list, DefaultListModel<String> model) {
        super();
        this.list = list;
        this.model = model;
    }

    @Override
    public int getSourceActions(JComponent comp) {
        return MOVE;
    }

    @Override
    public Transferable createTransferable(JComponent comp) {
        index = list.getSelectedIndex();
        return new StringSelection(list.getSelectedValue());
    }

    @Override
    public void exportDone(JComponent comp, Transferable trans, int action) {
        if (action == MOVE) {
            if (beforeIndex) {
                model.remove(index + 1);
            } else {
                model.remove(index);
            }
        }
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        try {
            String s = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
            JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
            model.add(dl.getIndex(), s);
            beforeIndex = dl.getIndex() < index;
            return true;
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
