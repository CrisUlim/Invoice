package com.example.invoice_2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.core.content.FileProvider;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InvoiceGenerator {
    public static void generatePDF(Context context, List<InvoiceItem> items) {
        try {
            String fileName = "Invoice_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date()) + ".pdf";
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);

            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add header
            Paragraph header = new Paragraph("INVOICE")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(24);
            document.add(header);

            // Add date
            document.add(new Paragraph("Date: " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date())));

            // Create table
            Table table = new Table(new float[]{3, 1, 1, 1});
            table.addCell("Product");
            table.addCell("Quantity");
            table.addCell("Price");
            table.addCell("Total");

            double total = 0;
            for (InvoiceItem item : items) {
                table.addCell(item.getProduct().getName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(String.format("$%.2f", item.getProduct().getPrice()));
                table.addCell(String.format("$%.2f", item.getTotal()));
                total += item.getTotal();
            }

            document.add(table);

            // Add total
            Paragraph totalParagraph = new Paragraph(String.format("Total: $%.2f", total))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(16)
                    .setBold();
            document.add(totalParagraph);

            document.close();

            // Share the PDF
            sharePDF(context, file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sharePDF(Context context, File file) {
        Uri uri = FileProvider.getUriForFile(context, 
                context.getApplicationContext().getPackageName() + ".provider", file);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, "Share Invoice"));
    }
} 