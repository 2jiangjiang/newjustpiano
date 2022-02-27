package ly.jj.newjustpiano;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import androidx.annotation.Nullable;
import ly.jj.newjustpiano.tools.Sequence;
import ly.jj.newjustpiano.tools.SequenceExtractor;
import ly.jj.newjustpiano.views.BarrageView;
import ly.jj.newjustpiano.views.KeyboardView;

import static ly.jj.newjustpiano.items.StaticItems.playingSong;
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
        sequenceExtractor.setSequenceListener(new Sequence.SequenceListener() {
            @Override
            public void onEnd() {
                Keyboard.this.finish();
            }

            @Override
            public void onNext(int value, int volume, int channel, int track) {
                barrageView.addKey(value, volume, channel, track);
            }
        });

        sequenceExtractor.sequence();
    }

    @Override
    protected void onDestroy() {
        sequenceExtractor.release();
        super.onDestroy();
    }
}
