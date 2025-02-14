package gui;

import db.SQLiteRepository;
import init.Main;
import model.DisplayCharacter;
import model.FECharacter;
import model.FEClass;
import service.CharacterUtils;
import service.ClassUtils;

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

    public static void init() {
        mainFrame.setPreferredSize(new Dimension(400, 200));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        charactersButton = new BorderedButton("Characters");
        charactersButton.addActionListener(() -> charactersFrame.setVisible(true));

        classesButton = new BorderedButton("Classes");
        classesButton.addActionListener(() -> classesFrame.setVisible(true));
 
        mainFrame.setLayout(new GridLayout(1, 2));
        mainFrame.add(charactersButton);
        mainFrame.add(classesButton);

        initClassesGUI();
        initCharactersGUI();

        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public static void initClassesGUI() {
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

    public static void initCharactersGUI() {
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
            if (!e.getValueIsAdjusting()) refreshCharacterPanel();
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

    private static void refreshClassPanel() {
        displayedClass = ClassUtils.findByName(classList.getSelectedValue()).clone();
        classPanel.fill(displayedClass);
    }

    private static void refreshCharacterPanel() {
        displayedCharacter = CharacterUtils.findByName(characterList.getSelectedValue()).clone();
        List<FEClass> initialPromotions = ClassUtils.getPromotions(displayedCharacter.baseClass.name);
        characterPanel.fill(new DisplayCharacter(displayedCharacter, initialPromotions.get(0), initialPromotions.get(1)));
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
}
