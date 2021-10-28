package ly.jj.newjustpiano;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import ly.jj.newjustpiano.Adapter.SettingListAdapter;

public class Setting extends Activity {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.setting_page);
        TableLayout table = findViewById(R.id.setting_table);
        new SettingListAdapter(this, table).start();
    }
}
