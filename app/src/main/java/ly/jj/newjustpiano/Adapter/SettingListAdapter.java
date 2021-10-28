package ly.jj.newjustpiano.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ly.jj.newjustpiano.R;

import static ly.jj.newjustpiano.items.StaticItems.*;

public class SettingListAdapter {
    private Context context;
    private LayoutInflater inflater;
    private TableLayout table;

    public SettingListAdapter(Context context, TableLayout table) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.table = table;
    }

    public void start() {
        table.removeAllViews();
        System.gc();
        TableRow row = new TableRow(context);
        TableRow.LayoutParams params=new TableRow.LayoutParams();
        params.span=2;
        for (int i = 0; i < switchSettings.length; i++) {
            final int a = i;
            View view = inflater.inflate(R.layout.set_switch, null);
            setBackground(database.readSetting(switchSettings[a]) == 1, view);
            ((TextView) view.findViewById(R.id.set_text)).setText(switchSettings[a]);
            ((Switch) view.findViewById(R.id.set_switch)).setChecked(database.readSetting(switchSettings[a]) == 1);
            ((Switch) view.findViewById(R.id.set_switch)).setOnCheckedChangeListener((compoundButton, b) -> {
                database.writeSetting(switchSettings[a], b ? 1 : 0);
                setBackground(database.readSetting(switchSettings[a]) == 1, view);
            });
            row.addView(view);
            row.setLayoutParams(params);

            if (row.getChildCount() == 5) {
                table.addView(row);
                row = new TableRow(context);
            }
        }
        table.addView(row);
        for (int i = 0; i < seekSettings.length; i++) {
            final int a = i;
            View view = inflater.inflate(R.layout.set_seek, null);
            setBackground(database.readSetting(seekSettings[a]) == 1, view);
            ((TextView) view.findViewById(R.id.set_text)).setText(seekSettings[a]);
            ((SeekBar) view.findViewById(R.id.set_switch)).setProgress(database.readSetting(seekSettings[a]));
            ((SeekBar) view.findViewById(R.id.set_switch)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            row.addView(view);
            row.setLayoutParams(params);

            if (row.getChildCount() == 2) {
                table.addView(row);
                row = new TableRow(context);
            }
        }
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
