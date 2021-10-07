package ly.jj.newjustpiano.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ly.jj.newjustpiano.R;

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
        }
        return view;
    }
}
