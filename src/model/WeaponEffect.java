package model;

public enum WeaponEffect {
    Brave,
    UsesMag("Uses magic"),
    UsesStr("Uses strength"),
    TargetsRes("Targets resistance"),
    TargetsDef("Targets defence"),
    Lethal,
    Silencer,
    Lifesteal,
    Devil;

    public final String displayName;

    WeaponEffect(String... displayName) {
        this.displayName = displayName.length > 0 ? displayName[0] : this.name();
    }

    public String toString() {
        return this.displayName;
    }
}
