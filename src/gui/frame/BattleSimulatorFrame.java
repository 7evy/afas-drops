package gui.frame;

import gui.dumb.BorderedButton;
import gui.dumb.BorderedLabeledComboBox;
import gui.dumb.BorderedPanel;
import gui.panel.BattleCharacterPanel;
import gui.panel.BattleForecastPanel;
import init.Main;
import model.FECharacter;
import model.Support;
import utils.BattleSimulation;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

public class BattleSimulatorFrame extends JFrame {
    private final BattleCharacterPanel characterPanelLeft;
    private final BattleCharacterPanel characterPanelRight;
    private final BattleForecastPanel battleForecastPanel;

    private final BorderedLabeledComboBox<FECharacter> characterLeft;
    private final BorderedLabeledComboBox<FECharacter> characterRight;

    public BattleSimulatorFrame() {
        super("Battle Simulator");
        setPreferredSize(new Dimension(1000, 1000));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        characterPanelLeft = new BattleCharacterPanel();
        characterPanelRight = new BattleCharacterPanel();
        battleForecastPanel = new BattleForecastPanel();

        FECharacter[] characters = Main.CHARACTERS.values().toArray(FECharacter[]::new);

        BorderedPanel attackerPanel = new BorderedPanel(0, 0);
        attackerPanel.pad(BorderLayout.WEST);
        JPanel attackerLayout = new JPanel();
        attackerLayout.setLayout(new BoxLayout(attackerLayout, BoxLayout.Y_AXIS));
        characterLeft = new BorderedLabeledComboBox<>("Initiator:", characters, characters[0]);
        characterLeft.addActionListener(() ->
                characterPanelLeft.refresh(characterLeft.getSelectedItem()), false);
        attackerLayout.add(characterLeft);
        attackerLayout.add(characterPanelLeft);
        attackerPanel.add(attackerLayout, BorderLayout.CENTER);

        BorderedPanel defenderPanel = new BorderedPanel(0, 0);
        defenderPanel.pad(BorderLayout.EAST);
        JPanel defenderLayout = new JPanel();
        defenderLayout.setLayout(new BoxLayout(defenderLayout, BoxLayout.Y_AXIS));
        characterRight = new BorderedLabeledComboBox<>("Retaliator:", characters, characters[0]);
        characterRight.addActionListener(() ->
                characterPanelRight.refresh(characterRight.getSelectedItem()), false);
        defenderLayout.add(characterRight);
        defenderLayout.add(characterPanelRight);
        defenderPanel.add(defenderLayout, BorderLayout.CENTER);

        BorderedPanel forecastPanel = new BorderedPanel(10, 0);
        BorderedButton simButton = new BorderedButton("Simulate");
        simButton.addActionListener(this::launchSimulation);
        forecastPanel.add(simButton, BorderLayout.NORTH);
        forecastPanel.add(battleForecastPanel, BorderLayout.CENTER);

        JPanel mainLayout = new JPanel(new GridLayout(1, 3));
        mainLayout.add(attackerPanel);
        mainLayout.add(forecastPanel);
        mainLayout.add(defenderPanel);

        characterPanelLeft.refresh(characters[0]);
        characterPanelRight.refresh(characters[0]);

        launchSimulation();

        add(mainLayout);
        pack();
    }

    private void launchSimulation() {
        Support attackerSupport = characterPanelLeft.support(characterLeft.getSelectedItem().affinity);
        Support defenderSupport = characterPanelRight.support(characterRight.getSelectedItem().affinity);
        battleForecastPanel.refresh(BattleSimulation.setUp(
                characterPanelLeft.stats(), characterPanelRight.stats(),
                characterPanelLeft.weapon(), characterPanelRight.weapon(),
                characterPanelLeft.feClass(), characterPanelRight.feClass(),
                characterPanelLeft.skills(), characterPanelRight.skills(),
                attackerSupport.doubledBonus(), defenderSupport.doubledBonus()
        ));
    }
}
