package gui;

import model.FEWeapon;

import javax.swing.JPanel;
import java.awt.GridLayout;

public class WeaponPanel extends JPanel {
    private BorderedLabeledTextField nameField;

    public WeaponPanel() {
        super(new GridLayout(1, 2, 10, 10));
    }

    public void fill(FEWeapon display) {
        removeAll();

        tieActionListeners(display);

        revalidate();
        repaint();
    }

    private void tieActionListeners(FEWeapon display) {
    }
}
