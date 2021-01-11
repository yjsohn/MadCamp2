package com.example.moneyproject;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

//수정->저장
public class EditContent extends AppCompatActivity {
    TextView date_tv;
    TextView loc_tv;
    TextView description_tv;

    EditText date_edit;
    EditText loc_edit;
    EditText desc_edit;

    ImageView imageView;
    ImageButton save_btn;

    Intent searchIntent;
    int AUTOCOMPLETE_REQUEST_CODE = 1;

    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
    // Set the fields to specify which types of place data to
    // return after the user has made a selection.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_edit);
        String ImageInfo[] = getIntent().getStringArrayExtra("DETAIL_INFO");
        SQLiteDatabase db;
        //가져온 value를  edit view 내용으로 저장
        ContentValues values = new ContentValues();
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        //TextView
        date_tv = findViewById(R.id.date_tv);
        loc_tv = findViewById(R.id.location_tv);
        description_tv = findViewById(R.id.description_tv);
        //EditText
        date_edit = findViewById(R.id.date_edit);
        loc_edit = findViewById(R.id.location_edit);
        desc_edit = findViewById(R.id.description_edit);

        imageView = findViewById(R.id.detailed_image2);
        GradientDrawable drawable= (GradientDrawable) getDrawable(R.drawable.background_rounding);
        imageView.setBackground(drawable);
        imageView.setClipToOutline(true);

        File tempFile = new File(ImageInfo[0]);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        imageView.setImageBitmap(originalBm);
        save_btn = findViewById(R.id.save_btn);
        save_btn.setOnClickListener(view -> {
            String date = date_edit.getText().toString();
            String loc = loc_edit.getText().toString();
            String description = desc_edit.getText().toString();
            String updatedInfo[] = {date, loc, description};

            values.put("date", date);
            values.put("location", loc);
            values.put("description", description);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("result", updatedInfo);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
        if(ImageInfo[1].equals(""))
            date_edit.setHint("날짜를 입력해주세요");
        if(ImageInfo[2].equals(""))
            loc_edit.setHint("위치를 입력해주세요");
        if(ImageInfo[3].equals(""))
            desc_edit.setHint("내용을 입력해주세요");

        date_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    SelectDate(v);
                }
            }
        });

        loc_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    SelectLocation(v);
                }
            }
        });

        date_edit.setText(ImageInfo[1]);
        loc_edit.setText(ImageInfo[2]);
        desc_edit.setText(ImageInfo[3]);
    }

    public void SelectDate(View v) {
        String date = date_edit.getText().toString();
        DatePickerDialog dialog = new DatePickerDialog(this, R.style.DialogTheme);
        dialog.show();
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                date_edit.setText(date);
            }
        });
        dialog.getDatePicker().setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear++;
                String month;
                if(monthOfYear < 10){
                    month = "0" + monthOfYear;
                }
                else{
                    month = "" + monthOfYear;
                }
                String date = "" + year + "." + month + "." + dayOfMonth;
                date_edit.setText(date);
            }
        });
    }

    public void SelectLocation(View v){
        searchIntent = new Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields)
            .build(this);
        startActivityForResult(searchIntent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                String location = place.getName();
                loc_edit.setText(location);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("Error", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

}