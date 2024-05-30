package com.insure.rfq.generator;

import com.insure.rfq.entity.InsurerBankDetails;
import com.insure.rfq.repository.InsurerBankDetailsRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class InsureBankDetailsPDFGenerator {

    @Autowired
    private InsurerBankDetailsRepository insureBankDetailsRepository;

    public byte[] generatePdf(Long insureBankDetailsId) throws IOException, DocumentException {
        Optional<InsurerBankDetails> optionalDetails = insureBankDetailsRepository.findById(insureBankDetailsId);

        if (!optionalDetails.isPresent()) {
            throw new IllegalArgumentException("InsureBankDetails with ID " + insureBankDetailsId + " not found");
        }

        InsurerBankDetails details = optionalDetails.get();

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, byteArrayOutputStream);

        document.open();

        // Add date and address text
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        document.add(new Paragraph("Date: " + currentDate.format(formatter)));

        // Add salutation
        document.add(new Paragraph("Dear Sir/Madam,\n"));

        // Add confirmation text
        document.add(new Paragraph("We hereby confirm the below details of your virtual account number with us.\n"));

        // Add space before table
        document.add(new Paragraph("\n"));

        // Create and add the table
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        // Define table columns
        String[] columns = {"Particulars", "Details"};

        for (String column : columns) {
            PdfPCell cell = new PdfPCell(new Phrase(column));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        // Define table data
        String[][] data = {
                {"Bank Name", details.getBankName()},
                {"Branch", details.getBranch()},
                {"Account Number", String.valueOf(details.getAccountNumber())},
                {"Account Holder Name", details.getAccountHolderNumber()},
                {"IFSC Code", details.getIfscCode()},
                {"Location", details.getLocation()}
        };

        for (String[] row : data) {
            for (String cellData : row) {
                table.addCell(new Phrase(cellData));
            }
        }

        document.add(table);

        // Add space after table
        document.add(new Paragraph("\n"));

        // Add closing text
        document.add(new Paragraph("This certificate is issued at the request of the client for their records and use with a confirmation from banker about their account details.\n"));
        document.add(new Paragraph("Thanking you,\n"));

        document.close();

        return byteArrayOutputStream.toByteArray();
    }
}
