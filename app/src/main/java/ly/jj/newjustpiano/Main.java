package ly.jj.newjustpiano;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static java.lang.Thread.sleep;
import static ly.jj.newjustpiano.items.StaticItems.playingSong;

public class Main extends Activity {
    private boolean exit;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main);
        findViewById(R.id.main_local).setOnClickListener(v -> {
            Intent intent = new Intent(this, Local.class);
            startActivity(intent);
        });
        findViewById(R.id.main_online).setOnClickListener(v -> {
            Intent intent = new Intent(this, Online.class);
            startActivity(intent);
        });
        findViewById(R.id.main_songs).setOnClickListener(v -> {
            Intent intent = new Intent(this, Songs.class);
            startActivity(intent);
        });
        findViewById(R.id.main_setting).setOnClickListener(v -> {
            Intent intent = new Intent(this, Setting.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (exit) {
                System.exit(0);
            } else {
                exit = true;
                new Thread(() -> {
                    try {
                        sleep(3000);
                    } catch (InterruptedException ignore) {
                    }
                    exit = false;
                }).start();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
