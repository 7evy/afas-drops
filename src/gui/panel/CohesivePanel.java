package gui.panel;

import model.DisplayedObject;

import javax.swing.JPanel;
import java.awt.GridLayout;

public abstract class CohesivePanel<T extends DisplayedObject> extends JPanel {
    public CohesivePanel(int rows, int cols, int hgap, int vgap) {
        super(new GridLayout(rows, cols, hgap, vgap));
    }

    public void refresh(T display) {
        removeAll();

        fill(display);
        tieActionListeners(display);

        revalidate();
        repaint();
    }

    protected abstract void fill(T display);

    protected abstract void tieActionListeners(T display);
}
