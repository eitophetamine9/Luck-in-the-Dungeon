package audio;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AudioManager {
    private static AudioManager instance;

    // 🆕 THE NUCLEAR FIX: A list to track ALL sounds, so none can "escape"
    private List<Clip> activeClips = new ArrayList<>();

    private String currentMusic = "";
    private float volume = 0.6f;
    private boolean isMuted = false;

    private AudioManager() {}

    public static synchronized AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    // 🆕 SYNCHRONIZED: Only one thread can touch the audio system at a time
    public synchronized void playMusic(String fileName) {
        if (isMuted) return;

        // Optimization: If asking for the exact same song, do nothing
        // BUT: We check if the clip is actually running to be safe
        if (currentMusic.equals(fileName) && !activeClips.isEmpty() && activeClips.get(activeClips.size()-1).isRunning()) {
            return;
        }

        System.out.println("🎵 Audio Manager: Request to play " + fileName);

        // 1. NUCLEAR CLEANUP: Kill EVERY clip we know about
        stopAllMusic();

        try {
            URL url = getClass().getResource(fileName);
            if (url == null) url = getClass().getResource("/" + fileName);

            if (url == null) {
                System.err.println("❌ CRITICAL: Audio file missing: " + fileName);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            Clip newClip = AudioSystem.getClip();
            newClip.open(audioStream);

            // Volume Control
            if (newClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) newClip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(Math.max(0.0001f, volume)) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            }

            // 2. Add to our tracking list BEFORE starting
            activeClips.add(newClip);
            currentMusic = fileName;

            // 3. Play
            newClip.setFramePosition(0);
            newClip.loop(Clip.LOOP_CONTINUOUSLY);
            newClip.start();

            System.out.println("🎵 Now playing: " + fileName + " [Active Clips: " + activeClips.size() + "]");

        } catch (Exception e) {
            System.err.println("❌ Error playing music: " + fileName);
            e.printStackTrace();
            currentMusic = "";
        }
    }

    // 🆕 The "Nuclear" Stop Method
    public synchronized void stopMusic() {
        System.out.println("🛑 Stopping ALL music...");
        stopAllMusic();
        currentMusic = "";
    }

    private void stopAllMusic() {
        // Iterator is safer for removing while looping
        Iterator<Clip> iterator = activeClips.iterator();
        while (iterator.hasNext()) {
            Clip clip = iterator.next();
            try {
                if (clip != null) {
                    if (clip.isRunning()) clip.stop();
                    clip.flush();
                    clip.close();
                }
            } catch (Exception e) {
                System.err.println("⚠️ Error closing clip: " + e.getMessage());
            }
            // Remove from list
            iterator.remove();
        }
        // Ensure list is empty
        activeClips.clear();
    }

    public void playSound(String fileName) {
        if (isMuted) return;
        new Thread(() -> {
            try {
                URL url = getClass().getResource(fileName);
                if (url == null) url = getClass().getResource("/" + fileName);
                if (url == null) return;

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);

                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                    gainControl.setValue(dB);
                }
                clip.start();
                clip.addLineListener(e -> {
                    if (e.getType() == LineEvent.Type.STOP) clip.close();
                });
            } catch (Exception e) {
                System.out.println("❌ SFX Error: " + fileName);
            }
        }).start();
    }

    public synchronized String getCurrentMusic() {
        return currentMusic;
    }

    public void setVolume(float newVolume) {
        this.volume = newVolume;
        // Update all active clips
        for (Clip clip : activeClips) {
            if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(Math.max(0.0001f, volume)) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            }
        }
    }
}