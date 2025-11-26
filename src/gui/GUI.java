package gui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;


public class GUI {

    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private JButton loginButton;
    private JPanel roomPanel;
    private JPanel triplePanel;
    private JPanel messagePanel;
    private JButton item1;
    private JButton item2;
    private JButton item3;
    private JButton item4;
    private JButton item5;
    private JButton item6;
    private JButton item7;
    private JButton item8;
    private JButton item9;
    private JList list1;
    private JLabel selectedItem;
    private JLabel selectedPuzzle;
    private JButton pull;
    private JLabel machineDisplay;
    private JLabel result;
    private JButton tryitem;
    private JButton solvepuzzle;
    private JButton gethint;
    private JButton nextroom;
    private JButton savegame;
    private JTextArea message;
    private JLabel roomTitle;
    private JTextArea roomDesc;
    private JLabel roomImgPlaceholder;
    private JPanel inventoryPanel;
    private JPanel puzzlePanel;
    private JPanel gachaPanel;


    public GUI(){
        initUI();
    }

    private void initUI() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("★ LUCK IN THE DUNGEON"), BorderLayout.WEST);


        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton loginButton = new JButton("Login");
        JLabel loginStatus = new JLabel("[User: Not Logged In]");
        loginPanel.add(loginStatus);
        loginPanel.add(loginButton);

        topPanel.add(loginPanel, BorderLayout.EAST);

        roomPanel = new JPanel();
        roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.Y_AXIS));
        roomTitle = new JLabel("Room Title");

        JPanel titleWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleWrapper.add(roomTitle);

        roomTitle.setHorizontalAlignment(SwingConstants.CENTER);
        roomImgPlaceholder = new JLabel(new ImageIcon("room.png")); // Replace with actual image
        roomDesc = new JTextArea(3, 20);
        roomDesc.setLineWrap(true);
        roomDesc.setWrapStyleWord(true);
        roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.Y_AXIS));
        roomPanel.add(titleWrapper);
        roomPanel.add(roomImgPlaceholder);
        roomPanel.add(new JScrollPane(roomDesc));

        triplePanel = new JPanel(new GridLayout(1, 3));

// Inventory Pane
        inventoryPanel = new JPanel(new GridLayout(3, 3));

        JLabel inventoryLabel = new JLabel("Inventory");
        JPanel inventoryWrapper = new JPanel(new BorderLayout());
        selectedItem = new JLabel("Selected Item: ");

        inventoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inventoryPanel.add(item1); inventoryPanel.add(item2); inventoryPanel.add(item3);
        inventoryPanel.add(item4); inventoryPanel.add(item5); inventoryPanel.add(item6);
        inventoryPanel.add(item7); inventoryPanel.add(item8); inventoryPanel.add(item9);
        selectedItem.setHorizontalAlignment(SwingConstants.CENTER);

        inventoryWrapper.add(inventoryLabel, BorderLayout.NORTH);
        inventoryWrapper.add(inventoryPanel, BorderLayout.CENTER);
        inventoryWrapper.add(selectedItem, BorderLayout.SOUTH);

// Puzzle Panel
        JLabel puzzleLabel = new JLabel("Puzzles");
        puzzleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        selectedPuzzle = new JLabel("Selected Puzzle: ");
        selectedPuzzle.setHorizontalAlignment(SwingConstants.CENTER);


        puzzlePanel = new JPanel(new BorderLayout());
        puzzlePanel.add(puzzleLabel,BorderLayout.NORTH);
        puzzlePanel.add(new JScrollPane(list1), BorderLayout.CENTER);
        puzzlePanel.add(selectedPuzzle, BorderLayout.SOUTH);

// Gacha Panel
        JLabel gachaLabel = new JLabel("Gacha Machine");
        gachaLabel.setHorizontalAlignment(SwingConstants.CENTER);

        machineDisplay = new JLabel(new ImageIcon("room.png")); // Replace with actual image

        result = new JLabel("Result: ");
        result.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel pullWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pullWrapper.add(pull);

        gachaPanel = new JPanel(new BorderLayout());
        gachaPanel.add(gachaLabel, BorderLayout.NORTH);
        gachaPanel.add(machineDisplay, BorderLayout.CENTER);
        gachaPanel.add(pullWrapper, BorderLayout.CENTER);
        gachaPanel.add(result, BorderLayout.SOUTH);

        triplePanel.add(inventoryWrapper);
        triplePanel.add(puzzlePanel);
        triplePanel.add(gachaPanel);

        messagePanel = new JPanel(new BorderLayout());
        message = new JTextArea(5, 40);
        message.setEditable(false);
        messagePanel.add(new JScrollPane(message), BorderLayout.CENTER);

        bottomPanel = new JPanel(new GridLayout(1, 5));
        bottomPanel.add(tryitem);
        bottomPanel.add(solvepuzzle);
        bottomPanel.add(gethint);
        bottomPanel.add(nextroom);
        bottomPanel.add(savegame);

        mainPanel.add(topPanel);
        mainPanel.add(roomPanel);
        mainPanel.add(triplePanel);
        mainPanel.add(messagePanel);
        mainPanel.add(bottomPanel);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
