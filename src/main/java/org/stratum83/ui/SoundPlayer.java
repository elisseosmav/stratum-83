package org.stratum83.ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SoundPlayer implements AutoCloseable {

    private final ExecutorService audioThread = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "stratum-audio");
        thread.setDaemon(true);
        return thread;
    });
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void click() {
        if (enabled) {
            audioThread.execute(this::playTone);
        }
    }

    private void playTone() {
        int sampleRate = 22050;
        int sampleCount = sampleRate / 25; // 40 milliseconds
        byte[] samples = new byte[sampleCount * 2];

        for (int i = 0; i < sampleCount; i++) {
            double fade = 1.0 - (double) i / sampleCount;
            short sample = (short) (Math.sin(2 * Math.PI * 660 * i / sampleRate)
                    * 3500 * fade);
            samples[i * 2] = (byte) (sample & 0xff);
            samples[i * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }

        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
        try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
            line.open(format, samples.length);
            line.start();
            line.write(samples, 0, samples.length);
            line.drain();
        } catch (LineUnavailableException | IllegalArgumentException ex) {
            System.err.println("Sound unavailable: " + ex.getMessage());
        }
    }

    @Override
    public void close() {
        audioThread.shutdownNow();
    }
}
