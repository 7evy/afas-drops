package model;

import java.util.List;

public class FEClass extends FEObject implements DisplayedObject {
    public int tier;
    public FEClass promotion1;
    public FEClass promotion2;
    public Stats caps;
    public Stats bonuses;
    public int movement;
    public Skill innateSkill;
    public Skill acquiredSkill;
    public List<WeaponType> weapons;
    public List<ClassCategory> categories;

    public Integer getPromotion1() {
        return promotion1 == null ? null : promotion1.id;
    }

    public Integer getPromotion2() {
        return promotion2 == null ? null : promotion2.id;
    }

    public FEClass clone() {
        FEClass clone = new FEClass();
        clone.id = id;
        clone.name = name;
        clone.tier = tier;
        clone.promotion1 = promotion1;
        clone.promotion2 = promotion2;
        clone.caps = caps.clone();
        clone.bonuses = bonuses.clone();
        clone.movement = movement;
        clone.innateSkill = innateSkill;
        clone.acquiredSkill = acquiredSkill;
        clone.weapons = List.copyOf(weapons);
        clone.categories = List.copyOf(categories);
        return clone;
    }
}
