package ly.jj.newjustpiano.items;

public class BarrageKey {
    public int volume;
    public int value;
    public int wait;
    public int time;
    public int channel;
    public int track;
    public float length;
    public boolean finish;

    public BarrageKey(int wait, int value, int volume, int channel) {
        this.wait = wait;
        this.volume = volume;
        this.value = value;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void waited(int wait) {
        this.wait -= wait;
    }

    public void addTime(int time) {
        this.time += time;
    }

    public void finish() {
        this.finish = true;
    }

    public void addLength(float length) {
        this.length += length;
    }

    @Override
    public String toString() {
        return "BarrageKey{" +
                "volume=" + volume +
                (value > 0x7f ? ", flag=" + Integer.toHexString(value & 0x7f) : ", value=" + value) +
                ", wait=" + wait +
                ", channel=" + channel +
                '}';
    }
}
