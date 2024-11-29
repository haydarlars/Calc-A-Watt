package com.example.calcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.slider.Slider;

public class MainActivity extends AppCompatActivity {

    TextView tvBill;

    Button clrButton;

    Button calcButton;

    EditText etWatt;

    EditText etReb;

    Toolbar myToolbar;

    TextView tvTC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tvBill = findViewById(R.id.tvBill);
        clrButton= findViewById(R.id.clrButton);
        calcButton= findViewById(R.id.calcButton);
        etWatt= findViewById(R.id.etWatt);
        etReb= findViewById(R.id.etReb);
        tvTC = findViewById(R.id.tvTC);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Calc-A-Watt");
        }



        clrButton.setOnClickListener(v -> {
            // Clear EditText and reset TextView to 0%
            etWatt.setText("");
            etReb.setText("");
            tvBill.setText("RM 0.00");
            tvTC.setText("Total Charges(Before Rebates): RM 0");
        });

        calcButton.setOnClickListener(v -> {
            String wattInput = etWatt.getText().toString();
            String rebateInput = etReb.getText().toString();

            try {
                if (!wattInput.isEmpty()) {
                    double usage = Double.parseDouble(wattInput); // kWh input
                    double rebate = rebateInput.isEmpty() ? 0 : Double.parseDouble(rebateInput); // rebate input (if any)

                    if (usage < 0) {
                        Toast.makeText(this, "Usage cannot be negative. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (rebate < 0 || rebate > 5) {
                        Toast.makeText(this, "Rebate must be between 0 and 5%", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Calculate the total bill based on the blocks
                    double totalBill = calculateBill(usage);

                    // Apply rebate if applicable
                    totalBill -= (totalBill * (rebate / 100));

                    // Ensure the bill is not negative
                    if (totalBill < 0) totalBill = 0;



                    // Set the calculated bill to the TextView
                    tvBill.setText(String.format("RM %.2f ", totalBill));
                } else {
                    Toast.makeText(this, "Please Enter Your Usage Watt and Rebate", Toast.LENGTH_SHORT).show();
                }
            }catch (NumberFormatException e){
                Toast.makeText(this, "Invalid input. Please enter numeric values only.", Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                Toast.makeText(this, "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.item_about){
            Intent aboutIntent=new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
            
        } else if (item.getItemId()==R.id.item_rate) {
            Intent rateIntent=new Intent(this, RateActivity.class);
            startActivity(rateIntent);
            
        }
        return false;
    }



    private double calculateBill(double usage) {
        double total = 0;

        if (usage <= 200) {
            total = usage * 0.218; //1-200 kWh
        } else if (usage <= 300) {
            total = (200 * 0.218) + ((usage - 200) * 0.334); //201-300 kWh
        } else if (usage <= 600) {
            total = (200 * 0.218) + (100 * 0.334) + ((usage - 300) * 0.516); //301-600 kWh
        } else {
            total = (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((usage - 600) * 0.546); //above 600 kWh
        }

        tvTC.setText(String.format("Total Charges(Before Rebates): RM %.2f", total));
        return total;
    }
}