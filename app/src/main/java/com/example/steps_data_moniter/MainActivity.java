package com.example.steps_data_moniter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.root.steps_data_moniter.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    TextView sensor_name, sensor_data, street_light_status;
    private Spinner spinner;
    public String battery_value, piezo_value, street_light_value;
    DatabaseReference dref;
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensor_name = findViewById(R.id.sensor_name);
        sensor_data = findViewById(R.id.sensor_data);
        spinner = findViewById(R.id.spinner);
        items = getResources().getStringArray(R.array.sensors);


        street_light_status = findViewById(R.id.sensor_light_data);

        if (!isNetworkAvailable())
            Showtoast();
        dref= FirebaseDatabase.getInstance().getReference();

        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                battery_value = Objects.requireNonNull(dataSnapshot.child("Battery(V)").getValue()).toString();
                battery_value = battery_value.concat("V");


                piezo_value = Objects.requireNonNull(dataSnapshot.child("Piezo(V)=").getValue()).toString();
                piezo_value = piezo_value.concat("V");

                street_light_value = Objects.requireNonNull(dataSnapshot.child("M=").getValue()).toString();
                if(street_light_value.equals("1")){
                    street_light_status.setText("ON");
                }
                else{
                    street_light_status.setText("OFF");
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, items);
        arrayAdapter.setDropDownViewResource(R.layout.dropdown_item);

        arrayAdapter.notifyDataSetChanged();
        spinner.setAdapter(arrayAdapter);

        int spinnerValue = getSharedPreferences("sensor", 0).getInt("user_choice", 0);
        spinner.setSelection(0);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> paramAdapterView, View view, int paramInt, long l) {


                if (!isNetworkAvailable())
                    Showtoast();


//                if (paramAdapterView.getId() == paramInt) {

                String str = paramAdapterView.getItemAtPosition(paramInt).toString();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(" selected");
                Toast.makeText(MainActivity.this, stringBuilder.toString(), Toast.LENGTH_SHORT).show();

                Log.d("onitem", " " + str + " " + paramInt + " " + paramAdapterView.getId());

                if (str.equals("none")) {
                    sensor_name.setVisibility(View.GONE);
                    sensor_data.setVisibility(View.GONE);
                } else if (str.equals(getResources().getStringArray(R.array.sensors)[1])) {
                    sensor_name.setVisibility(View.VISIBLE);
                    sensor_data.setVisibility(View.VISIBLE);
                    sensor_name.setText(str);
                    sensor_data.setText(battery_value);
                } else if (str.equals(getResources().getStringArray(R.array.sensors)[2])) {
                    sensor_name.setVisibility(View.VISIBLE);
                    sensor_data.setVisibility(View.VISIBLE);
                    sensor_name.setText(str);
                    sensor_data.setText(piezo_value);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });


    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void Showtoast() {
        Toast.makeText((Context)this, "No Connection Available.", Toast.LENGTH_SHORT).show();
    }


}