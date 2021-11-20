package ly.jj.newjustpiano;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import androidx.annotation.Nullable;
import ly.jj.newjustpiano.tools.SequenceExtractor;
import ly.jj.newjustpiano.views.BarrageView;
import ly.jj.newjustpiano.views.KeyboardView;

import static java.lang.Thread.sleep;
import static ly.jj.newjustpiano.items.StaticItems.playingSong;
import static ly.jj.newjustpiano.items.StaticItems.soundMixer;
import static ly.jj.newjustpiano.tools.StaticTools.setFullScreen;

public class Keyboard extends Activity {
    SequenceExtractor sequenceExtractor;

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
        barrageView.setFreshRate(60);
        barrageView.setSpeed(400);
        barrageView.setOnClickBarrageListener(System.out::println);
        keyboardView.setOnKeyDownListener(barrageView::pressKey);
        sequenceExtractor = new SequenceExtractor(Base64.decode(playingSong, Base64.DEFAULT));
        sequenceExtractor.extractor();
        sequenceExtractor.setOnEndListener(() -> {
            sleep(3000);
            this.finish();
        });
        sequenceExtractor.setOnNextListener((barrageView::addKey));
        new Thread(sequenceExtractor::sequence).start();
    }

    @Override
    protected void onDestroy() {
        sequenceExtractor.release();
        super.onDestroy();
    }
}
