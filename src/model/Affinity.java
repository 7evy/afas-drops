package model;

public enum Affinity {
    Light(5, 0, 5, 0, 1, 1),
    Dark(5, 5, 5, 5, 0, 0),
    Fire(5, 5, 5, 0, 1, 0),
    Ice(5, 5, 0, 5, 0, 1),
    Wind(5, 0, 5, 5, 1, 0),
    Thunder(0, 5, 5, 5, 0, 1),
    Anima(0, 5, 0, 5, 1, 1);

    public final Support.SupportBonus bonus;

    Affinity(int hit, int avoid, int crit, int dodge, int damage, int protection) {
        this.bonus = new Support.SupportBonus(hit, avoid, crit, dodge, damage, protection);
    }
}
