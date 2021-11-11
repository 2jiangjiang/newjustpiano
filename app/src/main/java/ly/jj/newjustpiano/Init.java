package ly.jj.newjustpiano;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ProgressBar;
import androidx.core.app.ActivityCompat;
import ly.jj.newjustpiano.tools.DatabaseRW;
import ly.jj.newjustpiano.tools.MediaDecoder;
import ly.jj.newjustpiano.tools.SoundMixer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static ly.jj.newjustpiano.items.StaticItems.*;
import static ly.jj.newjustpiano.tools.SoundMixer.AUDIO_MODE_DEFAULT;
import static ly.jj.newjustpiano.tools.StaticTools.setFullScreen;

public class Init extends Activity {
    static {
        System.loadLibrary("native-lib");
    }

    ProgressBar progress;
    @SuppressLint("SdCardPath")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setFullScreen(getWindow().getDecorView());
        //setNoNotchBar(getWindow());
        setContentView(R.layout.init);
        progress = findViewById(R.id.init_progress);
        new Thread(() -> {
            progress.setProgress(0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                data = getDataDir();
            } else {
                data = getDir("data", MODE_PRIVATE);
            }
            database = new DatabaseRW(openOrCreateDatabase("settings.db", MODE_PRIVATE, null), openOrCreateDatabase("songs.db", MODE_PRIVATE, null));
            freshRate = getWindowManager().getDefaultDisplay().getRefreshRate();
            try {
                white_m = BitmapFactory.decodeStream(getAssets().open("keyboard/white_m.png"));
                white_r = BitmapFactory.decodeStream(getAssets().open("keyboard/white_r.png"));
                white_l = BitmapFactory.decodeStream(getAssets().open("keyboard/white_l.png"));
                black = BitmapFactory.decodeStream(getAssets().open("keyboard/black.png"));

                white_m_p = BitmapFactory.decodeStream(getAssets().open("keyboard/white_m_p.png"));
                white_r_p = BitmapFactory.decodeStream(getAssets().open("keyboard/white_r_p.png"));
                white_l_p = BitmapFactory.decodeStream(getAssets().open("keyboard/white_l_p.png"));
                black_p = BitmapFactory.decodeStream(getAssets().open("keyboard/black_p.png"));

                note_white = BitmapFactory.decodeStream(getAssets().open("keyboard/white_note.png"));
                note_black = BitmapFactory.decodeStream(getAssets().open("keyboard/black_note.png"));
                note_play = BitmapFactory.decodeStream(getAssets().open("keyboard/play_note.png"));
                note_prac = BitmapFactory.decodeStream(getAssets().open("keyboard/prac_note.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            soundMixer = new SoundMixer();
            cache = getCacheDir();
            sounds = new File(cache, "sounds");
            if (!sounds.exists()) sounds.mkdirs();
            soundCaches = new File(cache, "decodes");
            if (!soundCaches.exists()) soundCaches.mkdirs();
            try {
                String[] soundStr = getAssets().list("sounds");
                for (int i = soundStr.length - 1; i >= 0; i--) {
                    InputStream inputStream = getAssets().open("sounds/" + soundStr[i]);
                    byte[] data = new byte[inputStream.available()];
                    inputStream.read(data);
                    new FileOutputStream(new File(sounds, soundStr[i])).write(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            File[] soundsFile = sounds.listFiles();
            String[] cachesList = soundCaches.list();
            List<String> caches = new ArrayList<>();
            if (cachesList != null)
                caches = Arrays.asList(cachesList);
            try {
                MediaDecoder decoder = new MediaDecoder(30);
                int i = 0;
                for (File sound : soundsFile) {
                    File outSound = new File(soundCaches, sound.getName().substring(0, sound.getName().indexOf('.')));
                    if (!caches.contains(outSound.getName())) {
                        decoder.set(sound.getPath());
                        decoder.decode();
                        new FileOutputStream(outSound).write(decoder.read());
                    }
                    i++;
                    progress.setProgress(i * 100 / 88);
                    soundMixer.setSound(outSound);
                }
                if (audioFormat == null) {
                    decoder.set(soundsFile[0].getPath());
                    decoder.decode();
                }
                decoder.release();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.gc();
                soundMixer.setMode(AUDIO_MODE_DEFAULT);
                soundMixer.build(sampleRate(), channelCount());
            }
            Intent intent = new Intent(this, Main.class);
            intent.setFlags(FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }).start();
    }
}
