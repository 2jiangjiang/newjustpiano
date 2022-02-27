package ly.jj.newjustpiano.tools;

import ly.jj.newjustpiano.items.BarrageKey;
import ly.jj.newjustpiano.items.LyricKey;

import java.util.ArrayList;
import java.util.List;

public class Sequence {
    public boolean useTick; //use tick time or sample time
    public boolean isReleased;
    protected int tick;
    protected Track[] tracks;
    protected SequenceListener sequenceListener;
    private int[] strength = new int[]{0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f};
    private int waitTime;
    private Track availableTrack;
    private int availableTrackNum;

    protected void release() {
        this.strength = null;
        this.tracks = null;
        this.availableTrack = null;
    }

    public void setSequenceListener(SequenceListener listener) {
        this.sequenceListener = listener;
    }

    protected void sequence() {
    }

    public void sort() {
        int j = 0;
        for (Track t : tracks) {
            if (hasValue(t)) j++;
            t.setPosition(j);
        }
    }

    public boolean hasValue(Track track) {
        for (BarrageKey b : track.events) {
            if (b.value < 0x7f) return true;
        }
        return false;
    }


    public void waitTime(int time) {
        for (BarrageKey e : availableTrack.events) {
            if (!e.finish) e.addTime(time);
        }
        this.waitTime += time;
    }

    public void addEvent(int value, int volume, int channel) {
        availableTrack.addEvent(new BarrageKey(waitTime, value, volume * strength[channel] / 0x7f, channel));
        waitTime = 0;
    }

    public void finishEvent(int value, int channel) {
        for (BarrageKey e : availableTrack.events) {
            if (!e.finish && e.value == value && e.channel == channel) e.finish();
        }
    }

    public void addEvent(byte[] data, int channel) {
        addEvent(data[0], data[1], channel);
    }

    public void finishEvent(byte[] data, int channel) {
        finishEvent(data[0], channel);
    }

    public void changeControl(byte[] data) {

    }

    public void nextTrack() {
        availableTrackNum++;
        if (availableTrackNum == tracks.length) return;
        tracks[availableTrackNum] = new Track();
        availableTrack = tracks[availableTrackNum];
        waitTime = 0;
    }

    public void setTracks(int nums) {
        this.tracks = new Track[nums];
        tracks[0] = new Track();
        availableTrack = tracks[0];
        availableTrackNum = 0;
    }

    public void setInstrument(int i) {
        availableTrack.setInstrument(i);
    }

    public void changeStrength(int channel, int strength) {
        this.strength[channel] = strength;
    }

    public void setTackInfo(String info) {
        availableTrack.setInfo(info);
    }

    public void setTackAuthor(String autor) {
        availableTrack.setAuthor(autor);
    }

    public void setTackName(String name) {
        availableTrack.setName(name);
    }

    public void setTackInstrument(String instrument) {
        availableTrack.setInstrument(instrument);
    }

    public void addLyric(String lyric) {
        availableTrack.addStringEvent(new LyricKey(waitTime, 0x80 & 0x05, lyric));
        waitTime = 0;
    }

    public void addFlag(int flag) {
        availableTrack.addEvent(new BarrageKey(waitTime, 0x80 & 0x06, flag, 0x00));
        waitTime = 0;
    }

    public void stringEvent(String event) {
        availableTrack.addStringEvent(new LyricKey(waitTime, 0x80 & 0x07, event));
        waitTime = 0;
    }

    public void setBPM(int event) {
        availableTrack.setBPM(event);
    }

    public void setIst(int i) {
        availableTrack.setIst(i);
    }

    public interface SequenceListener {
        void onEnd();

        void onNext(int value, int volume, int channel, int track);
    }

    public static class Track {
        protected int instrument;
        protected int sample; // 1/4 key's sample
        protected int bpm;    // the bpm
        protected String name;
        protected String author;
        protected String instrumentName;
        protected String info;
        protected int ist;
        protected int position;
        protected List<BarrageKey> events = new ArrayList<>();

        public void setIst(int i) {
            this.ist = i;
        }

        public void setPosition(int i) {
            this.position = i;
        }

        public void addEvent(BarrageKey event) {
            events.add(event);
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAuthor(String name) {
            this.author = author;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public void addStringEvent(LyricKey event) {
            events.add(event);
        }

        public void setInstrument(int i) {
            this.instrument = i;
        }

        public void setInstrument(String name) {
            this.instrumentName = name;
        }

        public void setBPM(int event) {
            this.bpm = event;
        }

        @Override
        public String toString() {
            return "Track{" +
                    "instrument=" + instrument +
                    ", sample=" + sample +
                    ", bpm=" + bpm +
                    ", name='" + name + '\'' +
                    ", author='" + author + '\'' +
                    ", instrumentName='" + instrumentName + '\'' +
                    ", info='" + info + '\'' +
                    ", ist='" + ist + '\'' +
                    ", position='" + position + '\'' +
                    ", events=" + events +
                    '}';
        }
    }
}
