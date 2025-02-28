package model;

public enum WeaponType {
    Sword, Lance, Axe, Bow,
    Light, Dark, Anima, Staff;

    private WeaponType advantage;
    private WeaponType disadvantage;

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
