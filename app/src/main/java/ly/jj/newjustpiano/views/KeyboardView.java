package ly.jj.newjustpiano.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import ly.jj.newjustpiano.R;
import ly.jj.newjustpiano.items.StaticItems;

import java.util.ArrayList;
import java.util.List;

import static ly.jj.newjustpiano.tools.StaticTools.zoomBitmap;

public class KeyboardView extends View {
    private final int[] lastPresses = new int[10];
    private final List<Integer> pressed = new ArrayList();
    private final Paint paint;
    private int keyCount;
    private int drawCount;
    private int viewHeight;
    private int viewWidth;
    private float keyWidth;
    private Bitmap white_m;
    private Bitmap white_l;
    private Bitmap white_r;
    private Bitmap black;
    private Bitmap white_m_p;
    private Bitmap white_l_p;
    private Bitmap white_r_p;
    private Bitmap black_p;
    private onKeyDownListener listener;

    public KeyboardView(Context context) {
        super(context);
        keyCount = 8;
        paint = new Paint();
        paint.setColor(Color.BLACK);
    }

    public KeyboardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.KeyboardView, 0, 0);
        keyCount = a.getInt(R.styleable.KeyboardView_Count, 8);
        a.recycle();
        paint = new Paint();
        paint.setColor(Color.BLACK);
    }

    public KeyboardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.KeyboardView, 0, 0);
        keyCount = a.getInt(R.styleable.KeyboardView_Count, 8);
        a.recycle();
        paint = new Paint();
        paint.setColor(Color.BLACK);
    }

    public void setOnKeyDownListener(onKeyDownListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        resize();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        invalidate();
    }

    private void resize() {
        int r = keyCount / 7;
        int l = keyCount % 7;
        drawCount = r * 12 + (l > 0 ? (l > 1 ? (l > 2 ? (l > 4 ? (l > 5 ? l + 5 : l + 4) : l + 3) : l + 2) : l + 1) : l);
        keyWidth = (float) viewWidth / keyCount;

        white_m = zoomBitmap(StaticItems.white_m, keyWidth, viewHeight);
        white_l = zoomBitmap(StaticItems.white_l, keyWidth, viewHeight);
        white_r = zoomBitmap(StaticItems.white_r, keyWidth, viewHeight);
        black = zoomBitmap(StaticItems.black, keyWidth / 3 * 2, (float) viewHeight * 23 / 40);

        white_m_p = zoomBitmap(StaticItems.white_m_p, keyWidth, viewHeight);
        white_l_p = zoomBitmap(StaticItems.white_l_p, keyWidth, viewHeight);
        white_r_p = zoomBitmap(StaticItems.white_r_p, keyWidth, viewHeight);
        black_p = zoomBitmap(StaticItems.black_p, keyWidth / 3 * 2, (float) viewHeight * 23 / 40);

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        for (int i = 0; i < drawCount; i++) {
            int r = i / 12;
            int l = i % 12;
            l = l > 4 ? l + 1 : l;
            float left = r * 7 * keyWidth + (((float) l + 1) / 2) * keyWidth - (float) black.getWidth() / 2;
            if (l % 2 == 1) {
                canvas.drawBitmap(pressed.contains(i) ? black_p : black, left, 0, paint);
            }
        }
        for (int i = 0; i < drawCount; i++) {
            int r = i / 12;
            int l = i % 12;
            l = l > 4 ? l + 1 : l;
            float left = ((float) l / 2) * keyWidth + r * 7 * keyWidth;
            if (l % 2 == 0) {
                switch (l) {
                    case 0:
                    case 6:
                        canvas.drawBitmap(pressed.contains(i) ? white_r_p : white_r, left, 0, paint);
                        break;
                    case 4:
                    case 12:
                        canvas.drawBitmap(pressed.contains(i) ? white_l_p : white_l, left, 0, paint);
                        break;
                    default:
                        canvas.drawBitmap(pressed.contains(i) ? white_m_p : white_m, left, 0, paint);
                        break;
                }
            }
        }
        super.onDraw(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action & 0xff) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                int position = (action & 0xf00) >> 8;
                int touch = division(event.getX(position), event.getY(position));
                lastPresses[event.getPointerId(position)] = touch;
                pressed.add(touch);
                doPress(touch);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                pressed.clear();
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                position = (action & 0xf00) >> 8;
                lastPresses[event.getPointerId(position)] = -1;
                touch = division(event.getX(position), event.getY(position));
                pressed.remove((Object) touch);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < event.getPointerCount(); i++) {
                    int id = event.getPointerId(i);
                    int press = division(event.getX(i), event.getY(i));
                    if (lastPresses[id] != press) {
                        pressed.remove((Object) lastPresses[id]);
                        pressed.add(press);
                        lastPresses[id] = press;
                        doPress(press);
                        invalidate();
                    }
                }
                break;
        }

        return true;
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

    private void doPress(int i) {
        listener.doKey(i);
    }

    private int division(float x/* the hor x ---> */, float y/* the vec y |*/) {
        if (y > viewHeight * 0.575f) {
            int p = (int) (x / keyWidth);
            int r = p / 7;
            int l = p % 7;
            return r * 12 + (l > 2 ? l * 2 - 1 : l * 2);
        }
        int p = (int) (x / (keyWidth / 3));
        int r = p / 21;
        int l = p % 21;
        return r * 12 + (l > 4 ? (l > 13 ? (l > 16 ? l + 3 : l + 2) : l + 1) : l) / 2;
    }

    @Override
    public OnFocusChangeListener getOnFocusChangeListener() {
        return super.getOnFocusChangeListener();
    }

    public interface onKeyDownListener {
        void doKey(int i);
    }
}
