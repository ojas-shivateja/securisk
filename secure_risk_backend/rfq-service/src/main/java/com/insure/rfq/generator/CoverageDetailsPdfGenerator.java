package com.insure.rfq.generator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.insure.rfq.dto.ClientListCoverageDetailsDto;
import com.insure.rfq.entity.PolicyTermsEntity;
import com.insure.rfq.repository.PolicyTermsRepository;
import com.insure.rfq.service.DownloadService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class CoverageDetailsPdfGenerator implements DownloadService {

	@Autowired
	private PolicyTermsRepository policyTermsRepository;

	@Value("classpath:configfile/logo.png")
	Resource resource;


	@Override
	public byte[] generateCoverageDetails(String rfqId) throws IOException, DocumentException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Document document = new Document();

		try {
			PdfWriter.getInstance(document, baos);
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		document.open();

        // Add logo image at the beginning of the document
        InputStream  imageStream = resource.getInputStream();
        byte[] imageBytes = imageStream.readAllBytes();
        Image logoImage = Image.getInstance(imageBytes);
        logoImage.scaleToFit(200, 100); // Adjust width and height as needed
        logoImage.setAlignment(Element.ALIGN_LEFT);
        document.add(logoImage);

		try {

			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
			Paragraph title = new Paragraph("Coverage Details", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);

			document.add(new Paragraph("\n\n"));

			PdfPTable table = new PdfPTable(2);

			float[] columnWidths = { 1.2f, 0.8f };
			try {
				table.setWidths(columnWidths);
			} catch (DocumentException e) {
				e.printStackTrace();
			}

			table.getDefaultCell().setBorderWidth(1);

			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
			PdfPCell coverageHeader = new PdfPCell(new Phrase("Coverage", headerFont));
			PdfPCell limitHeader = new PdfPCell(new Phrase("Limit", headerFont));

			float headerRowHeight = 30f;
			coverageHeader.setFixedHeight(headerRowHeight);
			limitHeader.setFixedHeight(headerRowHeight);

			coverageHeader.setBackgroundColor(new BaseColor(232, 232, 232));
			limitHeader.setBackgroundColor(new BaseColor(232, 232, 232));

			coverageHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
			coverageHeader.setHorizontalAlignment(Element.ALIGN_LEFT);
			limitHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
			limitHeader.setHorizontalAlignment(Element.ALIGN_LEFT);

			coverageHeader.setBorderWidth(1);
			limitHeader.setBorderWidth(1);

			table.addCell(coverageHeader);
			table.addCell(limitHeader);

			Optional<List<PolicyTermsEntity>> findByRfqId = policyTermsRepository.findByRfqId(rfqId);
			List<PolicyTermsEntity> list = findByRfqId.orElse(List.of());

			Font font = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

			boolean hasContent = false;
			for (PolicyTermsEntity policy : list) {
				if (!policy.getCoverageName().isEmpty() && !policy.getRemark().isEmpty()) {

					PdfPCell coverageCell = new PdfPCell(new Phrase(policy.getCoverageName(), font));
					PdfPCell limitCell = new PdfPCell(new Phrase(policy.getRemark(), font));

					coverageCell.setFixedHeight(30f);
					limitCell.setFixedHeight(30f);

					if (list.indexOf(policy) % 2 == 2) {
						coverageCell.setBackgroundColor(new BaseColor(232, 232, 232));
						limitCell.setBackgroundColor(new BaseColor(232, 232, 232));
					}

					coverageCell.setBorderWidth(1);
					limitCell.setBorderWidth(1);

					coverageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					coverageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					limitCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					limitCell.setHorizontalAlignment(Element.ALIGN_LEFT);

					table.addCell(coverageCell);
					table.addCell(limitCell);

					hasContent = true;
				}
			}
				table.setWidthPercentage(100);
				document.add(table);

		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			document.close();

			// Save the PDF to a file
			String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
			String directoryPath = "C:/Securisk/";
			String pdfFilePath = directoryPath + "coverageDetails_" + format + ".pdf";

			try {
				// Create the directory if it doesn't exist
				File directory = new File(directoryPath);
				if (!directory.exists()) {
					directory.mkdirs();
				}

				// Write the PDF content to the file
				try (FileOutputStream fos = new FileOutputStream(pdfFilePath)) {
					fos.write(baos.toByteArray());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return baos.toByteArray();
	}

	@Override
	public byte[] generateClientListCoverageDetails(Long productId, Long clientIdList,List<ClientListCoverageDetailsDto> clientListCoverageDetailsDto) throws IOException, DocumentException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Document document = new Document();

		try {
			PdfWriter.getInstance(document, baos);
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		document.open();

		// Add logo image at the beginning of the document
		InputStream  imageStream = resource.getInputStream();
		byte[] imageBytes = imageStream.readAllBytes();
		Image logoImage = Image.getInstance(imageBytes);
		logoImage.scaleToFit(200, 100); // Adjust width and height as needed
		logoImage.setAlignment(Element.ALIGN_LEFT);
		document.add(logoImage);

		try {

			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
			Paragraph title = new Paragraph("Coverage Details", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);

			document.add(new Paragraph("\n\n"));

			PdfPTable table = new PdfPTable(2);

			float[] columnWidths = { 1.2f, 0.8f };
			try {
				table.setWidths(columnWidths);
			} catch (DocumentException e) {
				e.printStackTrace();
			}

			table.getDefaultCell().setBorderWidth(1);

			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
			PdfPCell coverageHeader = new PdfPCell(new Phrase("Coverage", headerFont));
			PdfPCell limitHeader = new PdfPCell(new Phrase("Limit", headerFont));

			float headerRowHeight = 30f;
			coverageHeader.setFixedHeight(headerRowHeight);
			limitHeader.setFixedHeight(headerRowHeight);

			coverageHeader.setBackgroundColor(new BaseColor(232, 232, 232));
			limitHeader.setBackgroundColor(new BaseColor(232, 232, 232));

			coverageHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
			coverageHeader.setHorizontalAlignment(Element.ALIGN_LEFT);
			limitHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
			limitHeader.setHorizontalAlignment(Element.ALIGN_LEFT);

			coverageHeader.setBorderWidth(1);
			limitHeader.setBorderWidth(1);

			table.addCell(coverageHeader);
			table.addCell(limitHeader);



			Font font = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

			boolean hasContent = false;
			for (ClientListCoverageDetailsDto coverageDetailsDto : clientListCoverageDetailsDto) {
				if (!coverageDetailsDto.getCoverageName().isEmpty() && !coverageDetailsDto.getRemark().isEmpty()) {

					PdfPCell coverageCell = new PdfPCell(new Phrase(coverageDetailsDto.getCoverageName(), font));
					PdfPCell limitCell = new PdfPCell(new Phrase(coverageDetailsDto.getRemark(), font));

					coverageCell.setFixedHeight(30f);
					limitCell.setFixedHeight(30f);

					if (clientListCoverageDetailsDto.indexOf(coverageDetailsDto) % 2 == 2) {
						coverageCell.setBackgroundColor(new BaseColor(232, 232, 232));
						limitCell.setBackgroundColor(new BaseColor(232, 232, 232));
					}

					coverageCell.setBorderWidth(1);
					limitCell.setBorderWidth(1);

					coverageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					coverageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					limitCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					limitCell.setHorizontalAlignment(Element.ALIGN_LEFT);

					table.addCell(coverageCell);
					table.addCell(limitCell);

					hasContent = true;
				}
			}
			table.setWidthPercentage(100);
			document.add(table);

		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			document.close();

			// Save the PDF to a file
			String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
			String directoryPath = "C:/Securisk/";
			String pdfFilePath = directoryPath + "coverageDetails_" + format + ".pdf";

			try {
				// Create the directory if it doesn't exist
				File directory = new File(directoryPath);
				if (!directory.exists()) {
					directory.mkdirs();
				}

				// Write the PDF content to the file
				try (FileOutputStream fos = new FileOutputStream(pdfFilePath)) {
					fos.write(baos.toByteArray());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return baos.toByteArray();
	}
}
