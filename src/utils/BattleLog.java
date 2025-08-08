package utils;

import model.Skill;

public record BattleLog(boolean isInitiatorAttacker, int damage, int selfDamage, boolean miss, boolean crit, Skill attackerSkill, Skill defenderSkill) {
    public static BattleLogBuilder log(boolean isInitiatorAttacker) {
        return new BattleLogBuilder(isInitiatorAttacker);
    }

    public static BattleLog flatDamage(boolean attacker, int damage) {
        return new BattleLog(attacker, damage, 0, false, false, Skill.None, Skill.None);
    }

    public static BattleLog crit(boolean attacker, int damage) {
        return new BattleLog(attacker, damage, 0, false, true, Skill.None, Skill.None);
    }

    public static BattleLog miss(boolean attacker) {
        return new BattleLog(attacker, 0, 0, true, false, Skill.None, Skill.None);
    }

    public String toString() {
        // TODO get character name
        // TODO revert to stream
        String attackerName = isInitiatorAttacker ? "Initiator" : "Retaliator";
        StringBuilder log = new StringBuilder(attackerName);
        if (miss) {
            return attackerName + " missed.";
        } else if (crit) {
            log.append(" crit");
            if (attackerSkill != Skill.None) {
                log.append(" with ").append(attackerSkill);
            }
            if (defenderSkill != Skill.None) {
                log.append(" against ").append(defenderSkill);
            }
            log.append(" for ").append(damage).append(" damage.");
        } else {
            log.append(" hit");
            if (attackerSkill != Skill.None) {
                log.append(" with ").append(attackerSkill);
            }
            if (defenderSkill != Skill.None) {
                log.append(" against ").append(defenderSkill);
            }
            log.append(" for ").append(damage).append(" damage.");
        }
        return log.toString();
    }

    public static class BattleLogBuilder {
        private final boolean isInitiatorAttacker;
        private int damage;
        private int selfDamage;
        private boolean miss;
        private boolean crit;
        private Skill attackerSkill;
        private Skill defenderSkill;

        public BattleLogBuilder(boolean isInitiatorAttacker) {
            this.isInitiatorAttacker = isInitiatorAttacker;
            this.miss = false;
            this.crit = false;
        }

        public BattleLogBuilder withBaseDamage(int damage) {
            this.damage = damage;
            return this;
        }

        public BattleLogBuilder missed() {
            this.miss = true;
            return this;
        }

        public BattleLogBuilder crit() {
            this.crit = true;
            return this;
        }

        public BattleLogBuilder andAttackerHealed(int heal) {
            this.selfDamage = heal;
            return this;
        }

        public BattleLogBuilder andAttackerSelfHit(int damage) {
            this.selfDamage = damage;
            return this;
        }

        public BattleLogBuilder andAttackerTriggered(Skill attackerSkill) {
            this.attackerSkill = attackerSkill;
            return this;
        }

        public BattleLogBuilder andDefenderTriggered(Skill defenderSkill) {
            this.defenderSkill = defenderSkill;
            return this;
        }

        public BattleLog build() {
            return new BattleLog(isInitiatorAttacker, damage, selfDamage, miss, crit, attackerSkill, defenderSkill);
        }
    }
}
