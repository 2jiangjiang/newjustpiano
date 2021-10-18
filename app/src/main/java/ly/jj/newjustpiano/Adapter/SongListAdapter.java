package ly.jj.newjustpiano.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ly.jj.newjustpiano.Keyboard;
import ly.jj.newjustpiano.R;
import ly.jj.newjustpiano.tools.DatabaseRW;
import ly.jj.newjustpiano.tools.SequenceExtractor;

import java.nio.ByteBuffer;

import static ly.jj.newjustpiano.items.StaticItems.playingSong;
import static ly.jj.newjustpiano.items.StaticItems.soundMixer;

public class SongListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private Cursor cursor;

    public SongListAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        cursor.moveToPosition(i);
        if (view == null) {
            view = inflater.inflate(R.layout.song_panel, null);
            ((TextView) view.findViewById(R.id.song_name)).setText(cursor.getString(0));
            view.findViewById(R.id.song_play).setOnClickListener(v -> {
                playingSong = cursor.getString(DatabaseRW.SONG_DATA);
                Intent intent = new Intent(context, Keyboard.class);
                context.startActivity(intent);
            });
            view.findViewById(R.id.song_listen).setOnClickListener(v -> {
                SequenceExtractor sequenceExtractor = new SequenceExtractor(cursor.getBlob(DatabaseRW.SONG_DATA));
                sequenceExtractor.extractor();
                sequenceExtractor.setOnNextListener((value, volume) -> {
                    soundMixer.play(value, volume);
                });
                new Thread(sequenceExtractor::sequence).start();
            });
            view.findViewById(R.id.song_share).setOnClickListener(v -> {

            });
        }
        return view;
    }
}
