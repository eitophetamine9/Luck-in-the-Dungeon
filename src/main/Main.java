package main;

import gui.GUI;
import model.*;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Test the complete backend system
        GameManager game = GameManager.getInstance();

        SwingUtilities.invokeLater(()->{
            JFrame frame = new JFrame("Luck in the Dungeon");
            GUI gui = new GUI();
            frame.setContentPane(gui.getMainPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        System.out.println("=== Backend System Test ===");
        System.out.println("Game State: " + game.getGameState());
        System.out.println("Current Room: " + game.getCurrentRoom().getName());
        System.out.println("Player: " + game.getCurrentPlayer().getName());
        System.out.println("Player Coins: " + game.getCurrentPlayer().getCoinBalance());

        // Test room completion check
        System.out.println("Room Complete: " + game.getCurrentRoom().isComplete());

        // Test gacha machine
        GachaMachine gacha = game.getCurrentRoom().getGachaMachine();
        System.out.println("Gacha Machine: " + gacha.getMachineName());
        System.out.println("Pull Cost: " + gacha.getPullCost());
        System.out.println("Can Pull: " + gacha.canPull(game.getCurrentPlayer()));

        System.out.println("=== Backend Test Complete ===");
    }
}
