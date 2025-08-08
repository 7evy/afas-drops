package utils;

import model.FEClass;
import model.FEWeapon;
import model.Skill;
import model.Stats;
import model.Support;
import model.WeaponEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public record BattleSimulation(
        Stats initiatorStats, Stats retaliatorStats,
        int initiatorHitRate, int retaliatorHitRate,
        int initiatorCritRate, int retaliatorCritRate,
        int initiatorDamage, int retaliatorDamage,
        boolean initiatorDoubles, boolean retaliatorDoubles,
        Set<WeaponEffect> initiatorEffects, Set<WeaponEffect> retaliatorEffects,
        Set<Skill> initiatorSkills, Set<Skill> retaliatorSkills
) {
    public static BattleSimulation setUp(Stats initiatorStats, Stats retaliatorStats,
                                         FEWeapon initiatorWeapon, FEWeapon retaliatorWeapon,
                                         FEClass initiatorClass, FEClass retaliatorClass,
                                         Set<Skill> initiatorSkills, Set<Skill> retaliatorSkills,
                                         Support.SupportBonus initiatorSupport, Support.SupportBonus retaliatorSupport) {
        int initiatorAttackSpeed = attackSpeed(initiatorStats, initiatorWeapon);
        int retaliatorAttackSpeed = attackSpeed(retaliatorStats, retaliatorWeapon);

        int initiatorWeaponTriangle = initiatorWeapon.type.triangleAdvantageAgainst(retaliatorWeapon.type);
        int retaliatorWeaponTriangle = -initiatorWeaponTriangle;

        return new BattleSimulation(
                initiatorStats,
                retaliatorStats,
                hitRate(initiatorWeapon, initiatorStats, initiatorSupport, retaliatorAttackSpeed, retaliatorStats, retaliatorSupport, initiatorWeaponTriangle),
                hitRate(retaliatorWeapon, retaliatorStats, retaliatorSupport, initiatorAttackSpeed, initiatorStats, initiatorSupport, retaliatorWeaponTriangle),
                critRate(initiatorWeapon, initiatorStats, initiatorSupport, retaliatorStats, retaliatorSupport),
                critRate(retaliatorWeapon, retaliatorStats, retaliatorSupport, initiatorStats, initiatorSupport),
                damage(initiatorWeapon, initiatorStats, initiatorSupport, retaliatorStats, retaliatorSupport, retaliatorClass, initiatorWeaponTriangle),
                damage(retaliatorWeapon, retaliatorStats, retaliatorSupport, initiatorStats, initiatorSupport, initiatorClass, retaliatorWeaponTriangle),
                initiatorAttackSpeed > retaliatorAttackSpeed + 3,
                retaliatorAttackSpeed > initiatorAttackSpeed + 3,
                new HashSet<>(initiatorWeapon.effects),
                new HashSet<>(retaliatorWeapon.effects),
                new HashSet<>(initiatorSkills),
                new HashSet<>(retaliatorSkills)
        );
    }

    private static int attackSpeed(Stats stats, FEWeapon weapon) {
        return stats.speed + (stats.constitution > weapon.weight ? 0 : stats.constitution - weapon.weight);
    }

    private static int damage(FEWeapon weapon, Stats attackerStats, Support.SupportBonus attackerSupport,
                              Stats defenderStats, Support.SupportBonus defenderSupport, FEClass defenderClass,
                              int weaponTriangleAdvantage) {
        Stats.Stat effectiveStat =
                weapon.effects.contains(WeaponEffect.UsesMag) ? Stats.Stat.MAG :
                        weapon.effects.contains(WeaponEffect.UsesStr) ? Stats.Stat.STR :
                                weapon.type.effectiveStat;
        Stats.Stat targetStat =
                weapon.effects.contains(WeaponEffect.TargetsRes) ? Stats.Stat.RES :
                        weapon.effects.contains(WeaponEffect.TargetsDef) ? Stats.Stat.DEF :
                                weapon.type.targetStat;

        int weaponEffectiveness = defenderClass.categories.stream().anyMatch(weapon.effectiveness::contains) ? 3 : 1;

        return Math.max(0, (attackerStats.get(effectiveStat) * 2 + attackerSupport.damage
                + weapon.might * 2 * weaponEffectiveness + weaponTriangleAdvantage * 4
                - defenderStats.get(targetStat) * 2 - defenderSupport.protection) / 2);
    }

    private static int hitRate(
            FEWeapon weapon, Stats attackerStats, Support.SupportBonus attackerSupport,
            int defenderAttackSpeed, Stats defenderStats, Support.SupportBonus defenderSupport,
            int weaponTriangleAdvantage
    ) {
        return Math.max(0, Math.min(100,
                (weapon.hit * 2 + attackerStats.skill * 4 + attackerStats.luck + attackerSupport.hit
                        - defenderAttackSpeed * 4 - defenderStats.luck * 2 - defenderSupport.avoid
                        + weaponTriangleAdvantage * 20) / 2));
    }

    private static int critRate(FEWeapon weapon, Stats attackerStats, Support.SupportBonus attackerSupport,
                         Stats defenderStats, Support.SupportBonus defenderSupport) {
        return Math.max(0, Math.min(100,
                (attackerStats.skill + weapon.crit * 2 + attackerSupport.crit
                        - defenderStats.luck * 2 - defenderSupport.dodge) / 2));
    }

    public List<BattleOutcome> launch() {
        List<BattleOutcome> outcomes = new LinkedList<>();
        // First attack
        List<BattleOutcome> initiatorTurn = simulateAttack(
                new BattleOutcome(initiatorStats.hitpoints, retaliatorStats.hitpoints, 100, new ArrayList<>()),
                initiatorStats, retaliatorStats, initiatorHitRate, initiatorCritRate, initiatorDamage, initiatorEffects, initiatorSkills, retaliatorSkills, true);
        // End battle if retaliator dies, otherwise second attack
        List<BattleOutcome> retaliatorTurn = new ArrayList<>();
        for (BattleOutcome outcome : initiatorTurn) {
            if (outcome.retaliatorHp() == 0) {
                outcomes.add(outcome);
            } else {
                retaliatorTurn.addAll(simulateAttack(outcome,
                        retaliatorStats, initiatorStats, retaliatorHitRate, retaliatorCritRate, retaliatorDamage,
                        retaliatorEffects, retaliatorSkills, initiatorSkills, false));
            }
        }
        // End battle if initiator dies, otherwise check for follow-ups
        for (BattleOutcome outcome : retaliatorTurn) {
            if (outcome.initiatorHp() == 0) {
                outcomes.add(outcome);
            } else {
                if (initiatorDoubles) {
                    outcomes.addAll(simulateAttack(outcome,
                            initiatorStats, retaliatorStats, initiatorHitRate, initiatorCritRate, initiatorDamage,
                            initiatorEffects, initiatorSkills, retaliatorSkills, true));
                }
                else if (retaliatorDoubles) {
                    outcomes.addAll(simulateAttack(outcome,
                            retaliatorStats, initiatorStats, retaliatorHitRate, retaliatorCritRate, retaliatorDamage,
                            retaliatorEffects, retaliatorSkills, initiatorSkills, false));
                } else {
                    outcomes.add(outcome);
                }
            }
        }
        return outcomes;
    }

    private static List<BattleOutcome> simulateAttack(BattleOutcome origin, Stats attackerStats, Stats defenderStats,
                                               int hitRate, int critRate, int damage, Set<WeaponEffect> effects,
                                               Set<Skill> attackerSkills, Set<Skill> defenderSkills,
                                               boolean isInitiatorAttacker) {
        List<BattleOutcome> outcomes = new ArrayList<>();
        QuintFunction<BattleOutcome, Integer, Integer, Float, BattleLog, BattleOutcome> nextOutcome =
                isInitiatorAttacker ? BattleOutcome::attackRetaliator : BattleOutcome::attackInitiator;
        // Miss
        if (hitRate < 100) {
            outcomes.add(nextOutcome.apply(origin, 0, 0, (float) (100 - hitRate),
                    BattleLog.miss(isInitiatorAttacker)));
        }
        if (hitRate > 0) {
            int heal = effects.contains(WeaponEffect.Lifesteal) ? -1 : 0; // Negative damage to count as healing

            // Proc skills are mutually exclusive and proc chances are always truncated to integers
            // They are checked in sequence from least to most probable then most to least damaging
            float astraChance = skillProcChance(attackerSkills, Skill.Astra, attackerStats);
            float lunaChance = skillProcChance(attackerSkills, Skill.Luna, attackerStats) * (100 - astraChance) / 100f;
            float solChance = skillProcChance(attackerSkills, Skill.Sol, attackerStats) * (100 - lunaChance) / 100f;

            // TODO probability multiplier for recursion
            // TODO separate proc skills (exclusive) from passive ones
            if (astraChance > 0) {
                effects.remove(WeaponEffect.Brave); // Astra hits are unaffected by brave weapons
//                outcomes = outcomes.stream().flatMap(outcome -> {
//                    Stream<BattleOutcome> astraIterator = outcome;
//                    for (int i = 0; i < 5; i++) {
//                        astraIterator = simulateAttack(astraIterator,
//                                attackerStats, defenderStats, hitRate, critRate, damage / 2, effects,
//                                Collections.emptySet(), // Attacker cannot proc any skill during Astra
//                                defenderSkills, isInitiatorAttacker);
//                    }
//                }).toList();
                effects.add(WeaponEffect.Brave);
            }

            // Normal hit
            if (critRate < 100) {
                outcomes.add(nextOutcome.apply(origin, damage, heal * damage, hitRate * (100 - critRate) / 100f,
                        BattleLog.flatDamage(isInitiatorAttacker, damage)));
            }
            // Crit
            if (critRate > 0) {
                outcomes.add(nextOutcome.apply(origin, damage * 3, heal * damage * 3,hitRate * critRate / 100f,
                        BattleLog.crit(isInitiatorAttacker, damage)));
            }
        }
        // Brave weapon
        if (effects.contains(WeaponEffect.Brave)) {
            effects.remove(WeaponEffect.Brave);
            outcomes = outcomes.stream().flatMap(outcome ->
                    simulateAttack(outcome, attackerStats, defenderStats, hitRate, critRate, damage, effects,
                            attackerSkills, defenderSkills, isInitiatorAttacker).stream()
            ).toList();
            effects.add(WeaponEffect.Brave);
        }
        return outcomes;
    }

    private static float skillProcChance(Set<Skill> skills, Skill skill, Stats stats) {
        return skills.contains(skill) ? skill.triggerRate.apply(stats) : 0;
    }
}
