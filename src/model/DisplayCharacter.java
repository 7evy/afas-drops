package model;

public class DisplayCharacter {
    public FECharacter data;
    public FEClass secondClass;
    public FEClass thirdClass;

    public DisplayCharacter(FECharacter character, FEClass tier2, FEClass tier3) {
        data = character;
        secondClass = tier2;
        thirdClass = tier3;
    }
}
