package com.example.invoice_2;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.invoice_2.data.Product;

public class ManualProductDialog extends DialogFragment {
    private ProductAddedListener listener;

    public interface ProductAddedListener {
        void onProductAdded(Product product);
    }

    public void setProductAddedListener(ProductAddedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_product, null);

        EditText nameInput = view.findViewById(R.id.productNameInput);
        EditText priceInput = view.findViewById(R.id.productPriceInput);
        EditText quantityInput = view.findViewById(R.id.productQuantityInput);
        EditText unitInput = view.findViewById(R.id.productUnitInput);
        EditText barcodeInput = view.findViewById(R.id.productBarcodeInput);

        builder.setView(view)
                .setTitle("Add Product")
                .setPositiveButton("Add", null) // Set to null initially
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();

        // Override the click listener to prevent dialog from dismissing on error
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String name = nameInput.getText().toString().trim();
                String priceStr = priceInput.getText().toString().trim();
                String quantityStr = quantityInput.getText().toString().trim();
                String unit = unitInput.getText().toString().trim();
                String barcode = barcodeInput.getText().toString().trim();

                if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all required fields", 
                        Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double price = Double.parseDouble(priceStr);
                    int quantity = Integer.parseInt(quantityStr);

                    Product product = new Product();
                    product.setName(name);
                    product.setPrice(price);
                    product.setQuantity(quantity);
                    product.setUnit(unit.isEmpty() ? null : unit);
                    product.setBarcode(barcode.isEmpty() ? null : barcode);

                    if (listener != null) {
                        listener.onProductAdded(product);
                        dialog.dismiss();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid price or quantity", 
                        Toast.LENGTH_SHORT).show();
                }
            });
        });

        return dialog;
    }
} 