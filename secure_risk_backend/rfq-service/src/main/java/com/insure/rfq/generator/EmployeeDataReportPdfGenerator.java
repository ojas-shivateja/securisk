package com.insure.rfq.generator;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import com.insure.rfq.entity.EmployeeDepedentDetailsEntity;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class EmployeeDataReportPdfGenerator {

	public byte[] generateEmployeeDataReport(List<EmployeeDepedentDetailsEntity> empData) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Document document = new Document();

		try {
			PdfWriter.getInstance(document, baos);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		String pdf_file_path = null;

		document.open();

		try {
			// Add the title on top of the table
			Font titleFont = FontFactory.getFont(FontFactory.TIMES, 12, BaseColor.BLACK);
			Paragraph title = new Paragraph("Employee Data Report", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			title.setSpacingAfter(100f);
			document.add(title);

			// Add even more space between title and table
			for (int i = 0; i < 5; i++) {
				document.add(Chunk.NEWLINE);
			}

			Font font1 = FontFactory.getFont(FontFactory.TIMES, 8, BaseColor.BLACK);
			PdfPTable table = new PdfPTable(7);
			float[] columnWidths = { 0.5f, 1f, 2f, 1.3f, 1f, 0.8f, 1.5f };
			try {
				table.setWidths(columnWidths);
			} catch (DocumentException e) {
				e.printStackTrace();
			}
			Stream.of("S.No", "Employee No", "Employee Name", "Relationship", "Gender", "Age", "Sum insured")
					.forEach(headerTitle -> {
						PdfPCell header = new PdfPCell();
						header.setBackgroundColor(BaseColor.LIGHT_GRAY);
						header.setVerticalAlignment(Element.ALIGN_MIDDLE);
						header.setHorizontalAlignment(Element.ALIGN_CENTER);
						header.setPhrase(new Phrase(headerTitle, font1));
						table.addCell(header);
					});
			String format = "EmployeeDataReport"
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
			pdf_file_path = "C:\\Securisk\\myFiles" + format + ".pdf";

			PdfWriter.getInstance(document, new FileOutputStream(pdf_file_path));
			document.open();

			for (EmployeeDepedentDetailsEntity emp : empData) {

				Font font = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

				PdfPCell id = new PdfPCell(new Phrase(String.valueOf(emp.getId()), font));
				id.setVerticalAlignment(Element.ALIGN_MIDDLE);
				id.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell empId = new PdfPCell(new Phrase(String.valueOf(emp.getEmployeeId()), font));
				empId.setVerticalAlignment(Element.ALIGN_MIDDLE);
				empId.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell memberName = new PdfPCell(new Phrase(emp.getEmployeeName(), font));
				memberName.setVerticalAlignment(Element.ALIGN_MIDDLE);
				memberName.setHorizontalAlignment(Element.ALIGN_LEFT);

				PdfPCell relation = new PdfPCell(new Phrase(emp.getRelationship(), font));
				relation.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell gender = new PdfPCell(new Phrase(emp.getGender(), font));
				gender.setVerticalAlignment(Element.ALIGN_MIDDLE);
				gender.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell age = new PdfPCell(new Phrase(String.valueOf(emp.getAge()), font));
				age.setVerticalAlignment(Element.ALIGN_MIDDLE);
				age.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell sumInsured = new PdfPCell(new Phrase(String.valueOf(emp.getSumInsured()), font));
				sumInsured.setVerticalAlignment(Element.ALIGN_MIDDLE);
				sumInsured.setHorizontalAlignment(Element.ALIGN_CENTER);

				table.addCell(id);
				table.addCell(empId);
				table.addCell(memberName);
				table.addCell(relation);
				table.addCell(gender);
				table.addCell(age);
				table.addCell(sumInsured);

			}
			table.setWidthPercentage(100);
			document.add(table);

			document.close();
		} catch (DocumentException | FileNotFoundException e) {
			e.printStackTrace();
		}
		return baos.toByteArray();
	}

	public byte[] generateEmployeeDeptData(List<EmployeeDepedentDetailsEntity> empData) {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Document document = new Document();
		try {
			PdfWriter.getInstance(document, byteArrayOutputStream);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.open();
		try {

			// Create a two-column table
			PdfPTable table = new PdfPTable(1);
			table.setWidthPercentage(100);

			// Column 3: Wrapped Text
			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);

			PdfPCell stamp = new PdfPCell(new Phrase("Employee Data", titleFont));
			stamp.setBorder(Rectangle.NO_BORDER); // Set no borders
			stamp.setPadding(10);
			stamp.setVerticalAlignment(Element.ALIGN_MIDDLE);
			stamp.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(stamp);

			// Add the table to the document
			document.add(table);

			PdfPTable dataTable = new PdfPTable(new float[] { 0.5f, 1f, 2f, 1.3f, 1f, 0.8f, 1.5f });

			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
			Stream.of("S.No", "Employee No", "Employee Name", "Relationship", "Gender", "Age", "Sum insured")
					.forEach(headerTitle -> {
						PdfPCell header = new PdfPCell();
						header.setBackgroundColor(BaseColor.LIGHT_GRAY);

						header.setPhrase(new Phrase(headerTitle, headerFont));
						header.setPadding(5);
						dataTable.addCell(header);
					});

			int count = 1;
			for (EmployeeDepedentDetailsEntity emp : empData) {

				Font font = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);

				PdfPCell id = new PdfPCell(new Phrase(String.valueOf(count++), font));
				id.setPadding(5);

				PdfPCell empId = new PdfPCell(new Phrase(String.valueOf(emp.getEmployeeId()), font));
				empId.setPadding(5);

				PdfPCell memberName = new PdfPCell(new Phrase(emp.getEmployeeName(), font));
				memberName.setPadding(5);

				PdfPCell relation = new PdfPCell(new Phrase(emp.getRelationship(), font));
				relation.setPadding(5);

				PdfPCell gender = new PdfPCell(new Phrase(emp.getGender(), font));
				gender.setPadding(5);

				PdfPCell age = new PdfPCell(new Phrase(String.valueOf(emp.getAge()), font));
				age.setPadding(5);

				PdfPCell sumInsured = new PdfPCell(new Phrase(String.valueOf(emp.getSumInsured()), font));
				sumInsured.setPadding(5);

				dataTable.addCell(id);
				dataTable.addCell(empId);
				dataTable.addCell(memberName);
				dataTable.addCell(relation);
				dataTable.addCell(gender);
				dataTable.addCell(age);
				dataTable.addCell(sumInsured);

			}
			dataTable.setWidthPercentage(100);
			document.add(dataTable);

		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.close();
		return byteArrayOutputStream.toByteArray();

	}
}
