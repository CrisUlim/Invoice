package com.example.invoice_2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import androidx.annotation.NonNull;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button manageProductsButton = findViewById(R.id.manageProductsButton);
        Button createInvoiceButton = findViewById(R.id.createInvoiceButton);

        manageProductsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductManagementActivity.class);
            startActivity(intent);
        });

        createInvoiceButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, InvoiceActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}