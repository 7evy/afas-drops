package db;

import init.Main;
import model.Affinity;
import model.ClassCategory;
import model.WeaponEffect;
import model.FECharacter;
import model.FEClass;
import model.FEWeapon;
import model.Skill;
import model.Stats;
import model.WeaponType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SQLiteRepository {
    private static final String SQLITE_DB_FILE_PATH = "jdbc:sqlite:sqlite/afasdrops.db";
    
    private static void execute(String query) {
    try (Connection conn = DriverManager.getConnection(SQLITE_DB_FILE_PATH);
            Statement stmt = conn.createStatement()
        ) {
            stmt.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static int count(String query) {
    try (Connection conn = DriverManager.getConnection(SQLITE_DB_FILE_PATH);
            Statement stmt = conn.createStatement()
        ) {
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initDB() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        execute("""
            CREATE TABLE IF NOT EXISTS class (
                id INTEGER,
                name TEXT UNIQUE NOT NULL,
                tier INTEGER NOT NULL,
                promotion_1 INTEGER,
                promotion_2 INTEGER,
                cap_HP INTEGER NOT NULL,
                cap_STR INTEGER NOT NULL,
                cap_MAG INTEGER NOT NULL,
                cap_SKL INTEGER NOT NULL,
                cap_SPD INTEGER NOT NULL,
                cap_DEF INTEGER NOT NULL,
                cap_RES INTEGER NOT NULL,
                bonus_HP INTEGER NOT NULL,
                bonus_STR INTEGER NOT NULL,
                bonus_MAG INTEGER NOT NULL,
                bonus_SKL INTEGER NOT NULL,
                bonus_SPD INTEGER NOT NULL,
                bonus_DEF INTEGER NOT NULL,
                bonus_RES INTEGER NOT NULL,
                bonus_CON INTEGER NOT NULL,
                movement INTEGER NOT NULL,
                innate_skill TEXT NOT NULL,
                acquired_skill TEXT NOT NULL,
                weapons TEXT,
                categories TEXT,
                PRIMARY KEY(id),
                FOREIGN KEY (promotion_1) REFERENCES class(id),
                FOREIGN KEY (promotion_2) REFERENCES class(id)
            );
        """);
        execute("""
            CREATE TABLE IF NOT EXISTS character (
                id INTEGER,
                name TEXT UNIQUE NOT NULL,
                origin TEXT NOT NULL,
                start_class INTEGER NOT NULL,
                start_level INTEGER NOT NULL,
                affinity TEXT NOT NULL,
                base_HP INTEGER NOT NULL,
                base_STR INTEGER NOT NULL,
                base_MAG INTEGER NOT NULL,
                base_SKL INTEGER NOT NULL,
                base_SPD INTEGER NOT NULL,
                base_LUK INTEGER NOT NULL,
                base_DEF INTEGER NOT NULL,
                base_RES INTEGER NOT NULL,
                base_CON INTEGER NOT NULL,
                growth_HP INTEGER NOT NULL,
                growth_STR INTEGER NOT NULL,
                growth_MAG INTEGER NOT NULL,
                growth_SKL INTEGER NOT NULL,
                growth_SPD INTEGER NOT NULL,
                growth_LUK INTEGER NOT NULL,
                growth_DEF INTEGER NOT NULL,
                growth_RES INTEGER NOT NULL,
                PRIMARY KEY (id),
                FOREIGN KEY (start_class) REFERENCES class(id)
            );
        """);
        execute("""
            CREATE TABLE IF NOT EXISTS weapon (
                id INTEGER,
                name TEXT UNIQUE NOT NULL,
                might INTEGER NOT NULL,
                hit INTEGER NOT NULL,
                crit INTEGER NOT NULL,
                weight INTEGER NOT NULL,
                min_range INTEGER NOT NULL,
                max_range INTEGER NOT NULL,
                effects TEXT NOT NULL,
                skill TEXT NOT NULL,
                bonus_STR INTEGER NOT NULL,
                bonus_MAG INTEGER NOT NULL,
                bonus_SKL INTEGER NOT NULL,
                bonus_SPD INTEGER NOT NULL,
                bonus_LUK INTEGER NOT NULL,
                bonus_DEF INTEGER NOT NULL,
                bonus_RES INTEGER NOT NULL,
                type TEXT,
                effectiveness TEXT,
                PRIMARY KEY (id)
            );
        """);

        if (countClasses() == 0) {
            newClass();
        }

        if (countCharacters() == 0) {
            newCharacter();
        }

        if (countWeapons() == 0) {
            newWeapon();
        }
    }

    public static Map<String, FEClass> fetchAllClasses() {
        try (Connection conn = DriverManager.getConnection(SQLITE_DB_FILE_PATH);
            Statement stmt = conn.createStatement()
        ) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM class ORDER BY id;");
            Map<String, FEClass> allClasses = new HashMap<>();
            Map<String, Integer> firstPromotionIds = new HashMap<>();
            Map<String, Integer> secondPromotionIds = new HashMap<>();
            while (rs.next()) {
                FEClass feClass = extractClass(rs);
                allClasses.put(feClass.name, feClass);
                int firstPromotion = rs.getInt("promotion_1");
                firstPromotionIds.put(feClass.name, rs.wasNull() ? null : firstPromotion);
                int secondPromotion = rs.getInt("promotion_2");
                secondPromotionIds.put(feClass.name, rs.wasNull() ? null : secondPromotion);
            }
            extractPromotions(allClasses, firstPromotionIds, secondPromotionIds);
            Main.CLASSES = allClasses;
            return allClasses;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, FECharacter> fetchAllCharacters(Map<String, FEClass> allClasses) {
        try (Connection conn = DriverManager.getConnection(SQLITE_DB_FILE_PATH);
            Statement stmt = conn.createStatement()
        ) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM character ORDER BY id;");
            Map<String, FECharacter> allCharacters = new HashMap<>();
            while (rs.next()) {
                FECharacter character = extractCharacter(rs, allClasses.values().stream()
                        .collect(Collectors.toMap(c -> c.id, c -> c)));
                allCharacters.put(character.name, character);
            }
            Main.CHARACTERS = allCharacters;
            return allCharacters;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, FEWeapon> fetchAllWeapons() {
        try (Connection conn = DriverManager.getConnection(SQLITE_DB_FILE_PATH);
            Statement stmt = conn.createStatement()
        ) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM weapon ORDER BY id;");
            Map<String, FEWeapon> allWeapons = new HashMap<>();
            while (rs.next()) {
                FEWeapon weapon = extractWeapon(rs);
                allWeapons.put(weapon.name, weapon);
            }
            Main.WEAPONS = allWeapons;
            return allWeapons;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static FECharacter extractCharacter(ResultSet rs, Map<Integer, FEClass> allClassesById) throws SQLException {
        FECharacter character = new FECharacter();
        character.id = rs.getInt("id");
        character.name = rs.getString("name");
        character.origin = rs.getString("origin");
        character.baseLevel = rs.getInt("start_level");
        character.affinity = Affinity.valueOf(rs.getString("affinity"));
        character.bases = new Stats(
            rs.getInt("base_HP"),
            rs.getInt("base_STR"),
            rs.getInt("base_MAG"),
            rs.getInt("base_SKL"),
            rs.getInt("base_SPD"),
            rs.getInt("base_LUK"),
            rs.getInt("base_DEF"),
            rs.getInt("base_RES"),
            rs.getInt("base_CON")
        );
        character.growths = new Stats(
            rs.getInt("growth_HP"),
            rs.getInt("growth_STR"),
            rs.getInt("growth_MAG"),
            rs.getInt("growth_SKL"),
            rs.getInt("growth_SPD"),
            rs.getInt("growth_LUK"),
            rs.getInt("growth_DEF"),
            rs.getInt("growth_RES"),
            0
        );
        character.baseClass = allClassesById.get(rs.getInt("start_class"));
        return character;
    }

    private static FEClass extractClass(ResultSet rs) throws SQLException {
        FEClass feClass = new FEClass();
        feClass.id = rs.getInt("id");
        feClass.name = rs.getString("name");
        feClass.tier = rs.getInt("tier");
        feClass.caps = new Stats(
            rs.getInt("cap_HP"),
            rs.getInt("cap_STR"),
            rs.getInt("cap_MAG"),
            rs.getInt("cap_SKL"),
            rs.getInt("cap_SPD"),
            50,
            rs.getInt("cap_DEF"),
            rs.getInt("cap_RES"),
            25
        );
        feClass.bonuses = new Stats(
            rs.getInt("bonus_HP"),
            rs.getInt("bonus_STR"),
            rs.getInt("bonus_MAG"),
            rs.getInt("bonus_SKL"),
            rs.getInt("bonus_SPD"),
            0,
            rs.getInt("bonus_DEF"),
            rs.getInt("bonus_RES"),
            rs.getInt("bonus_CON")
        );
        feClass.movement = rs.getInt("movement");
        feClass.innateSkill = Skill.valueOf(rs.getString("innate_skill"));
        feClass.acquiredSkill = Skill.valueOf(rs.getString("acquired_skill"));
        feClass.weapons = Arrays.stream(rs.getString("weapons").split(","))
                .filter(w -> !w.isBlank())
                .map(WeaponType::valueOf)
                .toList();
        feClass.categories = Arrays.stream(rs.getString("categories").split(","))
                .filter(w -> !w.isBlank())
                .map(ClassCategory::valueOf)
                .toList();
        return feClass;
    }

    private static FEWeapon extractWeapon(ResultSet rs) throws SQLException {
        FEWeapon feWeapon = new FEWeapon();
        feWeapon.id = rs.getInt("id");
        feWeapon.name = rs.getString("name");
        feWeapon.might = rs.getInt("might");
        feWeapon.hit = rs.getInt("hit");
        feWeapon.crit = rs.getInt("crit");
        feWeapon.weight = rs.getInt("weight");
        feWeapon.minRange = rs.getInt("min_range");
        feWeapon.maxRange = rs.getInt("max_range");
        feWeapon.skill = Skill.valueOf(rs.getString("skill"));
        feWeapon.bonuses = new Stats(
                0,
                rs.getInt("bonus_STR"),
                rs.getInt("bonus_MAG"),
                rs.getInt("bonus_SKL"),
                rs.getInt("bonus_SPD"),
                rs.getInt("bonus_LUK"),
                rs.getInt("bonus_DEF"),
                rs.getInt("bonus_RES"),
                0
        );
        feWeapon.type = WeaponType.valueOf(rs.getString("type"));
        feWeapon.effects = Arrays.stream(rs.getString("effects").split(","))
                .filter(e -> !e.isBlank())
                .map(WeaponEffect::valueOf)
                .toList();
        feWeapon.effectiveness = Arrays.stream(rs.getString("effectiveness").split(","))
                .filter(e -> !e.isBlank())
                .map(ClassCategory::valueOf)
                .toList();
        return feWeapon;
    }

    private static void extractPromotions(
        Map<String, FEClass> allClasses,
        Map<String, Integer> firstPromotionIds,
        Map<String, Integer> secondPromotionIds
    ) throws SQLException {
        Map<Integer, FEClass> allClassesById = allClasses.values().stream()
                .collect(Collectors.toMap(c -> c.id, c -> c));
        allClassesById.put(null, null);
        for (FEClass currentClass : allClasses.values()) {
            currentClass.promotion1 = allClassesById.get(firstPromotionIds.get(currentClass.name));
            currentClass.promotion2 = allClassesById.get(secondPromotionIds.get(currentClass.name));
        }
    }

    public static void newClass() {
        int id = countClasses();
        execute("INSERT INTO class VALUES("
                + id + ",'New',1,null,null,"
                + "20,20,20,20,20,20,20,"
                + "0,0,0,0,0,0,0,0,5,"
                + "'None','None','','');"
        );
    }

    private static int countClasses() {
        return count("SELECT COUNT(*) FROM class;");
    }

    public static void newCharacter() {
        int id = countCharacters();
        execute("INSERT INTO character VALUES("
                + id + ",'New','',0,1,'Light',1,"
                + "0,0,0,0,0,0,0,0,"
                + "60,30,30,30,30,30,30,30);"
        );
    }

    private static int countCharacters() {
        return count("SELECT COUNT(*) FROM character;");
    }

    public static void newWeapon() {
        int id = countWeapons();
        execute("INSERT INTO weapon VALUES("
                + id + ",'New',0,0,0,0,1,1,'','None',"
                + "0,0,0,0,0,0,0,'Sword','');"
        );
    }

    private static int countWeapons() {
        return count("SELECT COUNT(*) FROM weapon;");
    }

    public static void updateClass(FEClass feClass) {
        execute("UPDATE class SET "
                + "name = '" + feClass.name + "',"
                + "tier = " + feClass.tier + ","
                + "promotion_1 = " + feClass.getPromotion1() + ","
                + "promotion_2 = " + feClass.getPromotion2() + ","
                + "cap_HP = " + feClass.caps.hitpoints + ","
                + "cap_STR = " + feClass.caps.strength + ","
                + "cap_MAG = " + feClass.caps.magic + ","
                + "cap_SKL = " + feClass.caps.skill + ","
                + "cap_SPD = " + feClass.caps.speed + ","
                + "cap_DEF = " + feClass.caps.defence + ","
                + "cap_RES = " + feClass.caps.resistance + ","
                + "bonus_HP = " + feClass.bonuses.hitpoints + ","
                + "bonus_STR = " + feClass.bonuses.strength + ","
                + "bonus_MAG = " + feClass.bonuses.magic + ","
                + "bonus_SKL = " + feClass.bonuses.skill + ","
                + "bonus_SPD = " + feClass.bonuses.speed + ","
                + "bonus_DEF = " + feClass.bonuses.defence + ","
                + "bonus_RES = " + feClass.bonuses.resistance + ","
                + "bonus_CON = " + feClass.bonuses.constitution + ","
                + "movement = " + feClass.movement + ","
                + "innate_skill = '" + feClass.innateSkill + "',"
                + "acquired_skill = '" + feClass.acquiredSkill + "',"
                + "weapons = '" + String.join(",", feClass.weapons.stream().map(WeaponType::name).toArray(String[]::new)) + "',"
                + "categories = '" + String.join(",", feClass.categories.stream().map(ClassCategory::name).toArray(String[]::new)) + "'"
                + " WHERE id = " + feClass.id + ";"
        );
    }

    public static void updateCharacter(FECharacter character) {
        execute("UPDATE character SET "
                + "name = '" + character.name + "',"
                + "origin = '" + character.origin + "',"
                + "start_class = " + character.baseClass.id + ","
                + "start_level = " + character.baseLevel + ","
                + "affinity = '" + character.affinity + "',"
                + "base_HP = " + character.bases.hitpoints + ","
                + "base_STR = " + character.bases.strength + ","
                + "base_MAG = " + character.bases.magic + ","
                + "base_SKL = " + character.bases.skill + ","
                + "base_SPD = " + character.bases.speed + ","
                + "base_LUK = " + character.bases.luck + ","
                + "base_DEF = " + character.bases.defence + ","
                + "base_RES = " + character.bases.resistance + ","
                + "base_CON = " + character.bases.constitution + ","
                + "growth_HP = " + character.growths.hitpoints + ","
                + "growth_STR = " + character.growths.strength + ","
                + "growth_MAG = " + character.growths.magic + ","
                + "growth_SKL = " + character.growths.skill + ","
                + "growth_SPD = " + character.growths.speed + ","
                + "growth_LUK = " + character.growths.luck + ","
                + "growth_DEF = " + character.growths.defence + ","
                + "growth_RES = " + character.growths.resistance
                + " WHERE id = " + character.id + ";"
        );
    }

    public static void updateWeapon(FEWeapon weapon) {
        execute("UPDATE weapon SET "
                + "name = '" + weapon.name + "',"
                + "might = " + weapon.might + ","
                + "hit = " + weapon.hit + ","
                + "crit = " + weapon.crit + ","
                + "weight = " + weapon.weight + ","
                + "min_range = " + weapon.minRange + ","
                + "max_range = " + weapon.maxRange + ","
                + "skill = '" + weapon.skill + "',"
                + "bonus_STR = " + weapon.bonuses.strength + ","
                + "bonus_MAG = " + weapon.bonuses.magic + ","
                + "bonus_SKL = " + weapon.bonuses.skill + ","
                + "bonus_SPD = " + weapon.bonuses.speed + ","
                + "bonus_LUK = " + weapon.bonuses.luck + ","
                + "bonus_DEF = " + weapon.bonuses.defence + ","
                + "bonus_RES = " + weapon.bonuses.resistance + ","
                + "type = '" + weapon.type + "',"
                + "effects = '" + String.join(",", weapon.effects.stream().map(WeaponEffect::name).toArray(String[]::new)) + "',"
                + "effectiveness = '" + String.join(",", weapon.effectiveness.stream().map(ClassCategory::name).toArray(String[]::new)) + "'"
                + " WHERE id = " + weapon.id + ";");
    }
}
