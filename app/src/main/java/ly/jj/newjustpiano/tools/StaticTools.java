package ly.jj.newjustpiano.tools;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import static ly.jj.newjustpiano.items.StaticItems.fullScreenFlags;

public class StaticTools {
    public static void setFullScreen(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(view);
            controller.hide(WindowInsetsCompat.Type.systemBars());
        } else {
            view.setSystemUiVisibility(fullScreenFlags);
        }
    }

    public static void setNoNotchBar(Window window) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(lp);
        }
    }

    public static Bitmap zoomBitmap(Bitmap source, float width, float height) {
        int x = source.getWidth();
        int y = source.getHeight();
        float scaleWidth = width / x;
        float scaleHeight = height / y;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(source, 0, 0, x, y, matrix, true);
    }
}
