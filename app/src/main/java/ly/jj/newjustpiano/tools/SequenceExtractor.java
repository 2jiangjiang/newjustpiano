package ly.jj.newjustpiano.tools;

import java.nio.ByteBuffer;

public class SequenceExtractor {
    public Sequence sequence;
    public byte lastControl;

    public SequenceExtractor(ByteBuffer midi) {
        extract(midi);
    }

    public SequenceExtractor(byte[] midi) {
        this(ByteBuffer.wrap(midi));
    }

    public void extract(ByteBuffer midi) {
        byte[] MThd = new byte[4];
        midi.get(MThd);
        byte[] length = new byte[4];
        midi.get(length);
        byte[] ff = new byte[2];
        midi.get(ff);
        if ((bytes2int(ff)) == 0x10) sequence = new AsynSequence();
        else sequence = new SyncSequence();
        byte[] nn = new byte[2];
        midi.get(nn);
        sequence.setTracks(bytes2int(nn));
        byte[] dd = new byte[2];
        midi.get(dd);
        sequence.useTick = bytes2int(dd) <= 0x7fff;
        sequence.tick = bytes2int(dd) & 0x7fff;
        for (int i = 0; i < bytes2int(nn); i++) {
            extractor(midi);
            sequence.nextTrack();
        }
        sequence.sort();
    }

    public void extractor(ByteBuffer midi) {
        byte[] MTrk = new byte[4];
        midi.get(MTrk);
        byte[] length = new byte[4];
        midi.get(length);
        int pos = midi.position();
        while (midi.position() < pos + bytes2int(length)) {
            int wait = autoLength(midi);
            sequence.waitTime(wait);
            handle(midi);
        }
    }

    public void handle(ByteBuffer midi) {
        byte con = midi.get();
        if ((con & 0xff) == 0xff) {
            handleControl(midi);
        } else {
            handleEvent(con, midi);
        }
    }

    public void handleControl(ByteBuffer midi) {
        byte con = midi.get();
        int length = autoLength(midi);
        byte[] data = new byte[length];
        midi.get(data);
        switch (con & 0xff) {
            case 0x00:
                sequence.setIst(bytes2int(data));
                break;
            case 0x01:
                sequence.setTackInfo(new String(data));
                break;
            case 0x02:
                sequence.setTackAuthor(new String(data));
                break;
            case 0x03:
                sequence.setTackName(new String(data));
                break;
            case 0x04:
                sequence.setTackInstrument(new String(data));
                break;
            case 0x05:
                sequence.addLyric(new String(data));
                break;
            case 0x06:
                sequence.addFlag(bytes2int(data));
                break;
            case 0x07:
                sequence.stringEvent(new String(data));
                break;
            case 0x2f:
                break;
            case 0x51:
                sequence.addEvent(0x51 | 0x80, bytes2int(data), 0);
            case 0x58:
                sequence.addEvent(0x58 | 0x80, bytes2int(data), 0);
        }
    }

    public void handleEvent(byte con, ByteBuffer midi) {
        if ((con & 0xff) < 0x80) handleEvent(lastControl, (ByteBuffer) midi.position(midi.position() - 1));
        else {
            byte[] data;
            switch (con & 0xf0) {
                case 0x80:
                    data = new byte[2];
                    midi.get(data);
                    sequence.finishEvent(data, con & 0x0f);
                    break;
                case 0x90:
                    data = new byte[2];
                    midi.get(data);
                    sequence.addEvent(data, (con & 0x0f));
                    break;
                case 0xa0:
                    data = new byte[2];
                    midi.get(data);
                    sequence.addEvent(data, (con & 0x0f));
                    sequence.finishEvent(data, con & 0x0f);
                    break;
                case 0xb0:
                    data = new byte[2];
                    midi.get(data);
                    sequence.changeControl(data);
                    break;
                case 0xc0:
                    data = new byte[1];
                    midi.get(data);
                    sequence.setInstrument(bytes2int(data));
                    break;
                case 0xd0:
                    data = new byte[1];
                    midi.get(data);
                    sequence.changeStrength(con & 0x0f, bytes2int(data));
                    break;
                case 0xe0:
                    data = new byte[2];
                    midi.get(data);
                    break;
            }
            lastControl = con;
        }
    }

    private int bytes2int(byte[] data) {
        int sum = 0;
        for (byte datum : data) {
            sum = (sum << 8) + (datum & 0xff);
        }
        return sum;
    }

    private int autoLength(ByteBuffer byteBuffer) {
        int sum = 0;
        byte b;
        do {
            b = byteBuffer.get();
            sum = (sum << 7) + (b & 0x7f);
        } while (((b & 0xff) >> 7) == 0x01);
        return sum;
    }

    public void setSequenceListener(Sequence.SequenceListener listener) {
        sequence.setSequenceListener(listener);
    }

    public void sequence() {
        new Thread(sequence::sequence).start();
    }

    public void release() {
        this.sequence.isReleased = true;
        this.sequence.release();
        this.sequence=null;
    }
}
