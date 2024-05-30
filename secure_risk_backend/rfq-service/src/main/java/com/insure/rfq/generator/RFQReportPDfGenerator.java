package com.insure.rfq.generator;

import com.insure.rfq.entity.CorporateDetailsEntity;
import com.insure.rfq.exception.InvalidUser;
import com.insure.rfq.repository.CorporateDetailsRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class RFQReportPDfGenerator {

    @Value("classpath:configfile/logo.png")
    Resource resource;

    @Value("${file.path.coverageMain}")
    private String mainpath;

    @Autowired
    private CorporateDetailsRepository repo;

    public byte[] generatePdf(String id) {
        byte[] pdfData = null;
        CorporateDetailsEntity corporateDetails = repo.findByRfqId(id)
                .orElseThrow(() -> new InvalidUser("Invalid User"));
        boolean flag = false;

        try {
            Document document = new Document();

            File folder = new File(mainpath);
            File rfqReportFile = new File(folder.getAbsolutePath(),
                    "RFQReport" + RandomStringUtils.random(10, true, false));

            FileOutputStream outputStream = new FileOutputStream(rfqReportFile);

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            document.open();

            addHeaderSection(document, writer);

            addBodySection(document, corporateDetails);

            document.close();
            outputStream.close();
            flag = true;
            pdfData = convertFileToByteArray(rfqReportFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pdfData;
    }

    private byte[] convertFileToByteArray(File file) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try {
            FileInputStream fis = new FileInputStream(file);
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void addHeaderSection(Document document, PdfWriter pdfWriter) throws DocumentException, IOException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingBefore(10);

        try (InputStream imageStream = resource.getInputStream()) {
            Image image = Image.getInstance(IOUtils.toByteArray(imageStream)); // Use IOUtils from Apache Commons IO
            image.scaleAbsolute(225, 100);
            PdfPCell logoCell = new PdfPCell(image);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(logoCell);
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }

        Font addressFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        PdfPCell addressCell = new PdfPCell();

        // Create a paragraph for the address with padding/margin
        Paragraph addressParagraph = new Paragraph();
        addressParagraph.setSpacingBefore(10); // Margin before the text
        addressParagraph.add(new Phrase("Secure Risk Insurance Brokers Pvt Ltd", addressFont));
        addressParagraph.add(Chunk.NEWLINE); // Add a line break
        addressParagraph.add(new Phrase("Shop 3B, Commercial Street, Lanco Hills Rd", addressFont));
        addressParagraph.add(Chunk.NEWLINE);
        addressParagraph.add(new Phrase("Manikonda Jagir, Phone: 093469 70205", addressFont));
        addressParagraph.add(Chunk.NEWLINE);
        addressParagraph.add(new Phrase("Hyderabad - 500008, Telangana, India", addressFont));

        addressCell.addElement(addressParagraph);
        addressCell.setBorder(Rectangle.NO_BORDER);
        addressCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        headerTable.addCell(addressCell);

        document.add(headerTable);
        document.add(new Paragraph("\n"));

        LineSeparator line = new LineSeparator();
        document.add(line);
        document.add(new Paragraph("\n"));
    }

    private void addBodySection(Document document, CorporateDetailsEntity corporateDetails) throws DocumentException {
        PdfPTable bodyTable = new PdfPTable(2);
        bodyTable.setWidthPercentage(100);

        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        PdfPCell corporateDetailsCell = new PdfPCell(new Paragraph("Corporate Details", sectionFont));

        corporateDetailsCell.setColspan(2);
        corporateDetailsCell.setBorder(Rectangle.NO_BORDER);
        bodyTable.addCell(corporateDetailsCell);

        addTableRow(bodyTable, "", "");
        addTableRow(bodyTable, " ", " ");

        addTableRow(bodyTable, "Insured Name:", corporateDetails.getInsuredName());
        addTableRow(bodyTable, "Address:", corporateDetails.getAddress());
        addTableRow(bodyTable, "NOB:", corporateDetails.getNob());
        addTableRow(bodyTable, "Contact Name:", corporateDetails.getContactName());
        addTableRow(bodyTable, "Email:", corporateDetails.getEmail());
        addTableRow(bodyTable, "Mobile Number:", corporateDetails.getPhNo());

        boolean tpaDetailsPresent = false;

        if (corporateDetails.getTpaName() != null && !corporateDetails.getTpaName().isEmpty()) {
            tpaDetailsPresent = true;
        }
        if (corporateDetails.getTpaContactName() != null && !corporateDetails.getTpaContactName().isEmpty()) {
            tpaDetailsPresent = true;
        }
        if (corporateDetails.getTpaEmail() != null && !corporateDetails.getTpaEmail().isEmpty()) {
            tpaDetailsPresent = true;
        }
        if (corporateDetails.getTpaPhNo() != null && !corporateDetails.getTpaPhNo().isEmpty()) {
            tpaDetailsPresent = true;
        }

        if (tpaDetailsPresent) {
            addTableRow(bodyTable, "", "");
            addTableRow(bodyTable, " ", " ");

            PdfPCell tpaDetailsCell = new PdfPCell(new Paragraph("TPA Details", sectionFont));
            tpaDetailsCell.setColspan(2);
            tpaDetailsCell.setBorder(Rectangle.NO_BORDER);
            bodyTable.addCell(tpaDetailsCell);

            addTableRow(bodyTable, "", "");
            addTableRow(bodyTable, " ", " ");

            addTableRow(bodyTable, "TPA Name:", corporateDetails.getTpaName());
            addTableRow(bodyTable, "Contact:", corporateDetails.getTpaContactName());
            addTableRow(bodyTable, "Email:", corporateDetails.getTpaEmail());
            addTableRow(bodyTable, "Mobile Number:", corporateDetails.getTpaPhNo());
        }

        // Always include the Intermediary section
        addTableRow(bodyTable, "", "");
        addTableRow(bodyTable, " ", " ");

        PdfPCell intermediaryDetailsCell = new PdfPCell(new Paragraph("Intermediary Details", sectionFont));
        intermediaryDetailsCell.setColspan(2);
        intermediaryDetailsCell.setBorder(Rectangle.NO_BORDER);
        bodyTable.addCell(intermediaryDetailsCell);

        addTableRow(bodyTable, "", "");
        addTableRow(bodyTable, " ", " ");

        addTableRow(bodyTable, "Intermediary Name:", corporateDetails.getIntermediaryName());
        addTableRow(bodyTable, "Contact Name:", corporateDetails.getIntermediaryContactName());
        addTableRow(bodyTable, "Email:", corporateDetails.getIntermediaryEmail());
        addTableRow(bodyTable, "Mobile Number:", corporateDetails.getIntermediaryPhNo());

        document.add(bodyTable);
    }

    private PdfPCell createEmptyCell(int colspan) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(colspan);
        return cell;
    }

    private PdfPCell createSeparatorCell(int colspan) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOTTOM);
        cell.setColspan(colspan);
        return cell;
    }

    private void addTableRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label));
        PdfPCell valueCell = new PdfPCell(new Phrase(value));

        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

}
