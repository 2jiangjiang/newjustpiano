package ly.jj.newjustpiano;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ListView;
import ly.jj.newjustpiano.Adapter.SettingListAdapter;

public class Setting extends Activity {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.setting_page);
        GridView gridView = findViewById(R.id.set_list);
        gridView.setAdapter(new SettingListAdapter(this));
    }
}
