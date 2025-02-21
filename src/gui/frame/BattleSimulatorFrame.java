package gui.frame;

import gui.dumb.BorderedButton;
import gui.dumb.BorderedLabeledComboBox;
import gui.dumb.BorderedPanel;
import gui.panel.BattleCharacterPanel;
import gui.panel.BattleForecastPanel;
import init.Main;
import model.FEWeapon;
import model.Stats;
import model.Support;
import utils.CharacterUtils;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

public class BattleSimulatorFrame extends JFrame {
    private final BattleCharacterPanel characterPanelLeft;
    private final BattleCharacterPanel characterPanelRight;
    private final BattleForecastPanel battleForecastPanel;

    private final BorderedLabeledComboBox characterLeft;
    private final BorderedLabeledComboBox characterRight;

    public BattleSimulatorFrame() {
        super("Battle Simulator");
        setPreferredSize(new Dimension(1000, 1000));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        characterPanelLeft = new BattleCharacterPanel();
        characterPanelRight = new BattleCharacterPanel();
        battleForecastPanel = new BattleForecastPanel();

        BorderedPanel mainLayout = new BorderedPanel(10, 10);
        BorderedPanel subLayout = new BorderedPanel(10, 10);
        subLayout.no(BorderLayout.EAST);
        subLayout.no(BorderLayout.WEST);
        BorderedPanel attackerLayout = new BorderedPanel(10, 10);
        BorderedPanel forecastLayout = new BorderedPanel(10, 10);
        BorderedPanel defenderLayout = new BorderedPanel(10, 10);

        List<String> characterNames = Main.CHARACTERS.stream().map(c -> c.name).toList();

        characterLeft = new BorderedLabeledComboBox("Attacker:", characterNames, characterNames.getFirst());
        characterLeft.addActionListener(() -> characterPanelLeft.refresh(
                CharacterUtils.findByName((String) characterLeft.inner.getSelectedItem())
        ), false);
        attackerLayout.add(characterLeft, BorderLayout.NORTH);
        attackerLayout.add(characterPanelLeft, BorderLayout.CENTER);

        characterRight = new BorderedLabeledComboBox("Defender:", characterNames, characterNames.getFirst());
        characterRight.addActionListener(() -> characterPanelRight.refresh(
                CharacterUtils.findByName((String) characterRight.inner.getSelectedItem())
        ), false);
        defenderLayout.add(characterRight, BorderLayout.NORTH);
        defenderLayout.add(characterPanelRight, BorderLayout.CENTER);

        BorderedButton simButton = new BorderedButton("Simulate");
        simButton.addActionListener(this::launchSimulation);
        forecastLayout.add(simButton, BorderLayout.NORTH);
        forecastLayout.add(battleForecastPanel, BorderLayout.CENTER);

        subLayout.add(attackerLayout, BorderLayout.WEST);
        subLayout.add(forecastLayout, BorderLayout.CENTER);
        subLayout.add(defenderLayout, BorderLayout.EAST);

        mainLayout.add(subLayout, BorderLayout.CENTER);

        characterPanelLeft.refresh(Main.CHARACTERS.getFirst());
        characterPanelRight.refresh(Main.CHARACTERS.getFirst());

        launchSimulation();

        pack();
    }

    private void launchSimulation() {
        Support attackerSupport = characterPanelLeft.support(
                CharacterUtils.findByName((String) characterLeft.inner.getSelectedItem()).affinity
        );
        Support defenderSupport = characterPanelRight.support(
                CharacterUtils.findByName((String) characterRight.inner.getSelectedItem()).affinity
        );
        battleForecastPanel.refresh(
                characterPanelLeft.stats(), characterPanelLeft.weapon(), attackerSupport.doubledBonus(),
                characterPanelRight.stats(), characterPanelRight.weapon(), defenderSupport.doubledBonus()
        );
    }
}
