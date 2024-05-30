package com.insure.rfq.generator;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import com.insure.rfq.repository.ClaimsMisRepository;
import org.apache.pdfbox.io.IOUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.insure.rfq.dto.ClaimAnayalisReportDto;
import com.insure.rfq.payload.ClaimTypeAnalysis;
import com.insure.rfq.payload.DiseaseWiseAnalysis;
import com.insure.rfq.payload.GenderWiseClaimReport;
import com.insure.rfq.payload.IncurredCliamRatio;
import com.insure.rfq.payload.RelationWiseClaimReport;
import com.insure.rfq.service.ClaimAnalysisReportService;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

@Service
public class ClaimAnalysisReport {

	@Value("classpath:configfile/logo.png")
	Resource resource;

	@Autowired
	private ClaimAnalysisReportService claimAnalysisReportService;

	@Autowired
	private ClaimsMisRepository analysisReportRepository;
	private static final Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
	private static final DecimalFormat decimalFormat = new DecimalFormat("#.00");
	private static final java.awt.Font DEFAULT_HEADER_FONT = new java.awt.Font(java.awt.Font.SANS_SERIF, Font.NORMAL,
			7);
	private static final java.awt.Font DEFAULT_LABEL_FONT = new java.awt.Font("SansSerif", Font.NORMAL, 10);
	
	private static final int noBorder=PdfPCell.NO_BORDER;

	public byte[] generatePdf(String rfqId) throws IOException, DocumentException {
		ClaimAnayalisReportDto generateClaimAnalysisReportPdf = claimAnalysisReportService
				.generateClaimAnalysisReportPdf(rfqId);

		Document document = new Document(PageSize.A2);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, baos);

		document.open();

		// Create header table
		PdfPTable head = new PdfPTable(1);
		try (InputStream imageStream = resource.getInputStream()) {
			Image image = Image.getInstance(IOUtils.toByteArray(imageStream)); // Use IOUtils from Apache Commons IO
			PdfPCell logoCell = new PdfPCell(image);
			logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			logoCell.setBorder(noBorder);
			head.addCell(logoCell); // for logo
			document.add(head);
		} catch (Exception e) {
			e.printStackTrace(); // Handle exceptions appropriately
		}

		PdfPTable headerTable = new PdfPTable(1);
		PdfPCell headerCell = new PdfPCell(
				new Phrase("Claim Analysis Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
		int alignCenter = PdfPCell.ALIGN_CENTER;
		
		headerCell.setHorizontalAlignment(alignCenter);
		headerCell.setBorder(noBorder);
		headerTable.addCell(headerCell);
		document.add(headerTable);
		// line separator

		document.add(getLineSeperator());


		document.add(new Paragraph("\n"));

		// ************************

		// Table 1
		// *****************

		// ****************

		PdfPTable memberWiseAnalysisHeaderTable = new PdfPTable(1);
		Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14); // Adjust font size and boldness
		PdfPCell headerCell1 = new PdfPCell(new Phrase("Member Wise Analysis", headerFont));
		headerCell1.setHorizontalAlignment(alignCenter);
		headerCell1.setBorder(noBorder);
		memberWiseAnalysisHeaderTable.addCell(headerCell1);
		document.add(memberWiseAnalysisHeaderTable);
		document.add(new Paragraph("\n"));

// table 1

		PdfPTable parentTable = new PdfPTable(4);
		parentTable.setWidthPercentage(100);
		float[] width = { 40f, 25f, 10f, 25f };
		parentTable.setWidths(width);
		parentTable.setLockedWidth(false);// Set the width to 100% to fill the page width
		// First cell: tableBar
		PdfPCell cell1 = new PdfPCell(getMemberTable(generateClaimAnalysisReportPdf));
		cell1.setBorder(noBorder); // Optional: remove cell borders
		parentTable.addCell(cell1);

		// Second cell: tableBarChart
		PdfPCell cell2 = new PdfPCell(createBarChartAsImage(generateClaimAnalysisReportPdf));
		cell2.setBorder(noBorder); // Optional: remove cell borders
		parentTable.addCell(cell2);

		PdfPCell cell3 = new PdfPCell(barChartConstents());
		cell3.setBorder(noBorder);
		parentTable.addCell(cell3);

		PdfPCell cell4 = new PdfPCell(createBarchartAsimageAmount(generateClaimAnalysisReportPdf));
		cell4.setBorder(noBorder); // Optional: remove cell borders
		parentTable.addCell(cell4);

		// Add the parent table to the document
		document.add(parentTable);

// table 1

		document.add(getLineSeperator());
		document.add(new Paragraph("\n"));

		// table 2
		// Create a parent table with two columns
		PdfPTable AgeWiseAnalysisHeaderTable = new PdfPTable(1);
		Font headerFont1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14); // Adjust font size and boldness
		PdfPCell headerCell11 = new PdfPCell(new Phrase("Gender Wise Analysis", headerFont1));
		headerCell11.setHorizontalAlignment(alignCenter);
		headerCell11.setBorder(noBorder);
		AgeWiseAnalysisHeaderTable.addCell(headerCell11);
		document.add(AgeWiseAnalysisHeaderTable);
		document.add(new Paragraph("\n")); // Add spacing between tables

		PdfPTable parentTableGenderWise = new PdfPTable(4);
		parentTableGenderWise.setWidthPercentage(100);
		parentTableGenderWise.setWidths(width);
////
		parentTableGenderWise.setLockedWidth(false);// Make the parent table take up the full width of the page

		// Add the first table (table1Pdf) as the first cell in the first column
		PdfPCell cellGenderWise = new PdfPCell(genderwiseTable(generateClaimAnalysisReportPdf));
		cellGenderWise.setBorder(noBorder);
		parentTableGenderWise.addCell(cellGenderWise);

		// Add the second table (table1Graph) as the second cell in the second column
		PdfPCell cell2GenderWise = new PdfPCell(createBarChartAsImageGenderWise(generateClaimAnalysisReportPdf));
		cell2GenderWise.setBorder(noBorder);
		parentTableGenderWise.addCell(cell2GenderWise);

		PdfPCell cell4BarConstents = new PdfPCell(barChartConstentssecond(generateClaimAnalysisReportPdf));
		cell4BarConstents.setBorder(noBorder);
		parentTableGenderWise.addCell(cell4BarConstents);

		PdfPCell cell3GenderWise = new PdfPCell(createBarChartAsImageAmountWise(generateClaimAnalysisReportPdf));
		cell3GenderWise.setBorder(noBorder);
		parentTableGenderWise.addCell(cell3GenderWise);

		// Add the parent table to the document
		document.add(parentTableGenderWise);
		// table 2

		document.add(getLineSeperator());

		// table 3
		document.add(new Paragraph("\n"));
		// Create a parent table with two columns

		PdfPTable relationWiseAnalysisHeaderTable = new PdfPTable(1);
		Font headerFont41 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14); // Adjust font size and boldness
		PdfPCell headerCell41 = new PdfPCell(new Phrase("Relation Wise Analysis", headerFont41));
		headerCell41.setHorizontalAlignment(alignCenter);
		headerCell41.setBorder(noBorder);
		relationWiseAnalysisHeaderTable.addCell(headerCell41);
		document.add(relationWiseAnalysisHeaderTable);
		document.add(new Paragraph("\n"));

		PdfPTable parentTableRelationWise = new PdfPTable(4);
		parentTableRelationWise.setWidthPercentage(100); // Make the parent table take up the full width of the page
		parentTableRelationWise.setWidths(width);

		// Add the first table (table3Pdf) as the first cell in the first column
		PdfPCell cell3Pdf = new PdfPCell(getRelationWiseTable(generateClaimAnalysisReportPdf));
		cell3Pdf.setBorder(noBorder);
		parentTableRelationWise.addCell(cell3Pdf);

		// Add the second table (table3Graph) as the second cell in the second column
		PdfPCell cell3Graph = new PdfPCell(createBarChartAsImageRelationWise(generateClaimAnalysisReportPdf));
		cell3Graph.setBorder(noBorder);
		parentTableRelationWise.addCell(cell3Graph);

		PdfPCell cell3BarConstent = new PdfPCell(barConstentsRelation(generateClaimAnalysisReportPdf));
		cell3BarConstent.setBorder(noBorder);
		parentTableRelationWise.addCell(cell3BarConstent);

		PdfPCell cell3Graph2 = new PdfPCell(createBarChartrAsImageAmountWise(generateClaimAnalysisReportPdf));
		cell3Graph2.setBorder(noBorder);
		parentTableRelationWise.addCell(cell3Graph2);

		// Add the parent table to the document
		document.add(parentTableRelationWise);

		// Add spacing between tables

		document.add(getLineSeperator());
		document.add(new Paragraph("\n"));

		// **************
		PdfPTable ageWiseAnalysisHeaderTable = new PdfPTable(1);
		Font headerFont2 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14); // Adjust font size and boldness
		PdfPCell headerCell2 = new PdfPCell(new Phrase("Age Wise Analysis", headerFont41));
		headerCell2.setHorizontalAlignment(alignCenter);
		headerCell2.setBorder(noBorder);
		ageWiseAnalysisHeaderTable.addCell(headerCell2);
		document.add(ageWiseAnalysisHeaderTable);
		document.add(new Paragraph("\n"));

// table 4
		PdfPTable Table4Pdf = new PdfPTable(4);
		Table4Pdf.setWidthPercentage(100);
		Table4Pdf.setWidths(width);
		PdfPCell Table4cell1 = new PdfPCell(getAgeWisePdfPTable(generateClaimAnalysisReportPdf));
		Table4cell1.setBorder(noBorder);
		Table4Pdf.addCell(Table4cell1);

		PdfPCell Table4cell2 = new PdfPCell(createBarChartAsImageAgeCountWise(generateClaimAnalysisReportPdf));
		Table4cell2.setBorder(noBorder);
		Table4Pdf.addCell(Table4cell2);

		PdfPCell Table4cell4 = new PdfPCell(barConstantsAge(generateClaimAnalysisReportPdf));
		Table4cell4.setBorder(noBorder);
		Table4Pdf.addCell(Table4cell4);

		PdfPCell Table4cell3 = new PdfPCell(createBarChartAsImageAgeAmountWise(generateClaimAnalysisReportPdf));
		Table4cell3.setBorder(noBorder);
		Table4Pdf.addCell(Table4cell3);

		parentTableGenderWise.setWidthPercentage(100);
		document.add(Table4Pdf);

		document.add(getLineSeperator());
		document.add(new Paragraph("\n"));

		PdfPTable claimWiseAnalysisHeaderTable = new PdfPTable(1);
		Font headerFont4 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14); // Adjust font size and boldness
		PdfPCell headerCell4 = new PdfPCell(new Phrase("Claim Wise Analysis", headerFont4));
		headerCell4.setHorizontalAlignment(alignCenter);
		headerCell4.setBorder(noBorder);
		claimWiseAnalysisHeaderTable.addCell(headerCell4);
		document.add(claimWiseAnalysisHeaderTable);

		PdfPTable parentTableClaimWiseWise = new PdfPTable(4);

		parentTableClaimWiseWise.setWidthPercentage(100);
		parentTableClaimWiseWise.setWidths(width);
		// Add the first table (table3Pdf) as the first cell in the first column
		PdfPCell cell3Cliam = new PdfPCell(getCliamAnalysis(generateClaimAnalysisReportPdf));
		cell3Cliam.setBorder(noBorder);
		parentTableClaimWiseWise.addCell(cell3Cliam);

		PdfPCell cell3CliamGraph = new PdfPCell(createBarChartAsImageCliamWise(generateClaimAnalysisReportPdf));
		cell3CliamGraph.setBorder(noBorder);
		parentTableClaimWiseWise.addCell(cell3CliamGraph);

		PdfPCell cell5CliamGraph = new PdfPCell(barChartConstentsClaim(rfqId));
		cell5CliamGraph.setBorder(noBorder);
		parentTableClaimWiseWise.addCell(cell5CliamGraph);

		PdfPCell call4CliamGraph = new PdfPCell(createBarChartAsImageAmountWisedata(generateClaimAnalysisReportPdf));
		call4CliamGraph.setBorder(noBorder);
		parentTableClaimWiseWise.addCell(call4CliamGraph);
		// Add table headers

		document.add(parentTableClaimWiseWise);
		// table 5
		// table 6

		document.add(getLineSeperator());
		document.add(new Paragraph("\n"));
		PdfPTable incurredClaimRatio = new PdfPTable(1);
		Font headerFont5 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14); // Adjust font size and boldness
		PdfPCell headerCell5 = new PdfPCell(new Phrase("Incurred Claim Ratio", headerFont4));
		headerCell5.setHorizontalAlignment(alignCenter);
		headerCell5.setBorder(noBorder);
		incurredClaimRatio.addCell(headerCell5);
		document.add(incurredClaimRatio);

		// Create a nested table with 2 columns
		PdfPTable nestedTable = new PdfPTable(4);
		nestedTable.setWidthPercentage(100);
		nestedTable.setWidths(width);

		// Add the first cell (table3Pdf) to the nested table
		PdfPCell cell3CliamRatio = new PdfPCell(getClaimInccuredRatio(generateClaimAnalysisReportPdf));
		cell3CliamRatio.setBorder(noBorder);
		nestedTable.addCell(cell3CliamRatio);

		PdfPCell cell4ClimRatio = new PdfPCell(createGraphStatusCount(generateClaimAnalysisReportPdf));
		cell4ClimRatio.setBorder(noBorder);
		nestedTable.addCell(cell4ClimRatio);

		PdfPCell cell6ClimRatio = new PdfPCell(baseConatents(rfqId));
		cell6ClimRatio.setBorder(noBorder);
		nestedTable.addCell(cell6ClimRatio);

		PdfPCell cell5ClimRatio = new PdfPCell(createGraphStatusAmount(generateClaimAnalysisReportPdf));
		cell5ClimRatio.setBorder(noBorder);
		nestedTable.addCell(cell5ClimRatio);

		// Add the second cell (table3CliamRatioGraph) to the nested table

		document.add(nestedTable);

		// table 6
		// table 7
		document.add(getLineSeperator());
		document.add(new Paragraph("\n"));
		PdfPTable diseaseWiseRatio = new PdfPTable(1);
		Font headerFont7 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14); // Adjust font size and boldness
		PdfPCell headerCell7 = new PdfPCell(new Phrase("Disease Wise Analysis", headerFont7));
		headerCell7.setHorizontalAlignment(
				alignCenter);
		headerCell7.setBorder(noBorder);
		diseaseWiseRatio.addCell(headerCell7);
		document.add(diseaseWiseRatio);
		document.add(new Paragraph("\n"));

		PdfPTable table7 = new PdfPTable(1);
		table7.setWidthPercentage(100);
		table7.addCell(getDiseasseWiseData(generateClaimAnalysisReportPdf));
		document.add(table7);

		// table 7

		document.close();

		return baos.toByteArray();
	}

	private Paragraph getLineSeperator() {
		LineSeparator lineSeparator = new LineSeparator();
		lineSeparator.setLineColor(BaseColor.LIGHT_GRAY);
		Paragraph separator = new Paragraph();
		separator.add(new Chunk(lineSeparator));
		return separator;
	}

	private Image addImage() throws BadElementException, MalformedURLException, IOException {
		Image logoImage = Image.getInstance("C:\\Users\\sm22176\\Downloads\\securisk.png"); // Replace with actual image
		// path
		logoImage.setAlignment(Element.ALIGN_LEFT);

		return logoImage;

	}
	// ***************graph*******************

	// table1 graph
	private Image createBarChartAsImage(ClaimAnayalisReportDto generateClaimAnalysisReportPdf)
			throws IOException, BadElementException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(generateClaimAnalysisReportPdf.getMemberTypeAnalysis().getMainMemberCount(), "Main Member",
				"");
		dataset.addValue(generateClaimAnalysisReportPdf.getMemberTypeAnalysis().getDependentCount(), "Dependents", "");

		// Add more data as needed

		JFreeChart chart = ChartFactory.createBarChart("", "", "Count", dataset, PlotOrientation.HORIZONTAL, false,
				false, true);
		chart.setBorderVisible(false);

		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		ValueAxis rangeAxis = plot.getRangeAxis();
		CategoryPlot plot1 = chart.getCategoryPlot();

		plot1.setBackgroundPaint(Color.WHITE); // Set background color of the plot area
		plot1.setRangeGridlinePaint(Color.WHITE); // Set color of gridlines

		// Customize bar colors
		BarRenderer renderer = (BarRenderer) plot1.getRenderer();
		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setDefaultItemLabelsVisible(true);
		renderer.setItemMargin(0.01);
		renderer.setMaximumBarWidth(0.07d);
		plot.setOutlineVisible(false);

		renderer.setSeriesPaint(0, new Color(16, 80, 153)); // Set color of the "Main Member" bars
		renderer.setSeriesPaint(1, new Color(16, 80, 153));

		domainAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		domainAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelsVisible(false);
		domainAxis.setTickLabelsVisible(false);

		domainAxis.setAxisLineVisible(false);
		rangeAxis.setAxisLineVisible(false);

		for (int i = 0; i <= dataset.getColumnCount(); i++) {

			renderer.setItemLabelAnchorOffset(10.0);

			renderer.setItemLabelAnchorOffset(5.0);

			renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(

					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER,

					TextAnchor.BASELINE_CENTER, 0.0

			));
		}

		BufferedImage bufferedImage = chart.createBufferedImage(300, 138);
		ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", imageStream);

		return Image.getInstance(imageStream.toByteArray());
	}

	// table 2 graph
	private Image createBarChartAsImageGenderWise(ClaimAnayalisReportDto generateClaimAnalysisReportPdf)
			throws IOException, BadElementException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		int sum = 0;

		for (GenderWiseClaimReport claimReport : generateClaimAnalysisReportPdf.getGenderWiseClaimReport()) {
			dataset.addValue(claimReport.getGenderCount(), claimReport.getGender(), "");

			sum = sum + claimReport.getGenderCount();

		}
		dataset.addValue(sum, "Total", "");

		// Add more data as needed

		JFreeChart chart = ChartFactory.createBarChart("", "", "Number", dataset, PlotOrientation.HORIZONTAL, false,
				false, true);

		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setTickMarksVisible(false);
		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setTickLabelsVisible(false);
		CategoryPlot plot1 = chart.getCategoryPlot();
		plot.setOutlineVisible(false);

		plot1.setBackgroundPaint(Color.WHITE); // Set background color of the plot area
		plot1.setRangeGridlinePaint(Color.WHITE); // Set color of gridlines

		// Customize bar colors
		BarRenderer renderer = (BarRenderer) plot1.getRenderer();
		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setDefaultItemLabelsVisible(true);
		renderer.setItemMargin(0.1);

		renderer.setSeriesPaint(0, new Color(16, 80, 153)); // Set color of the "Main Member" bars
		renderer.setSeriesPaint(1, new Color(16, 80, 153));
		renderer.setSeriesPaint(2, new Color(16, 80, 153));
		renderer.setMaximumBarWidth(0.06d);
		for (int i = 0; i <= dataset.getColumnCount(); i++) {

			renderer.setItemLabelAnchorOffset(10.0);

			renderer.setItemLabelAnchorOffset(5.0);

			renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(

					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER,

					TextAnchor.BASELINE_CENTER, 0.0

			));
		}

		domainAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		domainAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelsVisible(false);
		domainAxis.setAxisLineVisible(false);
		rangeAxis.setAxisLineVisible(false);

		BufferedImage bufferedImage = chart.createBufferedImage(285, 145);
		ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", imageStream);

		return Image.getInstance(imageStream.toByteArray());
	}

	// graph 3
	private Image createBarChartAsImageRelationWise(ClaimAnayalisReportDto generateClaimAnalysisReportPdf)
			throws IOException, BadElementException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		int size = 0;
		List<RelationWiseClaimReport> relationWiseClaimReport = generateClaimAnalysisReportPdf
				.getRelationWiseClaimReport();
		for (RelationWiseClaimReport rela : relationWiseClaimReport) {
			dataset.addValue(rela.getCount(), rela.getRelation(), "");
			size++;

		}

		// Add more data as needed

		JFreeChart chart = ChartFactory.createBarChart("", "", "Number", dataset, PlotOrientation.HORIZONTAL, false,
				false, true);
		chart.setBorderVisible(false);

		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setTickMarksVisible(false);

		ValueAxis rangeAxis = plot.getRangeAxis();
		CategoryPlot plot1 = chart.getCategoryPlot();
		plot1.setBackgroundPaint(Color.WHITE); // Set background color of the plot area
		plot1.setRangeGridlinePaint(Color.WHITE); // Set color of gridlines

		// Customize bar colors
		BarRenderer renderer = (BarRenderer) plot1.getRenderer();
		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setDefaultItemLabelsVisible(true);

		for (int i = 0; i <= size; i++) {
			renderer.setSeriesPaint(i, new Color(16, 80, 153));
		}
		renderer.setItemMargin(0.1);
		renderer.setMaximumBarWidth(0.051d);
		plot.setOutlineVisible(false);
		domainAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		domainAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelsVisible(false);
		domainAxis.setAxisLineVisible(false);
		rangeAxis.setAxisLineVisible(false);

		for (int i = 0; i <= dataset.getColumnCount(); i++) {

			renderer.setItemLabelAnchorOffset(10.0);

			renderer.setItemLabelAnchorOffset(5.0);

			renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(

					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER,

					TextAnchor.BASELINE_CENTER, 0.0

			));
		}
		if (size == 3) {
			BufferedImage bufferedImage = chart.createBufferedImage(285, 150);
			ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", imageStream);
			return Image.getInstance(imageStream.toByteArray());
		} else if (size == 5) {
			BufferedImage bufferedImage = chart.createBufferedImage(285, 210);
			ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", imageStream);
			return Image.getInstance(imageStream.toByteArray());
		} else if (size == 4) {
			BufferedImage bufferedImage = chart.createBufferedImage(285, 175);
			ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", imageStream);
			return Image.getInstance(imageStream.toByteArray());
		}
		BufferedImage bufferedImage = chart.createBufferedImage(285, 210);
		ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", imageStream);
		return Image.getInstance(imageStream.toByteArray());
	}

	// graph 4
	private Image createBarChartAsImageCliamWise(ClaimAnayalisReportDto generateClaimAnalysisReportPdf)
			throws IOException, BadElementException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (ClaimTypeAnalysis claim : generateClaimAnalysisReportPdf.getClaimTypeAnalysis()) {
			dataset.addValue(claim.getNumber(), claim.getStatus(), "");

		}

		// Add more data as needed

		JFreeChart chart = ChartFactory.createBarChart("", "", "Number", dataset, PlotOrientation.HORIZONTAL, false,
				false, true);
		chart.setBorderVisible(false);

		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		ValueAxis rangeAxis = plot.getRangeAxis();
		CategoryPlot plot1 = chart.getCategoryPlot();
		plot.setOutlineVisible(false);

		plot1.setBackgroundPaint(Color.WHITE); // Set background color of the plot area
		plot1.setRangeGridlinePaint(Color.WHITE); // Set color of gridlines
		// Customize bar colors
		BarRenderer renderer = (BarRenderer) plot1.getRenderer();
		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setDefaultItemLabelsVisible(true);
		renderer.setItemMargin(0.1);
		for (int i = 0; i < generateClaimAnalysisReportPdf.getClaimTypeAnalysis().size(); i++) {
			renderer.setSeriesPaint(i, new Color(16, 80, 153)); // Set color of the "Main Member" bars
		}

		renderer.setMaximumBarWidth(0.08d);

		domainAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		domainAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelsVisible(false);
		domainAxis.setAxisLineVisible(false);
		rangeAxis.setAxisLineVisible(false);

		for (int i = 0; i <= dataset.getColumnCount(); i++) {

			renderer.setItemLabelAnchorOffset(10.0);

			renderer.setItemLabelAnchorOffset(5.0);

			renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(

					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT,

					TextAnchor.BASELINE_CENTER, 0.0

			));
		}

		BufferedImage bufferedImage = chart.createBufferedImage(285, 130);
		ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", imageStream);

		return Image.getInstance(imageStream.toByteArray());
	}

	// graph 5
	private Image createBarChartAsImageCliamRatioIncurredWise(ClaimAnayalisReportDto generateClaimAnalysisReportPdf)
			throws IOException, BadElementException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (IncurredCliamRatio claimRatio : generateClaimAnalysisReportPdf.getIncurredCliamRatio()) {
			dataset.addValue(claimRatio.getCount(), claimRatio.getStatus(), claimRatio.getStatus());

		}

		// Add more data as needed

		JFreeChart chart = ChartFactory.createBarChart("", "Members", "Values", dataset, PlotOrientation.VERTICAL,
				false, false, false);

		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		ValueAxis rangeAxis = plot.getRangeAxis();
		CategoryPlot plot1 = chart.getCategoryPlot();
		plot1.setBackgroundPaint(new Color(220, 220, 220, 88)); // Set background color of the plot area
		plot1.setRangeGridlinePaint(Color.WHITE); // Set color of gridlines

		// Customize bar colors
		BarRenderer renderer = (BarRenderer) plot1.getRenderer();
		for (int i = 0; i < generateClaimAnalysisReportPdf.getClaimTypeAnalysis().size(); i++) {
			renderer.setSeriesPaint(i, new Color(16, 80, 153));
		}
		// Set color of the "Main Member" bars

		domainAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		domainAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setLabelFont(DEFAULT_LABEL_FONT);

		BufferedImage bufferedImage = chart.createBufferedImage(250, 200);
		ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", imageStream);

		return Image.getInstance(imageStream.toByteArray());
	}

	// **************graphs********************
	private PdfPCell createCenteredCell(String content, Font font, BaseColor backgroundColor) {
		PdfPCell cell = new PdfPCell(new Phrase(content, font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(backgroundColor);
		cell.setBorder(Rectangle.NO_BORDER);
		return cell;
	}

	private void addCenteredCell(PdfPTable table, String content, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(content, font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
	}

	// ******************table*********************
	private PdfPTable getMemberTable(ClaimAnayalisReportDto generateClaimAnalysisReportPdf) throws DocumentException {
		PdfPTable table = new PdfPTable(4); // 4 columns

		table.setWidthPercentage(100);

		// Define cell background colors for each column
		BaseColor baseColorhead = new BaseColor(95, 128, 158);
		BaseColor baseColor = new BaseColor(192, 192, 192); // Light blue background for SL.no column
//	    BaseColor memberTypeColor = new BaseColor(173, 216, 230); // Pink for Member Type column
//	    BaseColor numberColor = new BaseColor(173, 216, 230); // Peach background for Number column
//	    BaseColor amountColor = new BaseColor(173, 216, 230); // Peach for Amount column

		// Add table headers with custom background colors
		PdfPCell cell1 = createCenteredCell("SL.NO", boldFont, baseColorhead.brighter());
		cell1.setFixedHeight(25f);

		table.addCell(cell1);

		PdfPCell cell2 = createCenteredCell("MEMBER TYPE", boldFont, baseColorhead.brighter());
		cell2.setFixedHeight(25f);
		table.addCell(cell2);

		PdfPCell cell3 = createCenteredCell("COUNT", boldFont, baseColorhead.brighter());
		cell3.setFixedHeight(25f);
		table.addCell(cell3);

		PdfPCell cell4 = createCenteredCell("AMOUNT", boldFont, baseColorhead.brighter());
		cell4.setFixedHeight(25f);
		table.addCell(cell4);

		// Add dynamic data from ClaimAnayalisReportDto with colors
		// Create cells for the first row with a white background
		PdfPCell row1Cell1 = createCenteredCell("1", boldFont, null);
		PdfPCell row1Cell2 = createCenteredCell("Main Member", boldFont, null);
		PdfPCell row1Cell3 = createCenteredCell(
				String.valueOf(generateClaimAnalysisReportPdf.getMemberTypeAnalysis().getMainMemberCount()), boldFont,
				null);
		PdfPCell row1Cell4 = createCenteredCell(
				decimalFormat.format(generateClaimAnalysisReportPdf.getMemberTypeAnalysis().getMainMemberCountAmount()),
				boldFont, null);

		// Set the background color for all cells in the row

		row1Cell1.setBorder(Rectangle.NO_BORDER);
		row1Cell2.setBorder(Rectangle.NO_BORDER);
		row1Cell3.setBorder(Rectangle.NO_BORDER);
		row1Cell4.setBorder(Rectangle.NO_BORDER);

		// Add the cells to the table
		table.addCell(row1Cell1);
		table.addCell(row1Cell2);
		table.addCell(row1Cell3);
		table.addCell(row1Cell4);

		// Create cells for the second row with a black background
		PdfPCell row2Cell1 = createCenteredCell("2", boldFont, baseColor);
		PdfPCell row2Cell2 = createCenteredCell("Dependent", boldFont, baseColor);
		PdfPCell row2Cell3 = createCenteredCell(
				String.valueOf(generateClaimAnalysisReportPdf.getMemberTypeAnalysis().getDependentCount()), boldFont,
				baseColor);
		PdfPCell row2Cell4 = createCenteredCell(
				decimalFormat.format(
						generateClaimAnalysisReportPdf.getMemberTypeAnalysis().getDependentCountDepedentAmount()),
				boldFont, baseColor);

		row2Cell1.setBorder(Rectangle.NO_BORDER);
		row2Cell2.setBorder(Rectangle.NO_BORDER);
		row2Cell3.setBorder(Rectangle.NO_BORDER);
		row2Cell4.setBorder(Rectangle.NO_BORDER);

		// Set the background color for all cells in the row


		row2Cell1.setCalculatedHeight(0.2f);

		// Add the cells to the table
		table.addCell(row2Cell1);
		table.addCell(row2Cell2);
		table.addCell(row2Cell3);
		table.addCell(row2Cell4);

		PdfPCell row3Cell1 = createCenteredCell(null, null, null);
		PdfPCell row3Cell2 = createCenteredCell(null, null, null);
		PdfPCell row3Cell3 = createCenteredCell(null, null, null);
		PdfPCell row3Cell4 = createCenteredCell(null, null, null);

		table.addCell(row3Cell1);
		table.addCell(row3Cell2);
		table.addCell(row3Cell3);
		table.addCell(row3Cell4);

		return table;
	}

	private void addCenteredCell(PdfPTable table, String text, Font font, BaseColor backgroundColor) {
		PdfPCell cell = createCenteredCellData(text, font, backgroundColor);
		table.addCell(cell);
	}

	// Define the createCenteredCell method here (if not already defined)
	private PdfPCell createCenteredCellData(String text, Font font, BaseColor backgroundColor) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.NO_BORDER); // Remove cell borders

		cell.setBackgroundColor(backgroundColor);
		return cell;
	}

	private PdfPTable getDiseasseWiseData(ClaimAnayalisReportDto generateClaimAnalysisReportPdf) {
		PdfPTable table7 = new PdfPTable(4);
		table7.setWidthPercentage(100);

		List<DiseaseWiseAnalysis> diseaseWiseAnalysis = generateClaimAnalysisReportPdf.getDiseaseWiseAnalysis();
		int count2 = 0;

		BaseColor baseColorhead = new BaseColor(95, 128, 158);

		BaseColor percent2Color = new BaseColor(192, 192, 192); // Light blue background for SL.no column
		PdfPCell cell1 = createCenteredCell("SL.no", boldFont, baseColorhead.brighter());
		cell1.setFixedHeight(25f);
		table7.addCell(cell1);

		PdfPCell cell2 = createCenteredCell("DISEASE", boldFont, baseColorhead.brighter());
		cell2.setFixedHeight(25f);
		table7.addCell(cell2);

		PdfPCell cell3 = createCenteredCell("NUMBER", boldFont, baseColorhead.brighter());
		cell3.setFixedHeight(25f);
		table7.addCell(cell3);

		PdfPCell cell4 = createCenteredCell("AMOUNT", boldFont, baseColorhead.brighter());
		cell4.setFixedHeight(25f);
		table7.addCell(cell4);

		for (DiseaseWiseAnalysis disease : diseaseWiseAnalysis) {
			BaseColor rowColor = (count2 % 2 == 0) ? BaseColor.WHITE : new BaseColor(192, 192, 192); // Alternating row
																										// colors

			PdfPCell rowCell1 = createCenteredCell(String.valueOf(++count2), boldFont);
			rowCell1.setBackgroundColor(rowColor);
			rowCell1.setBorder(Rectangle.NO_BORDER);
			table7.addCell(rowCell1);

			PdfPCell rowCell2 = createCenteredCell(disease.getDiseaseName(), boldFont);
			rowCell2.setBackgroundColor(rowColor);
			rowCell2.setBorder(Rectangle.NO_BORDER);
			table7.addCell(rowCell2);

			PdfPCell rowCell3 = createCenteredCell(String.valueOf(disease.getDiseaseCount()), boldFont);
			rowCell3.setBackgroundColor(rowColor);
			rowCell3.setBorder(Rectangle.NO_BORDER);
			table7.addCell(rowCell3);

			PdfPCell rowCell4 = createCenteredCell(decimalFormat.format(disease.getAmount()), boldFont);
			rowCell4.setBackgroundColor(rowColor);
			rowCell4.setBorder(Rectangle.NO_BORDER);
			table7.addCell(rowCell4);
		}
		return table7;
	}

	private PdfPTable genderwiseTable(ClaimAnayalisReportDto generateClaimAnalysisReportPdf) {
		PdfPTable table1 = new PdfPTable(6); // 6 columns
		table1.setWidthPercentage(100);

		// Define cell background colors for each column
		// Light blue background for SL.no column
		BaseColor baseColorhead = new BaseColor(95, 128, 158);

		BaseColor percent2Color = new BaseColor(192, 192, 192); // Light blue background for SL.no column
		PdfPCell cell1 = createCenteredCell("SL.No", boldFont, baseColorhead.brighter());
		table1.addCell(cell1);

		PdfPCell cell2 = createCenteredCell("GENDER", boldFont, baseColorhead.brighter());
		table1.addCell(cell2);

		PdfPCell cell3 = createCenteredCell("NUMBER", boldFont, baseColorhead.brighter());
		table1.addCell(cell3);

		PdfPCell cell4 = createCenteredCell("%", boldFont, baseColorhead.brighter());
		table1.addCell(cell4);

		PdfPCell cell5 = createCenteredCell("AMOUNT", boldFont, baseColorhead.brighter());
		table1.addCell(cell5);

		PdfPCell cell6 = createCenteredCell("%", boldFont, baseColorhead.brighter());
		table1.addCell(cell6);
		List<GenderWiseClaimReport> genderWiseClaimReport = generateClaimAnalysisReportPdf.getGenderWiseClaimReport();
		int total = 0;
		int countAge = 0;
		double sum = 0;
		double percent = 0.0;

		PdfPCell rowCell1;

		PdfPCell rowCell2;

		PdfPCell rowCell3;

		PdfPCell rowCell4;

		PdfPCell rowCell5;

		PdfPCell rowCell6;

		for (GenderWiseClaimReport genderwise : genderWiseClaimReport) {
			BaseColor rowColor = (countAge % 2 == 0) ? BaseColor.WHITE : new BaseColor(192, 192, 192); // Alternating
																										// row colors

			rowCell1 = createCenteredCell(String.valueOf(++countAge), boldFont, percent2Color);
			rowCell1.setBackgroundColor(rowColor);
			rowCell1.setBorder(Rectangle.NO_BORDER);

			table1.addCell(rowCell1);

			rowCell2 = createCenteredCell(genderwise.getGender(), boldFont, percent2Color);
			rowCell2.setBackgroundColor(rowColor);
			rowCell2.setBorder(Rectangle.NO_BORDER);

			table1.addCell(rowCell2);

			rowCell3 = createCenteredCell(String.valueOf(genderwise.getGenderCount()), boldFont, percent2Color);
			rowCell3.setBackgroundColor(rowColor);
			rowCell3.setBorder(Rectangle.NO_BORDER);

			table1.addCell(rowCell3);

			rowCell4 = createCenteredCell(decimalFormat.format(genderwise.getCountPerct()), boldFont, percent2Color);
			rowCell4.setBackgroundColor(rowColor);
			rowCell4.setBorder(Rectangle.NO_BORDER);

			table1.addCell(rowCell4);

			rowCell5 = createCenteredCell(decimalFormat.format(genderwise.getAmount()), boldFont, percent2Color);
			rowCell5.setBackgroundColor(rowColor);
			rowCell5.setBorder(Rectangle.NO_BORDER);

			table1.addCell(rowCell5);

			rowCell6 = createCenteredCell(decimalFormat.format(genderwise.getAmountPerct()), boldFont, percent2Color);
			rowCell6.setBackgroundColor(rowColor);
			rowCell6.setBorder(Rectangle.NO_BORDER);

			table1.addCell(rowCell6);

			sum = sum + genderwise.getAmount();
			total = total + genderwise.getGenderCount();
			percent = percent + genderwise.getCountPerct();
		}

		rowCell1 = createCenteredCell("3", boldFont, percent2Color);
		rowCell1.setBackgroundColor(BaseColor.WHITE);
		rowCell1.setBorder(Rectangle.NO_BORDER);

		table1.addCell(rowCell1);

		rowCell2 = createCenteredCell("Total", boldFont, percent2Color);
		rowCell2.setBackgroundColor(BaseColor.WHITE);
		rowCell2.setBorder(Rectangle.NO_BORDER);

		table1.addCell(rowCell2);

		rowCell3 = createCenteredCell(String.valueOf(total), boldFont, percent2Color);
		rowCell3.setBackgroundColor(BaseColor.WHITE);
		rowCell3.setBorder(Rectangle.NO_BORDER);

		table1.addCell(rowCell3);

		rowCell4 = createCenteredCell("100", boldFont, percent2Color);
		rowCell4.setBackgroundColor(BaseColor.WHITE);
		rowCell4.setBorder(Rectangle.NO_BORDER);

		table1.addCell(rowCell4);

		rowCell5 = createCenteredCell(String.valueOf(decimalFormat.format(sum)), boldFont, percent2Color);
		rowCell5.setBackgroundColor(BaseColor.WHITE);
		rowCell5.setBorder(Rectangle.NO_BORDER);

		table1.addCell(rowCell5);

		rowCell6 = createCenteredCell("100", boldFont, percent2Color);
		rowCell6.setBackgroundColor(BaseColor.WHITE);
		rowCell6.setBorder(Rectangle.NO_BORDER);

		table1.addCell(rowCell6);

		return table1;
	}

	private PdfPCell createCenteredCell(String value, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(value, font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.NO_BORDER); // Remove cell borders
		return cell;
	}

	private PdfPTable getRelationWiseTable(ClaimAnayalisReportDto generateClaimAnalysisReportPdf)
			throws DocumentException {
		PdfPTable table3 = new PdfPTable(4); // 4 columns
		table3.setWidthPercentage(100); // Set the width of the table to 100% of the available width

		// Set the relative widths of the columns

		// Define cell background colors for each column
		BaseColor baseColorhead = new BaseColor(95, 128, 158);

		BaseColor headerColor = new BaseColor(192, 192, 192); // Sky Blue
		// Blanched Almond
		// Add table headers with background color
		PdfPCell cell1 = createCenteredCell("SL.NO", boldFont, baseColorhead.brighter());
		cell1.setFixedHeight(25f);
		table3.addCell(cell1);

		PdfPCell cell2 = createCenteredCell("RELATION", boldFont, baseColorhead.brighter());
		cell2.setFixedHeight(25f);
		table3.addCell(cell2);

		PdfPCell cell3 = createCenteredCell("NUMBER", boldFont, baseColorhead.brighter());
		cell3.setFixedHeight(25f);
		table3.addCell(cell3);

		PdfPCell cell4 = createCenteredCell("AMOUNT", boldFont, baseColorhead.brighter());
		cell4.setFixedHeight(25f);
		table3.addCell(cell4);

		// Define an array of column background colors

		// Add dynamic data from ClaimAnayalisReportDto
		List<RelationWiseClaimReport> relationWiseClaimReport = generateClaimAnalysisReportPdf
				.getRelationWiseClaimReport();
		int countRela = 0;
		PdfPCell rowCell1;
		PdfPCell rowCell2;
		PdfPCell rowCell3;
		PdfPCell rowCell4;
		PdfPCell rowCell5;
		for (RelationWiseClaimReport relation : relationWiseClaimReport) {
			BaseColor rowColor = (countRela % 2 == 0) ? BaseColor.WHITE : new BaseColor(192, 192, 192);

			rowCell1 = createCenteredCell(String.valueOf(++countRela), boldFont, rowColor);
			rowCell1.setBackgroundColor(rowColor);
			table3.addCell(rowCell1);

			rowCell2 = createCenteredCell(relation.getRelation(), boldFont, rowColor);
			rowCell2.setBackgroundColor(rowColor);
			rowCell2.setBorder(Rectangle.NO_BORDER);
			table3.addCell(rowCell2);

			rowCell3 = createCenteredCell(String.valueOf(relation.getCount()), boldFont, rowColor);
			rowCell3.setBackgroundColor(rowColor);
			rowCell3.setBorder(Rectangle.NO_BORDER);
			table3.addCell(rowCell3);

			rowCell4 = createCenteredCell(decimalFormat.format(relation.getAmount()), boldFont, rowColor);
			rowCell4.setBackgroundColor(rowColor);
			rowCell4.setBorder(Rectangle.NO_BORDER);
			table3.addCell(rowCell4);



		}

		rowCell1 = createCenteredCell(null, null, null);
		rowCell1.setBackgroundColor(null);
		table3.addCell(rowCell1);

		rowCell2 = createCenteredCell(null, null, null);
		rowCell2.setBackgroundColor(null);
		rowCell2.setBorder(Rectangle.NO_BORDER);
		table3.addCell(rowCell2);

		rowCell3 = createCenteredCell(null, null, null);
		rowCell3.setBackgroundColor(null);
		rowCell3.setBorder(Rectangle.NO_BORDER);
		table3.addCell(rowCell3);

		rowCell4 = createCenteredCell(null, null, null);
		rowCell4.setBackgroundColor(null);
		rowCell4.setBorder(Rectangle.NO_BORDER);
		table3.addCell(rowCell4);
		return table3;
	}

	// age wise table data
	private PdfPTable getAgeWisePdfPTable(ClaimAnayalisReportDto generateClaimAnalysisReportPdf) {
		PdfPTable table = new PdfPTable(4); // 4 columns
		table.setWidthPercentage(100);

		BaseColor baseColorhead = new BaseColor(95, 128, 158);

		BaseColor baseColor = new BaseColor(192, 192, 192);

		PdfPCell cell1 = createCenteredCell("SL.N0", boldFont, baseColorhead.brighter());
		cell1.setFixedHeight(25f);
		table.addCell(cell1);

		PdfPCell cell2 = createCenteredCell("AGE", boldFont, baseColorhead.brighter());
		cell2.setFixedHeight(25f);
		table.addCell(cell2);

		PdfPCell cell3 = createCenteredCell("NUMBER", boldFont, baseColorhead.brighter());
		cell3.setFixedHeight(25f);
		table.addCell(cell3);

		PdfPCell cell4 = createCenteredCell("AMOUNT", boldFont, baseColorhead.brighter());
		cell4.setFixedHeight(25f);
		table.addCell(cell4);

		PdfPCell row1Cell1 = createCenteredCell("1", boldFont, null);
		PdfPCell row1Cell2 = createCenteredCell("0-10", boldFont, null);
		PdfPCell row1Cell3 = createCenteredCell(
				String.valueOf(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount0To10()), boldFont,
				null);
		PdfPCell row1Cell4 = createCenteredCell(
				decimalFormat.format(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount0To10Amount()),
				boldFont, null);

		row1Cell1.setBorder(Rectangle.NO_BORDER);
		row1Cell2.setBorder(Rectangle.NO_BORDER);
		row1Cell3.setBorder(Rectangle.NO_BORDER);
		row1Cell4.setBorder(Rectangle.NO_BORDER);

		// Add the cells to the table
		table.addCell(row1Cell1);
		table.addCell(row1Cell2);
		table.addCell(row1Cell3);
		table.addCell(row1Cell4);

		PdfPCell row2Cell1 = createCenteredCell("2", boldFont, baseColor);
		PdfPCell row2Cell2 = createCenteredCell("11-20", boldFont, baseColor);
		PdfPCell row2Cell3 = createCenteredCell(
				String.valueOf(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount11To20()), boldFont,
				baseColor);
		PdfPCell row2Cell4 = createCenteredCell(
				decimalFormat
						.format(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount11To20Amount()),
				boldFont, baseColor);

		row2Cell1.setBorder(Rectangle.NO_BORDER);
		row2Cell2.setBorder(Rectangle.NO_BORDER);
		row2Cell3.setBorder(Rectangle.NO_BORDER);
		row2Cell4.setBorder(Rectangle.NO_BORDER);

		// Add the cells to the table
		table.addCell(row2Cell1);
		table.addCell(row2Cell2);
		table.addCell(row2Cell3);
		table.addCell(row2Cell4);

		PdfPCell row3Cell1 = createCenteredCell("3", boldFont, null);
		PdfPCell row3Cell2 = createCenteredCell("21-30", boldFont, null);
		PdfPCell row3Cell3 = createCenteredCell(
				String.valueOf(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount21To30()), boldFont,
				null);
		PdfPCell row3Cell4 = createCenteredCell(
				decimalFormat
						.format(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount21To30Amount()),
				boldFont, null);

		row3Cell1.setBorder(Rectangle.NO_BORDER);
		row3Cell2.setBorder(Rectangle.NO_BORDER);
		row3Cell3.setBorder(Rectangle.NO_BORDER);
		row3Cell4.setBorder(Rectangle.NO_BORDER);

		// Add the cells to the table
		table.addCell(row3Cell1);
		table.addCell(row3Cell2);
		table.addCell(row3Cell3);
		table.addCell(row3Cell4);

		PdfPCell row4Cell1 = createCenteredCell("4", boldFont, baseColor);
		PdfPCell row4Cell2 = createCenteredCell("31-40", boldFont, baseColor);
		PdfPCell row4Cell3 = createCenteredCell(
				String.valueOf(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount31To40()), boldFont,
				baseColor);
		PdfPCell row4Cell4 = createCenteredCell(
				decimalFormat
						.format(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount31To40Amount()),
				boldFont, baseColor);

		row4Cell1.setBorder(Rectangle.NO_BORDER);
		row4Cell2.setBorder(Rectangle.NO_BORDER);
		row4Cell3.setBorder(Rectangle.NO_BORDER);
		row4Cell4.setBorder(Rectangle.NO_BORDER);

		// Add the cells to the table
		table.addCell(row4Cell1);
		table.addCell(row4Cell2);
		table.addCell(row4Cell3);
		table.addCell(row4Cell4);

		PdfPCell row5Cell1 = createCenteredCell("5", boldFont, null);
		PdfPCell row5Cell2 = createCenteredCell("41-50", boldFont, null);
		PdfPCell row5Cell3 = createCenteredCell(
				String.valueOf(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount41To50()), boldFont,
				null);
		PdfPCell row5Cell4 = createCenteredCell(
				decimalFormat
						.format(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount41To50Amount()),
				boldFont, null);

		row5Cell1.setBorder(Rectangle.NO_BORDER);
		row5Cell2.setBorder(Rectangle.NO_BORDER);
		row5Cell3.setBorder(Rectangle.NO_BORDER);
		row5Cell4.setBorder(Rectangle.NO_BORDER);

		// Add the cells to the table
		table.addCell(row5Cell1);
		table.addCell(row5Cell2);
		table.addCell(row5Cell3);
		table.addCell(row5Cell4);

		PdfPCell row6Cell1 = createCenteredCell("6", boldFont, baseColor);
		PdfPCell row6Cell2 = createCenteredCell("51-60", boldFont, baseColor);
		PdfPCell row6Cell3 = createCenteredCell(
				String.valueOf(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount51To60()), boldFont,
				baseColor);
		PdfPCell row6Cell4 = createCenteredCell(
				decimalFormat
						.format(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount51To60Amount()),
				boldFont, baseColor);

		row6Cell1.setBorder(Rectangle.NO_BORDER);
		row6Cell2.setBorder(Rectangle.NO_BORDER);
		row6Cell3.setBorder(Rectangle.NO_BORDER);
		row6Cell4.setBorder(Rectangle.NO_BORDER);

		// Add the cells to the table
		table.addCell(row6Cell1);
		table.addCell(row6Cell2);
		table.addCell(row6Cell3);
		table.addCell(row6Cell4);

		PdfPCell row7Cell1 = createCenteredCell("7", boldFont, null);
		PdfPCell row7Cell2 = createCenteredCell("61-70", boldFont, null);
		PdfPCell row7Cell3 = createCenteredCell(
				String.valueOf(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount61To70()), boldFont,
				null);
		PdfPCell row7Cell4 = createCenteredCell(
				decimalFormat
						.format(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount61To70Amount()),
				boldFont, null);

		row7Cell1.setBorder(Rectangle.NO_BORDER);
		row7Cell2.setBorder(Rectangle.NO_BORDER);
		row7Cell3.setBorder(Rectangle.NO_BORDER);
		row7Cell4.setBorder(Rectangle.NO_BORDER);

		// Add the cells to the table
		table.addCell(row7Cell1);
		table.addCell(row7Cell2);
		table.addCell(row7Cell3);
		table.addCell(row7Cell4);

		PdfPCell row8Cell1 = createCenteredCell("8", boldFont, baseColor);
		PdfPCell row8Cell2 = createCenteredCell("> 70", boldFont, baseColor);
		PdfPCell row8Cell3 = createCenteredCell(
				String.valueOf(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount70AndAbove()),
				boldFont, baseColor);
		PdfPCell row8Cell4 = createCenteredCell(
				decimalFormat
						.format(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount70AndAboveAmount()),
				boldFont, baseColor);

		row8Cell1.setBorder(Rectangle.NO_BORDER);
		row8Cell2.setBorder(Rectangle.NO_BORDER);
		row8Cell3.setBorder(Rectangle.NO_BORDER);
		row8Cell4.setBorder(Rectangle.NO_BORDER);

		// Add the cells to the table
		table.addCell(row8Cell1);
		table.addCell(row8Cell2);
		table.addCell(row8Cell3);
		table.addCell(row8Cell4);

		PdfPCell row9Cell1 = createCenteredCell(null, null, null);
		PdfPCell row9Cell2 = createCenteredCell(null, null, null);
		PdfPCell row9Cell3 = createCenteredCell(null, null, null);
		PdfPCell row9Cell4 = createCenteredCell(null, null, null);

		row9Cell1.setBorder(Rectangle.NO_BORDER);
		row9Cell2.setBorder(Rectangle.NO_BORDER);
		row9Cell3.setBorder(Rectangle.NO_BORDER);
		row9Cell4.setBorder(Rectangle.NO_BORDER);

		// Add the cells to the table
		table.addCell(row9Cell1);
		table.addCell(row9Cell2);
		table.addCell(row9Cell3);
		table.addCell(row9Cell4);

		return table;

	}

	private PdfPTable getCliamAnalysis(ClaimAnayalisReportDto generateClaimAnalysisReportPdf) {
		// Add table headers
		PdfPTable table4 = new PdfPTable(4);
		table4.setWidthPercentage(100);

		// Define cell background colors for each column
		// Light blue background for SL.no column
		BaseColor baseColorhead = new BaseColor(95, 128, 158);

		BaseColor percent2Color = new BaseColor(192, 192, 192); // Light blue background for SL.no column
		PdfPCell cell1 = createCenteredCell("SL.NO", boldFont, baseColorhead.brighter());
		cell1.setFixedHeight(25f);
		table4.addCell(cell1);

		PdfPCell cell2 = createCenteredCell("CLAIM TYPE", boldFont, baseColorhead.brighter());
		cell2.setFixedHeight(25f);
		table4.addCell(cell2);

		PdfPCell cell3 = createCenteredCell("NUMBER", boldFont, baseColorhead.brighter());
		cell3.setFixedHeight(25f);
		table4.addCell(cell3);

		PdfPCell cell4 = createCenteredCell("AMOUNT", boldFont, baseColorhead.brighter());
		cell4.setFixedHeight(25f);
		table4.addCell(cell4);

		List<ClaimTypeAnalysis> claimTypeAnalysis = generateClaimAnalysisReportPdf.getClaimTypeAnalysis();
		int countAge = 0;

		PdfPCell rowCell1;

		PdfPCell rowCell2;

		PdfPCell rowCell3;

		PdfPCell rowCell4;

		for (ClaimTypeAnalysis c : claimTypeAnalysis) {
			BaseColor rowColor = (countAge % 2 == 0) ? BaseColor.WHITE : new BaseColor(192, 192, 192); // Alternating
																										// row colors

			rowCell1 = createCenteredCell(String.valueOf(++countAge), boldFont, percent2Color);
			rowCell1.setBackgroundColor(rowColor);
			rowCell1.setBorder(Rectangle.NO_BORDER);

			table4.addCell(rowCell1);

			rowCell2 = createCenteredCell(c.getStatus(), boldFont, percent2Color);
			rowCell2.setBackgroundColor(rowColor);
			rowCell2.setBorder(Rectangle.NO_BORDER);

			table4.addCell(rowCell2);

			rowCell3 = createCenteredCell(String.valueOf(c.getNumber()), boldFont, percent2Color);
			rowCell3.setBackgroundColor(rowColor);
			rowCell3.setBorder(Rectangle.NO_BORDER);

			table4.addCell(rowCell3);

			rowCell4 = createCenteredCell(decimalFormat.format(c.getAmount()), boldFont, percent2Color);
			rowCell4.setBackgroundColor(rowColor);
			rowCell4.setBorder(Rectangle.NO_BORDER);

			table4.addCell(rowCell4);
		}

		rowCell1 = createCenteredCell(null, null, null);
		rowCell2 = createCenteredCell(null, null, null);
		rowCell3 = createCenteredCell(null, null, null);
		rowCell4 = createCenteredCell(null, null, null);

		rowCell1.setBorder(Rectangle.NO_BORDER);
		rowCell2.setBorder(Rectangle.NO_BORDER);
		rowCell3.setBorder(Rectangle.NO_BORDER);
		rowCell4.setBorder(Rectangle.NO_BORDER);

		// Add the cells to the table
		table4.addCell(rowCell1);
		table4.addCell(rowCell2);
		table4.addCell(rowCell3);
		table4.addCell(rowCell4);

		return table4;
	}

	private PdfPTable getClaimInccuredRatio(ClaimAnayalisReportDto generateClaimAnalysisReportPdf) {
		PdfPTable table6 = new PdfPTable(4); // 4 columns
		table6.setWidthPercentage(100);

		BaseColor baseColorhead = new BaseColor(95, 128, 158);

		BaseColor percent2Color = new BaseColor(192, 192, 192); // Light blue background for SL.no column

		// Add table headers without column colors
		PdfPCell cell1 = createCenteredCell("SL.No", boldFont, baseColorhead.brighter());
		cell1.setBackgroundColor(baseColorhead.brighter());// Light blue background for SL.no column
		cell1.setFixedHeight(25f);
		table6.addCell(cell1);

		PdfPCell cell2 = createCenteredCell("STATUS", boldFont, baseColorhead.brighter());
		cell2.setBackgroundColor(baseColorhead.brighter());
		cell2.setFixedHeight(25f);
		table6.addCell(cell2);

		PdfPCell cell3 = createCenteredCell("NUMBER", boldFont, baseColorhead.brighter());
		cell3.setFixedHeight(25f);
		cell3.setBackgroundColor(baseColorhead.brighter());
		table6.addCell(cell3);

		PdfPCell cell4 = createCenteredCell("AMOUNT", boldFont, baseColorhead.brighter());
		cell4.setFixedHeight(25f);
		cell4.setBackgroundColor(baseColorhead.brighter());
		table6.addCell(cell4);

		// Add dynamic data from ClaimAnayalisReportDto with alternating row colors
		List<IncurredCliamRatio> incurredCliamRatio = generateClaimAnalysisReportPdf.getIncurredCliamRatio();
		int count1 = 0;

		PdfPCell rowCell1;
		PdfPCell rowCell2;
		PdfPCell rowCell3;
		PdfPCell rowCell4;

		for (IncurredCliamRatio incu : incurredCliamRatio) {
			BaseColor rowColor = (count1 % 2 == 0) ? BaseColor.WHITE : new BaseColor(192, 192, 192); // Alternating row
																										// colors

			rowCell1 = createCenteredCell(String.valueOf(++count1), boldFont);
			rowCell1.setBackgroundColor(rowColor);
			table6.addCell(rowCell1);

			rowCell2 = createCenteredCell(incu.getStatus(), boldFont);
			rowCell2.setBackgroundColor(rowColor);
			table6.addCell(rowCell2);

			rowCell3 = createCenteredCell(String.valueOf(incu.getCount()), boldFont);
			rowCell3.setBackgroundColor(rowColor);
			table6.addCell(rowCell3);

			rowCell4 = createCenteredCell(decimalFormat.format(incu.getAmount()), boldFont);
			rowCell4.setBackgroundColor(rowColor);
			table6.addCell(rowCell4);
		}

		rowCell1 = createCenteredCell(null, null, null);
		rowCell2 = createCenteredCell(null, null, null);
		rowCell3 = createCenteredCell(null, null, null);
		rowCell4 = createCenteredCell(null, null, null);

		rowCell1.setBorder(Rectangle.NO_BORDER);
		rowCell2.setBorder(Rectangle.NO_BORDER);
		rowCell3.setBorder(Rectangle.NO_BORDER);
		rowCell4.setBorder(Rectangle.NO_BORDER);

		// Add the cells to the table
		table6.addCell(rowCell1);
		table6.addCell(rowCell2);
		table6.addCell(rowCell3);
		table6.addCell(rowCell4);

		return table6;
	}

	public Image createBarchartAsimageAmount(ClaimAnayalisReportDto generateClaimAnalysisReportPdf)
			throws IOException, BadElementException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(generateClaimAnalysisReportPdf.getMemberTypeAnalysis().getMainMemberCountAmount(),
				"Main Member", "");
		dataset.addValue(generateClaimAnalysisReportPdf.getMemberTypeAnalysis().getDependentCountDepedentAmount(),
				"Dependent", "");

		// Add more data as needed

		JFreeChart chart = ChartFactory.createBarChart("", "", "Amount", dataset, PlotOrientation.HORIZONTAL, false,
				false, false);
		chart.setBorderVisible(false);

		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		ValueAxis rangeAxis = plot.getRangeAxis();
		CategoryPlot plot1 = chart.getCategoryPlot();
		plot.setOutlineVisible(false);

		plot1.setBackgroundPaint(Color.WHITE); // Set background color of the plot area
		plot1.setRangeGridlinePaint(Color.WHITE); // Set color of gridlines

		// Customize bar colors
		BarRenderer renderer = (BarRenderer) plot1.getRenderer();
		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setDefaultItemLabelsVisible(true);
		renderer.setItemMargin(0.1);

		renderer.setSeriesPaint(0, new Color(0, 102, 0)); // Set color of the "Main Member" bars
		renderer.setSeriesPaint(1, new Color(0, 102, 0));
		renderer.setMaximumBarWidth(0.07d);

		for (int i = 0; i <= dataset.getColumnCount(); i++) {

			renderer.setItemLabelAnchorOffset(10.0);

			renderer.setItemLabelAnchorOffset(5.0);

			renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(

					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT,

					TextAnchor.BASELINE_LEFT, 0.0

			));
		}

		domainAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		domainAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelsVisible(false);
		domainAxis.setAxisLineVisible(false);
		rangeAxis.setAxisLineVisible(false);

		BufferedImage bufferedImage = chart.createBufferedImage(285, 138);
		ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", imageStream);

		return Image.getInstance(imageStream.toByteArray());

	}

	public Image createBarChartAsImageAmountWise(ClaimAnayalisReportDto generateClaimAnalysisReportPdf)
			throws IOException, BadElementException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Double sum = 0.0;
		List<GenderWiseClaimReport> genderWiseClaimReport = generateClaimAnalysisReportPdf.getGenderWiseClaimReport();
		for (GenderWiseClaimReport genderwise : genderWiseClaimReport) {
			dataset.addValue(genderwise.getAmount(), genderwise.getGender(), "");
			sum = sum + genderwise.getAmount();
		}
		dataset.addValue(sum, "Total", "");

		// Add more data as needed

		JFreeChart chart = ChartFactory.createBarChart("", "", "Amount", dataset, PlotOrientation.HORIZONTAL, false,
				false, true);
		chart.setBorderVisible(false);

		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		ValueAxis rangeAxis = plot.getRangeAxis();
		CategoryPlot plot1 = chart.getCategoryPlot();
		plot.setOutlineVisible(false);

		plot1.setBackgroundPaint(Color.WHITE); // Set background color of the plot area
		plot1.setRangeGridlinePaint(Color.WHITE); // Set color of gridlines

		// Customize bar colors
		BarRenderer renderer = (BarRenderer) plot1.getRenderer();
		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setDefaultItemLabelsVisible(true);

		renderer.setItemMargin(0.1);

		renderer.setSeriesPaint(0, new Color(0, 102, 0)); // Set color of the "Main Member" bars
		renderer.setSeriesPaint(1, new Color(0, 102, 0));
		renderer.setSeriesPaint(2, new Color(0, 102, 0));
		renderer.setMaximumBarWidth(0.06d);

		domainAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		domainAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelsVisible(false);
		domainAxis.setAxisLineVisible(false);
		rangeAxis.setAxisLineVisible(false);

		for (int i = 0; i <= dataset.getColumnCount(); i++) {

			renderer.setItemLabelAnchorOffset(10.0);

			renderer.setItemLabelAnchorOffset(5.0);

			renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(

					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER,

					TextAnchor.BASELINE_CENTER, 0.0

			));
		}

		BufferedImage bufferedImage = chart.createBufferedImage(285, 145);
		ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", imageStream);

		return Image.getInstance(imageStream.toByteArray());

	}

	public Image createBarChartAsImageAgeCountWise(ClaimAnayalisReportDto generateClaimAnalysisReportPdf)
			throws IOException, BadElementException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount0To10(), "0-10", "");
		dataset.addValue(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount11To20(), "11-20", "");
		dataset.addValue(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount21To30(), "21-30", "");
		dataset.addValue(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount31To40(), "31-40", "");
		dataset.addValue(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount41To50(), "41-50", "");
		dataset.addValue(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount51To60(), "51-60", "");
		dataset.addValue(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount61To70(), "61-70", "");
		dataset.addValue(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount70AndAbove(), "> 70", "");

		// Add more data as needed

		JFreeChart chart = ChartFactory.createBarChart("", "", "Number", dataset, PlotOrientation.HORIZONTAL, false,
				false, false);
		chart.setBorderVisible(false);

		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		ValueAxis rangeAxis = plot.getRangeAxis();
		CategoryPlot plot1 = chart.getCategoryPlot();
		plot.setOutlineVisible(false);

		plot1.setBackgroundPaint(Color.WHITE); // Set background color of the plot area
		plot1.setRangeGridlinePaint(Color.WHITE); // Set color of gridlines

		// Customize bar colors
		BarRenderer renderer = (BarRenderer) plot1.getRenderer();
		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setDefaultItemLabelsVisible(true);
		renderer.setItemMargin(0.1);

		for (int i = 0; i <= dataset.getRowCount(); i++) {
			renderer.setSeriesPaint(i, new Color(16, 80, 153));
		}
		renderer.setMaximumBarWidth(0.026d);

		for (int i = 0; i <= dataset.getColumnCount(); i++) {

			renderer.setItemLabelAnchorOffset(10.0);

			renderer.setItemLabelAnchorOffset(5.0);

			renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(

					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT,

					TextAnchor.BASELINE_CENTER, 0.0

			));
		}
		domainAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		domainAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelsVisible(false);
		domainAxis.setAxisLineVisible(false);
		rangeAxis.setAxisLineVisible(false);
		BufferedImage bufferedImage = chart.createBufferedImage(285, 320);
		ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", imageStream);

		return Image.getInstance(imageStream.toByteArray());

	}

	public Image createBarChartAsImageAgeAmountWise(ClaimAnayalisReportDto generateClaimAnalysisReportPdf)
			throws IOException, BadElementException {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount0To10Amount(), "0-10", "");
		dataset.addValue(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount11To20Amount(), "11-20",
				"");
		dataset.addValue(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount21To30Amount(), "21-30",
				"");
		dataset.addValue(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount31To40Amount(), "31-40",
				"");
		dataset.addValue(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount41To50Amount(), "41-50",
				"");
		dataset.addValue(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount51To60Amount(), "51-60",
				"");
		dataset.addValue(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount61To70Amount(), "61-70",
				"");
		dataset.addValue(generateClaimAnalysisReportPdf.getAgeWiseClaimAnalysis().getAgeCount70AndAboveAmount(), "> 70",
				"");

		// Add more data as needed

		JFreeChart chart = ChartFactory.createBarChart("", "", "Amount", dataset, PlotOrientation.HORIZONTAL, false,
				false, true);

		chart.setBorderVisible(false);

		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		ValueAxis rangeAxis = plot.getRangeAxis();
		CategoryPlot plot1 = chart.getCategoryPlot();
		plot.setOutlineVisible(false);

		plot1.setBackgroundPaint(Color.WHITE); // Set background color of the plot area
		plot1.setRangeGridlinePaint(Color.WHITE); // Set color of gridlines

		// Customize bar colors
		BarRenderer renderer = (BarRenderer) plot1.getRenderer();
		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setDefaultItemLabelsVisible(true);
		renderer.setItemMargin(0.1);

		renderer.setSeriesPaint(0, new Color(0, 102, 0)); // Set color of the "Main Member" bars
		renderer.setSeriesPaint(1, new Color(0, 102, 0));
		renderer.setSeriesPaint(2, new Color(0, 102, 0)); // Set color of the "Main Member" bars
		renderer.setSeriesPaint(3, new Color(0, 102, 0));
		renderer.setSeriesPaint(4, new Color(0, 102, 0)); // Set color of the "Main Member" bars
		renderer.setSeriesPaint(5, new Color(0, 102, 0));
		renderer.setSeriesPaint(6, new Color(0, 102, 0)); // Set color of the "Main Member" bars
		renderer.setSeriesPaint(7, new Color(0, 102, 0));
		renderer.setMaximumBarWidth(0.026d);

		for (int i = 0; i <= dataset.getColumnCount(); i++) {

			renderer.setItemLabelAnchorOffset(10.0);

			renderer.setItemLabelAnchorOffset(5.0);

			renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(

					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT,

					TextAnchor.BASELINE_LEFT, 0.0

			));
		}

		domainAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		domainAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelsVisible(false);
		domainAxis.setAxisLineVisible(false);
		rangeAxis.setAxisLineVisible(false);

		BufferedImage bufferedImage = chart.createBufferedImage(285, 320);
		ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", imageStream);

		return Image.getInstance(imageStream.toByteArray());

	}

	public Image createBarChartAsImageAmountWisedata(ClaimAnayalisReportDto generateClaimAnalysisReportPdf)
			throws IOException, BadElementException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		List<ClaimTypeAnalysis> claimTypeAnalysis = generateClaimAnalysisReportPdf.getClaimTypeAnalysis();
		for (ClaimTypeAnalysis c : claimTypeAnalysis) {
			dataset.addValue(c.getAmount(), c.getStatus(), "");
		}

		// Add more data as needed

		JFreeChart chart = ChartFactory.createBarChart("", "", "Amount", dataset, PlotOrientation.HORIZONTAL, false,
				false, true);
		chart.setBorderVisible(false);

		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		ValueAxis rangeAxis = plot.getRangeAxis();
		CategoryPlot plot1 = chart.getCategoryPlot();
		plot.setOutlineVisible(false);

		plot1.setBackgroundPaint(Color.WHITE); // Set background color of the plot area
		plot1.setRangeGridlinePaint(Color.WHITE); // Set color of gridlines

		// Customize bar colors
		BarRenderer renderer = (BarRenderer) plot1.getRenderer();
		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setDefaultItemLabelsVisible(true);
		renderer.setItemMargin(0.1);

		for (int i = 0; i < generateClaimAnalysisReportPdf.getClaimTypeAnalysis().size(); i++) {
			renderer.setSeriesPaint(i, new Color(0, 102, 0));
		}
		renderer.setMaximumBarWidth(0.08d);
		plot.setOutlineVisible(false);

		domainAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		domainAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelsVisible(false);
		domainAxis.setAxisLineVisible(false);
		rangeAxis.setAxisLineVisible(false);

		for (int i = 0; i <= dataset.getColumnCount(); i++) {

			renderer.setItemLabelAnchorOffset(10.0);

			renderer.setItemLabelAnchorOffset(5.0);

			renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(

					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT,

					TextAnchor.BASELINE_LEFT, 0.0

			));
		}

		BufferedImage bufferedImage = chart.createBufferedImage(285, 130);
		ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", imageStream);

		return Image.getInstance(imageStream.toByteArray());
	}

	public PdfPTable barChartConstents() {
		PdfPTable table = new PdfPTable(1);

		PdfPCell cell1 = createCenteredCell("Member type analysis", boldFont, null);
		cell1.setBorder(Rectangle.NO_BORDER);

		table.addCell(cell1);

		PdfPCell cell2 = createCenteredCell(" ", boldFont, null);
		cell2.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell2);

		PdfPCell cell5 = createCenteredCell("Main Member", boldFont, null);
		cell5.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell5);

		PdfPCell cell6 = createCenteredCell(" ", boldFont, null);
		cell6.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell6);

		PdfPCell cell7 = createCenteredCell(" ", boldFont, null);
		cell7.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell7);

		PdfPCell cell10 = createCenteredCell("Dependents", boldFont, null);
		cell10.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell10);
		table.setWidthPercentage(50);

		return table;

	}

	public PdfPTable barChartConstentssecond(ClaimAnayalisReportDto generateClaimAnalysisReportPdf) {
		PdfPTable table = new PdfPTable(1);
		PdfPCell cell1 = createCenteredCell("Gender wise analysis", boldFont, null);
		cell1.setBorder(Rectangle.NO_BORDER);

		table.addCell(cell1);

		PdfPCell cell12 = createCenteredCell(" ", boldFont, null);
		cell12.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell12);

		PdfPCell cell5 = createCenteredCell("Female", boldFont, null);
		cell5.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell5);

		PdfPCell cell6 = createCenteredCell(" ", boldFont, null);
		cell6.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell6);

		PdfPCell cell10 = createCenteredCell("Male", boldFont, null);
		cell10.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell10);

		PdfPCell cell8 = createCenteredCell(" ", boldFont, null);
		cell8.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell8);

		PdfPCell cell9 = createCenteredCell("Total", boldFont, null);
		cell9.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell9);

		return table;

	}

	public Image createBarChartrAsImageAmountWise(ClaimAnayalisReportDto generateClaimAnalysisReportPdf)
			throws IOException, BadElementException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		int size = 0;
		for (RelationWiseClaimReport rela : generateClaimAnalysisReportPdf.getRelationWiseClaimReport()) {
			dataset.addValue(rela.getAmount(), rela.getRelation(), "");
			size++;

		}

		// Add more data as needed

		JFreeChart chart = ChartFactory.createBarChart("", "", "Amount", dataset, PlotOrientation.HORIZONTAL, false,
				false, true);
		chart.setBorderVisible(false);

		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		ValueAxis rangeAxis = plot.getRangeAxis();
		CategoryPlot plot1 = chart.getCategoryPlot();

		plot1.setBackgroundPaint(Color.WHITE); // Set background color of the plot area
		plot1.setRangeGridlinePaint(Color.WHITE); // Set color of gridlines

		// Customize bar colors
		BarRenderer renderer = (BarRenderer) plot1.getRenderer();

		for (int i = 0; i <= size; i++) {
			renderer.setSeriesPaint(i, new Color(0, 102, 0));
		}
		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setDefaultItemLabelsVisible(true);
		renderer.setItemMargin(0.01);
		renderer.setMaximumBarWidth(0.051d);
		plot.setOutlineVisible(false);
		domainAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		domainAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelsVisible(false);
		domainAxis.setAxisLineVisible(false);
		rangeAxis.setAxisLineVisible(false);

		for (int i = 0; i <= dataset.getColumnCount(); i++) {

			renderer.setItemLabelAnchorOffset(10.0);

			renderer.setItemLabelAnchorOffset(5.0);

			renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(

					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT,

					TextAnchor.BASELINE_LEFT, 0.0

			));
		}

		if (size == 3) {
			BufferedImage bufferedImage = chart.createBufferedImage(285, 150);
			ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", imageStream);
			return Image.getInstance(imageStream.toByteArray());
		} else if (size == 5) {
			BufferedImage bufferedImage = chart.createBufferedImage(285, 210);
			ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", imageStream);
			return Image.getInstance(imageStream.toByteArray());
		} else if (size == 4) {
			BufferedImage bufferedImage = chart.createBufferedImage(285, 175);
			ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", imageStream);
			return Image.getInstance(imageStream.toByteArray());
		}
		BufferedImage bufferedImage = chart.createBufferedImage(285, 210);
		ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", imageStream);
		return Image.getInstance(imageStream.toByteArray());

	}

	public PdfPTable barConstentsRelation(ClaimAnayalisReportDto generateClaimAnalysisReportPdf) {

		PdfPTable table = new PdfPTable(1);
		PdfPCell cell1 = createCenteredCell("Relation wise analysis", boldFont, null);
		cell1.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell1);

		PdfPCell cell13 = createCenteredCell(" ", boldFont, null);
		cell13.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell13);

		for (RelationWiseClaimReport relation : generateClaimAnalysisReportPdf.getRelationWiseClaimReport()) {

			PdfPCell cell5 = createCenteredCell(relation.getRelation(), boldFont, null);
			cell5.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell5);

			PdfPCell cell19 = createCenteredCell(" ", boldFont, null);
			cell19.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell19);

		}

		return table;
	}

	public PdfPTable barConstantsAge(ClaimAnayalisReportDto generateClaimAnalysisReportPdf) {
		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100);
		PdfPCell cell1 = createCenteredCell("Age   wise  analysis", boldFont, null);
		cell1.setBorder(Rectangle.NO_BORDER);

		table.addCell(cell1);

		PdfPCell cell22 = createCenteredCell(" ", boldFont, null);
		cell22.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell22);

		PdfPCell cell5 = createCenteredCell("0-10", boldFont, null);
		cell5.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell5);

		PdfPCell cell11 = createCenteredCell(" ", boldFont, null);
		cell11.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell11);

		PdfPCell cell10 = createCenteredCell("11-20", boldFont, null);
		cell10.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell10);

		PdfPCell cell8 = createCenteredCell(" ", boldFont, null);
		cell8.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell8);

		PdfPCell cell9 = createCenteredCell(" 21-30", boldFont, null);
		cell9.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell9);

		PdfPCell cell18 = createCenteredCell(" ", boldFont, null);
		cell18.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell18);

		PdfPCell cell15 = createCenteredCell("31-40", boldFont, null);
		cell15.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell15);

		PdfPCell cell16 = createCenteredCell(" ", boldFont, null);
		cell16.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell16);

		PdfPCell cell17 = createCenteredCell("41-50", boldFont, null);
		cell17.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell17);

		PdfPCell cell19 = createCenteredCell(" ", boldFont, null);
		cell19.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell19);

		PdfPCell cell20 = createCenteredCell(" 51-60", boldFont, null);
		cell20.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell20);

		PdfPCell cell112 = createCenteredCell(" ", boldFont, null);
		cell112.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell112);

		PdfPCell cell23 = createCenteredCell("61-70", boldFont, null);
		cell23.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell23);

		PdfPCell cell25 = createCenteredCell(" ", boldFont, null);
		cell19.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell19);

		PdfPCell cell28 = createCenteredCell(">70", boldFont, null);
		cell28.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell28);

		return table;

	}

	public PdfPTable barChartConstentsClaim(String rfqId) {
		PdfPTable table = new PdfPTable(1);

		PdfPCell cell13 = createCenteredCell(" ", boldFont, null);
		cell13.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell13);

		PdfPCell cell14 = createCenteredCell(" ", boldFont, null);
		cell14.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell14);

		PdfPCell cell15 = createCenteredCell(" ", boldFont, null);
		cell15.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell15);
		Set<String> allClaimType = analysisReportRepository.getAllClaimType(rfqId);
		for (String type : allClaimType) {
			PdfPCell cell1 = createCenteredCell(type, boldFont, null);
			cell1.setBorder(Rectangle.NO_BORDER);

			table.addCell(cell1);

			PdfPCell cell12 = createCenteredCell(" ", boldFont, null);
			cell12.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell12);

			PdfPCell cell16 = createCenteredCell(" ", boldFont, null);
			cell16.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell16);

		}

		return table;
	}

	public Image createGraphStatusCount(ClaimAnayalisReportDto generateClaimAnalysisReportPdf)
			throws IOException, BadElementException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		List<IncurredCliamRatio> incurredCliamRatio = generateClaimAnalysisReportPdf.getIncurredCliamRatio();

		for (IncurredCliamRatio claim : incurredCliamRatio) {
			dataset.addValue(claim.getCount(), claim.getStatus(), "");

		}

		// Add more data as needed

		JFreeChart chart = ChartFactory.createBarChart("", "", "Count", dataset, PlotOrientation.HORIZONTAL, false,
				false, true);
		chart.setBorderVisible(false);

		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		ValueAxis rangeAxis = plot.getRangeAxis();
		CategoryPlot plot1 = chart.getCategoryPlot();
		plot.setOutlineVisible(false);

		plot1.setBackgroundPaint(Color.WHITE); // Set background color of the plot area
		plot1.setRangeGridlinePaint(Color.WHITE); // Set color of gridlines
		// Customize bar colors
		BarRenderer renderer = (BarRenderer) plot1.getRenderer();
		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setDefaultItemLabelsVisible(true);
		renderer.setItemMargin(0.1);
		for (int i = 0; i < generateClaimAnalysisReportPdf.getClaimTypeAnalysis().size(); i++) {
			renderer.setSeriesPaint(i, new Color(16, 80, 153)); // Set color of the "Main Member" bars
		}
		renderer.setMaximumBarWidth(0.08d);

		domainAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		domainAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelsVisible(false);
		domainAxis.setAxisLineVisible(false);
		rangeAxis.setAxisLineVisible(false);

		for (int i = 0; i <= dataset.getColumnCount(); i++) {

			renderer.setItemLabelAnchorOffset(10.0);

			renderer.setItemLabelAnchorOffset(5.0);

			renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(

					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER,

					TextAnchor.BASELINE_CENTER, 0.0

			));
		}

		BufferedImage bufferedImage = chart.createBufferedImage(285, 130);
		ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", imageStream);

		return Image.getInstance(imageStream.toByteArray());
	}

	public Image createGraphStatusAmount(ClaimAnayalisReportDto generateClaimAnalysisReportPdf)
			throws IOException, BadElementException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		List<IncurredCliamRatio> incurredCliamRatio = generateClaimAnalysisReportPdf.getIncurredCliamRatio();

		for (IncurredCliamRatio claim : incurredCliamRatio) {
			dataset.addValue(claim.getAmount(), claim.getStatus(), "");

		}

		// Add more data as needed

		JFreeChart chart = ChartFactory.createBarChart("", "", "Count", dataset, PlotOrientation.HORIZONTAL, false,
				false, true);
		chart.setBorderVisible(false);

		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		ValueAxis rangeAxis = plot.getRangeAxis();
		CategoryPlot plot1 = chart.getCategoryPlot();
		plot.setOutlineVisible(false);

		plot1.setBackgroundPaint(Color.WHITE); // Set background color of the plot area
		plot1.setRangeGridlinePaint(Color.WHITE); // Set color of gridlines
		// Customize bar colors
		BarRenderer renderer = (BarRenderer) plot1.getRenderer();
		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setDefaultItemLabelsVisible(true);
		renderer.setItemMargin(0.1);

		for (int i = 0; i < generateClaimAnalysisReportPdf.getClaimTypeAnalysis().size(); i++) {
			renderer.setSeriesPaint(i, new Color(0, 102, 0));
		}
		renderer.setMaximumBarWidth(0.08d);

		domainAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		domainAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setLabelFont(DEFAULT_LABEL_FONT);
		rangeAxis.setTickLabelsVisible(false);
		domainAxis.setAxisLineVisible(false);
		rangeAxis.setAxisLineVisible(false);

		for (int i = 0; i <= dataset.getColumnCount(); i++) {

			renderer.setItemLabelAnchorOffset(10.0);

			renderer.setItemLabelAnchorOffset(5.0);

			renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(

					ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER,

					TextAnchor.BASELINE_CENTER, 0.0

			));
		}

		BufferedImage bufferedImage = chart.createBufferedImage(285, 130);
		ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", imageStream);

		return Image.getInstance(imageStream.toByteArray());
	}

	public PdfPTable baseConatents(String rfqId) {

		Set<String> allStatus;
		allStatus = analysisReportRepository.getAllStatus(rfqId);
		allStatus.remove(null);
		allStatus.remove("");
		if (allStatus.size() == 0) {

			allStatus.remove(null);
			allStatus.remove("");

		}
		PdfPTable table = new PdfPTable(1);
		PdfPCell cell13 = createCenteredCell(" ", boldFont, null);
		cell13.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell13);

		PdfPCell cell14 = createCenteredCell(" ", boldFont, null);
		cell14.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell14);

		PdfPCell cell15 = createCenteredCell(" ", boldFont, null);
		cell15.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell15);

		for (String id : allStatus) {
			PdfPCell cell133 = createCenteredCell(" ", boldFont, null);
			cell133.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell133);
			PdfPCell cell1 = createCenteredCell(id, boldFont, null);
			cell1.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell1);

			PdfPCell cell12 = createCenteredCell(" ", boldFont, null);
			cell12.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell12);

			PdfPCell cell22 = createCenteredCell(" ", boldFont, null);
			cell22.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell22);
		}

		return table;

	}

}