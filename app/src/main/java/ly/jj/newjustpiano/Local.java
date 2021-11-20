package ly.jj.newjustpiano;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import ly.jj.newjustpiano.Adapter.SongListAdapter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static ly.jj.newjustpiano.items.StaticItems.database;
import static ly.jj.newjustpiano.tools.StaticTools.verifyStorage;

public class Local extends Activity {
    private final List<Button> selectsList = new ArrayList<>();
    private LayoutInflater inflater;
    private float textSize;
    private EditText path;
    private Uri pathUri;

    @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.local);
        inflater = LayoutInflater.from(this);
        textSize = new TextView(this).getTextSize() / getResources().getDisplayMetrics().density;
        LinearLayout flipper_select = findViewById(R.id.local_flipper_select);
        ViewFlipper flipper = findViewById(R.id.local_flipper);
        Cursor selects = database.readSelects();
        while (selects.moveToNext()) {
            Button button = new Button(this);
            button.setText(selects.getString(0));
            button.setTextSize(textSize);
            button.setBackground(getDrawable(R.drawable.button_background_t));
            button.setOnClickListener(v -> {
                int i = flipper.getDisplayedChild();
                int j = selectsList.indexOf(v);
                if (i == j) return;
                if (i > j) {
                    flipper.setInAnimation(Local.this, R.anim.slide_left_in);
                    flipper.setOutAnimation(Local.this, R.anim.slide_right_out);
                } else {
                    flipper.setInAnimation(Local.this, R.anim.slide_right_in);
                    flipper.setOutAnimation(Local.this, R.anim.slide_left_out);
                }
                flipper.setDisplayedChild(j);
                for (Button b : selectsList) {
                    b.setTextSize(textSize);
                    ((Button) v).setTextSize((float) (textSize * 1.5));
                }
            });
            selectsList.add(button);
            flipper_select.addView(button);
        }
        if (selects.getCount() != 0)
            selectsList.get(0).setTextSize((float) (textSize * 1.5));
        setFlipperTouchListener(flipper, flipper);
        selects.moveToFirst();
        do {
            GridView gridView = new GridView(this);
            gridView.setNumColumns(5);
            setFlipperTouchListener(gridView, flipper);
            if (selects.getCount() != 0)
                gridView.setAdapter(new SongListAdapter(this, "subregion", selects.getString(0)));
            flipper.addView(gridView);
        } while (selects.moveToNext());
        flipper.setDisplayedChild(0);
        findViewById(R.id.local_upload).setOnClickListener(v -> {
            verifyStorage(this);
            Dialog dialog = new Dialog(this);
            dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
            dialog.setContentView(R.layout.local_song_upload);
            dialog.show();
            dialog.findViewById(R.id.upload_path_button).setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            });
            path = dialog.findViewById(R.id.upload_path_text);
            dialog.findViewById(R.id.upload_cancel).setOnClickListener(view -> dialog.dismiss());
            path.setOnEditorActionListener((v1, actionId, event) -> {
                String str = v1.getText().toString();
                str = str.substring(str.lastIndexOf("/") + 1);
                str = str.substring(0, str.lastIndexOf("."));
                ((EditText) dialog.findViewById(R.id.upload_name)).setText(str);
                return false;
            });
            dialog.findViewById(R.id.upload_upload).setOnClickListener(view -> {
                File file = new File(path.getText().toString());
                byte[] data = new byte[0];
                try {
                    BufferedInputStream reader = new BufferedInputStream(getContentResolver().openInputStream(pathUri));
                    data = new byte[reader.available()];
                    reader.read(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String str = Base64.encodeToString(data, Base64.DEFAULT);
                database.addSong(((EditText) dialog.findViewById(R.id.upload_name)).getText().toString(),
                        ((EditText) dialog.findViewById(R.id.upload_subregion)).getText().toString(),
                        ((EditText) dialog.findViewById(R.id.upload_author)).getText().toString(),
                        "test",
                        ((EditText) dialog.findViewById(R.id.upload_bank)).getText().toString(),
                        str);
                dialog.dismiss();
                super.onResume();
                onCreate(bundle);
            });
        });
    }

    @SuppressLint("SdCardPath")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        verifyStorage(this);
        if (resultCode == Activity.RESULT_OK) {
            pathUri = data.getData();
            String name = Uri.decode(pathUri.getPath());
            this.path.setText(name);
            this.path.onEditorAction(IME_ACTION_DONE);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setFlipperTouchListener(View v, ViewFlipper flipper) {
        v.setOnTouchListener(new View.OnTouchListener() {
            private float startX;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float moveX = 100f;
                float endX = motionEvent.getX();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = motionEvent.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (endX - startX > moveX) {
                            if (flipper.getDisplayedChild() == 0) return false;
                            selectsList.get(flipper.getDisplayedChild() - 1).callOnClick();
                            return true;
                        } else if (startX - endX > moveX) {
                            if (flipper.getDisplayedChild() + 1 == selectsList.size()) return false;
                            selectsList.get(flipper.getDisplayedChild() + 1).callOnClick();
                            return true;
                        }
                    case MotionEvent.ACTION_MOVE:
                        if (endX - startX > moveX || startX - endX > moveX) return true;
                }
                return false;
            }
        });
    }
}
