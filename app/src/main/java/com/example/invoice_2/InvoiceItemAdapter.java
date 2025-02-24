package com.example.invoice_2;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.invoice_2.data.Product;
import java.util.ArrayList;
import java.util.List;

public class InvoiceItemAdapter extends RecyclerView.Adapter<InvoiceItemAdapter.InvoiceItemViewHolder> {
    private List<InvoiceItem> items = new ArrayList<>();

    public static class InvoiceItem {
        Product product;
        int quantity;

        public InvoiceItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }

    @NonNull
    @Override
    public InvoiceItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invoice_product, parent, false);
        return new InvoiceItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceItemViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Product product) {
        items.add(new InvoiceItem(product, 1));
        notifyItemInserted(items.size() - 1);
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    static class InvoiceItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView priceText;
        private final EditText quantityInput;
        private final TextView unitText;

        public InvoiceItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.productName);
            priceText = itemView.findViewById(R.id.productPrice);
            quantityInput = itemView.findViewById(R.id.quantityInput);
            unitText = itemView.findViewById(R.id.unitText);
        }

        public void bind(InvoiceItem item) {
            Product product = item.product;
            nameText.setText(product.getName());
            priceText.setText(String.format("$%.2f", product.getPrice()));
            quantityInput.setText(String.valueOf(item.quantity));
            unitText.setText(product.getUnit() != null ? product.getUnit() : "pcs");

            quantityInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        item.quantity = Integer.parseInt(s.toString());
                    } catch (NumberFormatException e) {
                        item.quantity = 0;
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }
} 