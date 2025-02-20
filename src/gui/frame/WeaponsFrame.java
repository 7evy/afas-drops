package gui.frame;

import db.SQLiteRepository;
import gui.panel.CohesivePanel;
import gui.panel.WeaponPanel;
import model.DisplayedObject;
import model.FEWeapon;
import utils.WeaponUtils;

import java.util.List;

public class WeaponsFrame extends WritableItemFrame<FEWeapon> {

    private final WeaponPanel weaponPanel;

    public WeaponsFrame() {
        super("Weapons", "weapon", 1000, 1000);
        this.weaponPanel = new WeaponPanel();
    }

    @Override
    protected CohesivePanel<? extends DisplayedObject> getPanel() {
        return weaponPanel;
    }

    @Override
    protected void refreshPanel() {
        object = WeaponUtils.findByName(list.getSelectedValue()).clone();
        weaponPanel.refresh(object);
    }

    @Override
    protected List<FEWeapon> fetch() {
        return SQLiteRepository.fetchAllWeapons();
    }

    @Override
    protected void create() {
        SQLiteRepository.newWeapon();
    }

    @Override
    protected void update() {
        SQLiteRepository.updateWeapon(object);
    }
}
