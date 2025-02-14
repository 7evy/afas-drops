package init;

import db.SQLiteRepository;
import gui.GUI;
import model.FECharacter;
import model.FEClass;
import model.FEWeapon;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<FEClass> CLASSES = new ArrayList<>();
    public static List<FECharacter> CHARACTERS = new ArrayList<>();
    public static List<FEWeapon> WEAPONS = new ArrayList<>();
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                SQLiteRepository.initDB();
                GUI.actualizeClasses();
                GUI.actualizeCharacters();
                if (CLASSES.isEmpty()) {
                    SQLiteRepository.newClass();
                }
                if (CHARACTERS.isEmpty()) {
                    SQLiteRepository.newCharacter();
                }
                if (WEAPONS.isEmpty()) {
                    SQLiteRepository.newWeapon();
                }
                GUI.init();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
