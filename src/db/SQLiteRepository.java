package db;

import init.Main;
import model.Affinity;
import model.Effect;
import model.FECharacter;
import model.FEClass;
import model.FEWeapon;
import model.Skill;
import model.Stats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static List<FEClass> fetchAllClasses() {
        try (Connection conn = DriverManager.getConnection(SQLITE_DB_FILE_PATH);
            Statement stmt = conn.createStatement()
        ) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM class ORDER BY id;");
            List<FEClass> allClasses = new ArrayList<>();
            List<Integer> firstPromotions = new ArrayList<>();
            List<Integer> secondPromotions = new ArrayList<>();
            while (rs.next()) {
                allClasses.add(extractClass(rs));
                int firstPromotion = rs.getInt("promotion_1");
                firstPromotions.add(rs.wasNull() ? null : firstPromotion);
                int secondPromotion = rs.getInt("promotion_2");
                secondPromotions.add(rs.wasNull() ? null : secondPromotion);
            }
            extractPromotions(allClasses, firstPromotions, secondPromotions);
            Main.CLASSES = allClasses;
            return allClasses;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<FECharacter> fetchAllCharacters(List<FEClass> allClasses) {
        try (Connection conn = DriverManager.getConnection(SQLITE_DB_FILE_PATH);
            Statement stmt = conn.createStatement()
        ) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM character ORDER BY id;");
            List<FECharacter> allCharacters = new ArrayList<>();
            while (rs.next()) {
                allCharacters.add(extractCharacter(rs, allClasses));
            }
            Main.CHARACTERS = allCharacters;
            return allCharacters;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<FEWeapon> fetchAllWeapons() {
        try (Connection conn = DriverManager.getConnection(SQLITE_DB_FILE_PATH);
            Statement stmt = conn.createStatement()
        ) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM weapon ORDER BY id;");
            List<FEWeapon> allWeapons = new ArrayList<>();
            while (rs.next()) {
                allWeapons.add(extractWeapon(rs));
            }
            Main.WEAPONS = allWeapons;
            return allWeapons;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static FECharacter extractCharacter(ResultSet rs, List<FEClass> allClasses) throws SQLException {
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
        character.baseClass = allClasses.get(rs.getInt("start_class"));
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
        feWeapon.effects = Arrays.stream(rs.getString("effects").split(","))
                .filter(e -> !e.isBlank())
                .map(Effect::valueOf)
                .toList();
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
        return feWeapon;
    }

    private static void extractPromotions(
        List<FEClass> allClasses,
        List<Integer> firstPromotions,
        List<Integer> secondPromotions
    ) throws SQLException {
        for (int i = 0; i < allClasses.size(); i++) {
            FEClass currentClass = allClasses.get(i);
            currentClass.promotion1 = firstPromotions.get(i) == null ?
                    null : allClasses.get(firstPromotions.get(i));
            currentClass.promotion2 = secondPromotions.get(i) == null ?
                    null : allClasses.get(secondPromotions.get(i));
        }
    }

    public static void newClass() {
        int id = countClasses();
        execute("INSERT INTO class VALUES("
            + id + ",'New',1,null,null,"
            + "20,20,20,20,20,20,20,"
            + "0,0,0,0,0,0,0,0,5,"
            + "'None','None');"
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
            + "0,0,0,0,0,0,0);"
        );
    }

    private static int countWeapons() {
        return count("SELECT COUNT(*) FROM weapon;");
    }

    public static void updateClass(FEClass feClass) {
        execute("UPDATE class SET "
            + "name = '" + feClass.name + "',"
            + "tier = " + feClass.tier + ","
            + "promotion_1 = " + feClass.promotion1 + ","
            + "promotion_2 = " + feClass.promotion2 + ","
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
            + "acquired_skill = '" + feClass.acquiredSkill + "'"
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
                + "effects = '" + String.join(",", weapon.effects.stream().map(Effect::name).toArray(String[]::new)) + "',"
                + "skill = '" + weapon.skill + "',"
                + "bonus_STR = " + weapon.bonuses.strength + ","
                + "bonus_MAG = " + weapon.bonuses.magic + ","
                + "bonus_SKL = " + weapon.bonuses.skill + ","
                + "bonus_SPD = " + weapon.bonuses.speed + ","
                + "bonus_LUK = " + weapon.bonuses.luck + ","
                + "bonus_DEF = " + weapon.bonuses.defence + ","
                + "bonus_RES = " + weapon.bonuses.resistance
                + " WHERE id = " + weapon.id + ";");
    }
}
