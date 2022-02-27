package ly.jj.newjustpiano.items;

import android.graphics.Bitmap;
import android.media.AudioFormat;
import android.media.MediaFormat;
import android.os.Build;
import android.view.View;
import ly.jj.newjustpiano.tools.DatabaseRW;
import ly.jj.newjustpiano.tools.SoundMixer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StaticItems {
    public static MediaFormat audioFormat;
    public static File data;
    public static File cache;
    public static File sounds;
    public static File soundCaches;
    public static float freshRate;

    public static String[] switchSettings = {};
    public static DatabaseRW database;
    public static String playingSong;
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
    public static List<Map<String, Map<Integer, Map<String, Integer>>>> seekSettings = new ArrayList<>();

    public static void Init() {
        Map<String, Integer> value = new TreeMap<>();
        value.put("模式0", 0);
        value.put("模式1", 1);
        value.put("模式2", 2);
        Map<Integer, Map<String, Integer>> keys = new TreeMap<>();
        keys.put(3, value);
        Map<String, Map<Integer, Map<String, Integer>>> setting = new TreeMap<>();
        setting.put("声音引擎模式(重启生效)", keys);
        seekSettings.add(setting);
        value = new TreeMap<>();
        value.put("简单", 60);
        value.put("一般", 40);
        value.put("困难", 20);
        keys = new TreeMap<>();
        keys.put(3, value);
        setting = new TreeMap<>();
        setting.put("难度设置", keys);
        seekSettings.add(setting);
    }


    public static int sampleRate() {
        return audioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
    }

    public static int channelCount() {
        return audioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
    }

    public static int sampleSize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return 2;
        }

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

        return 2; //16bit
    }
}
