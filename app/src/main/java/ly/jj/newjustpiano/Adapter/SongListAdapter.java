package ly.jj.newjustpiano.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ly.jj.newjustpiano.Keyboard;
import ly.jj.newjustpiano.R;
import ly.jj.newjustpiano.tools.DatabaseRW;
import ly.jj.newjustpiano.tools.Sequence;
import ly.jj.newjustpiano.tools.SequenceExtractor;

import static ly.jj.newjustpiano.items.StaticItems.*;

public class SongListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private Cursor cursor;
    private String key;
    private String value;

    public SongListAdapter(Context context, String key, String value) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.key = key;
        this.value = value;
        this.cursor = database.readByKey(key, value);
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

    @SuppressLint("ResourceType")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        cursor.moveToPosition(i);
        if (view == null) {
            view = inflater.inflate(R.layout.song_panel, null);
            ((TextView) view.findViewById(R.id.song_name)).setText(cursor.getString(DatabaseRW.SONG_NAME));
            view.findViewById(R.id.song_play).setOnClickListener(v -> {
                cursor.moveToPosition(i);
                playingSong = cursor.getString(DatabaseRW.SONG_DATA);
                Intent intent = new Intent(context, Keyboard.class);
                context.startActivity(intent);
            });
            view.findViewById(R.id.song_listen).setOnClickListener(v -> {
                cursor.moveToPosition(i);
                playingSong = cursor.getString(DatabaseRW.SONG_DATA);
                SequenceExtractor sequenceExtractor = new SequenceExtractor(Base64.decode(playingSong, Base64.DEFAULT));
                sequenceExtractor.setSequenceListener(new Sequence.SequenceListener() {
                    @Override
                    public void onEnd() {

                    }

                    @Override
                    public void onNext(int value, int volume, int channel, int track) {
                        soundMixer.play(value, volume);
                    }
                });
                sequenceExtractor.sequence();
            });
            view.findViewById(R.id.song_share).setOnClickListener(v -> {

            });
            view.findViewById(R.id.song_info_page).setOnClickListener(v -> {
                cursor.moveToPosition(i);
                Dialog dialog = new Dialog(context);
                dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                dialog.setContentView(R.layout.song_info_page);
                ((TextView) dialog.findViewById(R.id.song_info_page_name)).setText(cursor.getString(DatabaseRW.SONG_NAME));
                ((TextView) dialog.findViewById(R.id.song_info_page_author)).setText(cursor.getString(DatabaseRW.SONG_AUTHOR));
                ((TextView) dialog.findViewById(R.id.song_info_page_pauthor)).setText(cursor.getString(DatabaseRW.SONG_PAUTHOR));
                ((TextView) dialog.findViewById(R.id.song_info_page_subregion)).setText(cursor.getString(DatabaseRW.SONG_SUBREGION));
                ((TextView) dialog.findViewById(R.id.song_info_page_bank)).setText(cursor.getString(DatabaseRW.SONG_BANK));
                ((TextView) dialog.findViewById(R.id.song_info_page_source_R)).setText(cursor.getString(DatabaseRW.SONG_SOURCE_R));
                ((TextView) dialog.findViewById(R.id.song_info_page_source_L)).setText(cursor.getString(DatabaseRW.SONG_SOURCE_L));
                ((TextView) dialog.findViewById(R.id.song_info_page_difficult_R)).setText(cursor.getString(DatabaseRW.SONG_DIFFICULT_R));
                ((TextView) dialog.findViewById(R.id.song_info_page_difficult_L)).setText(cursor.getString(DatabaseRW.SONG_DIFFICULT_L));
                dialog.findViewById(R.id.song_info_page_delete).setOnClickListener(v1 -> {
                    Dialog dialog1 = new Dialog(context);
                    dialog1.getWindow().setBackgroundDrawableResource(R.color.transparent);
                    dialog1.setContentView(R.layout.confirm);
                    ((TextView) dialog1.findViewById(R.id.confirm_info)).setText("确定要删除吗");
                    dialog1.findViewById(R.id.confirm_cancel).setOnClickListener(v2 -> dialog1.dismiss());
                    dialog1.findViewById(R.id.confirm_confirm).setOnClickListener(v2 -> {
                        dialog1.dismiss();
                        dialog.dismiss();
                        database.deleteSong(cursor.getString(DatabaseRW.SONG_DATA));
                        cursor = database.readByKey(key, value);
                        notifyDataSetChanged();
                    });
                    dialog1.show();
                });
                dialog.findViewById(R.id.song_info_page_cancel).setOnClickListener(v1 -> dialog.dismiss());
                dialog.show();
            });
        }
        return view;
    }
}
