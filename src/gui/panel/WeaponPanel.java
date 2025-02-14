package gui.panel;

import gui.dumb.BorderedLabeledTextField;
import model.FEWeapon;

public class WeaponPanel extends CohesivePanel<FEWeapon> {
    private BorderedLabeledTextField nameField;

    public WeaponPanel() {
        super(1, 2);
    }

    protected void fill(FEWeapon display) {

    }

    protected void tieActionListeners(FEWeapon display) {

    }
}
