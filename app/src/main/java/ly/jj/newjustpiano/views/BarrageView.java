package ly.jj.newjustpiano.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import ly.jj.newjustpiano.R;
import ly.jj.newjustpiano.items.BarrageKey;
import ly.jj.newjustpiano.items.StaticItems;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;
import static ly.jj.newjustpiano.items.StaticItems.soundMixer;
import static ly.jj.newjustpiano.tools.StaticTools.zoomBitmap;

public class BarrageView extends View {
    private final Paint paint = new Paint();
    private final List<BarrageKey> drawKeys = new CopyOnWriteArrayList<>();
    private final List<BarrageKey> playKeys = new CopyOnWriteArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private OnClickBarrageListener onClickBarrageListener;
    private int keyCount;
    private Bitmap note_white;
    private Bitmap note_black;
    private Bitmap note_play;
    private Bitmap note_prac;
    private float interval;
    private float barrageWidth;
    private float barrageHeight;
    private int viewWidth;
    private int viewHeight;
    private int fresh_ms;
    private int fresh_ns;
    private float step;
    private final Thread iterator = new Thread(() -> {
        new Thread(() -> {
            try {
                while (true) {
                    sleep(fresh_ms, fresh_ns);
                    invalidate();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        try {
            int sleep_ns = 2000000;
            while (step > 1.0) {
                sleep_ns /= 2;
                step /= 2;
            }
            int sleep_ms = sleep_ns / 1000000;
            sleep_ns %= 1000000;
            while (true) {
                lock.lock();
                List<BarrageKey> list = new ArrayList<>();
                for (BarrageKey key : drawKeys) {
                    key.addLength(step);
                    if (key.length > (viewHeight + barrageHeight * 2)) list.add(key);
                }
                drawKeys.removeAll(list);
                list.clear();
                for (BarrageKey key : playKeys) {
                    key.addLength(step);
                    if (key.length > viewHeight) list.add(key);
                }
                playKeys.removeAll(list);
                for (BarrageKey key : list) {
                    soundMixer.play(key.value, key.volume);
                }
                lock.unlock();
                sleep(sleep_ms, sleep_ns);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    });
    private int speed;
    private int drawCount;

    public BarrageView(Context context) {
        super(context);
        keyCount = 8;
        paint.setColor(Color.BLACK);
        iterator.start();
    }

    public BarrageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.KeyboardView, 0, 0);
        keyCount = a.getInt(R.styleable.KeyboardView_Count, 8);
        a.recycle();
        paint.setColor(Color.BLACK);
        iterator.start();
    }

    public BarrageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.KeyboardView, 0, 0);
        keyCount = a.getInt(R.styleable.KeyboardView_Count, 8);
        a.recycle();
        paint.setColor(Color.BLACK);
        iterator.start();
    }

    public void setFreshRate(int rate) {
        fresh_ms = 1000 / rate;
        fresh_ns = 1000000000 / rate - fresh_ms * 1000000;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        step = (float) viewHeight / speed;
    }

    public void setCount(int i) {
        keyCount = i;
        resize();
        invalidate();
    }

    public void addCount(int i) {
        keyCount += i;
        resize();
        invalidate();
    }

    public void pressKey(int key) {
        for (BarrageKey k : drawKeys) {
            if (k.value % drawCount == key) {
                drawKeys.remove(k);
                if (onClickBarrageListener != null) {
                    int a = (int) ((viewHeight + barrageHeight - k.length) / barrageHeight * 2);
                    a = (a < 0) ? -a : a;
                    a = Math.min(a, 4);
                    onClickBarrageListener.onClickBarrage(a);
                }
                soundMixer.play(k.value, 0x67);
                return;
            }
        }

        if (drawKeys.size() != 0) {
            soundMixer.play(((drawKeys.get(0).value) / drawCount) * 12 + key, 0x67);
            if (onClickBarrageListener != null) {
                onClickBarrageListener.onClickBarrage(4);
            }
        } else soundMixer.play(12 * 5 + key, 0x67);
    }

    public void setOnClickBarrageListener(OnClickBarrageListener onClickBarrageListener) {
        this.onClickBarrageListener = onClickBarrageListener;
    }

    public void addKey(int value, int volume, int channel,int track) {
        lock.lock();
        if (track % 2 == 1 && (drawKeys.size() == 0 || drawKeys.get(drawKeys.size() - 1).length > 40))
            drawKeys.add(new BarrageKey(0, value, volume, 0));
        else playKeys.add(new BarrageKey(0, value, volume, 0));
        lock.unlock();
    }

    private void resize() {
        int r = keyCount / 7;
        int l = keyCount % 7;
        drawCount = r * 12 + (l > 1 ? (l > 2 ? (l > 4 ? (l > 5 ? l + 4 : l + 3) : l + 2) : l + 1) : l);
        interval = (float) viewWidth / keyCount / 2;

        barrageWidth = (float) viewWidth / keyCount * 2 / 5;
        barrageHeight = barrageWidth * 3 / 5;

        note_white = zoomBitmap(StaticItems.note_white, barrageWidth, barrageHeight);
        note_black = zoomBitmap(StaticItems.note_black, barrageWidth, barrageHeight);
        note_play = zoomBitmap(StaticItems.note_play, barrageWidth, barrageHeight);
        note_prac = zoomBitmap(StaticItems.note_prac, barrageWidth, barrageHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boolean first = true;
        int play, r, l, value;
        for (BarrageKey key : drawKeys) {
            play = key.value % drawCount;
            r = play / 12;
            l = play % 12;
            l = (l > 4 ? l + 1 : l) + 1;
            value = r * 14 + l;
            if (first) {
                first = false;
                canvas.drawBitmap((value % 2) == 0 ? note_prac : note_play, value * interval - barrageWidth / 2, (key.length - barrageHeight), paint);
                continue;
            }
            canvas.drawBitmap((value % 2) == 0 ? note_black : note_white, value * interval - barrageWidth / 2, (key.length - barrageHeight), paint);
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        resize();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        invalidate();
        step = (float) viewHeight / speed;
    }

    public interface OnClickBarrageListener {
        void onClickBarrage(int level);
    }
}
