package utils;

import init.Main;
import model.FEClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClassUtils {
    private ClassUtils() {}

    public static FEClass findByName(String name) {
        return Main.CLASSES.stream().filter(c -> c.name.equals(name)).findFirst().orElse(null);
    }

    public static FEClass[] getTier(int tier) {
        return Main.CLASSES.stream().filter(c -> c.tier == tier).toArray(FEClass[]::new);
    }

    public static FEClass[] getDirectPromotions(FEClass origin) {
        return new FEClass[]{
            origin == null ? null : origin.promotion1,
            origin == null ? null : origin.promotion2
        };
    }

    public static FEClass[] getPromotionTree(FEClass origin) {
        List<FEClass> tree = new ArrayList<>();
        tree.add(origin);
        if (origin.tier < 3) {
            tree.add(origin.promotion1);
            tree.add(origin.promotion2);
            if (origin.tier == 1) {
                tree.add(origin.promotion1.promotion1);
                tree.add(origin.promotion2.promotion1);
                tree.add(origin.promotion1.promotion2);
                tree.add(origin.promotion2.promotion2);
            }
        }
        return tree.stream().filter(Objects::nonNull).toArray(FEClass[]::new);
    }
}
