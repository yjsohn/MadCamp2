package com.example.moneyproject;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;


import java.io.File;

public class GalleryContent extends AppCompatActivity {

    ImageView image;
    TextView date_tv;
    TextView location_tv;
    TextView description_tv;
    ImageButton edit;
    SQLiteDatabase db;
    String path;
    String date;
    String loc;
    String description;
    String ImageInfo[] = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_content);
        path = getIntent().getStringExtra("PATH");
        image = findViewById(R.id.detailed_image2);
        //set Rounding
        GradientDrawable drawable= (GradientDrawable) getDrawable(R.drawable.background_rounding);
        image.setBackground(drawable);
        image.setClipToOutline(true);

        date_tv= findViewById(R.id.date_tv);
        location_tv = findViewById(R.id.location_tv);
        description_tv = findViewById(R.id.description_tv);
        edit = findViewById(R.id.edit_btn);
        edit.setOnClickListener(view -> {
            ImageInfo[1] = date_tv.getText().toString();
            ImageInfo[2] = location_tv.getText().toString();
            ImageInfo[3] = description_tv.getText().toString();

            Intent intent = new Intent(getApplicationContext(), EditContent.class);
            intent.putExtra("DETAIL_INFO", ImageInfo);
            startActivityForResult(intent, 1);
        });
        DBSearch("images", path);

        //set image
        File tempFile = new File(ImageInfo[0]);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        image.setImageBitmap(originalBm);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String returnedInfo[] = data.getStringArrayExtra("result");
                date_tv.setText(returnedInfo[0]);
                location_tv.setText(returnedInfo[1]);
                description_tv.setText(returnedInfo[2]);
                /*ContentValues values = new ContentValues();
                values.put("date", returnedInfo[0]);
                values.put("location", returnedInfo[1]);
                values.put("description", returnedInfo[2]);
                db.update("images", values,"path=?", new String[]{ImageInfo[0]});
                */
            }
        }
    }

    void DBSearch(String tableName, String path) {
        Cursor cursor = null;
        try {
            cursor = db.query(tableName, null, "path"+ " = ?", new String[]{path}, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    date = cursor.getString(cursor.getColumnIndex("date"));
                    loc = cursor.getString(cursor.getColumnIndex("location"));
                    description = cursor.getString(cursor.getColumnIndex("description"));

                    //db에서 받아온 값으로 설정
                    date_tv.setText(date);
                    location_tv.setText(loc);
                    description_tv.setText(description);

                    ImageInfo[0] = path;
                    ImageInfo[1] = date;
                    ImageInfo[2] = loc;
                    ImageInfo[3] = description;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
