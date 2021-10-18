package ly.jj.newjustpiano;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Cursor cursor = database.readBanks(editable.toString());
                adapter.setCursor(cursor);
                list.setAdapter(adapter);
            }
        });
        list.setAdapter(adapter);
    }
}
