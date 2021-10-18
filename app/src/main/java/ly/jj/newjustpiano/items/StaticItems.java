package ly.jj.newjustpiano.items;

import android.graphics.Bitmap;
import android.media.MediaFormat;
import android.view.View;
import ly.jj.newjustpiano.tools.DatabaseRW;
import ly.jj.newjustpiano.tools.SoundMixer;

import java.io.File;
import java.nio.ByteBuffer;

public class StaticItems {
    public static MediaFormat audioFormat;
    public static File data;
    public static File cache;
    public static File sounds;
    public static File soundCaches;
    public static float freshRate;

    public static String[] settings = {"123", "456", "789", "123", "456", "789", "123", "456", "789", "123", "456", "789"};

    public static DatabaseRW database;

    public static ByteBuffer playingSong;

    public static Bitmap white_m;
    public static Bitmap white_r;
    public static Bitmap white_l;
    public static Bitmap black;

    public static Bitmap white_m_p;
    public static Bitmap white_r_p;
    public static Bitmap white_l_p;
    public static Bitmap black_p;

    public static Bitmap note_white;
    public static Bitmap note_black;
    public static Bitmap note_play;
    public static Bitmap note_prac;

    public static int fullScreenFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    public static SoundMixer soundMixer;

    public static int sampleRate() {
        return audioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
    }

    public static int channelCount() {
        return audioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
    }

    public static int sampleSize() {
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            switch (audioFormat.getInteger(MediaFormat.KEY_PCM_ENCODING)) {
                case AudioFormat.ENCODING_PCM_8BIT:
                    return 1;
                case AudioFormat.ENCODING_PCM_16BIT:
                    return 2;
                case AudioFormat.ENCODING_PCM_24BIT_PACKED:
                    return 3;
                case AudioFormat.ENCODING_PCM_32BIT:
                case AudioFormat.ENCODING_PCM_FLOAT:
                    return 4;
            }
        }
        it's seem has error on sdk31
         */
        return 2; //16bit
    }
}
