package main;

import model.*;
import view.*;

public class Main {
    public static void main(String[] args) {
        // Launch the GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            GameManager game = GameManager.getInstance();
            MainFrame mainFrame = new MainFrame(game);
            mainFrame.setVisible(true);
        });
    }
}