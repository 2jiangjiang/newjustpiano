package ly.jj.newjustpiano.tools;

import ly.jj.newjustpiano.items.BarrageKey;

import static java.lang.Thread.sleep;

public class syncSequence extends Sequence {
    private int sample = 0;

    @Override
    public void sequence() {
        super.sequence();
        BarrageKey endKey = new BarrageKey(0, 0, 0, 0);
        for (Track track : tracks) {
            if (endKey.time < track.keys.get(track.keys.size() - 1).time) {
                endKey = track.keys.get(track.keys.size() - 1);
            }
        }
        System.out.println("this midi file has " + tracks.size() + " tracks");
        for (Track track : tracks) {
            System.out.println("the " + track.trackName + " has " + track.keys.size() + " keys");
            System.out.println(track);
        }
        return;
        /*
        BarrageKey lastKey = new BarrageKey(0, 0, 0, 0);
        BarrageKey nextKey = endKey;
        Track nextTrack = null;
        try {
            while (true) {
                if (tracks.size() == 0) return;
                for (Track track : tracks) {
                    if (track.get().value != (0x2F | 0x80) && track.get().time < nextKey.time) {
                        nextKey = track.get();
                        nextTrack = track;
                    }
                }
                nextTrack.play();
                int sleep_tick = nextKey.time - lastKey.time;
                int sleep_ms = sample * sleep_tick / 1000 / tick;
                int sleep_ns = (sample * 1000 * sleep_tick / tick % 1000000);
                if (sleep_ms < 0) sleep_ms = 0;
                if (sleep_ns < 0) sleep_ns = 0;
                sleep(sleep_ms, sleep_ns);
                if (nextKey.value < 0x80) {
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
                            boolean end = true;
                            for (Track track : tracks) {
                                if (!track.isEnd()) {
                                    end = false;
                                    break;
                                }
                            }
                            if (!end) {
                                break;
                            }
                            if (onEndListener != null)
                                onEndListener.exit();
                            return;
                    }
                }
                lastKey = nextKey;
                nextKey = endKey;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

         */
    }
}
