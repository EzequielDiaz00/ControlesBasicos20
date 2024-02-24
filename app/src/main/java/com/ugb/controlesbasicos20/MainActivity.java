import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import com.ugb.controlesbasicos20.R;

public class MainActivity extends AppCompatActivity {

    TabHost tbh;
    Button btn1;
    Button btn2;
    Spinner spn1;
    Spinner spn2;
    EditText var1;
    EditText var2;
    TextView result;
    conversores miObj; // Instantiate conversores

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tbh = findViewById(R.id.tbhP1);
        tbh.setup();

        tbh.addTab(tbh.newTabSpec("AGU").setContent(R.id.tab1).setIndicator("AGUA", null));
        tbh.addTab(tbh.newTabSpec("CON").setContent(R.id.tab2).setIndicator("CONVERSOR", null));

        var1 = findViewById(R.id.cant1);
        var2 = findViewById(R.id.cant2);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2); // Initialize btn2
        result = findViewById(R.id.result);

        miObj = new conversores(); // Initialize miObj

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calcular();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spn1 = findViewById(R.id.spn1);
                int de = spn1.getSelectedItemPosition();

                spn2 = findViewById(R.id.spn2); // Corrected variable name
                int a = spn2.getSelectedItemPosition(); // Corrected variable name

                var2 = findViewById(R.id.cant2);

                double cantidad = obtenerCantidadValidada(var2); // Simplified method call

                double resp = miObj.convertir(0, de, a, cantidad);
                Toast.makeText(getApplicationContext(), "Respuesta: " + resp, Toast.LENGTH_LONG).show();
                result.setText("El resultado es: " + resp);
            }
        });

    }

    private double obtenerCantidadValidada(EditText editText) {
        String cantidadString = editText.getText().toString().trim();
        if (cantidadString.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Por favor ingresa una cantidad", Toast.LENGTH_LONG).show();
            return 0;
        }
        return Double.parseDouble(cantidadString);
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

        result.setText("Resultado: " + tarifa); // Set the text to the calculated result
    }
}

class conversores {
    double[][] valores1 = {
            {1, 100, 39.3701, 3.28084, 1.193, 1.09361, 0.001, 0.000621371}
    };
    public double convertir (int opcion, int de, int a, double cantidad){
        return valores1[opcion][a]/valores1[opcion][de]*cantidad;
    }
}
