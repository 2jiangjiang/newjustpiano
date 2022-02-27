package ly.jj.newjustpiano.items;

public class LyricKey extends BarrageKey{
    public String lyric;

    public LyricKey(int wait, int event, String lyric) {
        super(wait, event, 0, 0);
        this.lyric = lyric;
    }
}
