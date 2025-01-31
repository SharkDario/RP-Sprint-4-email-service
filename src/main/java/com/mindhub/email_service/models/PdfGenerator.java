package com.mindhub.email_service.models;
import com.mindhub.email_service.dtos.NewProductDTO;
import com.mindhub.email_service.dtos.OrderCreatedEvent;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class PdfGenerator {

    private static final float MARGIN = 50;
    private static final float TABLE_TOP = 700;
    private static final float ROW_HEIGHT = 20;
    private static final float[] COLUMN_WIDTHS = {120, 120, 80, 80, 80};

    public static byte[] generatePdf(OrderCreatedEvent event) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            addContentToDocument(document, event);

            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private static void addContentToDocument(PDDocument document, OrderCreatedEvent event) throws IOException {
        PDPage page = new PDPage();
        document.addPage(page);

        float yPosition = TABLE_TOP;
        String[] headers = {"Product", "Description", "Quantity", "Price", "Subtotal"};
        DecimalFormat df = new DecimalFormat("#,##0.00");
        double total = 0.0;
        List<NewProductDTO> products = event.products();
        int productIndex = 0;

        while (productIndex < products.size()) {
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                if (yPosition == TABLE_TOP) {
                    // Add title
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(MARGIN + 150, 750);
                    contentStream.showText("Order Details");
                    contentStream.endText();

                    // Add order information
                    addText(contentStream, "Order ID: " + event.id(), MARGIN, TABLE_TOP + 40);
                    addText(contentStream, "Status: " + event.status(), MARGIN, TABLE_TOP + 20);

                    // Add table headers
                    addTableHeaders(contentStream, headers, yPosition);
                    yPosition -= ROW_HEIGHT;
                }

                // Add table rows
                while (productIndex < products.size() && yPosition > MARGIN) {
                    NewProductDTO product = products.get(productIndex);
                    Double subtotal = product.price() * product.stock();
                    String[] rowData = {
                            product.name(),
                            product.description(),
                            String.valueOf(product.stock()),
                            "$" + df.format(product.price()),
                            "$" + df.format(subtotal)
                    };
                    addTableRow(contentStream, rowData, yPosition);
                    yPosition -= ROW_HEIGHT;

                    total += subtotal;
                    productIndex++;
                }

                // Add total on the last page
                if (productIndex == products.size()) {
                    yPosition -= ROW_HEIGHT;
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(MARGIN + COLUMN_WIDTHS[0] + COLUMN_WIDTHS[1] + COLUMN_WIDTHS[2] + COLUMN_WIDTHS[3], yPosition);
                    contentStream.showText("Total: $" + df.format(total));
                    contentStream.endText();
                }
            }

            // Create a new page if there are more products
            if (productIndex < products.size()) {
                page = new PDPage();
                document.addPage(page);
                yPosition = TABLE_TOP;
            }
        }
    }

    private static void addText(PDPageContentStream contentStream, String text, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    private static void addTableHeaders(PDPageContentStream contentStream, String[] headers, float y) throws IOException {
        float x = MARGIN;
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        for (int i = 0; i < headers.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(x, y);
            contentStream.showText(headers[i]);
            contentStream.endText();
            x += COLUMN_WIDTHS[i];
        }
    }

    private static void addTableRow(PDPageContentStream contentStream, String[] rowData, float y) throws IOException {
        float x = MARGIN;
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        for (int i = 0; i < rowData.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(x, y);
            contentStream.showText(rowData[i]);
            contentStream.endText();
            x += COLUMN_WIDTHS[i];
        }
    }
}




