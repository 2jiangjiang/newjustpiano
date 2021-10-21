package ly.jj.newjustpiano.tools;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

public class SoundMixer {
    public final static int AUDIO_MODE_LOW_LATENCY = 2;
    public final static int AUDIO_MODE_DEFAULT = 1;
    public final static int AUDIO_MODE_POWER_SAVE = 0;

    private final File[] sounds = new File[128];
    private final ReentrantLock lock = new ReentrantLock();

    public SoundMixer() {
    }

    public SoundMixer(int sampleRate, int channels) {
        build(sampleRate, channels);
    }

    public void setSound(File path) {
        this.sounds[Integer.parseInt(path.getName())] = path;
    }

    public void setSound(String path) {
        setSound(new File(path));
    }


    public void build() {
        this.build(44100);
    }

    public void build(int sampleRate) {
        this.build(sampleRate, 2);
    }

    public void build(int sampleRate, int channels) {
        nativeSet(sampleRate, channels);
        nativeBuild();
    }


    public void play(int i) {
        play(sounds[i]);
    }

    public void play(File file) {
        if (file != null)
            play(file.getPath());
    }

    public void play(String path) {
        play(path, 128);
    }

    public void play(int i, int volume) {
        play(sounds[i], volume);
    }

    public void play(File file, int volume) {
        if (file != null)
            play(file.getPath(), volume);
    }

    public void play(String path, int volume) {
        lock.lock();
        nativePlay(path, ((float) volume) / 255);
        lock.unlock();
    }

    public void setMode(int i) {
        nativeModeSet(i);
    }

    private native String nativeModeSet(int i);

    private native String nativeBuild();

    private native String nativeSet(int sampleRate, int channels);

    private native void nativePlay(String path, float volume);
}
