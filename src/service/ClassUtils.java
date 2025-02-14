package service;

import init.Main;
import model.FEClass;

import java.util.ArrayList;
import java.util.List;

public class ClassUtils {
    private ClassUtils() {}

    public static FEClass findByName(String name) {
        return Main.CLASSES.stream().filter(c -> c.name.equals(name)).findFirst().orElse(null);
    }

    public static List<String> getTier(int tier) {
        return Main.CLASSES.stream().filter(c -> c.tier == tier).map(c -> c.name).toList();
    }

    public static List<FEClass> getPromotions(String name) {
        FEClass origin = findByName(name);
        List<FEClass> promotions = new ArrayList<>();
        promotions.add(origin == null ? null : origin.promotion1);
        promotions.add(origin == null ? null : origin.promotion2);
        return promotions;
    }
}
