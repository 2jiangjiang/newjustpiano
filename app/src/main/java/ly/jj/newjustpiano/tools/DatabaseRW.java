package ly.jj.newjustpiano.tools;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseRW {
    private final SQLiteDatabase settings;

    public DatabaseRW(SQLiteDatabase settings, SQLiteDatabase songs) {
        this.settings = settings;
        settings.execSQL("create table if not exists local(set_name text not null primary key,value text not null)");
    }

    public void writeSetting(String key, int value) {
        if (readSetting(key) != -1) {
            settings.execSQL("update local set value='" + value + "' where set_name like '" + key + "'");
        } else {
            settings.execSQL("insert into local(set_name,value) values('" + key + "','" + value + "')");
        }
    }

    public int readSetting(String key) {
        @SuppressLint("Recycle")
        Cursor cursor = settings.query("local", new String[]{"set_name", "value"}, "set_name like '" + key + "'", null, null, null, null);
        if (cursor.moveToNext()) {
            return cursor.getInt(1);
        }
        return -1;
    }

}
