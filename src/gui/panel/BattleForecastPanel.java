package gui.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gui.dumb.BorderedPanel;
import model.FEClass;
import model.FEWeapon;
import model.Stats;
import model.Support.SupportBonus;
import model.WeaponEffect;
import utils.TriFunction;

public class BattleForecastPanel extends JPanel {

    private final JButton previousButton;
    private final JButton nextButton;
    private final JLabel resultProbability;

    private final JLabel hitRateAttacker;
    private final JLabel critRateAttacker;
    private final JLabel damageAttacker;
    private final JLabel hpAttacker;

    private final JLabel hitRateDefender;
    private final JLabel critRateDefender;
    private final JLabel damageDefender;
    private final JLabel hpDefender;

    public BattleForecastPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        BorderedPanel hitPanel = new BorderedPanel(0, 2);
        hitPanel.pad();
        hitPanel.setMaximumSize(new Dimension(400, 45));
        JPanel hitSubPanel = new JPanel();
        hitRateAttacker = new JLabel();
        JLabel hitRateLabel = new JLabel("    < Hit >    ");
        hitRateDefender = new JLabel();
        hitSubPanel.add(hitRateAttacker);
        hitSubPanel.add(hitRateLabel);
        hitSubPanel.add(hitRateDefender);
        hitPanel.add(hitSubPanel, BorderLayout.CENTER);

        BorderedPanel critPanel = new BorderedPanel(0, 2);
        critPanel.pad();
        critPanel.setMaximumSize(new Dimension(400, 45));
        JPanel critSubPanel = new JPanel();
        critRateAttacker = new JLabel();
        JLabel critRateLabel = new JLabel("    < Crit >    ");
        critRateDefender = new JLabel();
        critSubPanel.add(critRateAttacker);
        critSubPanel.add(critRateLabel);
        critSubPanel.add(critRateDefender);
        critPanel.add(critSubPanel, BorderLayout.CENTER);

        BorderedPanel damagePanel = new BorderedPanel(0, 2);
        damagePanel.pad();
        damagePanel.setMaximumSize(new Dimension(400, 45));
        JPanel damageSubPanel = new JPanel();
        damageAttacker = new JLabel();
        JLabel damageLabel = new JLabel("   < Damage >   ");
        damageDefender = new JLabel();
        damageSubPanel.add(damageAttacker);
        damageSubPanel.add(damageLabel);
        damageSubPanel.add(damageDefender);
        damagePanel.add(damageSubPanel, BorderLayout.CENTER);

        BorderedPanel arrowsPanel = new BorderedPanel(0, 2);
        arrowsPanel.pad();
        arrowsPanel.setMaximumSize(new Dimension(400, 70));
        JPanel arrowsSubPanel = new JPanel();
        nextButton = new JButton("▶");
        previousButton = new JButton("◀");
        arrowsSubPanel.add(previousButton);
        arrowsSubPanel.add(nextButton);
        arrowsPanel.add(arrowsSubPanel, BorderLayout.CENTER);

        resultProbability = new JLabel();
        resultProbability.setAlignmentX(CENTER_ALIGNMENT);

        JPanel hpLeftPanel = new JPanel();
        hpLeftPanel.setMaximumSize(new Dimension(400, 60));
        hpAttacker = new JLabel();
        JLabel hpLeftLabel = new JLabel("   < HP left >   ");
        hpDefender = new JLabel();
        hpLeftPanel.add(hpAttacker);
        hpLeftPanel.add(hpLeftLabel);
        hpLeftPanel.add(hpDefender);

        JPanel simulationDetailsPanel = new JPanel();
        simulationDetailsPanel.setLayout(new BoxLayout(simulationDetailsPanel, BoxLayout.Y_AXIS));
        hpLeftPanel.setMaximumSize(new Dimension(400, 45));
        // TODO

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.add(arrowsPanel);
        resultPanel.add(resultProbability);
        resultPanel.add(hpLeftPanel);
        resultPanel.add(simulationDetailsPanel);

        add(hitPanel);
        add(critPanel);
        add(damagePanel);
        add(resultPanel);
    }

    public void refresh(Stats statsAttacker, FEWeapon weaponAttacker, FEClass classAttacker, SupportBonus supportAttacker,
                        Stats statsDefender, FEWeapon weaponDefender, FEClass classDefender, SupportBonus supportDefender) {
        int attackSpeedAttacker = statsAttacker.speed + (statsAttacker.constitution > weaponAttacker.weight ? 0 : statsAttacker.constitution - weaponAttacker.weight);
        int attackSpeedDefender = statsDefender.speed + (statsDefender.constitution > weaponDefender.weight ? 0 : statsDefender.constitution - weaponDefender.weight);

        boolean attackerDoubles = attackSpeedAttacker > attackSpeedDefender + 3;
        boolean defenderDoubles = attackSpeedDefender > attackSpeedAttacker + 3;

        boolean attackerBrave = weaponAttacker.effects.contains(WeaponEffect.Brave);
        boolean defenderBrave = weaponDefender.effects.contains(WeaponEffect.Brave);

        int weaponTriangleAttacker = weaponAttacker.type.triangleAdvantageAgainst(weaponDefender.type);
        int weaponTriangleDefender = -weaponTriangleAttacker;

        int weaponEffectivenessAttacker = classDefender.categories.stream().anyMatch(weaponAttacker.effectiveness::contains) ? 3 : 1;
        int weaponEffectivenessDefender = classAttacker.categories.stream().anyMatch(weaponDefender.effectiveness::contains) ? 3 : 1;

        int hitRateAttacker = computeHitRate(
                weaponAttacker.hit, statsAttacker.skill, statsAttacker.luck, supportAttacker.hit,
                attackSpeedDefender, statsDefender.luck, supportDefender.avoid, weaponTriangleAttacker);
        int hitRateDefender = computeHitRate(
                weaponDefender.hit, statsDefender.skill, statsDefender.luck, supportDefender.hit,
                attackSpeedAttacker, statsAttacker.luck, supportAttacker.avoid, weaponTriangleDefender);

        int critRateAttacker = computeCritRate(
                weaponAttacker.crit, statsAttacker.skill, supportAttacker.crit,
                statsDefender.luck, supportDefender.dodge);
        int critRateDefender = computeCritRate(
                weaponDefender.crit, statsDefender.skill, supportDefender.crit,
                statsAttacker.luck, supportAttacker.dodge);
        
        int damageAttacker = computeDamage(statsAttacker, supportAttacker.damage, statsDefender, supportDefender.protection,
                                           weaponAttacker, weaponEffectivenessAttacker, weaponTriangleAttacker);
        int damageDefender = computeDamage(statsDefender, supportDefender.damage, statsAttacker, supportAttacker.protection,
                                           weaponDefender, weaponEffectivenessDefender, weaponTriangleDefender);

        this.hitRateAttacker.setText(hitRateAttacker + "%");
        this.hitRateDefender.setText(hitRateDefender + "%");

        this.critRateAttacker.setText(critRateAttacker + "%");
        this.critRateDefender.setText(critRateDefender + "%");

        this.damageAttacker.setText(damageAttacker + (attackerDoubles ? " x2" : ""));
        this.damageDefender.setText(damageDefender + (defenderDoubles ? " x2" : ""));

        List<BattleOutcome> outcomes = simulate(
                statsAttacker.hitpoints, hitRateAttacker, critRateAttacker, damageAttacker, attackerDoubles, attackerBrave,
                statsDefender.hitpoints, hitRateDefender, critRateDefender, damageDefender, defenderDoubles, defenderBrave);
        AtomicInteger iterator = new AtomicInteger(0);

        for (ActionListener al : previousButton.getActionListeners()) {
            previousButton.removeActionListener(al);
        }
        previousButton.addActionListener(ignored -> {
            if (iterator.incrementAndGet() >= outcomes.size()) {
                iterator.set(0);
            }
            refreshResultPanel(outcomes.get(iterator.get()), statsAttacker.hitpoints, statsDefender.hitpoints);
        });
        
        for (ActionListener al : nextButton.getActionListeners()) {
            nextButton.removeActionListener(al);
        }
        nextButton.addActionListener(ignored -> {
            if (iterator.decrementAndGet() < 0) {
                iterator.set(outcomes.size() - 1);
            }
            refreshResultPanel(outcomes.get(iterator.get()), statsAttacker.hitpoints, statsDefender.hitpoints);
        });

        refreshResultPanel(outcomes.getFirst(), statsAttacker.hitpoints, statsDefender.hitpoints);

        revalidate();
        repaint();
    }

    private int computeDamage(Stats attackerStats, int attackerSupportDamage,
                              Stats defenderStats, int defenderSupportProtection,
                              FEWeapon weapon, int weaponEffectiveness, int weaponTriangle) {
        // TODO weapon target stats
        return Math.max(0, (attackerStats.strength * 2 + attackerSupportDamage
                + weapon.might * 2 * weaponEffectiveness + weaponTriangle * 4
                - defenderStats.defence * 2 - defenderSupportProtection) / 2);
    }

    private int computeHitRate(int attackerWeaponHit, int attackerSkill, int attackerLuck, int attackerSupportHit,
                               int defenderAttackSpeed, int defenderLuck, int defenderSupportAvoid, int weaponTriangleAdvantage) {
        return Math.max(0, Math.min(100,
                (attackerWeaponHit * 2 + attackerSkill * 4 + attackerLuck + attackerSupportHit
                        - defenderAttackSpeed * 4 - defenderLuck * 2 - defenderSupportAvoid
                        + weaponTriangleAdvantage * 20) / 2));
    }

    private int computeCritRate(int attackerWeaponCrit, int attackerSkill, int attackerSupportCrit,
                                int defenderLuck, int defenderSupportDodge) {
        return Math.max(0, Math.min(100,
                (attackerSkill + attackerWeaponCrit * 2 + attackerSupportCrit
                        - defenderLuck * 2 - defenderSupportDodge) / 2
        ));
    }

    private void refreshResultPanel(BattleOutcome result, int maxHpAttacker, int maxHpDefender) {
        this.resultProbability.setText(result.percentProbability + "%");

        this.hpAttacker.setText(result.hpAttacker + "/" + maxHpAttacker);
        this.hpDefender.setText(result.hpDefender + "/" + maxHpDefender);
    }

    private List<BattleOutcome> simulate(
            int maxHpAttacker, int hitRateAttacker, int critRateAttacker, int damageAttacker, boolean attackerDoubles, boolean attackerBrave,
            int maxHpDefender, int hitRateDefender, int critRateDefender, int damageDefender, boolean defenderDoubles, boolean defenderBrave
    ) {
        List<BattleOutcome> outcomes = new LinkedList<>();
        // First attack
        List<BattleOutcome> attackerTurn = simulateAttack(new BattleOutcome(maxHpAttacker, maxHpDefender, 100),
                hitRateAttacker, critRateAttacker, damageAttacker, BattleOutcome::damageDefender, attackerBrave);
        // End battle if defender dies, otherwise second attack
        List<BattleOutcome> defenderTurn = new ArrayList<>();
        for (BattleOutcome outcome : attackerTurn) {
            if (outcome.hpDefender == 0) {
                outcomes.add(outcome);
            } else {
                defenderTurn.addAll(simulateAttack(outcome, hitRateDefender, critRateDefender, damageDefender, BattleOutcome::damageAttacker, defenderBrave));
            }
        }
        // End of battle if attacker dies, otherwise check for follow-up
        for (BattleOutcome outcome : defenderTurn) {
            if (outcome.hpAttacker == 0) {
                outcomes.add(outcome);
            } else {
                if (attackerDoubles) {
                    outcomes.addAll(simulateAttack(outcome, hitRateAttacker, critRateAttacker, damageAttacker, BattleOutcome::damageDefender, attackerBrave));
                }
                else if (defenderDoubles) {
                    outcomes.addAll(simulateAttack(outcome, hitRateDefender, critRateDefender, damageDefender, BattleOutcome::damageAttacker, defenderBrave));
                } else {
                    outcomes.add(outcome);
                }
            }
        }
        return outcomes;
    }

    private List<BattleOutcome> simulateAttack(BattleOutcome origin, int hitRate, int critRate, int damage,
                                               TriFunction<BattleOutcome, Integer, Float, BattleOutcome> nextOutcome,
                                               boolean braveAttack) {
        List<BattleOutcome> outcomes = new ArrayList<>();
        // Miss
        if (hitRate < 100) {
            outcomes.add(nextOutcome.apply(origin, 0, (float) (100 - hitRate)));
        }
        if (hitRate > 0) {
            // Normal hit
            if (critRate < 100) {
                outcomes.add(nextOutcome.apply(origin, damage, hitRate * (100 - critRate) / 100f));
            }
            // Crit
            if (critRate > 0) {
                outcomes.add(nextOutcome.apply(origin, damage * 3, hitRate * critRate / 100f));
            }
        }
        // Brave weapon
        if (braveAttack) {
            outcomes = outcomes.stream().flatMap(outcome ->
                    simulateAttack(outcome, hitRate, critRate, damage, nextOutcome, false).stream()
            ).toList();
        }
        // Collapse equivalent outcomes
        return outcomes.stream().collect(Collectors.groupingBy(Function.identity()))
                .values().stream()
                .map(equalOutcomes -> new BattleOutcome(
                        equalOutcomes.getFirst().hpAttacker,
                        equalOutcomes.getFirst().hpDefender,
                        equalOutcomes.stream().reduce(0f, (p, outcome) -> p + outcome.percentProbability, Float::sum)))
                .toList();
    }

    record BattleOutcome(int hpAttacker, int hpDefender, float percentProbability) {
        public BattleOutcome damageAttacker(int damage, float probability) {
            return new BattleOutcome(Math.max(0, hpAttacker - damage), hpDefender, probability * percentProbability / 100);
        }

        public BattleOutcome damageDefender(int damage, float probability) {
            return new BattleOutcome(hpAttacker, Math.max(0, hpDefender - damage), probability * percentProbability / 100);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BattleOutcome that)) return false;
            return that.hpAttacker == hpAttacker && that.hpDefender == hpDefender;
        }

        @Override
        public int hashCode() {
            return Objects.hash(hpAttacker, hpDefender);
        }
    }
}
