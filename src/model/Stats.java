package model;

public class Stats {
    public int hitpoints;
    public int strength;
    public int magic;
    public int skill;
    public int speed;
    public int luck;
    public int defence;
    public int resistance;
    public int constitution;

    public Stats() {}

    public Stats(int hp, int str, int mag, int skl, int spd, int luk, int def, int res, int con) {
        hitpoints = hp;
        strength = str;
        magic = mag;
        skill = skl;
        speed = spd;
        luck = luk;
        defence = def;
        resistance = res;
        constitution = con;
    }

    public Stats clone() {
        return new Stats(hitpoints, strength, magic, skill, speed, luck, defence, resistance, constitution);
    }

    public Stats add(Stats stats) {
        return new Stats(
            hitpoints + stats.hitpoints,
            strength + stats.strength,
            magic + stats.magic,
            skill + stats.skill,
            speed + stats.speed,
            luck + stats.luck,
            defence + stats.defence,
            resistance + stats.resistance,
            constitution + stats.constitution
        );
    }

    public Stats growth(int levelDiff) {
        return new Stats(
            Math.round(levelDiff * hitpoints / 100f),
            Math.round(levelDiff * strength / 100f),
            Math.round(levelDiff * magic / 100f),
            Math.round(levelDiff * skill / 100f),
            Math.round(levelDiff * speed / 100f),
            Math.round(levelDiff * luck / 100f),
            Math.round(levelDiff * defence / 100f),
            Math.round(levelDiff * resistance / 100f),
            constitution
        );
    }

    public Stats cap(Stats caps) {
        return new Stats(
            Math.min(caps.hitpoints, hitpoints),
            Math.min(caps.strength, strength),
            Math.min(caps.magic, magic),
            Math.min(caps.skill, skill),
            Math.min(caps.speed, speed),
            Math.min(caps.luck, luck),
            Math.min(caps.defence, defence),
            Math.min(caps.resistance, resistance),
            Math.min(caps.constitution, constitution)
        );
    }

    public static Stats computeBaseClassAtLevel(FECharacter character, int level) {
        int levelDiff = level - (character.baseLevel - 1) % 20 - 1;
        if (levelDiff < 0) {
            throw new RuntimeException("Level can't be under " + ((character.baseLevel - 1) % 20 - 1));
        }
        return character.bases
                .add(character.growths.growth(levelDiff))
                .cap(character.baseClass.caps);
    }

    public static Stats computeSecondClassAtLevel(FECharacter character, int level, Stats promotionBonuses, Stats caps) {
        if (level < 1) {
            throw new RuntimeException("Level can't be under 1");
        }
        return computeBaseClassAtLevel(character, 20)
            .add(promotionBonuses)
            .add(character.growths.growth(level - 1))
            .cap(caps);
    }

    public static Stats computeThirdClassAtLevel(FECharacter character, int level, Stats firstPromotionBonuses, Stats secondPromotionBonuses, Stats caps1, Stats caps2) {
        if (level < 1) {
            throw new RuntimeException("Level can't be under 1");
        }
        return computeSecondClassAtLevel(character, 20, firstPromotionBonuses, caps1)
            .add(secondPromotionBonuses)
            .add(character.growths.growth(level - 1))
            .cap(caps2);
    }

    public static int computeStat(int stat, int bonus, int growth, int cap, int levelDiff) {
        return Math.min(cap, (levelDiff * growth) / 100 + bonus + stat);
    }

    public int get(Stat stat) {
        return switch(stat) {
            case HP -> hitpoints;
            case STR -> strength;
            case MAG -> magic;
            case SKL -> skill;
            case SPD -> speed;
            case LUK -> luck;
            case DEF -> defence;
            case RES -> resistance;
            case CON -> constitution;
        };
    }

    public void set(Stat stat, int value) {
        switch(stat) {
            case HP -> hitpoints = value;
            case STR -> strength = value;
            case MAG -> magic = value;
            case SKL -> skill = value;
            case SPD -> speed = value;
            case LUK -> luck = value;
            case DEF -> defence = value;
            case RES -> resistance = value;
            case CON -> constitution = value;
        };
    }

    public enum Stat {
        HP("HP"),
        STR("Strength"),
        MAG("Magic"),
        SKL("Skill"),
        SPD("Speed"),
        LUK("Luck"),
        DEF("Defence"),
        RES("Resistance"),
        CON("Constitution");

        public final String label;

        Stat(String label) {
            this.label = label;
        }
    }
}
