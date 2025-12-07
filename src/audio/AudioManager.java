package audio;

import javax.sound.sampled.*;
import java.net.URL;

public class AudioManager {
    private static AudioManager instance;
    private Clip backgroundMusic;
    private float volume = 0.6f;
    private boolean isMuted = false;
    private String currentMusic = "";

    private AudioManager() {}

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    // 🆕 FIX: Improved music playing with proper cleanup
    public void playMusic(String fileName) {
        if (isMuted) {
            System.out.println("🔇 Music muted, skipping: " + fileName);
            return;
        }

        // Don't restart if already playing the same music
        if (currentMusic.equals(fileName) && backgroundMusic != null &&
                backgroundMusic.isRunning()) {
            System.out.println("🎵 Already playing: " + fileName);
            return;
        }

        try {
            // 🆕 FIX: Stop and clean up previous music completely
            if (backgroundMusic != null) {
                if (backgroundMusic.isRunning()) {
                    backgroundMusic.stop();
                }
                backgroundMusic.close();
                backgroundMusic = null;
            }

            // Load new audio file
            URL url = getClass().getResource(fileName);
            if (url == null) {
                System.out.println("❌ Audio file not found: " + fileName);
                currentMusic = "";
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);

            // Set volume
            if (backgroundMusic.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            }

            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            currentMusic = fileName;
            System.out.println("🎵 Now playing: " + fileName);

        } catch (Exception e) {
            System.out.println("❌ Error playing music: " + fileName + " - " + e.getMessage());
            currentMusic = "";
        }
    }

    // Play sound effect (plays once)
    public void playSound(String fileName) {
        if (isMuted) return;

        new Thread(() -> {
            try {
                URL url = getClass().getResource(fileName);
                if (url == null) {
                    System.out.println("❌ Sound file not found: " + fileName);
                    return;
                }

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);

                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                    gainControl.setValue(dB);
                }

                clip.start();
                System.out.println("🔊 Playing sound: " + fileName);

                // 🆕 FIX: Close clip when done to free resources
                clip.addLineListener(new LineListener() {
                    @Override
                    public void update(LineEvent event) {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                        }
                    }
                });

            } catch (Exception e) {
                System.out.println("❌ Error playing sound: " + fileName + " - " + e.getMessage());
            }
        }).start();
    }

    // Stop music with proper cleanup
    public void stopMusic() {
        if (backgroundMusic != null) {
            if (backgroundMusic.isRunning()) {
                backgroundMusic.stop();
            }
            backgroundMusic.close();
            backgroundMusic = null;
        }
        currentMusic = "";
        System.out.println("⏹️ Music stopped");
    }

    // Volume control (0.0 to 1.0)
    public void setVolume(float newVolume) {
        volume = Math.max(0.0f, Math.min(1.0f, newVolume));

        if (backgroundMusic != null && backgroundMusic.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }

        System.out.println("🔊 Volume set to: " + volume);
    }

    public void toggleMute() {
        isMuted = !isMuted;
        System.out.println(isMuted ? "🔇 Muted" : "🔊 Unmuted");

        if (isMuted) {
            stopMusic();
        } else if (!currentMusic.isEmpty()) {
            playMusic(currentMusic);
        }
    }

    // Get current playing music
    public String getCurrentMusic() {
        return currentMusic;
    }

    public float getVolume() { return volume; }
    public boolean isMuted() { return isMuted; }
}