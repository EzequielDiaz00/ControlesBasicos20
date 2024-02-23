package com.ugb.controlesbasicos20;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    TabHost tbh;
    Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tbh = findViewById(R.id.tbhP1);
        tbh.setup();

        tbh.addTab(tbh.newTabSpec("AGU").setContent(R.id.tab1).setIndicator("AGUA", null));
        tbh.addTab(tbh.newTabSpec("CON").setContent(R.id.tab2).setIndicator("CONVERSOR", null));

        btn1.setOnClickListener();
    }
}