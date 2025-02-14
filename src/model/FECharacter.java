package model;

public class FECharacter extends FEObject {
    public String origin;
    public FEClass baseClass;
    public int baseLevel;
    public Affinity affinity;
    public Stats bases;
    public Stats growths;

    public FECharacter clone() {
        FECharacter clone = new FECharacter();
        clone.id = id;
        clone.name = name;
        clone.origin = origin;
        clone.baseClass = baseClass;
        clone.baseLevel = baseLevel;
        clone.affinity = affinity;
        clone.bases = bases.clone();
        clone.growths = growths.clone();
        return clone;
    }
}
