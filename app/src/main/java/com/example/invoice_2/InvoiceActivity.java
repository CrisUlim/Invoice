package com.example.invoice_2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InvoiceActivity extends BaseActivity {
    private EditText customerNameInput;
    private EditText customerEmailInput;
    private InvoiceItemAdapter invoiceItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Invoice");

        customerNameInput = findViewById(R.id.customerNameInput);
        customerEmailInput = findViewById(R.id.customerEmailInput);
        Button generateButton = findViewById(R.id.generateInvoiceButton);
        Button addItemButton = findViewById(R.id.scanItemButton);

        RecyclerView recyclerView = findViewById(R.id.invoiceItemsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        invoiceItemAdapter = new InvoiceItemAdapter();
        recyclerView.setAdapter(invoiceItemAdapter);

        generateButton.setOnClickListener(v -> generateInvoice());
        addItemButton.setOnClickListener(v -> showSelectProductDialog());
    }

    private void generateInvoice() {
        String customerName = customerNameInput.getText().toString().trim();
        String customerEmail = customerEmailInput.getText().toString().trim();
        List<InvoiceItemAdapter.InvoiceItem> items = invoiceItemAdapter.getItems();

        if (customerName.isEmpty() || customerEmail.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (items.isEmpty()) {
            Toast.makeText(this, "Please add at least one product", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File pdfFile = createPdfFile();
            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add header
            Paragraph header = new Paragraph("INVOICE");
            header.setFontSize(20);
            document.add(header);
            document.add(new Paragraph("\n"));

            // Add customer info
            Paragraph customerInfo = new Paragraph("Customer Information:");
            customerInfo.setFontSize(14);
            document.add(customerInfo);
            document.add(new Paragraph("Name: " + customerName));
            document.add(new Paragraph("Email: " + customerEmail));
            document.add(new Paragraph("Date: " + 
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date())));
            document.add(new Paragraph("\n"));

            // Create table for items
            float[] columnWidths = {200f, 75f, 75f, 75f, 75f};
            Table table = new Table(columnWidths);

            // Add table headers
            table.addCell("Product");
            table.addCell("Price");
            table.addCell("Quantity");
            table.addCell("Unit");
            table.addCell("Total");

            // Add items to table
            double total = 0;
            for (InvoiceItemAdapter.InvoiceItem item : items) {
                table.addCell(item.product.getName());
                table.addCell(String.format("$%.2f", item.product.getPrice()));
                table.addCell(String.valueOf(item.quantity));
                table.addCell(item.product.getUnit() != null ? item.product.getUnit() : "pcs");
                
                double itemTotal = item.product.getPrice() * item.quantity;
                total += itemTotal;
                table.addCell(String.format("$%.2f", itemTotal));
            }

            // Add table to document
            document.add(table);
            document.add(new Paragraph("\n"));

            // Add total
            Paragraph totalAmount = new Paragraph(String.format("Total Amount: $%.2f", total));
            document.add(totalAmount);

            document.close();

            // Share the PDF
            sharePdf(pdfFile);

        } catch (IOException e) {
            Toast.makeText(this, "Error generating PDF: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }

    private File createPdfFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(new Date());
        String fileName = "Invoice_" + timeStamp + ".pdf";

        File storageDir = new File(getFilesDir(), "invoices");
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            throw new IOException("Could not create directory");
        }

        return new File(storageDir, fileName);
    }

    private void sharePdf(File pdfFile) {
        Uri pdfUri = FileProvider.getUriForFile(this, 
            getApplicationContext().getPackageName() + ".provider", pdfFile);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, pdfUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(intent, "Share Invoice"));
    }

    private void showSelectProductDialog() {
        new SelectProductDialog(product -> {
            invoiceItemAdapter.addItem(product);
        }).show(getSupportFragmentManager(), "SelectProduct");
    }
} 