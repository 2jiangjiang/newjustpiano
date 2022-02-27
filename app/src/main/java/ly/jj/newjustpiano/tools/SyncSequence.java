package ly.jj.newjustpiano.tools;

import ly.jj.newjustpiano.items.BarrageKey;

public class SyncSequence extends Sequence {
    private int sample;

    @Override
    protected void sequence() {
        super.sequence();
        for (Track t : tracks) System.out.println(t);
        while (!isReleased) {
            BarrageKey key = getNext();
            int sleep = key.wait;
            float wait = ((float) sample / tick) * sleep;
            sleep(wait);
            if (key.value < 0x80) sequenceListener.onNext(key.value, key.volume, key.channel, key.track);
            else switch (key.value & 0x7f) {
                case 0x51:
                    sample = key.volume;
                    break;
                case 0x2f:
                    if (isEnd()) {
                        sequenceListener.onEnd();
                        return;
                    }
            }
        }
    }

    private void sleep(float time) {
        try {
            Thread.sleep((int) time / 1000, (int) ((time % 1000) * 1000));
        } catch (InterruptedException ignore) {
        }
    }

    private BarrageKey getNext() {
        BarrageKey key = new BarrageKey(Integer.MAX_VALUE, 0, 0, 0);
        Track track = null;
        for (Track t : tracks) {
            if (t.events.size() == 0) continue;
            BarrageKey next = t.events.get(0);
            if (next.wait < key.wait) {
                key = next;
                track = t;
            }
        }
        for (Track t : tracks) {
            if (t.events.size() == 0) continue;
            if (t == track) continue;
            t.events.get(0).waited(key.wait);
        }
        if (track == null) return new BarrageKey(0, 0x2f | 0x80, 0, 0);
        track.events.remove(0);
        if (track.position != 0)
            if (track.position % 2 == 0) key.setVolume((int) (key.volume * 0.7f));
        key.track = track.position;
        return key;
    }

    private boolean isEnd() {
        for (Track t : tracks) {
            if (t.events.size() > 0) return false;
        }
        return true;
    }
}
