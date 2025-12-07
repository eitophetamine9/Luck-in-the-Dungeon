package audio;

public class AudioFiles {
    // Background Music
    public static final String MAIN_MENU = "main_menu.wav";
    public static final String ROOM_1 = "room1.wav";
    public static final String ROOM_2 = "room2.wav";
    public static final String ROOM_3 = "room3.wav";
    public static final String ROOM_4 = "room4.wav";
    public static final String ROOM_5 = "room5.wav";

    // Sound Effects
    public static final String CLICK = "click.wav";
    public static final String GACHA = "gacha.wav";
    public static final String SUCCESS = "success.wav";
    public static final String ERROR = "error.wav";
    public static final String UNLOCK = "unlock.wav";
    public static final String COIN = "coin.wav";
    public static final String NEXUS = "nexus.wav";
    public static final String MAP = "map.wav";

    // Simple method to get room music
    public static String getRoomMusic(int roomIndex) {
        switch (roomIndex) {
            case 0: return ROOM_1;
            case 1: return ROOM_2;
            case 2: return ROOM_3;
            case 3: return ROOM_4;
            case 4: return ROOM_5;
            default: return MAIN_MENU;
        }
    }
}