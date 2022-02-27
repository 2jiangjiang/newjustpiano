package ly.jj.newjustpiano.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import ly.jj.newjustpiano.R;

import java.util.Map;

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
            if (row.getChildCount() == 5) {
                table.addView(row);
                row = new TableRow(context);
            }
        }
        table.addView(row);
        row = new TableRow(context);
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.span = 5;
        for (int i = 0; i < seekSettings.size(); i++) {
            Map<String, Map<Integer, Map<String, Integer>>> setting = seekSettings.get(i);
            String set = (String) setting.keySet().toArray()[0];
            Map<Integer, Map<String, Integer>> values = setting.get(set);
            Integer v = (Integer) values.keySet().toArray()[0];
            Map<String, Integer> value = values.get(v);
            Object[] v1 = value.keySet().toArray();
            Object[] v2 = value.values().toArray();
            final int a = i;
            View view = inflater.inflate(R.layout.set_seek, null);
            setBackground(database.readSetting(set) == 1, view);
            ((TextView) view.findViewById(R.id.set_text)).setText(set);
            ((SeekBar) view.findViewById(R.id.set_seek)).setProgress(database.readSetting(set));
            ((SeekBar) view.findViewById(R.id.set_seek)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        progress = (progress / 25 + 1) / 2;
                        seekBar.setProgress(progress * 50);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    String str = "";
                    switch (seekBar.getProgress() / 50) {
                        case 0:
                            str = (String) v1[0];
                            break;
                        case 1:
                            str = (String) v1[1];
                            break;
                        case 2:
                            str = (String) v1[2];
                            break;
                    }
                    ((TextView) view.findViewById(R.id.set_info)).setText(str);
                    database.writeSetting(set, (Integer) v2[seekBar.getProgress() / 50]);
                    System.out.println("set setting " + set + " to " + v2[seekBar.getProgress() / 50]);
                }
            });
            view.setLayoutParams(params);
            row.addView(view);
            table.addView(row);
            row = new TableRow(context);
        }
        System.gc();
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
