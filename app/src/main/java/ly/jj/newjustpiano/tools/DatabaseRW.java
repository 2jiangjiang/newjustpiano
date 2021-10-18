package ly.jj.newjustpiano.tools;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ly.jj.newjustpiano.items.StaticItems;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Base64;

public class DatabaseRW {
    private final SQLiteDatabase settings;
    private final SQLiteDatabase songs;

    public static int SONG_DATA;
    public static int SONG_ID;
    public static int SONG_NAME;
    public static int SONG_SUBREGION;
    public static int SONG_AUTHOR;
    public static int SONG_PAUTHOR;
    public static int SONG_SOURCE;
    public static int SONG_BANK;

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
                "bank text not null," +
                "song blob not null)");
        String[] columnNames = songs.query("songs", new String[]{"*"}, null, null, null, null, null).getColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            switch (columnNames[i]) {
                case "song":
                    SONG_DATA = i;
                    break;
                case "id":
                    SONG_ID = i;
                    break;
                case "name":
                    SONG_NAME = i;
                    break;
                case "subregion":
                    SONG_SUBREGION = i;
                    break;
                case "author":
                    SONG_AUTHOR = i;
                    break;
                case "pauthor":
                    SONG_PAUTHOR = i;
                    break;
                case "source":
                    SONG_SOURCE = i;
                    break;
                case "bank":
                    SONG_BANK = i;
                    break;
            }
        }
        test();
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
        String data = StaticItems.playingSong;

        songs.delete("songs", "id", new String[]{});
        songs.execSQL("insert into songs(name,subregion,author,pauthor,bank,song) values('1','测试1','1','1','default','" + data + "')");
        songs.execSQL("insert into songs(name,subregion,author,pauthor,bank,song) values('2','测试1','1','1','default','" + data + "')");
        songs.execSQL("insert into songs(name,subregion,author,pauthor,bank,song) values('3','测试1','1','1','default','" + data + "')");
        songs.execSQL("insert into songs(name,subregion,author,pauthor,bank,song) values('4','测试1','1','1','default','" + data + "')");
        songs.execSQL("insert into songs(name,subregion,author,pauthor,bank,song) values('1','测试2','1','1','default2','" + data + "')");
        songs.execSQL("insert into songs(name,subregion,author,pauthor,bank,song) values('1','测试3','1','1','default2','" + data + "')");
        songs.execSQL("insert into songs(name,subregion,author,pauthor,bank,song) values('1','测试4','1','1','default1','" + data + "')");
        songs.execSQL("insert into songs(name,subregion,author,pauthor,bank,song) values('1','测试5','1','1','default1','" + data + "')");
    }

    public Cursor readSelects() {
        return songs.query("songs", new String[]{"subregion"}, "", null, "subregion", null, null);
    }

    public Cursor readBanks(String key) {
        return songs.query("songs", new String[]{"bank", "name", "subregion", "author", "pauthor"},
                "name like '%"
                        + key + "%' or subregion like '%"
                        + key + "%' or author like '%"
                        + key + "%' or pauthor like '%"
                        + key + "%' or bank like '%"
                        + key + "%'", null, "bank", null, null);
    }

    public Cursor readByKey(String key, String name) {
        return songs.query("songs", new String[]{"*"},
                key + " like '" + name + "'",
                null, null, null, null);
    }
}
