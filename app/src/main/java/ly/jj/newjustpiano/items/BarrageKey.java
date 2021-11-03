package ly.jj.newjustpiano.items;

public class BarrageKey {
    public int channel;//from 0 single left double right
    public int value;
    public int volume;
    public float length = 0;
    public int time;
    public boolean isOver = false;

    public BarrageKey(int time, int volume, int value, int channel) {
        this.time = time;
        this.value = value;
        this.volume = volume;
        this.channel = channel;
        if (value > 0x7f) isOver = true;
    }

    public void addTime(float tick) {
        length += tick;
    }

    public void finish() {
        this.isOver = true;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "BarrageKey{" +
                "channel=" + channel +
                (value > 0x7f ? ", flag=" + (value & 0x7f) : ", value=" + value) +
                ", volume=" + volume +
                ", length=" + length +
                ", time=" + time +
                '}';
    }
}
