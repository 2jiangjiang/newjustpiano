package ly.jj.newjustpiano;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.Nullable;
import ly.jj.newjustpiano.tools.SequenceExtractor;
import ly.jj.newjustpiano.views.BarrageView;
import ly.jj.newjustpiano.views.KeyboardView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static ly.jj.newjustpiano.items.StaticItems.playingSong;
import static ly.jj.newjustpiano.items.StaticItems.soundMixer;
import static ly.jj.newjustpiano.tools.StaticTools.setFullScreen;
import static ly.jj.newjustpiano.tools.StaticTools.setNoNotchBar;

public class Keyboard extends Activity {
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setFullScreen(getWindow().getDecorView());
        //setNoNotchBar(getWindow());
        setContentView(R.layout.keyboard);
        BarrageView barrageView = findViewById(R.id.barrage_view);
        KeyboardView keyboardView = findViewById(R.id.keyboard_view);
        Button play = findViewById(R.id.testplay);
        play.setText("play!");
        barrageView.setFreshRate(90);
        keyboardView.setOnKeyDownListener(e -> {
            e += 12 * 4;
            if (e < 0x7f) {
                soundMixer.play(e, 0x67);
            }
        });
        SequenceExtractor sequenceExtractor = new SequenceExtractor(playingSong);
        sequenceExtractor.extractor();
        sequenceExtractor.setOnNextListener((barrageView::addKey));
        new Thread(sequenceExtractor::sequence).start();
        Button add = findViewById(R.id.testadd);
        add.setText("add!");
        add.setOnClickListener(e -> {
            barrageView.addCount(1);
            keyboardView.addCount(1);
        });
        Button reduce = findViewById(R.id.testreduce);
        reduce.setText("reduce!");
        reduce.setOnClickListener(e -> {
            barrageView.addCount(-1);
            keyboardView.addCount(-1);
        });
    }
}
