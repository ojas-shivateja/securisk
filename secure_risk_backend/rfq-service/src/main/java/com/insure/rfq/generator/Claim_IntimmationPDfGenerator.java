package com.insure.rfq.generator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.insure.rfq.entity.ClientListEmployee_ClaimIntimmation;
import com.insure.rfq.exception.InvalidUser;
import com.insure.rfq.repository.ClientListEmployee_Claim_Intimmationrepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Claim_IntimmationPDfGenerator {
	@Autowired
	private ClientListEmployee_Claim_Intimmationrepository claimIntimationrepository;
	@Autowired
	private SubmitClaimsEmailService emailService;

	@Value("classpath:configfile/logo.png")
	Resource resource;

	@Value("${file.path.coverageMain}")
	private String mainpath;

	public byte[] generatePdf(Long id) {
		byte[] pdfData = null;
		ClientListEmployee_ClaimIntimmation claimIntimation = claimIntimationrepository.findById(id)
				.orElseThrow(() -> new InvalidUser("Invalid User"));
		boolean flag = false;

		try {
			Document document = new Document();

			File folder = new File(mainpath);
			File rfqReportFile = new File(folder.getAbsolutePath(),
					"Claim_Intimation" + RandomStringUtils.random(10, true, false));

			FileOutputStream outputStream = new FileOutputStream(rfqReportFile);

			PdfWriter writer = PdfWriter.getInstance(document, outputStream);

			document.open();

			addHeaderSection(document, writer);

			addBodySection(document, claimIntimation);

			document.close();
			outputStream.close();
			flag = true;
			pdfData = convertFileToByteArray(rfqReportFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return pdfData;
	}

	private byte[] convertFileToByteArray(File file) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		try (FileInputStream fis = new FileInputStream(file)) {
			int bytesRead;
			while ((bytesRead = fis.read(buffer)) != -1) {
				byteArrayOutputStream.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			log.error("Error occurred while converting file to byte array: ", e);
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
			log.error("Error occurred while processing image: ", e);
		}
		PdfPCell addressCell = new PdfPCell();
		addressCell.setBorder(Rectangle.NO_BORDER);
		addressCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		headerTable.addCell(addressCell);

		document.add(headerTable);
		document.add(new Paragraph("\n"));

		LineSeparator line = new LineSeparator();
		document.add(line);
		document.add(new Paragraph("\n"));
	}

	private void addBodySection(Document document, ClientListEmployee_ClaimIntimmation claimIntimation)
			throws DocumentException {
		PdfPTable bodyTable = new PdfPTable(2);
		bodyTable.setWidthPercentage(100);

		Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
		PdfPCell claimIntimationcell = new PdfPCell(new Paragraph("Claim Intimation", sectionFont));

		claimIntimationcell.setColspan(2);
		claimIntimationcell.setBorder(Rectangle.NO_BORDER);
		bodyTable.addCell(claimIntimationcell);

		addTableRow(bodyTable, "", "");
		addTableRow(bodyTable, " ", " ");

		addTableRow(bodyTable, "Insured Name:", claimIntimation.getPatient_Name());
		addTableRow(bodyTable, "Relation to Employee:", claimIntimation.getRelationToEmployee());
		addTableRow(bodyTable, "Employee Name:", claimIntimation.getEmployeeName());
		addTableRow(bodyTable, "Email:", claimIntimation.getEmailId());
		addTableRow(bodyTable, "Hospital:", claimIntimation.getHospital());
		addTableRow(bodyTable, "Doctor Name:", claimIntimation.getDoctorName());
		addTableRow(bodyTable, "Employee ID:", claimIntimation.getEmployeeId());
		addTableRow(bodyTable, "Mobile Number:", claimIntimation.getMobileNumber());
		addTableRow(bodyTable, "Reason for Admission:", claimIntimation.getReasonForAdmission());
		addTableRow(bodyTable, "Date of Hospitalisation:", claimIntimation.getDateOfHospitalisation());
		addTableRow(bodyTable, "Other Details:", claimIntimation.getOther_Details());
		addTableRow(bodyTable, "Sum Insured:", claimIntimation.getSumInsured());
		addTableRow(bodyTable, "Reason for Claim:", claimIntimation.getReasonforClaim());

		document.add(bodyTable);
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

	public void generateAndSendPDF(Long id, List<String> emailRecipients) {
		byte[] pdfData = generatePdf(id);
		try {
			emailService.sendEmailWithAttachment("ClaimsIntimationDetails", "Please find attached the claim details.",
					emailRecipients, pdfData, "ClaimsIntimationDetails.pdf");
		} catch (MessagingException e) {
			e.printStackTrace();
			// Handle the exception appropriately
		}
	}

}
