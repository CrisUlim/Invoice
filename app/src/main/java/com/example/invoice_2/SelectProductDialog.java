package com.example.invoice_2;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.invoice_2.data.AppDatabase;
import com.example.invoice_2.data.Product;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SelectProductDialog extends DialogFragment {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ProductSelectionAdapter adapter;
    private final OnProductSelectedListener listener;

    public interface OnProductSelectedListener {
        void onProductSelected(Product product);
    }

    public SelectProductDialog(OnProductSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_select_product, null);

        RecyclerView recyclerView = view.findViewById(R.id.productsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductSelectionAdapter(product -> {
            listener.onProductSelected(product);
            dismiss();
        });
        recyclerView.setAdapter(adapter);

        loadProducts();

        builder.setView(view)
                .setTitle("Select Product")
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        return builder.create();
    }

    private void loadProducts() {
        executorService.execute(() -> {
            var products = AppDatabase.getInstance(requireContext())
                    .productDao()
                    .getAllProducts();
            requireActivity().runOnUiThread(() -> adapter.setProducts(products));
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
} 