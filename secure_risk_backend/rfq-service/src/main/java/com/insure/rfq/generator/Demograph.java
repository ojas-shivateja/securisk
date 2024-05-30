package com.insure.rfq.generator;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class Demograph {

	public byte[] generatePdf(String rfqId) throws IOException, DocumentException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Document document = new Document();

		try {
			PdfWriter.getInstance(document, baos);
			document.open();

			// Add Title
			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
			Paragraph title = new Paragraph(" Memeber Wise Analysis ", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);

			// Create Table
			PdfPTable table = new PdfPTable(2); // Two columns: Data and Charts
			table.setWidthPercentage(100);
			table.setWidths(new float[] { 2, 1 }); // Adjust column widths as needed
			table.getDefaultCell().setBorderWidth(0);

			// Create a nested table for data
			PdfPTable dataTable = new PdfPTable(4); // Four columns: slno, name, age, amount
			dataTable.setWidthPercentage(100);
			dataTable.setWidths(new float[] { 1, 2, 1, (float) 1.5 }); // Adjust column widths as needed
			dataTable.getDefaultCell().setBorderWidth(0);

			// Add Table Header
			Font cellFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
			cellFont.setColor(BaseColor.DARK_GRAY);
			PdfPCell slnoCell = new PdfPCell(new Phrase("Sl No", cellFont));
			PdfPCell nameCell = new PdfPCell(new Phrase("MemeberType", cellFont));
			PdfPCell ageCell = new PdfPCell(new Phrase("Number", cellFont));
			PdfPCell amountCell = new PdfPCell(new Phrase("Amount", cellFont));

			// Set horizontal alignment to center for all cells
			slnoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			ageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);

			// Set padding, border color, and background color
			slnoCell.setPadding(5);
			slnoCell.setBorderColor(BaseColor.LIGHT_GRAY);
			slnoCell.setBackgroundColor(BaseColor.GRAY);

			nameCell.setPadding(5);
			nameCell.setBorderColor(BaseColor.LIGHT_GRAY);
			nameCell.setBackgroundColor(BaseColor.GRAY);

			ageCell.setPadding(5);
			ageCell.setBorderColor(BaseColor.LIGHT_GRAY);
			ageCell.setBackgroundColor(BaseColor.GRAY);

			amountCell.setPadding(5);
			amountCell.setBorderColor(BaseColor.LIGHT_GRAY);
			amountCell.setBackgroundColor(BaseColor.GRAY);

			// Add Employee Data
			addEmployeeDataToTable(dataTable, "1", "Main Memeber", "30", "270000");
			addEmployeeDataToTable(dataTable, "2", "Dependent", "28", "500788");

			// Add more employee data as needed

			// Add the data table to the main table
			PdfPCell dataCell = new PdfPCell(dataTable);
			dataCell.setColspan(2); // Span both columns
			table.addCell(dataCell);

//
//            // Add Table to Document
			document.add(table);

			// Close the Document
			document.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return baos.toByteArray();
	}

	// Helper method to add employee data to the table
	private void addEmployeeDataToTable(PdfPTable table, String slno, String name, String age, String amount) {
		PdfPCell slnoCell = new PdfPCell(new Phrase(slno));
		PdfPCell nameCell = new PdfPCell(new Phrase(name));
		PdfPCell ageCell = new PdfPCell(new Phrase(age));
		PdfPCell amountCell = new PdfPCell(new Phrase(amount));

		// Set horizontal alignment to center for all cells
		slnoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		ageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);

		// Add cells to the table
		table.addCell(slnoCell);
		table.addCell(nameCell);
		table.addCell(ageCell);
		table.addCell(amountCell);
	}
}
