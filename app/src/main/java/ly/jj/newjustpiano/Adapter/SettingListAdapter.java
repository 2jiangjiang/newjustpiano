package ly.jj.newjustpiano.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;
import ly.jj.newjustpiano.R;

import static ly.jj.newjustpiano.items.StaticItems.database;
import static ly.jj.newjustpiano.items.StaticItems.settings;

public class SettingListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;

    public SettingListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return settings.length;
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
        if (view == null) {
            view = inflater.inflate(R.layout.set_switch, null);
            setBackground(database.readSetting(settings[i]) == 1, view);
            ((TextView) view.findViewById(R.id.set_text)).setText(settings[i]);
            ((Switch) view.findViewById(R.id.set_switch)).setOnCheckedChangeListener((compoundButton, b) -> {
                database.writeSetting(settings[i], b ? 1: 0);
                notifyDataSetChanged();
            });
        } else {
            setBackground(database.readSetting(settings[i]) == 1, view);
        }
        return view;
    }

    private void setBackground(boolean b, View view) {
        @SuppressLint("UseCompatLoadingForDrawables")
        GradientDrawable drawable = (GradientDrawable) context.getResources().getDrawable(R.drawable.page_background).mutate();
        int color;
        if (b) color = context.getResources().getColor(R.color.purple_200_t);
        else color = context.getResources().getColor(R.color.black_200);
        drawable.setColor(color);
        view.setBackground(drawable);
    }
}
