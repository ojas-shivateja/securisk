package com.insure.rfq.generator;

import com.insure.rfq.entity.ClientListEmployee_Submit_ClaimHospitalizationDetails;
import com.insure.rfq.entity.ClientListEmployee_Submit_Claim_User_Details;
import com.insure.rfq.repository.ClientListEmployee_Submit_ClaimHospitalizationDetailsRepository;
import com.insure.rfq.repository.ClientListEmployee_Submit_Claim_User_Detailsrepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

@Component
public class SubmitClaimPDFGenerator {
    @Autowired
    private ClientListEmployee_Submit_Claim_User_Detailsrepository clientListEmployeeSubmitClaimUserDetailsrepository;

    @Autowired
    private ClientListEmployee_Submit_ClaimHospitalizationDetailsRepository clientListEmployeeSubmitClaimHospitalizationDetailsRepository;

    @Value("classpath:configfile/logo.png")
    Resource resource;

    @Autowired
    private SubmitClaimsEmailService emailService;

    public byte[] generatePDF(String id) {
        Optional<ClientListEmployee_Submit_Claim_User_Details> optionalUserDetails = clientListEmployeeSubmitClaimUserDetailsrepository.findByUser_detailsId(id);
        Optional<ClientListEmployee_Submit_ClaimHospitalizationDetails> optionalHospitalizationDetails = clientListEmployeeSubmitClaimHospitalizationDetailsRepository.findByUser_detailsId(id);

        if (optionalUserDetails.isEmpty() || optionalHospitalizationDetails.isEmpty()) {
            throw new IllegalArgumentException("Details not found for ID: " + id);
        }

        ClientListEmployee_Submit_Claim_User_Details userDetails = optionalUserDetails.get();
        ClientListEmployee_Submit_ClaimHospitalizationDetails hospitalizationDetails = optionalHospitalizationDetails.get();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

            InputStream imageStream = resource.getInputStream();
            byte[] imageBytes = imageStream.readAllBytes();
            Image logoImage = Image.getInstance(imageBytes);
            logoImage.scaleToFit(800, 120); // Adjust width and height as needed
            logoImage.setAlignment(Element.ALIGN_LEFT);
            document.add(logoImage);

            // Add User Details
            document.add(new Paragraph("Claim User Details", new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD)));
            document.add(new Paragraph(" ", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));
            document.add(new Paragraph(" ", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));
            PdfPTable userTable = new PdfPTable(2);
            userTable.setWidthPercentage(100);
            addTableHeader(userTable, "Label", "value");
            addTableRow(userTable, "Patient Name", userDetails.getPatientName(), font);
            addTableRow(userTable, "Employee Name", userDetails.getEmployeeName(), font);
            addTableRow(userTable, "UHID", userDetails.getUhid(), font);
            addTableRow(userTable, "Date of Admission", userDetails.getDateOfAdmission(), font);
            addTableRow(userTable, "Date of Discharge", userDetails.getDateOfDischarge(), font);
            addTableRow(userTable, "Employee ID", userDetails.getEmployeeId(), font);
            addTableRow(userTable, "Email", userDetails.getEmail(), font);
            addTableRow(userTable, "Mobile Number", userDetails.getMobileNumber(), font);
            addTableRow(userTable, "Sum Insured", userDetails.getSumInsured(), font);
            addTableRow(userTable, "Beneficiary Name", userDetails.getBenificiaryName(), font);
            addTableRow(userTable, "Relation to Employee", userDetails.getRelationToEmployee(), font);
            addTableRow(userTable, "Claim Number", userDetails.getClaimNumber(), font);
            document.add(userTable);

            // Add some space before the next section
            document.add(new Paragraph(" ", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));
            document.add(new Paragraph(" ", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));

            // Add Hospitalization Details
            document.add(new Paragraph("Claim Hospitalization Details", new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD)));
            document.add(new Paragraph(" ", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));
            document.add(new Paragraph(" ", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));
            PdfPTable hospitalizationTable = new PdfPTable(2);
            hospitalizationTable.setWidthPercentage(100);
            addTableHeader(hospitalizationTable, "Label", "Value");
            addTableRow(hospitalizationTable, "State", hospitalizationDetails.getState(), font);
            addTableRow(hospitalizationTable, "City", hospitalizationDetails.getCity(), font);
            addTableRow(hospitalizationTable, "Hospital Name", hospitalizationDetails.getHospitalName(), font);
            addTableRow(hospitalizationTable, "Hospital Address", hospitalizationDetails.getHospitalAddress(), font);
            addTableRow(hospitalizationTable, "Nature of Illness", hospitalizationDetails.getNatureofIllness(), font);
            addTableRow(hospitalizationTable, "Pre-Hospitalization Amount", hospitalizationDetails.getPreHospitalizationAmount(), font);
            addTableRow(hospitalizationTable, "Post-Hospitalization Amount", hospitalizationDetails.getPostHospitalizationAmount(), font);
            addTableRow(hospitalizationTable, "Total Amount Claimed", hospitalizationDetails.getTotalAmountClaimed(), font);
            addTableRow(hospitalizationTable, "Hospitalization Amount", hospitalizationDetails.getHospitalizationAmount(), font);
            document.add(hospitalizationTable);

            // Close the document
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(header, new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(headerCell);
        }
    }

    private void addTableRow(PdfPTable table, String field, String value, Font font) {
        PdfPCell fieldCell = new PdfPCell(new Phrase(field, font));
        fieldCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(fieldCell);
        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(valueCell);
    }

    public void generateAndSendPDF(String id, List<String> emailRecipients) {
        byte[] pdfData = generatePDF(id);
        try {
            emailService.sendEmailWithAttachment(
                    "Claim Details",
                    "Please find attached the claim details.",
                    emailRecipients,
                    pdfData,
                    "ClaimDetails.pdf"
            );
        } catch (MessagingException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }
}
