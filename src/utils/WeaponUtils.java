package utils;

import init.Main;
import model.FEWeapon;

public class WeaponUtils {
    private WeaponUtils() {}

    public static FEWeapon findByName(String name) {
        return Main.WEAPONS.get(name);
    }
}
