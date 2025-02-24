package com.example.invoice_2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.invoice_2.data.AppDatabase;
import com.example.invoice_2.data.Product;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.activity.result.ActivityResultLauncher;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import androidx.annotation.NonNull;
import android.view.MenuItem;

public class ProductManagementActivity extends BaseActivity {
    private static final String TAG = "ProductManagement";
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private AppDatabase db;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String barcode = result.getContents();
                    runOnUiThread(() -> showAddProductDialog(barcode));
                }
            });

    public interface ProductUpdateListener {
        void onProductUpdated();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "Starting onCreate");
            
            setContentView(R.layout.activity_product_management);

            // Set up toolbar
            androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Manage Products");
            }

            // Initialize database
            db = AppDatabase.getInstance(this);
            if (db == null) {
                throw new IllegalStateException("Database instance is null");
            }

            // Set up RecyclerView
            recyclerView = findViewById(R.id.productsRecyclerView);
            if (recyclerView == null) {
                throw new IllegalStateException("RecyclerView not found");
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ProductAdapter();
            adapter.setProducts(new ArrayList<>());
            recyclerView.setAdapter(adapter);

            // Set up FABs
            ExtendedFloatingActionButton addProductFab = findViewById(R.id.addProductFab);
            ExtendedFloatingActionButton scanProductFab = findViewById(R.id.scanProductFab);

            if (addProductFab == null || scanProductFab == null) {
                throw new IllegalStateException("FABs not found");
            }

            addProductFab.setOnClickListener(v -> showManualProductDialog());
            scanProductFab.setOnClickListener(v -> startBarcodeScanner());

            Log.d(TAG, "Loading initial products");
            loadProducts();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void startBarcodeScanner() {
        ScanOptions options = new ScanOptions()
                .setPrompt("Scan product barcode")
                .setBeepEnabled(true)
                .setOrientationLocked(true);

        barcodeLauncher.launch(options);
    }

    private void showManualProductDialog() {
        ManualProductDialog dialog = new ManualProductDialog();
        dialog.setProductAddedListener(product -> {
            if (product != null) {
                addProduct(product);
            }
        });
        dialog.show(getSupportFragmentManager(), "ManualProduct");
    }

    private void showAddProductDialog(String barcode) {
        AddProductDialog dialog = new AddProductDialog(barcode);
        dialog.setProductAddedListener(product -> {
            if (product != null) {
                addProduct(product);
            }
        });
        dialog.show(getSupportFragmentManager(), "AddProduct");
    }

    private void addProduct(Product product) {
        executorService.execute(() -> {
            try {
                db.productDao().insert(product);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
                    loadProducts();
                });
            } catch (Exception e) {
                runOnUiThread(() -> 
                    Toast.makeText(this, "Error adding product: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show());
            }
        });
    }

    public void refreshProducts() {
        loadProducts();
    }

    private void loadProducts() {
        Log.d(TAG, "Starting loadProducts");
        if (adapter == null) {
            Log.e(TAG, "Adapter is null");
            return;
        }

        executorService.execute(() -> {
            try {
                List<Product> products = db.productDao().getAllProducts();
                Log.d(TAG, "Loaded " + products.size() + " products");
                
                runOnUiThread(() -> {
                    try {
                        adapter.setProducts(products);
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "Updated adapter with products");
                    } catch (Exception e) {
                        Log.e(TAG, "Error updating adapter", e);
                        Toast.makeText(this, "Error displaying products", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error loading products", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading products: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                    adapter.setProducts(new ArrayList<>());
                    adapter.notifyDataSetChanged();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            if (!executorService.isShutdown()) {
                executorService.shutdown();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy", e);
        }
    }
} 