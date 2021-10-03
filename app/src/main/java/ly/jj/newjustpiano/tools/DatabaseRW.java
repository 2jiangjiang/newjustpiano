package ly.jj.newjustpiano.tools;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Arrays;

public class DatabaseRW {
    private final SQLiteDatabase settings;
    private final SQLiteDatabase songs;

    public DatabaseRW(SQLiteDatabase settings, SQLiteDatabase songs) {
        this.settings = settings;
        this.songs = songs;
        settings.execSQL("create table if not exists local(" +
                "set_name text not null primary key," +
                "value text not null)");
        songs.execSQL("create table if not exists songs(" +
                "id integer not null primary key autoincrement," +
                "name text not null," +
                "subregion text not null," +
                "author text not null," +
                "pauthor text not null," +
                "source int default 0," +
                "song blob not null)");
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

    public void test() {
        songs.delete("songs", "id", new String[]{});
        songs.execSQL("insert into songs(name,subregion,author,pauthor,song) values('1','测试1','1','1','1')");
        songs.execSQL("insert into songs(name,subregion,author,pauthor,song) values('1','测试2','1','1','1')");
        songs.execSQL("insert into songs(name,subregion,author,pauthor,song) values('1','测试3','1','1','1')");
        songs.execSQL("insert into songs(name,subregion,author,pauthor,song) values('1','测试4','1','1','1')");
        songs.execSQL("insert into songs(name,subregion,author,pauthor,song) values('1','测试5','1','1','1')");
    }

    public Cursor readSelects() {
        return songs.query("songs", new String[]{"subregion"}, "", null, "subregion", null, null);
    }

    public Cursor readBySubregion(String subregion) {
        return songs.query("songs", new String[]{"subregion"}, "subregion like '" + subregion + "'", null, null, null, null);
    }

}
