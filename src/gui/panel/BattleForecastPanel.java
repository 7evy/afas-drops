package gui.panel;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.FEWeapon;
import model.Stats;
import model.Support.SupportBonus;
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
        super(new GridLayout(1, 4, 10, 10));

        JPanel hitPanel = new JPanel();
        hitPanel.setLayout(new GridLayout(1, 2));
        hitRateDefender = new JLabel();
        hitRateAttacker = new JLabel();
        hitPanel.add(hitRateDefender);
        hitPanel.add(hitRateAttacker);

        JPanel critPanel = new JPanel();
        critPanel.setLayout(new GridLayout(1, 2));
        critRateDefender = new JLabel();
        critRateAttacker = new JLabel();
        critPanel.add(critRateDefender);
        critPanel.add(critRateAttacker);

        JPanel damagePanel = new JPanel();
        damagePanel.setLayout(new GridLayout(1, 2));
        damageDefender = new JLabel();
        damageAttacker = new JLabel();
        damagePanel.add(damageDefender);
        damagePanel.add(damageAttacker);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new GridLayout(4, 1));

        JPanel arrowsPanel = new JPanel();
        previousButton = new JButton("▶");
        nextButton = new JButton("◀");
        arrowsPanel.add(previousButton);
        arrowsPanel.add(nextButton);

        resultProbability = new JLabel();

        JPanel hpLeftPanel = new JPanel();
        hpLeftPanel.setLayout(new GridLayout(1, 2));
        hpDefender = new JLabel();
        hpAttacker = new JLabel();
        hpLeftPanel.add(hpDefender);
        hpLeftPanel.add(hpAttacker);

        JPanel simulationDetailsPanel = new JPanel();
        simulationDetailsPanel.setLayout(new BoxLayout(simulationDetailsPanel, BoxLayout.Y_AXIS));

        resultPanel.add(arrowsPanel);
        resultPanel.add(resultProbability);
        resultPanel.add(hpLeftPanel);
        resultPanel.add(simulationDetailsPanel);

        add(hitPanel);
        add(critPanel);
        add(damagePanel);
        add(resultPanel);
    }

    public void refresh(Stats statsAttacker, FEWeapon weaponAttacker, SupportBonus supportAttacker,
                        Stats statsDefender, FEWeapon weaponDefender, SupportBonus supportDefender) {
        int attackSpeedAttacker = statsAttacker.speed - statsAttacker.constitution > weaponAttacker.weight ? 0 : statsAttacker.constitution - weaponAttacker.weight;
        int attackSpeedDefender = statsDefender.speed - statsDefender.constitution > weaponDefender.weight ? 0 : statsDefender.constitution - weaponDefender.weight;

        boolean attackerDoubles = attackSpeedAttacker > attackSpeedDefender + 3;
        boolean defenderDoubles = attackSpeedDefender > attackSpeedAttacker + 3;

        int hitRateAttacker = computeHitRate(
                weaponAttacker.hit, statsAttacker.skill, statsAttacker.luck, supportAttacker.hit,
                attackSpeedDefender, statsDefender.luck, supportDefender.avoid);
        int hitRateDefender = computeHitRate(
                weaponDefender.hit, statsDefender.skill, statsDefender.luck, supportDefender.hit,
                attackSpeedAttacker, statsAttacker.luck, supportAttacker.avoid);

        int critRateAttacker = computeCritRate(
                weaponAttacker.crit, statsAttacker.skill, supportAttacker.crit,
                statsDefender.luck, supportDefender.dodge);
        int critRateDefender = computeCritRate(
                weaponDefender.crit, statsDefender.skill, supportDefender.crit,
                statsAttacker.luck, supportAttacker.dodge);

        int damageAttacker = Math.max(0, statsAttacker.strength + weaponAttacker.might - statsDefender.defence);
        int damageDefender = Math.max(0, statsDefender.strength + weaponDefender.might - statsAttacker.defence);

        this.hitRateAttacker.setText(hitRateAttacker + "%");
        this.hitRateDefender.setText(hitRateDefender + "%");

        this.critRateAttacker.setText(critRateAttacker + "%");
        this.critRateDefender.setText(critRateDefender + "%");

        this.damageAttacker.setText(damageAttacker + (attackerDoubles ? " x2" : ""));
        this.damageDefender.setText(damageDefender + (defenderDoubles ? " x2" : ""));

        List<BattleOutcome> outcomes = simulate(
                statsAttacker.hitpoints, hitRateAttacker, critRateAttacker, damageAttacker, attackerDoubles,
                statsDefender.hitpoints, hitRateDefender, critRateDefender, damageDefender, defenderDoubles);
        AtomicInteger iterator = new AtomicInteger(0);

        previousButton.addActionListener(ignored -> {
            if (iterator.incrementAndGet() > outcomes.size()) {
                iterator.set(0);
            }
            refreshResultPanel(outcomes.get(iterator.get()), statsAttacker.hitpoints, statsDefender.hitpoints);
        });
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

    private int computeHitRate(int attackerWeaponHit, int attackerSkill, int attackerLuck, int attackerSupportHit,
                               int defenderAttackSpeed, int defenderLuck, int defenderSupportAvoid) {
        return Math.max(0, Math.min(100,
                (attackerWeaponHit * 2 + attackerSkill * 4 + attackerLuck + attackerSupportHit
                        - defenderAttackSpeed * 4 - defenderLuck * 2 - defenderSupportAvoid) / 2));
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
            int maxHpAttacker, int hitRateAttacker, int critRateAttacker, int damageAttacker, boolean attackerDoubles,
            int maxHpDefender, int hitRateDefender, int critRateDefender, int damageDefender, boolean defenderDoubles
    ) {
        List<BattleOutcome> outcomes = new LinkedList<>();
        // First attack
        List<BattleOutcome> attackerTurn = simulateAttack(new BattleOutcome(maxHpAttacker, maxHpDefender, 100),
                hitRateAttacker, critRateAttacker, damageAttacker, BattleOutcome::damageDefender);
        // End battle if defender dies, otherwise second attack
        List<BattleOutcome> defenderTurn = new ArrayList<>();
        for (BattleOutcome outcome : attackerTurn) {
            if (outcome.hpDefender == 0) {
                outcomes.add(outcome);
            } else {
                defenderTurn.addAll(simulateAttack(outcome, hitRateDefender, critRateDefender, damageDefender, BattleOutcome::damageAttacker));
            }
        }
        // End of battle if attacker dies, otherwise check for follow-up
        for (BattleOutcome outcome : defenderTurn) {
            if (outcome.hpAttacker == 0) {
                outcomes.add(outcome);
            } else {
                if (attackerDoubles) {
                    outcomes.addAll(simulateAttack(outcome, hitRateAttacker, critRateAttacker, damageAttacker, BattleOutcome::damageDefender));
                }
                else if (defenderDoubles) {
                    outcomes.addAll(simulateAttack(outcome, hitRateDefender, critRateDefender, damageDefender, BattleOutcome::damageAttacker));
                } else {
                    outcomes.add(outcome);
                }
            }
        }
        return outcomes;
    }

    private List<BattleOutcome> simulateAttack(BattleOutcome origin, int hitRate, int critRate, int damage, TriFunction<BattleOutcome, Integer, Float, BattleOutcome> nextOutcome) {
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
        return outcomes;
    }

    record BattleOutcome(int hpAttacker, int hpDefender, float percentProbability) {
        public BattleOutcome damageAttacker(int damage, float probability) {
            return new BattleOutcome(Math.max(0, hpAttacker - damage), hpDefender, probability * percentProbability / 100);
        }

        public BattleOutcome damageDefender(int damage, float probability) {
            return new BattleOutcome(hpAttacker, Math.max(0, hpDefender - damage), probability * percentProbability / 100);
        }
    }
}
