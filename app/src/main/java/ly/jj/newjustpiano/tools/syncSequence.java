package ly.jj.newjustpiano.tools;

import ly.jj.newjustpiano.items.BarrageKey;

import static java.lang.Thread.sleep;

public class syncSequence extends Sequence {
    private int sample;

    @Override
    public void sequence() {
        super.sequence();
        BarrageKey endKey = new BarrageKey(0, 0, 0, 0);
        for (Track track : tracks) {
            if (endKey.time < track.keys.get(track.keys.size() - 1).time) {
                endKey = track.keys.get(track.keys.size() - 1);
            }
        }
        BarrageKey lastKey = new BarrageKey(0, 0, 0, 0);
        BarrageKey nextKey = endKey;
        Track nextTrack = null;
        try {
            while (true) {
                for (Track track : tracks) {
                    if (track.get().time < nextKey.time) {
                        nextKey = track.get();
                        nextTrack = track;
                    }
                }
                nextTrack.play();
                if (nextKey.value < 0x7f) {
                    onKey(nextKey.value, nextKey.volume);
                } else {
                    switch (nextKey.value & 0x7f) {
                        case 0x51:
                            sample = nextKey.volume;
                            break;
                        case 0x58:
                        case 0x59:
                            break;
                        case 0x2f:
                            for (Track track : tracks) {
                                if (track.isEnd()) return;
                            }
                    }
                }
                int sleep_tick = nextKey.time - lastKey.time;
                int sleep_ms = sample * sleep_tick / 1000 / tick;
                int sleep_ns = (sample * sleep_tick / tick % 1000) * 1000;
                sleep(sleep_ms, sleep_ns);
                lastKey = nextKey;
                nextKey = endKey;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
