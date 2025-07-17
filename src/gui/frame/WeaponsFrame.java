package gui.frame;

import db.SQLiteRepository;
import gui.panel.WeaponPanel;
import model.FEWeapon;
import utils.WeaponUtils;

import java.util.List;
import java.util.Set;

public class WeaponsFrame extends WritableItemFrame<FEWeapon> {

    private WeaponPanel weaponPanel;

    public WeaponsFrame() {
        super("Weapons", "weapon", 1000, 1000);
    }

    @Override
    protected void makePanel() {
        weaponPanel = new WeaponPanel();
    }

    @Override
    protected WeaponPanel getPanel() {
        return weaponPanel;
    }

    @Override
    protected void refreshPanel() {
        object = WeaponUtils.findByName(list.getSelectedValue()).clone();
        weaponPanel.refresh(object);
    }

    @Override
    protected Set<String> fetch() {
        return SQLiteRepository.fetchAllWeapons().keySet();
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
