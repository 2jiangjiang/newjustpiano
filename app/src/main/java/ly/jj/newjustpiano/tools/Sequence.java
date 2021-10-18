package ly.jj.newjustpiano.tools;

import ly.jj.newjustpiano.items.BarrageKey;

import java.util.ArrayList;
import java.util.List;

public class Sequence {
    public final List<Track> tracks = new ArrayList<>();
    public OnNextListener onNextListener;
    public Track operateTrack;
    public int tick;

    public void setOnNextListener(OnNextListener onNextListener) {
        this.onNextListener = onNextListener;
    }

    public void onKey(int value, int volume) {
        onNextListener.doKey(value, volume);
    }

    public void addKey(int time, int volume, int value, int channel) {
        operateTrack.addKey(new BarrageKey(time, volume, value, channel));
    }

    public void addTime(int time) {
        if (time == 0) return;
        for (BarrageKey key : operateTrack.keys) {
            if (!key.isOver) key.addTime(time);
        }
    }

    public void finishKey(int value, int channel) {
        for (BarrageKey key : operateTrack.keys) {
            if (!key.isOver && key.value == value && key.channel == channel) key.finish();
        }
    }

    public void addTrack() {
        Track track = new Track();
        tracks.add(track);
        operateTrack = track;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public void setTrack(int track) {
        operateTrack.setTrack(track);
    }

    public void setRemark(String remark) {
        operateTrack.setRemark(remark);
    }

    public void setCopyright(String copyright) {
        operateTrack.setCopyright(copyright);
    }

    public void setTrackName(String trackName) {
        operateTrack.setTrackName(trackName);
    }

    public void setInstrument(String instrument) {
        operateTrack.setInstrument(instrument);
    }

    public void addLyricEvent(int time, String str) {
        operateTrack.addLyricEvent(time, str);
    }

    public void addTagEvent(int time, String str) {
        operateTrack.addTagEvent(time, str);
    }

    public void addFlagEvent(int time, String str) {
        operateTrack.addFlagEvent(time, str);
    }

    @Override
    public String toString() {
        return "Sequence{" +
                "tracks=" + tracks +
                ", tick=" + tick +
                '}';
    }

    protected void sequence() {
    }

    public interface OnNextListener {
        void doKey(int value, int volume);
    }

    public static class Track {
        public final List<BarrageKey> keys = new ArrayList<>();
        public final List<StringEvent> lyricEvents = new ArrayList<>();
        public final List<StringEvent> tagEvents = new ArrayList<>();
        public final List<StringEvent> flagEvents = new ArrayList<>();
        public int track;
        public int position;
        public String remark;
        public String copyright;
        public String trackName;
        public String instrument;

        public void play() {
            position++;
        }

        public boolean isEnd() {
            return position == keys.size()-1;
        }

        public BarrageKey get() {
            if (position == keys.size()) return null;
            return keys.get(position);
        }

        public void addKey(BarrageKey key) {
            keys.add(key);
        }

        public void setTrack(int track) {
            this.track = track;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public void setCopyright(String copyright) {
            this.copyright = copyright;
        }

        public void setTrackName(String trackName) {
            this.trackName = trackName;
        }

        public void setInstrument(String instrument) {
            this.instrument = instrument;
        }

        public void addLyricEvent(int time, String str) {
            lyricEvents.add(new StringEvent(time, str));
        }

        public void addTagEvent(int time, String str) {
            tagEvents.add(new StringEvent(time, str));
        }

        public void addFlagEvent(int time, String str) {
            flagEvents.add(new StringEvent(time, str));
        }

        @Override
        public String toString() {
            return "Track{" +
                    "keys=" + keys +
                    ", lyricEvents=" + lyricEvents +
                    ", tagEvents=" + tagEvents +
                    ", flagEvents=" + flagEvents +
                    ", position=" + track +
                    ", remark='" + remark + '\'' +
                    ", copyright='" + copyright + '\'' +
                    ", trackName='" + trackName + '\'' +
                    ", instrument='" + instrument + '\'' +
                    '}';
        }

        public static class StringEvent {
            private String str;
            private int time;

            public StringEvent(int time, String str) {
                this.str = str;
                this.time = time;
            }

            @Override
            public String toString() {
                return "StringEvent{" +
                        "str='" + str + '\'' +
                        ", time=" + time +
                        '}';
            }
        }
    }
}
