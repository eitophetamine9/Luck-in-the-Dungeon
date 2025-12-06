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
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    // Play background music (loops automatically)
    public void playMusic(String fileName) {
        if (isMuted) return;
        if (currentMusic.equals(fileName) && backgroundMusic != null && backgroundMusic.isRunning()) {
            return; // Already playing
        }

        try {
            // Stop current music
            if (backgroundMusic != null && backgroundMusic.isRunning()) {
                backgroundMusic.stop();
            }

            // Load audio file from the same package
            URL url = getClass().getResource(fileName);
            if (url == null) {
                System.out.println("❌ Audio file not found: " + fileName);
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
            System.out.println("❌ Error playing music: " + e.getMessage());
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

            } catch (Exception e) {
                System.out.println("❌ Error playing sound: " + e.getMessage());
            }
        }).start();
    }

    // Stop music
    public void stopMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            currentMusic = "";
        }
    }

    // Volume control (0.0 to 1.0)
    public void setVolume(float newVolume) {
        volume = Math.max(0.0f, Math.min(1.0f, newVolume));

        if (backgroundMusic != null && backgroundMusic.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }

    public void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            stopMusic();
        } else if (!currentMusic.isEmpty()) {
            playMusic(currentMusic);
        }
    }

    public float getVolume() { return volume; }
    public boolean isMuted() { return isMuted; }
}