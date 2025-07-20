package model;

import model.Stats.Stat;

public enum WeaponType {
    Sword(Stat.STR, Stat.DEF),
    Lance(Stat.STR, Stat.DEF),
    Axe(Stat.STR, Stat.DEF),
    Bow(Stat.STR, Stat.DEF),
    Light(Stat.MAG, Stat.RES),
    Dark(Stat.MAG, Stat.RES),
    Anima(Stat.MAG, Stat.RES),
    Staff(Stat.MAG, Stat.RES);

    private WeaponType advantage;
    private WeaponType disadvantage;
    public final Stat effectiveStat;
    public final Stat targetStat;

    WeaponType(Stat effectiveStat, Stat targetStat) {
        this.effectiveStat = effectiveStat;
        this.targetStat = targetStat;
    }

    public int triangleAdvantageAgainst(WeaponType opposed) {
        return advantage == opposed ? 1
                : disadvantage == opposed ? -1
                : 0;
    }

    static {
        ((Sword.advantage = Axe)
                .advantage = Lance)
                .advantage = Sword;
        ((Sword.disadvantage = Lance)
                .disadvantage = Axe)
                .disadvantage = Sword;
        ((Light.advantage = Dark)
                .advantage = Anima)
                .advantage = Light;
        ((Light.disadvantage = Anima)
                .disadvantage = Dark)
                .disadvantage = Light;
    }
}
