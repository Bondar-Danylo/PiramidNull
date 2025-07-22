package com.example.piramidnull;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class biography2 extends AppCompatActivity {

    ListView list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_biography2);

        list_view=(ListView) findViewById(R.id.listview1);

        String[] lan={"NAME: \nDr Alan Robweilzer","DOB: \n10-03-1964", "EXPERIENCE: \n• Archaeologist \n• Professor of Archeology of Oxford University (10 years)",
                "SPECIALISATION: \n• Ancient languages", "CURRENT POSITION: \n• Researcher for the Egyptian Antiquities Authority in Egypt"};

        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,R.layout.lisview1,R.id.txt_view,lan);

        list_view.setAdapter(adapter);
    }
}