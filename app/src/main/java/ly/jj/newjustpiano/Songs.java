package ly.jj.newjustpiano;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import ly.jj.newjustpiano.Adapter.SongBankListAdapter;
import ly.jj.newjustpiano.Adapter.SongListAdapter;

import static ly.jj.newjustpiano.items.StaticItems.database;

public class Songs extends Activity {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.songs);
        GridView list = findViewById(R.id.songs_find_list);
        EditText search = findViewById(R.id.songs_find_song_name);
        View find = findViewById(R.id.songs_find_song);
        SongBankListAdapter adapter = new SongBankListAdapter(this, database.readBanks("%"));
        find.setOnClickListener(v -> {
            Cursor cursor = database.readBanks(search.getText().toString());
            adapter.setCursor(cursor);
            list.setAdapter(adapter);
        });
        search.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                find.callOnClick();
                return true;
            }
            Cursor cursor = database.readBanks(textView.getText().toString());
            adapter.setCursor(cursor);
            list.setAdapter(adapter);
            return false;
        });
        list.setAdapter(adapter);
    }
}
