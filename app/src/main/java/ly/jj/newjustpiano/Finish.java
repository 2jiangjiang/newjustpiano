package ly.jj.newjustpiano;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;

public class Finish extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        finish();
    }
}
