package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for validating game state and preventing corruption
 */
public class GameValidator {

    public static List<String> validateGame(GameManager game) {
        List<String> issues = new ArrayList<>();

        if (game == null) {
            issues.add("Game manager is null");
            return issues;
        }

        if (game.getCurrentPlayer() == null) {
            issues.add("No current player");
        } else if (!game.getCurrentPlayer().validatePlayerState()) {
            issues.add("Player state invalid");
        }

        List<Room> rooms = game.getRooms();
        if (rooms == null || rooms.isEmpty()) {
            issues.add("No rooms available");
        } else {
            for (int i = 0; i < rooms.size(); i++) {
                Room room = rooms.get(i);
                if (room == null) {
                    issues.add("Room " + i + " is null");
                } else if (room.getPuzzles() == null) {
                    issues.add("Room " + room.getName() + " has no puzzles");
                }
            }
        }

        if (game.getCurrentRoom() == null) {
            issues.add("No current room");
        }

        return issues;
    }

    public static boolean isGamePlayable(GameManager game) {
        List<String> issues = validateGame(game);
        return issues.isEmpty();
    }

    public static String getPlayabilityStatus(GameManager game) {
        List<String> issues = validateGame(game);

        if (issues.isEmpty()) {
            return "✅ Game state is valid and playable";
        } else {
            StringBuilder status = new StringBuilder("❌ Game state issues:\n");
            for (String issue : issues) {
                status.append("• ").append(issue).append("\n");
            }
            return status.toString();
        }
    }
}