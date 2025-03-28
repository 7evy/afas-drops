package gui;

import gui.dumb.BorderedButton;
import gui.frame.BattleSimulatorFrame;
import gui.frame.CharactersFrame;
import gui.frame.ClassesFrame;
import gui.frame.WeaponsFrame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridLayout;

public class GUI {
    private GUI() {}

    public static void init() {
        JFrame mainFrame = new JFrame("Afa's Drops");
        mainFrame.setPreferredSize(new Dimension(600, 200));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ClassesFrame classesFrame = new ClassesFrame();
        CharactersFrame charactersFrame = new CharactersFrame();
        WeaponsFrame weaponsFrame = new WeaponsFrame();
        BattleSimulatorFrame battleSimulatorFrame = new BattleSimulatorFrame();

        BorderedButton charactersButton = new BorderedButton("Characters");
        charactersButton.addActionListener(() -> charactersFrame.setVisible(true));

        BorderedButton classesButton = new BorderedButton("Classes");
        classesButton.addActionListener(() -> classesFrame.setVisible(true));

        BorderedButton weaponsButton = new BorderedButton("Weapons");
        weaponsButton.addActionListener(() -> weaponsFrame.setVisible(true));

        BorderedButton simulatorButton = new BorderedButton("Battle simulation");
        simulatorButton.addActionListener(() -> battleSimulatorFrame.setVisible(true));
 
        mainFrame.setLayout(new GridLayout(2, 3));
        mainFrame.add(charactersButton);
        mainFrame.add(classesButton);
        mainFrame.add(weaponsButton);
        mainFrame.add(new JPanel());
        mainFrame.add(simulatorButton);

        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}
