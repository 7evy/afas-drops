package model;

import java.util.List;

public class FEWeapon {
    public int id;
    public String name;
    public int might;
    public int hit;
    public int crit;
    public int weight;
    public int minRange;
    public int maxRange;
    public List<Effect> effects;
    public Skill skill;
    public Stats bonuses;

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
        return clone;
    }
}
