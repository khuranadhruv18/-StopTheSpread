package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        final EditText username = (EditText) findViewById(R.id.editText2);

        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = db.getReference("Users");

        Button corona = (Button) findViewById(R.id.button);
        corona.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String temp = username.getText().toString();
                temp = temp.replaceAll("@", "AT");
                temp = temp.replaceAll("[.]", "DOT");
                final String user1 = temp;

                if (username.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter your email", Toast.LENGTH_SHORT);
                } else {
                    myRef.child(user1).child("corona").setValue(true);
                    Intent myIntent = new Intent(MainActivity.this, CoronaActivity.class);
                    myIntent.putExtra("email", user1); //Optional parameters
                    MainActivity.this.startActivity(myIntent);
                }
            }
        });
        Button noCorona = (Button) findViewById(R.id.button2);
        noCorona.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String temp = username.getText().toString();
                temp = temp.replaceAll("@", "AT");
                temp = temp.replaceAll("[.]", "DOT");
                final String user = temp;

                if (username.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter your email", Toast.LENGTH_SHORT);
                } else {
                    myRef.child(user).child("corona").setValue(false);
                    Intent myIntent = new Intent(MainActivity.this, NoCoronaActivity.class);
                    myIntent.putExtra("email", user); //Optional parameters
                    MainActivity.this.startActivity(myIntent);
                }
            }
        });
    }
}
