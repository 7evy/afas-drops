package utils;

import init.Main;
import model.FECharacter;

public class CharacterUtils {
    private CharacterUtils() {}

    public static FECharacter findByName(String name) {
        return Main.CHARACTERS.stream().filter(c -> c.name.equals(name)).findFirst().orElse(null);
    }
}
