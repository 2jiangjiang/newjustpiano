package ly.jj.newjustpiano;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ViewFlipper;
import androidx.annotation.Nullable;
import ly.jj.newjustpiano.Adapter.SettingListAdapter;

public class Local extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.local);
        LinearLayout flipper_select = findViewById(R.id.local_flipper_select);
        ViewFlipper flipper = findViewById(R.id.local_flipper);
    }
}
