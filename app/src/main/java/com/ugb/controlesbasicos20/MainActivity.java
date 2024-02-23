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
    EditText var1;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tbh = findViewById(R.id.tbhP1);
        tbh.setup();

        tbh.addTab(tbh.newTabSpec("AGU").setContent(R.id.tab1).setIndicator("AGUA", null));
        tbh.addTab(tbh.newTabSpec("CON").setContent(R.id.tab2).setIndicator("CONVERSOR", null));

        double resp;
        var1 = findViewById(R.id.cant1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calcular();
            }
        });
    }
    private void Calcular(){
        double metros = Double.parseDouble(var1.getText().toString());
        double tarifa;

        if (metros <= 18){
            tarifa = 6.0;
        }
        else if (metros <= 28){
            tarifa = 6.0 + 0.45 * (metros - 18);
        }
        else {
            tarifa = 6.0 + 0.45 * 10 + 0.65 * (metros - 28);
        }

        result.setText(String.format("Resultado: ", tarifa));
    }
}