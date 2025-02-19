package utils;

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

    public static List<FEClass> getDirectPromotions(String name) {
        FEClass origin = findByName(name);
        List<FEClass> promotions = new ArrayList<>();
        promotions.add(origin == null ? null : origin.promotion1);
        promotions.add(origin == null ? null : origin.promotion2);
        return promotions;
    }

    public static List<String> getPromotionTree(String name) {
        FEClass origin = findByName(name);
        if (origin == null) {
            throw new RuntimeException("Class " + name + " does not exist");
        }
        List<String> tree = new ArrayList<>();
        tree.add(name);
        if (origin.tier < 3) {
            tree.add(origin.promotion1.name);
            tree.add(origin.promotion2.name);
            if (origin.tier == 1) {
                tree.add(origin.promotion1.promotion1.name);
                tree.add(origin.promotion2.promotion1.name);
                tree.add(origin.promotion1.promotion2.name);
                tree.add(origin.promotion2.promotion2.name);
            }
        }
        return tree;
    }
}
