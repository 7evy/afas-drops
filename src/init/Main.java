package init;

import db.SQLiteRepository;
import gui.GUI;
import model.FECharacter;
import model.FEClass;
import model.FEWeapon;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static Map<String, FEClass> CLASSES = new HashMap<>();
    public static Map<String, FECharacter> CHARACTERS = new HashMap<>();
    public static Map<String, FEWeapon> WEAPONS = new HashMap<>();
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                SQLiteRepository.initDB();
                GUI.init();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
