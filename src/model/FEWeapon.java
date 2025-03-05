package model;

import java.util.List;

public class FEWeapon extends FEObject implements DisplayedObject {
    public int might;
    public int hit;
    public int crit;
    public int weight;
    public int minRange;
    public int maxRange;
    public Skill skill;
    public Stats bonuses;
    public WeaponType type;
    public List<WeaponEffect> effects;
    public List<ClassCategory> effectiveness;

    public FEWeapon clone() {
        FEWeapon clone = new FEWeapon();
        clone.id = id;
        clone.name = name;
        clone.might = might;
        clone.hit = hit;
        clone.crit = crit;
        clone.weight = weight;
        clone.minRange = minRange;
        clone.maxRange = maxRange;
        clone.effects = List.copyOf(effects);
        clone.skill = skill;
        clone.bonuses = bonuses.clone();
        clone.effectiveness = List.copyOf(effectiveness);
        return clone;
    }
}
