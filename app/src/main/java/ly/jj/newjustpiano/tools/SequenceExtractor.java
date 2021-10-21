package ly.jj.newjustpiano.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static java.lang.Thread.sleep;

public class SequenceExtractor {
    private Sequence sequence;
    private ByteBuffer midiFile;

    public SequenceExtractor(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] data = new byte[fileInputStream.available()];
            fileInputStream.read(data);
            midiFile = ByteBuffer.wrap(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SequenceExtractor(byte[] data) {
        this(ByteBuffer.wrap(data));
    }

    public SequenceExtractor(String path) {
        this(new File(path));
    }

    public SequenceExtractor(ByteBuffer buffer) {
        this.midiFile = buffer;
    }

    public void extractor() {
        System.out.println(new String(midiFile.array()));
        byte[] mime = new byte[4];
        midiFile.get(mime);
        String MIME = new String(mime);
        if (!MIME.equals("MThd")) {
            return;
        }
        byte[] length = new byte[4];
        midiFile.get(length);
        int headLength = bytes2int(length);
        System.out.println("the head length is " + headLength);
        byte[] head = new byte[headLength];
        midiFile.get(head);
        int format = bytes2int(new byte[]{head[0], head[1]});
        switch (format) {
            case 0:
            case 1:
                sequence = new syncSequence();
                break;
            case 2:
                sequence = new asynSequence();
                break;
        }
        int channels = bytes2int(new byte[]{head[2], head[3]});
        int tick = bytes2int(new byte[]{head[4], head[5]});
        sequence.setTick(tick);
        int t = 0;
        while (midiFile.position() < midiFile.limit()) {
            byte[] event = new byte[4];
            midiFile.get(event);
            String EVENT = new String(event);
            if (!EVENT.equals("MTrk")) {
                System.out.println("Not MTrk but " + EVENT);
                break;
            }
            t++;
            length = new byte[4];
            midiFile.get(length);
            int dataLength = bytes2int(length);
            System.out.println("the next track length is " + dataLength);
            byte[] data = new byte[dataLength];
            midiFile.get(data);
            int time = 0;
            sequence.addTrack();
            for (int i = 0; i < data.length; i++) {
                int wait = 0;
                do {
                    wait = (wait << 7) + (data[i] & 0x7f);
                    i++;
                } while ((data[i - 1] & 0xff) >> 7 == 1);
                sequence.addTime(wait);
                time += wait;
                switch (data[i] & 0xff) {
                    case 0xf0:
                        break;
                    case 0xff:
                        int con = data[i + 1] & 0xff;
                        int len = data[i + 2];
                        i += 2;
                        byte[] seq = new byte[len];
                        for (int j = 0; j < len; j++) {
                            i++;
                            seq[j] = data[i];
                        }
                        switch (con) {
                            case 0x00:
                                sequence.setTrack(bytes2int(seq));
                                break;
                            case 0x01:
                                sequence.setRemark(new String(seq));
                                break;
                            case 0x02:
                                sequence.setCopyright(new String(seq));
                                break;
                            case 0x03:
                                sequence.setTrackName(new String(seq));
                                break;
                            case 0x04:
                                sequence.setInstrument(new String(seq));
                                break;
                            case 0x05:
                                sequence.addLyricEvent(time, new String(seq));
                                break;
                            case 0x06:
                                sequence.addTagEvent(time, new String(seq));
                                break;
                            case 0x07:
                                sequence.addFlagEvent(time, new String(seq));
                                break;
                            default:
                                sequence.addKey(time, bytes2int(seq), 0x80 | con, 0);
                                break;
                        }
                        break;
                    default:
                        switch (data[i] & 0xf0) {
                            case 0x80:
                                sequence.finishKey(data[i + 1] & 0xff, data[i] & 0x0f);
                                i += 2;
                                break;
                            case 0x90:
                                sequence.addKey(time, data[i + 2] & 0xff, data[i + 1] & 0xff, t % 2);
                                i += 2;
                                break;
                            case 0xc0:
                            case 0xd0:
                                i++;
                                break;
                            default:
                                i += 2;
                                break;
                        }
                        break;
                }
            }
        }
    }

    public void setOnNextListener(Sequence.OnNextListener onNextListener) {
        sequence.setOnNextListener(onNextListener);
    }

    public void sequence() {
        try {
            sleep(1000);
        } catch (InterruptedException ignored) {
        }
        sequence.sequence();
    }

    private void LargeLogcat(Object o) {
        String string = o.toString();
        while (string.length() > 4096) {
            System.out.println(string.substring(0, 4096));
            string = string.substring(4096);
        }
        System.out.println(string);
    }

    private int bytes2int(byte[] data) {
        int sum = 0;
        for (byte datum : data) {
            sum = (sum << 8) + (datum & 0xff);
        }
        return sum;
    }

}
