package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;



public class CoronaActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corona);
        String apiKey;

        final String email = getIntent().getStringExtra("email");

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = db.getReference("");

        // Initialize the SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//        autocompleteFragment.getView().se;

        EditText etPlace = (EditText) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input);
        etPlace.setTextColor(Color.WHITE);
        etPlace.setHintTextColor(Color.WHITE);

        final TableLayout tableLayout = (TableLayout) findViewById(R.id.table);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("yooo", "Place: " + place.getName() + ", " + place.getId());

                LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                TableRow tableRow = (TableRow) inflater.inflate(R.layout.list_element,null);

                RelativeLayout relativeLayout = (RelativeLayout) tableRow.getChildAt(0);
                TextView mPlace = (TextView) relativeLayout.getChildAt(0);
                final TextView mTime = (TextView) relativeLayout.getChildAt(1);


                Calendar c = Calendar.getInstance();
                final int mYear = c.get(Calendar.YEAR);
                final int mMonth = c.get(Calendar.MONTH);
                final int mDay = c.get(Calendar.DAY_OF_MONTH);

                final Place myPlace = place;

                final Calendar c2 = Calendar.getInstance();
                final int mHour = c2.get(Calendar.HOUR_OF_DAY);
                final int mMinute = c2.get(Calendar.MINUTE);


                DatePickerDialog datePickerDialog = new DatePickerDialog(autocompleteFragment.getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mTime.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                TimePickerDialog timePickerDialog = new TimePickerDialog(autocompleteFragment.getContext(),
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                                  int minute) {
                                                String myDate = mTime.getText().toString();
                                                String[] dateArr = myDate.split("-");
                                                int myYear = Integer.parseInt(dateArr[2]);
                                                int myMonth = Integer.parseInt(dateArr[1]);
                                                int myDay = Integer.parseInt(dateArr[0]);
                                                Date date = new GregorianCalendar(myYear, myMonth - 1, myDay, hourOfDay, minute).getTime();
                                                long millis = date.getTime();
                                                float fMillis = (float) millis;

                                                EditText etPlace = (EditText) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input);
                                                etPlace.setText("");

                                                DatabaseReference places = myRef.child("Users").child(email).child("places");
                                                places.push().setValue(new PlaceTime(fMillis, myPlace.getId()));

                                            }
                                        }, mHour, mMinute, false);
                                timePickerDialog.show();

                            }
                        }, mYear, mMonth, mDay);


                datePickerDialog.show();
                mPlace.setText(place.getName());

//                Toast.makeText(getApplicationContext(), mMonth + "", Toast.LENGTH_SHORT).show();

                // Launch Time Picker Dialog
//                TimePickerDialog timePickerDialog = new TimePickerDialog(autocompleteFragment.getContext(),
//                        new TimePickerDialog.OnTimeSetListener() {
//
//                            @Override
//                            public void onTimeSet(TimePicker view, int hourOfDay,
//                                                  int minute) {
//
//                                Date date = new GregorianCalendar(mYear, mMonth, mDay, hourOfDay, minute).getTime();
//                                long millis = date.getTime();
//                                float fMillis = currentTimeMillis(millis);
//
//                                EditText etPlace = (EditText) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input);
//                                etPlace.setText("");
//
//                                DatabaseReference places = myRef.child("Users").child(email).child("places");
//                                places.push().setValue(new PlaceTime(fMillis, myPlace.getId()));
//
//                            }
//                        }, mHour, mMinute, false);
//                timePickerDialog.show();

//                Date date = new GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute).getTime();
//                long millis = date.getTime();
//                float fMillis = currentTimeMillis(millis);
//
//                EditText etPlace = (EditText) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input);
//                etPlace.setText("");
//
//                DatabaseReference places = myRef.child("Users").child(email).child("places");
//                places.push().setValue(new PlaceTime(fMillis, place.getId()));

                tableLayout.addView(tableRow);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("yooo", "An error occurred: " + status);
            }
        });
    }

    public static float currentTimeMillis(long millis) {
        return (float)(millis);
    }

    public class PlaceTime {

        public float time;
        public String place_id;

        public PlaceTime(float time, String place_id) {
            this.time = time;
            this.place_id = place_id;
        }

    }
}
