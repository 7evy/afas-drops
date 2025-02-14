package gui;

import db.SQLiteRepository;
import init.Main;
import model.DisplayCharacter;
import model.FECharacter;
import model.FEClass;
import model.FEWeapon;
import utils.CharacterUtils;
import utils.ClassUtils;
import utils.WeaponUtils;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

public class GUI {
    private GUI() {}

    // Main GUI
    private static final JFrame mainFrame = new JFrame("Afa's Drops");

    private static BorderedButton charactersButton;
    private static BorderedButton classesButton;
    private static BorderedButton weaponsButton;

    // Characters GUI
    private static JFrame charactersFrame;

    private static BorderedButton newCharacterButton;
    private static BorderedButton saveCharacterButton;
    private static BorderedButton refreshCharacterButton;

    private static CharacterPanel characterPanel;
    
    private static final DefaultListModel<String> characterListModel = new DefaultListModel<>();
    private static JList<String> characterList;

    private static FECharacter displayedCharacter;

    // Classes GUI
    private static JFrame classesFrame;

    private static BorderedButton newClassButton;
    private static BorderedButton saveClassButton;
    private static BorderedButton refreshClassButton;

    private static ClassPanel classPanel;

    private static final DefaultListModel<String> classListModel = new DefaultListModel<>();
    private static JList<String> classList;

    private static FEClass displayedClass;

    // Weapons GUI
    private static JFrame weaponsFrame;

    private static BorderedButton newWeaponButton;
    private static BorderedButton saveWeaponButton;
    private static BorderedButton refreshWeaponButton;

    private static WeaponPanel weaponPanel;

    private static final DefaultListModel<String> weaponListModel = new DefaultListModel<>();
    private static JList<String> weaponList;

    private static FEWeapon displayedWeapon;

    public static void init() {
        mainFrame.setPreferredSize(new Dimension(400, 200));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        charactersButton = new BorderedButton("Characters");
        charactersButton.addActionListener(() -> charactersFrame.setVisible(true));

        classesButton = new BorderedButton("Classes");
        classesButton.addActionListener(() -> classesFrame.setVisible(true));

        weaponsButton = new BorderedButton("Weapons");
        weaponsButton.addActionListener(() -> weaponsFrame.setVisible(true));
 
        mainFrame.setLayout(new GridLayout(1, 3));
        mainFrame.add(charactersButton);
        mainFrame.add(classesButton);
        mainFrame.add(weaponsButton);

        initClassesGUI();
        initCharactersGUI();
        initWeaponsGUI();

        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private static void initClassesGUI() {
        classesFrame = new JFrame("Classes");
        classesFrame.setPreferredSize(new Dimension(800, 650));
        classesFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        newClassButton = new BorderedButton("New class");
        newClassButton.addActionListener(GUI::createNewBlankClass);

        saveClassButton = new BorderedButton("Write to memory");
        saveClassButton.addActionListener(GUI::saveSelectedClass);

        refreshClassButton = new BorderedButton("Refresh");
        refreshClassButton.addActionListener(GUI::refreshClassPanel);

        BorderedPanel mainLayout = new BorderedPanel(10, 10);
        BorderedPanel subLayout = new BorderedPanel(10, 10);
        subLayout.no(BorderLayout.EAST);
        subLayout.no(BorderLayout.WEST);

        BorderedPanel buttonContainer = new BorderedPanel(100, 0);
        buttonContainer.no(BorderLayout.NORTH);
        JPanel subButtonContainer = new JPanel(new GridLayout(1, 3));
        subButtonContainer.add(newClassButton);
        subButtonContainer.add(saveClassButton);
        subButtonContainer.add(refreshClassButton);
        buttonContainer.add(subButtonContainer, BorderLayout.CENTER);

        classList = new JList<>(classListModel);
        classList.setSelectedIndex(0);
        classList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refreshClassPanel();
            }
        });
        subLayout.add(classList, BorderLayout.WEST);
        subLayout.add(buttonContainer, BorderLayout.NORTH);
        
        classPanel = new ClassPanel();
        subLayout.add(classPanel, BorderLayout.CENTER);
        
        mainLayout.add(subLayout, BorderLayout.CENTER);

        classesFrame.add(mainLayout);
        classesFrame.pack();
        
        refreshClassPanel();
    }

    private static void initCharactersGUI() {
        charactersFrame = new JFrame("Characters");
        charactersFrame.setPreferredSize(new Dimension(1850, 700));
        charactersFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        newCharacterButton = new BorderedButton("New character");
        newCharacterButton.addActionListener(GUI::createNewBlankCharacter);

        saveCharacterButton = new BorderedButton("Write to memory");
        saveCharacterButton.addActionListener(GUI::saveSelectedCharacter);

        refreshCharacterButton = new BorderedButton("Refresh");
        refreshCharacterButton.addActionListener(GUI::refreshCharacterPanel);
        
        BorderedPanel mainLayout = new BorderedPanel(10, 10);
        BorderedPanel subLayout = new BorderedPanel(10, 10);
        subLayout.no(BorderLayout.EAST);
        subLayout.no(BorderLayout.WEST);

        BorderedPanel buttonContainer = new BorderedPanel(100, 0);
        buttonContainer.no(BorderLayout.NORTH);
        JPanel subButtonContainer = new JPanel(new GridLayout(1, 3));
        subButtonContainer.add(newCharacterButton);
        subButtonContainer.add(saveCharacterButton);
        subButtonContainer.add(refreshCharacterButton);
        buttonContainer.add(subButtonContainer, BorderLayout.CENTER);
        
        characterList = new JList<>(characterListModel);
        characterList.setSelectedIndex(0);
        characterList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refreshCharacterPanel();
            }
        });
        subLayout.add(characterList, BorderLayout.WEST);
        subLayout.add(buttonContainer, BorderLayout.NORTH);
        
        characterPanel = new CharacterPanel();
        subLayout.add(characterPanel, BorderLayout.CENTER);
        
        mainLayout.add(subLayout, BorderLayout.CENTER);

        charactersFrame.add(mainLayout);
        charactersFrame.pack();

        refreshCharacterPanel();
    }

    private static void initWeaponsGUI() {
        weaponsFrame = new JFrame("Weapons");
        weaponsFrame.setPreferredSize(new Dimension(1000, 1000));
        weaponsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        newWeaponButton = new BorderedButton("New weapon");
        newWeaponButton.addActionListener(GUI::createNewBlankWeapon);

        saveWeaponButton = new BorderedButton("Write to memory");
        saveWeaponButton.addActionListener(GUI::saveSelectedWeapon);

        refreshWeaponButton = new BorderedButton("Refresh");
        refreshWeaponButton.addActionListener(GUI::refreshWeaponPanel);

        BorderedPanel mainLayout = new BorderedPanel(10, 10);
        BorderedPanel subLayout = new BorderedPanel(10, 10);
        subLayout.no(BorderLayout.EAST);
        subLayout.no(BorderLayout.WEST);

        BorderedPanel buttonContainer = new BorderedPanel(100, 0);
        buttonContainer.no(BorderLayout.NORTH);
        JPanel subButtonContainer = new JPanel(new GridLayout(1, 3));
        subButtonContainer.add(newWeaponButton);
        subButtonContainer.add(saveWeaponButton);
        subButtonContainer.add(refreshWeaponButton);
        buttonContainer.add(subButtonContainer, BorderLayout.CENTER);

        weaponList = new JList<>(weaponListModel);
        weaponList.setSelectedIndex(0);
        weaponList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refreshWeaponPanel();
            }
        });
        subLayout.add(weaponList, BorderLayout.WEST);
        subLayout.add(buttonContainer, BorderLayout.NORTH);

        weaponPanel = new WeaponPanel();
        subLayout.add(weaponPanel, BorderLayout.CENTER);

        mainLayout.add(subLayout, BorderLayout.CENTER);

        weaponsFrame.add(mainLayout);
        weaponsFrame.pack();

        refreshWeaponPanel();
    }

    public static void actualizeClasses() {
        classListModel.clear();
        SQLiteRepository.fetchAllClasses();
        classListModel.addAll(
            Main.CLASSES
                .stream()
                .map(c -> c.name)
                .toList());
    }

    public static void actualizeCharacters() {
        characterListModel.clear();
        SQLiteRepository.fetchAllCharacters(Main.CLASSES);
        characterListModel.addAll(
            Main.CHARACTERS
                .stream()
                .map(c -> c.name)
                .toList());
    }

    public static void actualizeWeapons() {
        weaponListModel.clear();
        SQLiteRepository.fetchAllWeapons();
        weaponListModel.addAll(
            Main.WEAPONS
                .stream()
                .map(c -> c.name)
                .toList());
    }

    private static void refreshClassPanel() {
        displayedClass = ClassUtils.findByName(classList.getSelectedValue()).clone();
        classPanel.fill(displayedClass);
    }

    private static void refreshCharacterPanel() {
        displayedCharacter = CharacterUtils.findByName(characterList.getSelectedValue()).clone();
        List<FEClass> initialPromotions = ClassUtils.getPromotions(displayedCharacter.baseClass.name);
        characterPanel.fill(new DisplayCharacter(displayedCharacter, initialPromotions.get(0), initialPromotions.get(1)));
    }

    private static void refreshWeaponPanel() {
        displayedWeapon = WeaponUtils.findByName(weaponList.getSelectedValue()).clone();
        weaponPanel.fill(displayedWeapon);
    }

    private static void createNewBlankClass() {
        SQLiteRepository.newClass();
        classList.setValueIsAdjusting(true);
        actualizeClasses();
        classList.setSelectedIndex(classListModel.size() - 1);
        refreshClassPanel();
        classList.setValueIsAdjusting(false);
    }

    private static void saveSelectedClass() {
        SQLiteRepository.updateClass(displayedClass);
        int selected = classList.getSelectedIndex();
        classList.setValueIsAdjusting(true);
        actualizeClasses();
        classList.setSelectedIndex(selected);
        refreshClassPanel();
        classList.setValueIsAdjusting(false);
    }

    private static void createNewBlankCharacter() {
        SQLiteRepository.newCharacter();
        characterList.setValueIsAdjusting(true);
        actualizeCharacters();
        characterList.setSelectedIndex(characterListModel.size() - 1);
        refreshCharacterPanel();
        characterList.setValueIsAdjusting(false);
    }

    private static void saveSelectedCharacter() {
        SQLiteRepository.updateCharacter(displayedCharacter);
        int selected = characterList.getSelectedIndex();
        characterList.setValueIsAdjusting(true);
        actualizeCharacters();
        characterList.setSelectedIndex(selected);
        refreshCharacterPanel();
        characterList.setValueIsAdjusting(false);
    }

    private static void createNewBlankWeapon() {
        SQLiteRepository.newWeapon();
        weaponList.setValueIsAdjusting(true);
        actualizeWeapons();
        weaponList.setSelectedIndex(weaponListModel.size() - 1);
        refreshWeaponPanel();
        weaponList.setValueIsAdjusting(false);
    }

    private static void saveSelectedWeapon() {
        SQLiteRepository.updateWeapon(displayedWeapon);
        int selected = weaponList.getSelectedIndex();
        weaponList.setValueIsAdjusting(true);
        actualizeWeapons();
        weaponList.setSelectedIndex(selected);
        refreshWeaponPanel();
        weaponList.setValueIsAdjusting(false);
    }
}
