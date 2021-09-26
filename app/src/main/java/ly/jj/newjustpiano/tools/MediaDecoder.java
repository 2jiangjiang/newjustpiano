package ly.jj.newjustpiano.tools;


import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import ly.jj.newjustpiano.items.StaticItems;

import java.io.IOException;
import java.nio.ByteBuffer;

import static ly.jj.newjustpiano.items.StaticItems.*;

public class MediaDecoder {
    private final int bufferTime;
    private MediaCodec mediaCodec;
    private MediaExtractor extractor;
    private ByteBuffer data;

    public MediaDecoder(int bufferTime) {
        this.bufferTime = bufferTime;
    }

    public void set(String path) {
        extractor = new MediaExtractor();
        try {
            extractor.setDataSource(path);
            extractor.selectTrack(0);
            MediaFormat inputFormat = extractor.getTrackFormat(0);
            if (!inputFormat.getString(MediaFormat.KEY_MIME).startsWith("audio")) {
                return;
            }
            mediaCodec = MediaCodec.createDecoderByType(inputFormat.getString(MediaFormat.KEY_MIME));
            mediaCodec.configure(inputFormat, null, null, 0);
            mediaCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decode() {
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        do {
            int TIMEOUT_US = 1000;
            int iPosition = mediaCodec.dequeueInputBuffer(TIMEOUT_US);
            if (iPosition >= 0) {
                ByteBuffer buffer = mediaCodec.getInputBuffer(iPosition);
                int sampleSize = extractor.readSampleData(buffer, 0);
                if (sampleSize < 0) {
                    mediaCodec.queueInputBuffer(iPosition, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                } else {
                    mediaCodec.queueInputBuffer(iPosition, 0, sampleSize, extractor.getSampleTime(), 0);
                    extractor.advance();
                }
            }
            int oPosition = mediaCodec.dequeueOutputBuffer(info, TIMEOUT_US);
            switch (oPosition) {
                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                    StaticItems.audioFormat = mediaCodec.getOutputFormat();
                    data = ByteBuffer.allocate(sampleRate() * sampleSize() * channelCount() * bufferTime);
                    break;
                case MediaCodec.INFO_TRY_AGAIN_LATER:
                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                    break;
                default:
                    ByteBuffer outBuffer = mediaCodec.getOutputBuffer(oPosition);
                    byte[] decoded = new byte[info.size];
                    outBuffer.get(decoded);
                    outBuffer.clear();
                    data.put(decoded);
                    mediaCodec.releaseOutputBuffer(oPosition, false);
                    break;
            }
        } while (info.flags == 0);
        data.flip();
    }

    public byte[] read() {
        byte[] audio = new byte[data.limit()];
        data.get(audio);
        data.clear();
        return audio;
    }

    public void release() {
        try {
            extractor.release();
            extractor = null;
            mediaCodec.stop();
            mediaCodec.release();
            mediaCodec = null;
            data.clear();
            data = null;
        } catch (NullPointerException ignored) {
        }
    }
}