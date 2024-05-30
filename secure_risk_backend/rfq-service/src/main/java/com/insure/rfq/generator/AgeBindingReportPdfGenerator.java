package com.insure.rfq.generator;

import com.insure.rfq.dto.AgeBindingRatioAnalysisDto;
import com.insure.rfq.dto.AgeBindingSummaryDto;
import com.insure.rfq.dto.CoverageDetailsDto;
import com.insure.rfq.entity.EmployeeDepedentDetailsEntity;
import com.insure.rfq.repository.EmpDependentRepository;
import com.insure.rfq.repository.EmployeeRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AgeBindingReportPdfGenerator {
    @Autowired
    private EmpDependentRepository dependentRepository;

    @Autowired
    private AgeBindingData ageBindingData;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Value("classpath:configfile/logo.png")
    Resource resource;
    private static final Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

    public byte[] generatePdf(CoverageDetailsDto coverageDetails) throws IOException, DocumentException {

        final int count = 1;
        boolean onePlusThree = false;
        boolean onePlusFive = false;
        int pageCount = 1;
        int ratioCountForOnePlusThree = 1;
        int ratioCountForOnePlusFive = 1;
        int summaryCountOnePlusThree = 1;
        int summaryCountOnePlusFive = 1;
        int maternityCountOnePlusThree = 1;
        int maternityCountOnePlusFive = 1;

        final String id = coverageDetails.getRfqId();
        List<EmployeeDepedentDetailsEntity> golbalEmployeeDetailsBasedOnRfqId = dependentRepository.findByrfqId(id);

        long totalEmployeesCount = golbalEmployeeDetailsBasedOnRfqId.stream()
                .map(EmployeeDepedentDetailsEntity::getEmployeeId).distinct().count();
        log.info("Total Employee Count :{}", totalEmployeesCount);

        // Group dependent details by sumInsured
        Map<Double, List<EmployeeDepedentDetailsEntity>> groupedBySumInsured = golbalEmployeeDetailsBasedOnRfqId
                .stream().collect(Collectors.groupingBy(EmployeeDepedentDetailsEntity::getSumInsured));
        Document document = new Document(PageSize.A3);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);

        document.open();

        // Add logo image at the beginning of the document
        InputStream imageStream = resource.getInputStream();
        byte[] imageBytes = imageStream.readAllBytes();
        Image logoImage = Image.getInstance(imageBytes);
        logoImage.scaleToFit(200, 100); // Adjust width and height as needed
        logoImage.setAlignment(Element.ALIGN_LEFT);
        document.add(logoImage);


        Font boldBlueFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLUE);
        Font boldBlueFonts = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(64, 64, 64));
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

        // Main heading
        // Add a new page based on familyDefication13
        int alignCenter = Paragraph.ALIGN_CENTER;
        String child = "Child";
        String children = "Children";
        if (coverageDetails.isFamilyDefication13()) {
            int count1 = 0;
            for (Map.Entry<Double, List<EmployeeDepedentDetailsEntity>> entry : groupedBySumInsured.entrySet()) {
                Double sumInsuredValue = entry.getKey();
                List<EmployeeDepedentDetailsEntity> sumInsuredGroup = entry.getValue();
                Paragraph pageHeading2 = new Paragraph(" ");
                pageHeading2.setAlignment(alignCenter);
                document.add(pageHeading2);
                if (isOnePlusThree(sumInsuredGroup, id, golbalEmployeeDetailsBasedOnRfqId)) {
                    onePlusThree = isOnePlusThree(sumInsuredGroup, id, golbalEmployeeDetailsBasedOnRfqId);
                    count1++;
                    if (pageCount % 4 == 0) {
                        document.newPage();
                    }

                    Paragraph pageHeading = new Paragraph("Family Defination 1+3", boldBlueFont);
                    pageHeading.setAlignment(alignCenter);
                    document.add(pageHeading);
                    Paragraph mainHeading = new Paragraph("RFQ ANALYSIS", boldBlueFont);
                    mainHeading.setAlignment(alignCenter);
                    document.add(mainHeading);
                    Paragraph pageHeading1 = new Paragraph(" ");
                    pageHeading1.setAlignment(alignCenter);
                    document.add(pageHeading1);
                    // Add a table for familyDefication13Amount
                    PdfPTable table1 = new PdfPTable(2);
                    table1.setWidthPercentage(100);
                    table1.setWidthPercentage(100);
                    table1.addCell(createCenteredCell("AnalysisName"));
                    table1.addCell(createCenteredCell("Count"));
                    document.add(table1);
                    log.info("No of Tables Generated for 1+3 = {}", pageCount);
                    pageCount++;

                    Paragraph mainHeading1 = new Paragraph(" Sum Insured : " + sumInsuredValue + " :: 1+3",
                            boldBlueFonts);
                    mainHeading1.setAlignment(alignCenter);
                    document.add(mainHeading1);
                    Paragraph pageHeading22 = new Paragraph(" ");
                    pageHeading22.setAlignment(alignCenter);
                    document.add(pageHeading22);
                    PdfPTable table2 = new PdfPTable(10);

                    table2.setWidthPercentage(100);
                    table2.addCell(createCenteredCell("Age"));
                    table2.addCell(createCenteredCell("0-25"));
                    table2.addCell(createCenteredCell("26-35"));
                    table2.addCell(createCenteredCell("36-45"));
                    table2.addCell(createCenteredCell("46-55"));
                    table2.addCell(createCenteredCell("56-65"));
                    table2.addCell(createCenteredCell("66-75"));
                    table2.addCell(createCenteredCell("75-85"));
                    table2.addCell(createCenteredCell(">85 "));
                    table2.addCell(createCenteredCell("Total"));

                    document.add(table2);

                    // Add a new page for the Total

                    // Table for Total

                    PdfPTable table3 = new PdfPTable(10);
                    table3.setWidthPercentage(100);

                    int countOfEmployeesByAgeRangeBetween0_25 = ageBindingData.getCountOfDataOfOnePlusThree(id, "Emp",
                            0, 25, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "self", 0, 25.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId);

                    int countOfEmployeesByAgeRangeBetween26to35 = ageBindingData.getCountOfDataOfOnePlusThree(id, "Emp",
                            25, 35, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "self", 25.500000000001, 35.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);

                    int countOfEmployeesByAgeRangeBetween36_45 = ageBindingData.getCountOfDataOfOnePlusThree(id, "Emp",
                            35, 45, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "self", 35.500000000001, 45.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);

                    int countOfEmployeesByAgeRangeBetween46_55 = ageBindingData.getCountOfDataOfOnePlusThree(id, "Emp",
                            45, 55, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "self", 45.50000000001, 55.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);

                    int countOfEmployeesByAgeRangeBetween56_65 = ageBindingData.getCountOfDataOfOnePlusThree(id, "Emp",
                            55, 65, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "self", 55.501, 65.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId);

                    int countOfEmployeesByAgeRangeBetween66_75 = ageBindingData.getCountOfDataOfOnePlusThree(id, "Emp",
                            65, 75, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "self", 65.500000000001, 75.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);

                    int countOfEmployeesByAgeRangeBetween76_85 = ageBindingData.getCountOfDataOfOnePlusThree(id, "Emp",
                            75, 85, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "self", 75.500000000001, 85.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);

                    int countOfEmployeesByAgeRangeBetweenmoreThan85 = ageBindingData.getCountOfDataOfOnePlusThree(id,
                            "Emp", 85, 150, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "self", 85.500000000001, 150,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int total_self = countOfEmployeesByAgeRangeBetween0_25 + countOfEmployeesByAgeRangeBetween26to35
                            + countOfEmployeesByAgeRangeBetween36_45 + countOfEmployeesByAgeRangeBetween46_55
                            + countOfEmployeesByAgeRangeBetween56_65 + countOfEmployeesByAgeRangeBetween66_75
                            + countOfEmployeesByAgeRangeBetween76_85 + countOfEmployeesByAgeRangeBetweenmoreThan85;
                    table3.addCell(createCenteredCell("Self"));
                    table3.addCell(createCell(countOfEmployeesByAgeRangeBetween0_25 + ""));
                    table3.addCell(createCell(countOfEmployeesByAgeRangeBetween26to35 + ""));
                    table3.addCell(createCell(countOfEmployeesByAgeRangeBetween36_45 + ""));
                    table3.addCell(createCell(countOfEmployeesByAgeRangeBetween46_55 + ""));
                    table3.addCell(createCell(countOfEmployeesByAgeRangeBetween56_65 + ""));
                    table3.addCell(createCell(countOfEmployeesByAgeRangeBetween66_75 + ""));
                    table3.addCell(createCell(countOfEmployeesByAgeRangeBetween76_85 + ""));
                    table3.addCell(createCell(countOfEmployeesByAgeRangeBetweenmoreThan85 + ""));
                    table3.addCell(createCell(total_self + ""));

                    // for spouse
                    int countOfSpouseByAgeRangeBetween0_25 = ageBindingData.getCountOfDataOfOnePlusThree(id, "Spouse",
                            0, 25.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "wife", 0, 25.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "husband", 0, 25.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId);
                    int countOfSpouseByAgeRangeBetween26_35 = ageBindingData.getCountOfDataOfOnePlusThree(id, "Spouse",
                            25.500000000001, 35.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "wife", 25.500000000001, 35.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "husband", 25.500000000001, 35.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfSpouseByAgeRangeBetween36_45 = ageBindingData.getCountOfDataOfOnePlusThree(id, "Spouse",
                            35.500000000001, 45.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "wife", 35.500000000001, 45.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "husband", 35.500000000001, 45.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfSpouseByAgeRangeBetween46_55 = ageBindingData.getCountOfDataOfOnePlusThree(id, "Spouse",
                            45.500000000001, 55.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "wife", 45.500000000001, 55.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "husband", 45.500000000001, 55.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfSpouseByAgeRangeBetween56_65 = ageBindingData.getCountOfDataOfOnePlusThree(id, "Spouse",
                            55.500000000001, 65.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "wife", 55.500000000001, 65.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "husband", 55.500000000001, 65.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfSpouseByAgeRangeBetween66_75 = ageBindingData.getCountOfDataOfOnePlusThree(id, "Spouse",
                            65.500000000001, 75.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "wife", 65.500000000001, 75.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "husband", 65.500000000001, 75.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfSpouseByAgeRangeBetween76_85 = ageBindingData.getCountOfDataOfOnePlusThree(id, "Spouse",
                            75.500000000001, 85.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "wife", 75.500000000001, 85.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "husband", 75.500000000001, 85.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfSpouseByAgeRangeBetweenMoreThan85 = ageBindingData.getCountOfDataOfOnePlusThree(id,
                            "Spouse", 85.500000000001, 150, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "wife", 85.500000000001, 150,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "husband", 85.500000000001, 150,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int total_Spouse = countOfSpouseByAgeRangeBetween0_25 + countOfSpouseByAgeRangeBetween26_35
                            + countOfSpouseByAgeRangeBetween36_45 + countOfSpouseByAgeRangeBetween46_55
                            + countOfSpouseByAgeRangeBetween56_65 + countOfSpouseByAgeRangeBetween66_75
                            + countOfSpouseByAgeRangeBetween76_85 + countOfSpouseByAgeRangeBetweenMoreThan85;
                    document.add(table3);
                    PdfPTable table4 = new PdfPTable(10);
                    table4.setWidthPercentage(100);
                    table4.addCell(createCenteredCell("Spouse"));
                    table4.addCell(createCell(countOfSpouseByAgeRangeBetween0_25 + ""));
                    table4.addCell(createCell(countOfSpouseByAgeRangeBetween26_35 + ""));
                    table4.addCell(createCell(countOfSpouseByAgeRangeBetween36_45 + ""));
                    table4.addCell(createCell(countOfSpouseByAgeRangeBetween46_55 + ""));
                    table4.addCell(createCell(countOfSpouseByAgeRangeBetween56_65 + ""));
                    table4.addCell(createCell(countOfSpouseByAgeRangeBetween66_75 + ""));
                    table4.addCell(createCell(countOfSpouseByAgeRangeBetween76_85 + ""));
                    table4.addCell(createCell(countOfSpouseByAgeRangeBetweenMoreThan85 + ""));
                    table4.addCell(createCell(total_Spouse + ""));
                    document.add(table4);
                    int countOfChildByAgeRangeBetween0_25 = ageBindingData.getCountOfDataOfOnePlusThree(id, child, 0,
                            25.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "son", 0, 25.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "daughter", 0, 25.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId);
                    int countOfChildByAgeRangeBetween26_35 = ageBindingData.getCountOfDataOfOnePlusThree(id, child,
                            25.500000000001, 35.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "son", 25.500000000001, 35.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "daughter", 25.500000000001, 35.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfChildByAgeRangeBetween36_45 = ageBindingData.getCountOfDataOfOnePlusThree(id, child,
                            35.500000000001, 45.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "son", 35.500000000001, 45.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "daughter", 35.500000000001, 45.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfChildByAgeRangeBetween46_55 = ageBindingData.getCountOfDataOfOnePlusThree(id, child,
                            45.500000000001, 55.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "son", 45.500000000001, 55.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "daughter", 45.500000000001, 55.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfChildByAgeRangeBetween56_65 = ageBindingData.getCountOfDataOfOnePlusThree(id, child,
                            55.500000000001, 65.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "son", 55.500000000001, 65.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "daughter", 55.500000000001, 65.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfChildByAgeRangeBetween66_75 = ageBindingData.getCountOfDataOfOnePlusThree(id, child,
                            65.500000000001, 75.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "son", 65.500000000001, 75.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "daughter", 65.500000000001, 75.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfChildByAgeRangeBetween76_85 = ageBindingData.getCountOfDataOfOnePlusThree(id, child,
                            75.500000000001, 85.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "son", 75.500000000001, 85.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "daughter", 75.500000000001, 85.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfChildByAgeRangeBetweenMoreThan85 = ageBindingData.getCountOfDataOfOnePlusThree(id,
                            child, 85.500000000001, 150, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "son", 85.500000000001, 150,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusThree(id, "daughter", 85.500000000001, 150,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int total_Child = countOfChildByAgeRangeBetween0_25 + countOfChildByAgeRangeBetween26_35
                            + countOfChildByAgeRangeBetween36_45 + countOfChildByAgeRangeBetween46_55
                            + countOfChildByAgeRangeBetween56_65 + countOfChildByAgeRangeBetween66_75
                            + countOfChildByAgeRangeBetween76_85 + countOfChildByAgeRangeBetweenMoreThan85;

                    PdfPTable table5 = new PdfPTable(10);
                    table5.setWidthPercentage(100);
                    table5.addCell(createCenteredCell(children));
                    table5.addCell(createCell(countOfChildByAgeRangeBetween0_25 + ""));
                    table5.addCell(createCell(countOfChildByAgeRangeBetween26_35 + ""));
                    table5.addCell(createCell(countOfChildByAgeRangeBetween36_45 + ""));
                    table5.addCell(createCell(countOfChildByAgeRangeBetween46_55 + ""));
                    table5.addCell(createCell(countOfChildByAgeRangeBetween56_65 + ""));
                    table5.addCell(createCell(countOfChildByAgeRangeBetween66_75 + ""));
                    table5.addCell(createCell(countOfChildByAgeRangeBetween76_85 + ""));
                    table5.addCell(createCell(countOfChildByAgeRangeBetweenMoreThan85 + ""));
                    table5.addCell(createCell(total_Child + ""));
                    document.add(table5);
                    int total_0_25 = countOfEmployeesByAgeRangeBetween0_25 + countOfSpouseByAgeRangeBetween0_25
                            + countOfChildByAgeRangeBetween0_25;
                    int total_26_35 = countOfEmployeesByAgeRangeBetween26to35 + countOfSpouseByAgeRangeBetween26_35
                            + countOfChildByAgeRangeBetween26_35;
                    int total_36_45 = countOfEmployeesByAgeRangeBetween36_45 + countOfSpouseByAgeRangeBetween36_45
                            + countOfChildByAgeRangeBetween36_45;
                    int total_46_55 = countOfEmployeesByAgeRangeBetween46_55 + countOfSpouseByAgeRangeBetween46_55
                            + countOfChildByAgeRangeBetween46_55;
                    int total_56_65 = countOfEmployeesByAgeRangeBetween56_65 + countOfSpouseByAgeRangeBetween56_65
                            + countOfChildByAgeRangeBetween56_65;
                    int total_66_75 = countOfEmployeesByAgeRangeBetween66_75 + countOfSpouseByAgeRangeBetween66_75
                            + countOfChildByAgeRangeBetween66_75;
                    int total_76_85 = countOfEmployeesByAgeRangeBetween76_85 + countOfSpouseByAgeRangeBetween76_85
                            + countOfChildByAgeRangeBetween76_85;
                    int totalMoreThan85 = countOfChildByAgeRangeBetweenMoreThan85
                            + countOfEmployeesByAgeRangeBetweenmoreThan85 + countOfSpouseByAgeRangeBetweenMoreThan85;

                    int total = total_0_25 + total_26_35 + total_36_45 + total_46_55 + total_56_65 + total_66_75
                            + total_76_85 + totalMoreThan85;

                    PdfPTable table6 = new PdfPTable(10);
                    table6.setWidthPercentage(100);
                    table6.addCell(createCenteredCell("Total"));
                    table6.addCell(createCell(total_0_25 + ""));
                    table6.addCell(createCell(total_26_35 + ""));
                    table6.addCell(createCell(total_36_45 + ""));
                    table6.addCell(createCell(total_46_55 + ""));
                    table6.addCell(createCell(total_56_65 + ""));
                    table6.addCell(createCell(total_66_75 + ""));
                    table6.addCell(createCell(total_76_85 + ""));
                    table6.addCell(createCell(totalMoreThan85 + ""));
                    table6.addCell(createCell(total + ""));

                    document.add(table6);

                }
            }
        }

        // Add a table for familyDefication15Amount
        if (coverageDetails.isFamilyDefication15()) {
            int count2 = 0;
            for (Map.Entry<Double, List<EmployeeDepedentDetailsEntity>> entry : groupedBySumInsured.entrySet()) {
                Double sumInsuredValue = entry.getKey();
                List<EmployeeDepedentDetailsEntity> sumInsuredGroup = entry.getValue();
                Paragraph pageHeading2 = new Paragraph(" ");
                pageHeading2.setAlignment(alignCenter);
                document.add(pageHeading2);
                if (isOnePlusFive(sumInsuredGroup, id, golbalEmployeeDetailsBasedOnRfqId)) {
                    onePlusFive = isOnePlusFive(sumInsuredGroup, id, golbalEmployeeDetailsBasedOnRfqId);
                    count2++;
                    if (coverageDetails.isFamilyDefication13() && onePlusThree) {
                        if (pageCount % 4 == 0) {
                            document.newPage();
                        }
                    }
                    Paragraph pageHeading222 = new Paragraph("Family Defination 1+5", boldBlueFont);
                    pageHeading222.setAlignment(alignCenter);
                    document.add(pageHeading222);
                    Paragraph mainHeading222 = new Paragraph("RFQ ANALYSIS", boldBlueFont);
                    mainHeading222.setAlignment(alignCenter);
                    document.add(mainHeading222);
                    Paragraph pageHeading122 = new Paragraph(" ");
                    pageHeading122.setAlignment(alignCenter);
                    document.add(pageHeading122);
                    PdfPTable table122 = new PdfPTable(2);
                    table122.setWidthPercentage(100);
                    table122.setWidthPercentage(100);
                    table122.addCell(createCenteredCell("AnalysisName"));
                    table122.addCell(createCenteredCell("Count"));
                    document.add(table122);
                    log.info("No of Tables Generated for 1+5 = {}", pageCount);
                    pageCount++;


                    Paragraph mainHeading1 = new Paragraph("Sum Insured : " + sumInsuredValue + " :: 1+5",
                            boldBlueFonts);
                    mainHeading1.setAlignment(alignCenter);
                    mainHeading1.setFont(font);
                    document.add(mainHeading1);
                    Paragraph pageHeading4 = new Paragraph(" ");
                    pageHeading4.setAlignment(alignCenter);
                    document.add(pageHeading4);

                    PdfPTable table21 = new PdfPTable(10);
                    table21.setWidthPercentage(100);
                    table21.addCell(createCenteredCell("Age"));
                    table21.addCell(createCenteredCell("0-25"));
                    table21.addCell(createCenteredCell("26-35"));
                    table21.addCell(createCenteredCell("36-45"));
                    table21.addCell(createCenteredCell("46-55"));
                    table21.addCell(createCenteredCell("56-65"));
                    table21.addCell(createCenteredCell("66-75"));
                    table21.addCell(createCenteredCell("75-85"));
                    table21.addCell(createCenteredCell(">85"));
                    table21.addCell(createCenteredCell("Total"));
                    document.add(table21);

                    PdfPTable table3 = new PdfPTable(10);
                    table3.setWidthPercentage(100);
                    int countOfEmployeesByAgeRangeBetween0_25 = ageBindingData.getCountOfData(id, "Emp", 0, 25.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfData(id, "self", 0, 25.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId);
                    int countOfEmployeesByAgeRangeBetween26to35 = ageBindingData.getCountOfData(id, "Emp",
                            25.500000000001, 35.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfData(id, "self", 25.500000000001, 35.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId);
                    int countOfEmployeesByAgeRangeBetween36_45 = ageBindingData.getCountOfData(id, "Emp",
                            35.500000000001, 45.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfData(id, "self", 35.500000000001, 45.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId);
                    int countOfEmployeesByAgeRangeBetween46_55 = ageBindingData.getCountOfData(id, "Emp",
                            45.500000000001, 55.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfData(id, "self", 45.500000000001, 55.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId);
                    int countOfEmployeesByAgeRangeBetween56_65 = ageBindingData.getCountOfData(id, "Emp",
                            55.500000000001, 65.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfData(id, "self", 55.500000000001, 65.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId);
                    int countOfEmployeesByAgeRangeBetween66_75 = ageBindingData.getCountOfData(id, "Emp",
                            65.500000000001, 75.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfData(id, "self", 65.500000000001, 75.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId);
                    int countOfEmployeesByAgeRangeBetween76_85 = ageBindingData.getCountOfData(id, "Emp",
                            75.500000000001, 85.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfData(id, "self", 75.500000000001, 85.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId);
                    int countOfEmployeesByAgeRangeBetweenMoreThan85 = ageBindingData.getCountOfData(id, "Emp",
                            85.500000000001, 150, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfData(id, "self", 85.500000000001, 150, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId);
                    int total_self = countOfEmployeesByAgeRangeBetween0_25 + countOfEmployeesByAgeRangeBetween26to35
                            + countOfEmployeesByAgeRangeBetween36_45 + countOfEmployeesByAgeRangeBetween46_55
                            + countOfEmployeesByAgeRangeBetween56_65 + countOfEmployeesByAgeRangeBetween66_75
                            + countOfEmployeesByAgeRangeBetween76_85 + countOfEmployeesByAgeRangeBetweenMoreThan85;

                    table3.addCell(createCenteredCell("Self"));

                    table3.addCell(createCell(countOfEmployeesByAgeRangeBetween0_25 + ""));
                    table3.addCell(createCell(countOfEmployeesByAgeRangeBetween26to35 + ""));
                    table3.addCell(createCell(countOfEmployeesByAgeRangeBetween36_45 + ""));
                    table3.addCell(createCell(countOfEmployeesByAgeRangeBetween46_55 + ""));
                    table3.addCell(createCell(countOfEmployeesByAgeRangeBetween56_65 + ""));
                    table3.addCell(createCell(countOfEmployeesByAgeRangeBetween66_75 + ""));
                    table3.addCell(createCell(countOfEmployeesByAgeRangeBetween76_85 + ""));
                    table3.addCell(createCell(countOfEmployeesByAgeRangeBetweenMoreThan85 + ""));
                    table3.addCell(createCell(total_self + ""));

                    document.add(table3);

                    int countOfSpouseByAgeRangeBetween0_25_1_5 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            "Spouse", 0, 25.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "wife", 0, 25.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "husband", 0, 25.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId);
                    int countOfSpouseByAgeRangeBetween26_35_1_5 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            "Spouse", 25.500000000001, 35.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "wife", 25.500000000001, 35.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "husband", 25.500000000001, 35.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfSpouseByAgeRangeBetween36_45_1_5 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            "Spouse", 35.500000000001, 45.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "wife", 35.500000000001, 45.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "husband", 35.500000000001, 45.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfSpouseByAgeRangeBetween46_55_1_5 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            "Spouse", 45.500000000001, 55.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "wife", 45.500000000001, 55.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "husband", 45.500000000001, 55.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfSpouseByAgeRangeBetween56_65_1_5 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            "Spouse", 55.500000000001, 65.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "wife", 55.500000000001, 65.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "husband", 55.500000000001, 65.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfSpouseByAgeRangeBetween66_75_1_5 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            "Spouse", 65.500000000001, 75.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "wife", 65.500000000001, 75.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "husband", 65.500000000001, 75.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfSpouseByAgeRangeBetween76_85_1_5 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            "Spouse", 75.500000000001, 85.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "wife", 75.500000000001, 85.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "husband", 75.500000000001, 85.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfSpouseByAgeRangeBetween86_150_1_5 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            "Spouse", 85.500000000001, 150, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "wife", 85.500000000001, 150,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "husband", 85.500000000001, 150,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int total_Spouse_1_5 = countOfSpouseByAgeRangeBetween0_25_1_5
                            + countOfSpouseByAgeRangeBetween26_35_1_5 + countOfSpouseByAgeRangeBetween36_45_1_5
                            + countOfSpouseByAgeRangeBetween46_55_1_5 + countOfSpouseByAgeRangeBetween56_65_1_5
                            + countOfSpouseByAgeRangeBetween66_75_1_5 + countOfSpouseByAgeRangeBetween76_85_1_5
                            + countOfSpouseByAgeRangeBetween86_150_1_5;

                    PdfPTable tableSpouse = new PdfPTable(10);
                    tableSpouse.setWidthPercentage(100);
                    tableSpouse.addCell(createCenteredCell("Spouse"));
                    tableSpouse.addCell(createCell(countOfSpouseByAgeRangeBetween0_25_1_5 + ""));
                    tableSpouse.addCell(createCell(countOfSpouseByAgeRangeBetween26_35_1_5 + ""));
                    tableSpouse.addCell(createCell(countOfSpouseByAgeRangeBetween36_45_1_5 + ""));
                    tableSpouse.addCell(createCell(countOfSpouseByAgeRangeBetween46_55_1_5 + ""));
                    tableSpouse.addCell(createCell(countOfSpouseByAgeRangeBetween56_65_1_5 + ""));
                    tableSpouse.addCell(createCell(countOfSpouseByAgeRangeBetween66_75_1_5 + ""));
                    tableSpouse.addCell(createCell(countOfSpouseByAgeRangeBetween76_85_1_5 + ""));
                    tableSpouse.addCell(createCell(countOfSpouseByAgeRangeBetween86_150_1_5 + ""));
                    tableSpouse.addCell(createCell(total_Spouse_1_5 + ""));
                    document.add(tableSpouse);

                    int countOfChildrenByAgeRangeBetween0_25_1_5 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            child, 0, 25.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "son", 0, 25.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "daughter", 0, 25.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId);
                    int countOfChildrenByAgeRangeBetween26_35_1_5 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            child, 25.500000000001, 35.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "son", 25.500000000001, 35.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "daughter", 25.500000000001, 35.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfChildrenByAgeRangeBetween36_45_1_5 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            child, 35.500000000001, 45.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "son", 35.500000000001, 45.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "daughter", 35.500000000001, 45.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfChildrenByAgeRangeBetween46_55_1_5 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            child, 45.500000000001500000000001, 55.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "son", 45.500000000001, 55.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "daughter", 45.500000000001, 55.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfChildrenByAgeRangeBetween56_65_1_5 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            child, 55.500000000001, 65.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "son", 55.500000000001, 65.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "daughter", 55.500000000001, 65.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfChildrenByAgeRangeBetween66_75_1_5 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            child, 65.500000000001, 75.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "son", 65.500000000001, 75.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "daughter", 65.500000000001, 75.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfChildrenByAgeRangeBetween76_85_1_5 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            child, 75.500000000001, 85.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "son", 75.500000000001, 85.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "daughter", 75.500000000001, 85.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfChildrenByAgeRangeBetween86_150_1_5 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            child, 85.500000000001, 150, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "son", 85.500000000001, 150,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "daughter", 85.500000000001, 150,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int total_Children_1_5 = countOfChildrenByAgeRangeBetween0_25_1_5
                            + countOfChildrenByAgeRangeBetween26_35_1_5 + countOfChildrenByAgeRangeBetween36_45_1_5
                            + countOfChildrenByAgeRangeBetween46_55_1_5 + countOfChildrenByAgeRangeBetween56_65_1_5
                            + countOfChildrenByAgeRangeBetween66_75_1_5 + countOfChildrenByAgeRangeBetween76_85_1_5
                            + countOfChildrenByAgeRangeBetween86_150_1_5;

                    PdfPTable table5 = new PdfPTable(10);
                    table5.setWidthPercentage(100);
                    table5.addCell(createCenteredCell(children));
                    table5.addCell(createCell(countOfChildrenByAgeRangeBetween0_25_1_5 + ""));
                    table5.addCell(createCell(countOfChildrenByAgeRangeBetween26_35_1_5 + ""));
                    table5.addCell(createCell(countOfChildrenByAgeRangeBetween36_45_1_5 + ""));
                    table5.addCell(createCell(countOfChildrenByAgeRangeBetween46_55_1_5 + ""));
                    table5.addCell(createCell(countOfChildrenByAgeRangeBetween56_65_1_5 + ""));
                    table5.addCell(createCell(countOfChildrenByAgeRangeBetween66_75_1_5 + ""));
                    table5.addCell(createCell(countOfChildrenByAgeRangeBetween76_85_1_5 + ""));
                    table5.addCell(createCell(countOfChildrenByAgeRangeBetween86_150_1_5 + ""));
                    table5.addCell(createCell(total_Children_1_5 + ""));

                    document.add(table5);
                    int countOfEmployeesByAgeRangesParents_0_25 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            "Mother", 0, 25.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "Father", 0, 25.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "Parent", 0, 25.5, sumInsuredValue,
                            golbalEmployeeDetailsBasedOnRfqId);
                    int countOfEmployeesByAgeRangesParents_25_35 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            "Mother", 25.500000000001, 35.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "Father", 25.500000000001, 35.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "Parent", 25.500000000001, 35.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfEmployeesByAgeRangesParents_36_45 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            "Mother", 35.500000000001, 45.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "Father", 35.500000000001, 45.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "Parent", 35.500000000001, 45.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfEmployeesByAgeRangesParents_46_55 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            "Mother", 45.500000000001, 55.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "Father", 45.500000000001, 55.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "Parent", 45.500000000001, 55.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfEmployeesByAgeRangesParents_56_65 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            "Mother", 55.500000000001, 65.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "Father", 55.500000000001, 65.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "Parent", 55.500000000001, 65.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfEmployeesByAgeRangesParents_66_75 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            "Mother", 65.500000000001, 75.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "Father", 65.500000000001, 75.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "Parent", 65.500000000001, 75.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfEmployeesByAgeRangesParents_76_85 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            "Mother", 75.500000000001, 85.5, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "Father", 75.500000000001, 85.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "Parent", 75.500000000001, 85.5,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int countOfEmployeesByAgeRangesParentsmoreThan85 = ageBindingData.getCountOfDataOfOnePlusFive(id,
                            "Mother", 85.500000000001, 150, sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "Father", 85.500000000001, 150,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId)
                            + ageBindingData.getCountOfDataOfOnePlusFive(id, "Parent", 85.500000000001, 150,
                            sumInsuredValue, golbalEmployeeDetailsBasedOnRfqId);
                    int totalage_1_5 = countOfEmployeesByAgeRangesParents_0_25
                            + countOfEmployeesByAgeRangesParents_25_35 + countOfEmployeesByAgeRangesParents_36_45
                            + countOfEmployeesByAgeRangesParents_46_55 + countOfEmployeesByAgeRangesParents_56_65
                            + countOfEmployeesByAgeRangesParents_66_75 + countOfEmployeesByAgeRangesParents_76_85
                            + countOfEmployeesByAgeRangesParentsmoreThan85;
                    PdfPTable table6 = new PdfPTable(10);
                    table6.setWidthPercentage(100);
                    table6.addCell(createCenteredCell("Parents"));
                    table6.addCell(createCell(countOfEmployeesByAgeRangesParents_0_25 + ""));
                    table6.addCell(createCell(countOfEmployeesByAgeRangesParents_25_35 + ""));
                    table6.addCell(createCell(countOfEmployeesByAgeRangesParents_36_45 + ""));
                    table6.addCell(createCell(countOfEmployeesByAgeRangesParents_46_55 + ""));
                    table6.addCell(createCell(countOfEmployeesByAgeRangesParents_56_65 + ""));
                    table6.addCell(createCell(countOfEmployeesByAgeRangesParents_66_75 + ""));
                    table6.addCell(createCell(countOfEmployeesByAgeRangesParents_76_85 + ""));
                    table6.addCell(createCell(countOfEmployeesByAgeRangesParentsmoreThan85 + ""));
                    table6.addCell(createCell(totalage_1_5 + ""));

                    int total_0_25_1_5 = countOfEmployeesByAgeRangeBetween0_25 + countOfSpouseByAgeRangeBetween0_25_1_5
                            + countOfChildrenByAgeRangeBetween0_25_1_5 + countOfEmployeesByAgeRangesParents_0_25;
                    int total_26_35_1_5 = countOfEmployeesByAgeRangeBetween26to35
                            + countOfSpouseByAgeRangeBetween26_35_1_5 + countOfChildrenByAgeRangeBetween26_35_1_5
                            + countOfEmployeesByAgeRangesParents_25_35;
                    int total_36_45_1_5 = countOfEmployeesByAgeRangeBetween36_45
                            + countOfSpouseByAgeRangeBetween36_45_1_5 + countOfChildrenByAgeRangeBetween36_45_1_5
                            + countOfEmployeesByAgeRangesParents_36_45;
                    int total_46_55_1_5 = countOfEmployeesByAgeRangeBetween46_55
                            + countOfSpouseByAgeRangeBetween46_55_1_5 + countOfChildrenByAgeRangeBetween46_55_1_5
                            + countOfEmployeesByAgeRangesParents_46_55;
                    int total_56_65_1_5 = countOfEmployeesByAgeRangeBetween56_65
                            + countOfSpouseByAgeRangeBetween56_65_1_5 + countOfChildrenByAgeRangeBetween56_65_1_5
                            + countOfEmployeesByAgeRangesParents_56_65;
                    int total_66_75_1_5 = countOfEmployeesByAgeRangeBetween66_75
                            + countOfSpouseByAgeRangeBetween66_75_1_5 + countOfChildrenByAgeRangeBetween66_75_1_5
                            + countOfEmployeesByAgeRangesParents_66_75;
                    int total_76_85_1_5 = countOfEmployeesByAgeRangeBetween76_85
                            + countOfSpouseByAgeRangeBetween76_85_1_5 + countOfChildrenByAgeRangeBetween76_85_1_5
                            + countOfEmployeesByAgeRangesParents_76_85;
                    int total86T0150 = countOfChildrenByAgeRangeBetween86_150_1_5
                            + +countOfSpouseByAgeRangeBetween86_150_1_5 + countOfChildrenByAgeRangeBetween86_150_1_5
                            + countOfEmployeesByAgeRangesParentsmoreThan85;

                    int total_1_5 = total_0_25_1_5 + total_26_35_1_5 + total_36_45_1_5 + total_46_55_1_5
                            + total_56_65_1_5 + total_66_75_1_5 + total_76_85_1_5 + total86T0150;

                    document.add(table6);
                    PdfPTable table7 = new PdfPTable(10);
                    table7.setWidthPercentage(100);
                    table7.addCell(createCenteredCell("Total"));
                    table7.addCell(createCell(total_0_25_1_5 + ""));
                    table7.addCell(createCell(total_26_35_1_5 + ""));
                    table7.addCell(createCell(total_36_45_1_5 + ""));
                    table7.addCell(createCell(total_46_55_1_5 + ""));
                    table7.addCell(createCell(total_56_65_1_5 + ""));
                    table7.addCell(createCell(total_66_75_1_5 + ""));
                    table7.addCell(createCell(total_76_85_1_5 + ""));
                    table7.addCell(createCell(total86T0150 + ""));
                    table7.addCell(createCell(total_1_5 + ""));

                    document.add(table7);

                }
            }
        }



        /*
         * Ratio Analysis Table Logic
         */
        if (coverageDetails.isFamilyDefication13() || coverageDetails.isFamilyDefication15()
        ) {
            int count3 = 0;
            int count4 = 0;

            if (pageCount % 2 == 0 && ratioCountForOnePlusThree % 5 == 0 || (ratioCountForOnePlusThree % 6 == 0)
                    || (pageCount % 3 == 0 && ratioCountForOnePlusThree % 4 == 0) || ratioCountForOnePlusThree % 6 == 0
                    || pageCount % 4 == 0) {
                document.newPage();
            }

            Paragraph pageHeading = new Paragraph(" ");
            pageHeading.setAlignment(alignCenter);
            document.add(pageHeading);
            Paragraph heading = new Paragraph("Ratio Analysis", boldBlueFont);
            heading.setAlignment(alignCenter);
            document.add(heading);

            // Process Family Definition 13
            if (coverageDetails.isFamilyDefication13()
                    && isOnePlusThree(golbalEmployeeDetailsBasedOnRfqId, id, golbalEmployeeDetailsBasedOnRfqId)) {
                Paragraph pageHeading222 = new Paragraph("Family Defination 1+3", boldBlueFont);
                pageHeading222.setAlignment(alignCenter);
                document.add(pageHeading222);
                Paragraph pageHeading122 = new Paragraph(" ");
                pageHeading122.setAlignment(alignCenter);
                document.add(pageHeading122);

                for (Map.Entry<Double, List<EmployeeDepedentDetailsEntity>> entry : groupedBySumInsured.entrySet()) {
                    Double sumInsuredValue = entry.getKey();
                    List<EmployeeDepedentDetailsEntity> sumInsuredGroup = entry.getValue();

                    if (isOnePlusThree(sumInsuredGroup, id, golbalEmployeeDetailsBasedOnRfqId)) {
                        onePlusThree = isOnePlusThree(sumInsuredGroup, id, golbalEmployeeDetailsBasedOnRfqId);
                        count3++;

                        // Process header for 1+3 and sumInsuredValue
                        Paragraph mainHeading1 = new Paragraph(" Sum Insured : " + sumInsuredValue + " :: 1+3",
                                boldBlueFonts);
                        mainHeading1.setAlignment(alignCenter);
                        document.add(mainHeading1);

                        Paragraph pageHeading12 = new Paragraph(" ");
                        pageHeading12.setAlignment(alignCenter);
                        document.add(pageHeading12);

                        // Process Ratio Analysis
                        processRatioAnalysis(golbalEmployeeDetailsBasedOnRfqId, sumInsuredValue, id, document, true,
                                ageBindingData.getAllSetOfRelationOfOnePlusThree(golbalEmployeeDetailsBasedOnRfqId));
                        log.info("No of Tables Generated for 1+3 from Ratio Table = {}", ratioCountForOnePlusThree);
                        ratioCountForOnePlusThree++;
                    }
                }
            }

            if ((pageCount % 4 == 0 && ratioCountForOnePlusThree == 0) || ratioCountForOnePlusThree % 6 == 0
                    || ratioCountForOnePlusFive % 6 == 0) {
                document.newPage();
            }

            // Process Family Definition 15
            if (coverageDetails.isFamilyDefication15()
                    && isOnePlusFive(golbalEmployeeDetailsBasedOnRfqId, id, golbalEmployeeDetailsBasedOnRfqId)) {

                Paragraph pageHeading222 = new Paragraph("Family Defination 1+5", boldBlueFont);
                pageHeading222.setAlignment(alignCenter);
                document.add(pageHeading222);
                Paragraph pageHeading122 = new Paragraph(" ");
                pageHeading122.setAlignment(alignCenter);
                document.add(pageHeading122);

                for (Map.Entry<Double, List<EmployeeDepedentDetailsEntity>> entry : groupedBySumInsured.entrySet()) {
                    Double sumInsuredValue = entry.getKey();
                    List<EmployeeDepedentDetailsEntity> sumInsuredGroup = entry.getValue();

                    if (isOnePlusFive(sumInsuredGroup, id, golbalEmployeeDetailsBasedOnRfqId)) {
                        onePlusFive = isOnePlusFive(sumInsuredGroup, id, golbalEmployeeDetailsBasedOnRfqId);
                        count4++;

                        // Process header for 1+5 and sumInsuredValue
                        Paragraph mainHeading1 = new Paragraph(" Sum Insured : " + sumInsuredValue + " :: 1+5",
                                boldBlueFonts);
                        mainHeading1.setAlignment(alignCenter);
                        document.add(mainHeading1);

                        Paragraph pageHeading12 = new Paragraph(" ");
                        pageHeading12.setAlignment(alignCenter);
                        document.add(pageHeading12);

                        // Process Ratio Analysis
                        processRatioAnalysis(golbalEmployeeDetailsBasedOnRfqId, sumInsuredValue, id, document, false,
                                ageBindingData.onePlusFive(golbalEmployeeDetailsBasedOnRfqId));
                        log.info("No of Tables Generated for 1+5 from Ratio table = {}", ratioCountForOnePlusFive);
                        ratioCountForOnePlusFive++;
                    }
                }
            }
        }

        /*
         * Summary Table Logic
         */
        if (coverageDetails.isFamilyDefication13() || coverageDetails.isFamilyDefication15()
        ) {
            int count3 = 0;
            int count4 = 0;

            if ((ratioCountForOnePlusThree % 2 == 0 || ratioCountForOnePlusFive % 2 == 0)
                    && summaryCountOnePlusThree % 2 == 0 || pageCount % 3 == 0) {
                document.newPage();
            }

            Paragraph pageHeading = new Paragraph(" ");
            pageHeading.setAlignment(alignCenter);
            document.add(pageHeading);
            Paragraph heading = new Paragraph(" Summary ", boldBlueFont);
            heading.setAlignment(alignCenter);
            document.add(heading);

            // Process Family Definition 13
            if (coverageDetails.isFamilyDefication13()
                    && isOnePlusThree(golbalEmployeeDetailsBasedOnRfqId, id, golbalEmployeeDetailsBasedOnRfqId)) {
                Paragraph pageHeading222 = new Paragraph(" Family Defination 1+3 ", boldBlueFont);
                pageHeading222.setAlignment(alignCenter);
                document.add(pageHeading222);
                Paragraph pageHeading122 = new Paragraph(" ");
                pageHeading122.setAlignment(alignCenter);
                document.add(pageHeading122);

                for (Map.Entry<Double, List<EmployeeDepedentDetailsEntity>> entry : groupedBySumInsured.entrySet()) {
                    Double sumInsuredValue = entry.getKey();
                    List<EmployeeDepedentDetailsEntity> sumInsuredGroup = entry.getValue();

                    if (isOnePlusThree(sumInsuredGroup, id, golbalEmployeeDetailsBasedOnRfqId)) {
                        onePlusThree = isOnePlusThree(sumInsuredGroup, id, golbalEmployeeDetailsBasedOnRfqId);
                        count3++;

                        // Process header for 1+3 and sumInsuredValue
                        Paragraph mainHeading1 = new Paragraph(" Sum Insured : " + sumInsuredValue + " :: 1+3",
                                boldBlueFonts);
                        mainHeading1.setAlignment(alignCenter);
                        document.add(mainHeading1);

                        Paragraph pageHeading12 = new Paragraph(" ");
                        pageHeading12.setAlignment(alignCenter);
                        document.add(pageHeading12);

                        // Process Ratio Analysis
                        processRatioOnePlus3AndOnePlusFive(golbalEmployeeDetailsBasedOnRfqId, sumInsuredValue, id,
                                document, true,
                                ageBindingData.getAllSetOfRelationOfOnePlusThree(golbalEmployeeDetailsBasedOnRfqId));
                        log.info("No of Tables Generated for 1+3 from Summary table = {}", summaryCountOnePlusThree);
                        summaryCountOnePlusThree++;
                    }
                }
            }

            if ((ratioCountForOnePlusThree % 2 == 0 || ratioCountForOnePlusFive % 2 == 0)
                    && summaryCountOnePlusThree % 3 == 0 || summaryCountOnePlusFive % 3 == 0) {
                document.newPage();
            }

            // Process Family Definition 15
            if (coverageDetails.isFamilyDefication15()
                    && isOnePlusFive(golbalEmployeeDetailsBasedOnRfqId, id, golbalEmployeeDetailsBasedOnRfqId)) {
                Paragraph pageHeading222 = new Paragraph(" Family Defination 1+5 ", boldBlueFont);
                pageHeading222.setAlignment(alignCenter);
                document.add(pageHeading222);
                Paragraph pageHeading122 = new Paragraph(" ");
                pageHeading122.setAlignment(alignCenter);
                document.add(pageHeading122);

                for (Map.Entry<Double, List<EmployeeDepedentDetailsEntity>> entry : groupedBySumInsured.entrySet()) {
                    Double sumInsuredValue = entry.getKey();
                    List<EmployeeDepedentDetailsEntity> sumInsuredGroup = entry.getValue();

                    if (isOnePlusFive(sumInsuredGroup, id, golbalEmployeeDetailsBasedOnRfqId)) {
                        onePlusFive = isOnePlusFive(sumInsuredGroup, id, golbalEmployeeDetailsBasedOnRfqId);
                        count4++;

                        // Process header for 1+5 and sumInsuredValue
                        Paragraph mainHeading1 = new Paragraph(" Sum Insured : " + sumInsuredValue + " :: 1+5",
                                boldBlueFonts);
                        mainHeading1.setAlignment(alignCenter);
                        document.add(mainHeading1);

                        Paragraph pageHeading12 = new Paragraph(" ");
                        pageHeading12.setAlignment(alignCenter);
                        document.add(pageHeading12);

                        // Process Ratio Analysis
                        processRatioOnePlus3AndOnePlusFive(golbalEmployeeDetailsBasedOnRfqId, sumInsuredValue, id,
                                document, false, ageBindingData.onePlusFive(golbalEmployeeDetailsBasedOnRfqId));
                        log.info("No of Tables Generated for 1+5 from Summary table = {}", summaryCountOnePlusFive);
                        summaryCountOnePlusFive++;
                    }
                }
            }

        }
        /*
         * Maternity Table Logic
         */
        if (coverageDetails.isFamilyDefication13() || coverageDetails.isFamilyDefication15()
//				| coverageDetails.isFamilyDeficationParents()
        ) {

            if ((summaryCountOnePlusThree % 2 == 0 || summaryCountOnePlusFive % 2 == 0)
                    && maternityCountOnePlusThree % 2 == 0 || maternityCountOnePlusThree % 3 == 0) {
                document.newPage();
            }

            Paragraph pageHeading = new Paragraph(" ");
            pageHeading.setAlignment(alignCenter);
            document.add(pageHeading);
            Paragraph heading = new Paragraph(" Maternity claims highly unlikely  ", boldBlueFont);
            heading.setAlignment(alignCenter);
            document.add(heading);

            // Process Family Definition 13
            if (coverageDetails.isFamilyDefication13()
                    && isOnePlusThree(golbalEmployeeDetailsBasedOnRfqId, id, golbalEmployeeDetailsBasedOnRfqId)) {
                Paragraph pageHeading222 = new Paragraph(" Family Defination 1+3 ", boldBlueFont);
                pageHeading222.setAlignment(alignCenter);
                document.add(pageHeading222);
                Paragraph pageHeading122 = new Paragraph(" ");
                pageHeading122.setAlignment(alignCenter);
                document.add(pageHeading122);

                for (Map.Entry<Double, List<EmployeeDepedentDetailsEntity>> entry : groupedBySumInsured.entrySet()) {
                    Double sumInsuredValue = entry.getKey();
                    List<EmployeeDepedentDetailsEntity> sumInsuredGroup = entry.getValue();

                    if (isOnePlusThree(sumInsuredGroup, id, golbalEmployeeDetailsBasedOnRfqId)) {
                        onePlusThree = isOnePlusThree(sumInsuredGroup, id, golbalEmployeeDetailsBasedOnRfqId);

                        // Process header for 1+3 and sumInsuredValue
                        Paragraph mainHeading1 = new Paragraph(" Sum Insured : " + sumInsuredValue + " :: 1+3",
                                boldBlueFonts);
                        mainHeading1.setAlignment(alignCenter);
                        document.add(mainHeading1);

                        Paragraph pageHeading12 = new Paragraph(" ");
                        pageHeading12.setAlignment(alignCenter);
                        document.add(pageHeading12);

                        // Process Maternity Claims
                        processForMaternityClaims(golbalEmployeeDetailsBasedOnRfqId, sumInsuredValue, id, document,
                                true,
                                ageBindingData.getAllSetOfRelationOfOnePlusThree(golbalEmployeeDetailsBasedOnRfqId));
                        log.info("No of Tables Generated for 1+3 from Maternity table = {}",
                                maternityCountOnePlusThree);
                        maternityCountOnePlusThree++;
                    }
                }
            }

            if ((summaryCountOnePlusThree % 2 == 0 || summaryCountOnePlusFive % 2 == 0)
                    && maternityCountOnePlusThree % 2 == 0 || maternityCountOnePlusThree % 3 == 0
                    || maternityCountOnePlusFive % 3 == 0
                    || pageCount % 2 == 0 && (ratioCountForOnePlusThree % 2 == 0 || ratioCountForOnePlusFive % 2 == 0)
                    && (summaryCountOnePlusThree % 2 == 0 || summaryCountOnePlusFive % 2 == 0)) {
                document.newPage();
            }

            // Process Family Definition 15
            if (coverageDetails.isFamilyDefication15()
                    && isOnePlusFive(golbalEmployeeDetailsBasedOnRfqId, id, golbalEmployeeDetailsBasedOnRfqId)) {
                Paragraph pageHeading222 = new Paragraph(" Family Defination 1+5 ", boldBlueFont);
                pageHeading222.setAlignment(alignCenter);
                document.add(pageHeading222);
                Paragraph pageHeading122 = new Paragraph(" ");
                pageHeading122.setAlignment(alignCenter);
                document.add(pageHeading122);

                for (Map.Entry<Double, List<EmployeeDepedentDetailsEntity>> entry : groupedBySumInsured.entrySet()) {
                    Double sumInsuredValue = entry.getKey();
                    List<EmployeeDepedentDetailsEntity> sumInsuredGroup = entry.getValue();

                    if (isOnePlusFive(sumInsuredGroup, id, golbalEmployeeDetailsBasedOnRfqId)) {
                        onePlusFive = isOnePlusFive(sumInsuredGroup, id, golbalEmployeeDetailsBasedOnRfqId);

                        // Process header for 1+5 and sumInsuredValue
                        Paragraph mainHeading1 = new Paragraph(" Sum Insured : " + sumInsuredValue + " :: 1+5",
                                boldBlueFonts);
                        mainHeading1.setAlignment(alignCenter);
                        document.add(mainHeading1);

                        Paragraph pageHeading12 = new Paragraph(" ");
                        pageHeading12.setAlignment(alignCenter);
                        document.add(pageHeading12);

                        // Process Maternity Claims
                        processForMaternityClaims(golbalEmployeeDetailsBasedOnRfqId, sumInsuredValue, id, document,
                                false, ageBindingData.onePlusFive(golbalEmployeeDetailsBasedOnRfqId));
                        log.info("No of Tables Generated for 1+5 from Maternity table = {}", maternityCountOnePlusFive);
                        maternityCountOnePlusFive++;
                    }
                }
            }

        }

        if (coverageDetails.isFamilyDefication13() == false && coverageDetails.isFamilyDefication15() == false
                && coverageDetails.isFamilyDeficationParents() == false) {

            Paragraph paragraph = new Paragraph("No data Available");
            Font paragraphFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(25));
            paragraph.setFont(paragraphFont);
            paragraph.setAlignment(alignCenter);
            document.add(paragraph);
        }

        document.close();

        pdfWriter.close();

        return byteArrayOutputStream.toByteArray();

    }

    /*
     * Summary Table
     */
    private void processRatioOnePlus3AndOnePlusFive(
            List<EmployeeDepedentDetailsEntity> golbalEmployeeDetailsBasedOnRfqId, Double sumInsuredValue, String id,
            Document document, boolean isOnePlusThree, Set<String> employeeIds) throws DocumentException, IOException {

        long totalEmployeesCount = golbalEmployeeDetailsBasedOnRfqId.stream()
                .map(EmployeeDepedentDetailsEntity::getEmployeeId).distinct().count();

        // Create employee table
        PdfPTable employeeTable = new PdfPTable(3);
        employeeTable.setWidthPercentage(100);
        employeeTable.addCell(createCenteredCell("Content"));
        employeeTable.addCell(createCenteredCell("Count"));
        employeeTable.addCell(createCenteredCell("Percentage(%)"));
        document.add(employeeTable);

        // Count of Employee With 2 Kids
        int countEmployeesWithChildrenCount2 = countEmployeesWithChildrenCount(golbalEmployeeDetailsBasedOnRfqId, 0.0,
                135.0, 2, employeeIds, sumInsuredValue);
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(createCell("With 2 kids"));
        table.addCell(createCell(String.valueOf(countEmployeesWithChildrenCount2)));
        double percentage = (double) countEmployeesWithChildrenCount2 / totalEmployeesCount * 100;
        int roundedPercentage = (int) Math.round(percentage);
        String formattedPercentage = String.format("%d%%", roundedPercentage);
        table.addCell(createCell(formattedPercentage));
        document.add(table);

        // Count of Employee With 1 Kid
        int countEmployeesWithChildrenCount1 = countEmployeesWithChildrenCount(golbalEmployeeDetailsBasedOnRfqId, 0.0,
                135.0, 1, employeeIds, sumInsuredValue);
        PdfPTable table1 = new PdfPTable(3);
        table1.setWidthPercentage(100);
        table1.addCell(createCell("With 1 kid"));
        table1.addCell(createCell(String.valueOf(countEmployeesWithChildrenCount1)));
        double percentage1 = (double) countEmployeesWithChildrenCount1 / totalEmployeesCount * 100;
        int roundedPercentage1 = (int) Math.round(percentage1);
        String formattedPercentage1 = String.format("%d%%", roundedPercentage1);
        table1.addCell(createCell(formattedPercentage1));
        document.add(table1);

        // Count of Employee Who are Married or Single
        AgeBindingSummaryDto counts = getAllCountsOfEmployeesWithMartialStatus(golbalEmployeeDetailsBasedOnRfqId,
                employeeIds, sumInsuredValue, id);
        String marriedCount = counts.getMarried();
        String singleCount = counts.getSingle();

        // Count of Employee Who are married
        PdfPTable table2 = new PdfPTable(3);
        table2.setWidthPercentage(100);
        table2.addCell(createCell("Married"));
        table2.addCell(createCell(marriedCount));
        double marriedPercentage = (double) Integer.parseInt(marriedCount) / totalEmployeesCount * 100;
        int roundedMarriedPercentage = (int) Math.round(marriedPercentage);
        String formattedMarriedPercentage = String.format("%d%%", roundedMarriedPercentage);
        table2.addCell(createCell(formattedMarriedPercentage));
        document.add(table2);

        // Count of Employee Who are Single
        PdfPTable table3 = new PdfPTable(3);
        table3.setWidthPercentage(100);
        table3.addCell(createCell("Single"));
        table3.addCell(createCell(singleCount));
        double singlePercentage = (double) Integer.parseInt(singleCount) / totalEmployeesCount * 100;
        int roundedSinglePercentage = (int) Math.round(singlePercentage);
        String formattedSinglePercentage = String.format("%d%%", roundedSinglePercentage);
        table3.addCell(createCell(formattedSinglePercentage));
        document.add(table3);

        // Count of Employee Whose age is above 30
        int countEmployeesWithChildrenCount1AndAgeRangeAbove30 = countOfEmployeeWithKidsCountByAge(
                golbalEmployeeDetailsBasedOnRfqId, 30.5, 35.5, 1, employeeIds, sumInsuredValue);
        PdfPTable table4 = new PdfPTable(3);
        table4.setWidthPercentage(100);
        table4.addCell(createCell("With 1 kid Over 30"));
        table4.addCell(createCell(String.valueOf(countEmployeesWithChildrenCount1AndAgeRangeAbove30)));
        double percentage3 = (double) countEmployeesWithChildrenCount1AndAgeRangeAbove30 / totalEmployeesCount * 100;
        double roundedPercentage3 = Math.round(percentage3); // Rounding up the percentage
        int roundedPercentageInt = (int) roundedPercentage3; // Converting to int to remove decimal points
        String formattedPercentage3 = String.format("%d%%", roundedPercentageInt);
        table4.addCell(createCell(formattedPercentage3));
        document.add(table4);

        // Count of Employee Whose age is above 35
        int countEmployeesWithChildrenCount1AndAgeRangeAbove35 = countOfEmployeeWithKidsCountByAge(
                golbalEmployeeDetailsBasedOnRfqId, 35.5, 40.5, 1, employeeIds, sumInsuredValue);
        PdfPTable table5 = new PdfPTable(3);
        table5.setWidthPercentage(100);
        table5.addCell(createCell("With 1 kid Over 35"));
        table5.addCell(createCell(String.valueOf(countEmployeesWithChildrenCount1AndAgeRangeAbove35)));
        double percentage4 = (double) countEmployeesWithChildrenCount1AndAgeRangeAbove35 / totalEmployeesCount * 100;
        int roundedPercentage4 = (int) Math.round(percentage4);
//        int roundedPercentageInt1 = (int) roundedPercentage4;
        String formattedPercentage4 = String.format("%d%%", roundedPercentage4);
        table5.addCell(createCell(formattedPercentage4));
        document.add(table5);

        // Count of Employee Whose age is above 40
        int countEmployeesWithChildrenCount1AndAgeRangeAbove40 = countOfEmployeeWithKidsCountByAge(
                golbalEmployeeDetailsBasedOnRfqId, 40.5, 45.5, 1, employeeIds, sumInsuredValue);
        PdfPTable table6 = new PdfPTable(3);
        table6.setWidthPercentage(100);
        table6.addCell(createCell("With 1 kid Over 40"));
        table6.addCell(createCell(String.valueOf(countEmployeesWithChildrenCount1AndAgeRangeAbove40)));
        double percentage5 = (double) countEmployeesWithChildrenCount1AndAgeRangeAbove40 / totalEmployeesCount * 100;
        int roundedPercentage5 = (int) Math.round(percentage5);
        String formattedPercentage5 = String.format("%d%%", roundedPercentage5);
        table6.addCell(createCell(formattedPercentage5));
        document.add(table6);

        // Count of Employee Whose age is above 45
        int countEmployeesWithChildrenCount1AndAgeRangeAbove45 = countOfEmployeeWithKidsCountByAge(
                golbalEmployeeDetailsBasedOnRfqId, 45.5, 115.0, 1, employeeIds, sumInsuredValue);
        PdfPTable table7 = new PdfPTable(3);
        table7.setWidthPercentage(100);
        table7.addCell(createCell("With 1 kid Over 45"));
        table7.addCell(createCell(String.valueOf(countEmployeesWithChildrenCount1AndAgeRangeAbove45)));
        double percentage6 = (double) countEmployeesWithChildrenCount1AndAgeRangeAbove45 / totalEmployeesCount * 100;
        int roundedPercentage6 = (int) Math.round(percentage6);
        String formattedPercentage6 = String.format("%d%%", roundedPercentage6);
        table7.addCell(createCell(formattedPercentage6));
        document.add(table7);

        if (!isOnePlusThree) {
            // Count of Employee Who have more than 1 parent
            int countEmployeesWithParentsCount1 = countEmployeesWithParentsCount(golbalEmployeeDetailsBasedOnRfqId, 0,
                    150, 1, employeeIds, sumInsuredValue);
            PdfPTable table8 = new PdfPTable(3);
            table8.setWidthPercentage(100);
            table8.addCell(createCell("With 1 Parent"));
            table8.addCell(createCell(String.valueOf(countEmployeesWithParentsCount1)));
            double percentage7 = (double) countEmployeesWithParentsCount1 / totalEmployeesCount * 100;
            int roundedOneParentPercentage = (int) Math.round(percentage7);
            String formattedPercentage7 = String.format("%d%%", roundedOneParentPercentage);
            table8.addCell(createCell(formattedPercentage7));
            document.add(table8);

            // Count of Employee Who have more than 2 parents
            int countEmployeesWithParentsCount2 = countEmployeesWithParentsCount(golbalEmployeeDetailsBasedOnRfqId, 0,
                    150, 2, employeeIds, sumInsuredValue);
            PdfPTable table9 = new PdfPTable(3);
            table9.setWidthPercentage(100);
            table9.addCell(createCell("With 2 Parents"));
            table9.addCell(createCell(String.valueOf(countEmployeesWithParentsCount2)));
            double percentage8 = (double) countEmployeesWithParentsCount2 / totalEmployeesCount * 100;
            int roundedTwoParentPercentage = (int) Math.round(percentage8);
            String formattedPercentage8 = String.format("%d%%", roundedTwoParentPercentage);
            table9.addCell(createCell(formattedPercentage8));
            document.add(table9);
        }

    }

    /*
     * Maternity
     */

    private void processForMaternityClaims(List<EmployeeDepedentDetailsEntity> golbalEmployeeDetailsBasedOnRfqId,
                                           Double sumInsuredValue, String id, Document document, boolean isOnePlusThree, Set<String> employeeIds)
            throws DocumentException, IOException {

        int countEmployeesWithParentsCount1 = 0;
        int countEmployeesWithParentsCount2 = 0;

        // Create employee table
        PdfPTable employeeTable = new PdfPTable(2);
        employeeTable.addCell(createCenteredCell("Content"));
        employeeTable.addCell(createCenteredCell("Count"));

        // Count of Employee With 2 Kids
        int countEmployeesWithChildrenCount2 = countEmployeesWithChildrenCount(golbalEmployeeDetailsBasedOnRfqId, 0.0,
                135.0, 2, employeeIds, sumInsuredValue);
        employeeTable.addCell(createCell("With 2 kids"));
        employeeTable.addCell(createCell(String.valueOf(countEmployeesWithChildrenCount2)));

        // Count of Employee With 1 Kid
        int countEmployeesWithChildrenCount1 = countEmployeesWithChildrenCount(golbalEmployeeDetailsBasedOnRfqId, 0.0,
                135.0, 1, employeeIds, sumInsuredValue);
        employeeTable.addCell(createCell("With 1 kid"));
        employeeTable.addCell(createCell(String.valueOf(countEmployeesWithChildrenCount1)));

        // Count of Employee Who are Married or Single
        AgeBindingSummaryDto counts = getAllCountsOfEmployeesWithMartialStatus(golbalEmployeeDetailsBasedOnRfqId,
                employeeIds, sumInsuredValue, id);
        String marriedCount = counts.getMarried();
        String singleCount = counts.getSingle();

        // Count of Employee Who are married
        employeeTable.addCell(createCell("Married"));
        employeeTable.addCell(createCell(marriedCount));

        // Count of Employee Who are Single
        employeeTable.addCell(createCell("Single"));
        employeeTable.addCell(createCell(singleCount));

        // Count of Employee Whose age is above 30
        int countEmployeesWithChildrenCount1AndAgeRangeAbove30 = countOfEmployeeWithKidsCountByAge(
                golbalEmployeeDetailsBasedOnRfqId, 30.5, 35.5, 1, employeeIds, sumInsuredValue);
        employeeTable.addCell(createCell("With 1 kid Over 30"));
        employeeTable.addCell(createCell(String.valueOf(countEmployeesWithChildrenCount1AndAgeRangeAbove30)));

        // Count of Employee Whose age is above 35
        int countEmployeesWithChildrenCount1AndAgeRangeAbove35 = countOfEmployeeWithKidsCountByAge(
                golbalEmployeeDetailsBasedOnRfqId, 35.5, 40.5, 1, employeeIds, sumInsuredValue);
        employeeTable.addCell(createCell("With 1 kid Over 35"));
        employeeTable.addCell(createCell(String.valueOf(countEmployeesWithChildrenCount1AndAgeRangeAbove35)));

        // Count of Employee Whose age is above 40
        int countEmployeesWithChildrenCount1AndAgeRangeAbove40 = countOfEmployeeWithKidsCountByAge(
                golbalEmployeeDetailsBasedOnRfqId, 40.5, 45.5, 1, employeeIds, sumInsuredValue);
        employeeTable.addCell(createCell("With 1 kid Over 40"));
        employeeTable.addCell(createCell(String.valueOf(countEmployeesWithChildrenCount1AndAgeRangeAbove40)));

        // Count of Employee Whose age is above 45
        int countEmployeesWithChildrenCount1AndAgeRangeAbove45 = countOfEmployeeWithKidsCountByAge(
                golbalEmployeeDetailsBasedOnRfqId, 45.5, 115.0, 1, employeeIds, sumInsuredValue);
        employeeTable.addCell(createCell("With 1 kid Over 45"));
        employeeTable.addCell(createCell(String.valueOf(countEmployeesWithChildrenCount1AndAgeRangeAbove45)));

        if (!isOnePlusThree) {
            // Count of Employee Who have more than 1 parent
            countEmployeesWithParentsCount1 = countEmployeesWithParentsCount(golbalEmployeeDetailsBasedOnRfqId, 0, 150,
                    1, employeeIds, sumInsuredValue);
            employeeTable.addCell(createCell("With 1 Parent"));
            employeeTable.addCell(createCell(String.valueOf(countEmployeesWithParentsCount1)));

            // Count of Employee Who have more than 2 parents
            countEmployeesWithParentsCount2 = countEmployeesWithParentsCount(golbalEmployeeDetailsBasedOnRfqId, 0, 150,
                    2, employeeIds, sumInsuredValue);
            employeeTable.addCell(createCell("With 2 Parents"));
            employeeTable.addCell(createCell(String.valueOf(countEmployeesWithParentsCount2)));

        }

        long totalEmployeesCount = countEmployeesWithChildrenCount2 + countEmployeesWithChildrenCount1
                + Integer.parseInt(counts.getMarried()) + Integer.parseInt(counts.getSingle())
                + countEmployeesWithChildrenCount1AndAgeRangeAbove30
                + countEmployeesWithChildrenCount1AndAgeRangeAbove35
                + countEmployeesWithChildrenCount1AndAgeRangeAbove40
                + countEmployeesWithChildrenCount1AndAgeRangeAbove45 + countEmployeesWithParentsCount1
                + countEmployeesWithParentsCount2;
        log.info("The Count Of Total Employee's From Maternity Table = {} ", totalEmployeesCount);

        long totalCount = golbalEmployeeDetailsBasedOnRfqId.stream().map(EmployeeDepedentDetailsEntity::getEmployeeId)
                .count();
        log.info("The Count Of Total members From Maternity Table = {} ", totalCount);

        double nonMaternityPercentage = (double) totalEmployeesCount / totalCount * 100; // Convert one operand to
        // double for correct
        // division
        double maternityPercentage = 100.0 - nonMaternityPercentage; // Calculate maternity percentage

        int roundedNonMaternityPercentage = (int) Math.round(nonMaternityPercentage);
        int roundedMaternityPercentage = (int) Math.round(maternityPercentage);

        log.info("Non-Maternity Percentage: {}%", roundedNonMaternityPercentage); // Log non-maternity percentage
        // rounded to one decimal point
        log.info("Maternity Percentage: {}%", roundedMaternityPercentage); // Log maternity percentage rounded to one
        // decimal point

        employeeTable.addCell(createCellWithFont("Total", boldFont));
        employeeTable.addCell(createCell(String.valueOf(totalEmployeesCount)));
        employeeTable.addCell(createCellWithFont("Percentage", boldFont));
        employeeTable.addCell(createCell(String.format("%d%%", roundedNonMaternityPercentage)));

        PdfPTable tableWithCommonText = new PdfPTable(2);
        tableWithCommonText.setWidthPercentage(100);

        // Set the default border color for the entire table to black
        tableWithCommonText.getDefaultCell().setBorderColor(BaseColor.BLACK);

        // Add the employeeTable to the first cell
        PdfPCell employeeTableCell = new PdfPCell(employeeTable);

        tableWithCommonText.addCell(employeeTableCell);

        // Add the common text to the second cell
        PdfPCell commonTextCell = new PdfPCell();
        commonTextCell.setBorderColor(BaseColor.DARK_GRAY);
        tableWithCommonText.setWidths(new float[]{70f, 30f});
        commonTextCell.setPaddingTop(10f);
        commonTextCell.setPaddingBottom(10f);

        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        boldFont.setSize(28); // Increase font size
        Phrase phrase = new Phrase();
        phrase.add(new Chunk("\n"));
        phrase.add(new Chunk("\n"));
        phrase.add(new Chunk(" The Chance for", boldFont));
        phrase.add(new Chunk("\n"));
        phrase.add(new Chunk("\n"));
        phrase.add(new Chunk(" Maternity", boldFont));
        phrase.add(new Chunk("\n"));
        phrase.add(new Chunk("\n"));
        phrase.add(new Chunk(" Claims is" + " " + String.format("%d%%", roundedMaternityPercentage) + "%", boldFont));

        commonTextCell.setPhrase(phrase);

        tableWithCommonText.addCell(commonTextCell);

        document.add(tableWithCommonText);

    }

    /*
     * This method calculates Ratio's of respective ages
     */
    private String calculateRatio(double maleCount, double femaleCount) {
        if (maleCount == 0 && femaleCount == 0) {
            return "0:0"; // Both counts are zero, returning default ratio
        } else if (maleCount == 0) {
            long roundedFemalePercentage = Math.round(femaleCount / (maleCount + femaleCount) * 100); // Calculate and round female percentage
            return String.format("0:%d", roundedFemalePercentage); // Male count is zero, returning 0:roundedFemalePercentage
        } else if (femaleCount == 0) {
            long roundedMalePercentage = Math.round(maleCount / (maleCount + femaleCount) * 100); // Calculate and round male percentage
            return String.format("%d:0", roundedMalePercentage); // Female count is zero, returning roundedMalePercentage:0
        } else {
            double total = maleCount + femaleCount;
            long roundedMalePercentage = Math.round(maleCount / total * 100); // Calculate and round male percentage
            long roundedFemalePercentage = Math.round(femaleCount / total * 100); // Calculate and round female percentage
            return String.format("%d:%d", roundedMalePercentage, roundedFemalePercentage);
        }
    }

    private PdfPCell createCell(String text) throws IOException, DocumentException {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new BaseColor(240, 240, 240));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);


        return cell;
    }

    private PdfPCell createCellWithFont(String text, Font font) throws DocumentException, IOException {
        // Create a cell with bold text
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new BaseColor(240, 240, 240));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.BOTTOM); // Add a bottom border to create a horizontal line

        return cell;
    }

    private PdfPCell createCenteredCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setFixedHeight(30f);


        BaseColor baseColorhead = new BaseColor(95, 128, 158);
        cell.setBackgroundColor(baseColorhead.brighter());

        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        cell.setPadding(5);
        cell.setPhrase(new Phrase(text, font));
        return cell;
    }

    public static PdfPCell createColumnBody(String text) {
        PdfPCell cell = new PdfPCell();
        cell.addElement(new Paragraph(text));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    public Set<String> getAllSetOfRelationOfOnePlusFive(String rfqId) {
        return employeeRepository.findByRfqId(rfqId).stream()
                .filter(data -> data.getRelationship().equalsIgnoreCase("Father")
                        || data.getRelationship().equalsIgnoreCase("Mother")
                        || data.getRelationship().equalsIgnoreCase("Father-in-law")
                        || data.getRelationship().equalsIgnoreCase("Mother"))
                .map(data -> {
                    return data.getEmployeeId();
                }).collect(Collectors.toSet());
    }

    public Set<String> getAllSetOfRelationOfParentsOnly(String rfqId) {
        Set<String> excludedRelationships = Set.of("Spouse", "Wife", "Child", "Son", "Daughter", "Son-II",
                "Daughter-II", "Child-II");

        return employeeRepository.findByRfqId(rfqId).stream()
                .filter(data -> (data.getRelationship().equalsIgnoreCase("Father")
                        || data.getRelationship().equalsIgnoreCase("Father-in-law")
                        || data.getRelationship().equalsIgnoreCase("Mother")
                        || data.getRelationship().equalsIgnoreCase("Mother-in-law"))
                        && !excludedRelationships.contains(data.getRelationship().toUpperCase()))
                .map(data -> {
                    return data.getEmployeeId();
                }).collect(Collectors.toSet());
    }

    public Set<String> getAllSetOfRelationOfOnePlusThree(String rfqId) {
        Set<String> allSetOfRelation = getAllSetOfRelationOfOnePlusFive(rfqId);
        Set<String> collect = employeeRepository.findByRfqId(rfqId).stream().map(data -> {
            return data.getEmployeeId();
        }).collect(Collectors.toSet());

        for (String data : allSetOfRelation) {
            collect.remove(data);
        }

        return collect;

    }

    /*
     * This method will calculate and checks all sum insured values for both
     * 1+5,parents
     */


    private boolean isOnePlusFive(List<EmployeeDepedentDetailsEntity> sumInsuredGroup, String rfqId,
                                  List<EmployeeDepedentDetailsEntity> golbalEmployeeDetailsBasedOnRfqId) {
        Set<String> onePlusFiveRelations = ageBindingData.onePlusFive(golbalEmployeeDetailsBasedOnRfqId);
        for (String employeeId : onePlusFiveRelations) {
            if (sumInsuredGroup.stream().filter(entity -> entity.getEmployeeId().equals(employeeId))
                    .anyMatch(entity -> {
                        return true;
                    })) {
                return true;
            }
        }
        return false;
    }

    /*
     * This method will calculate and checks all sum insured values for 1+3
     */
    public boolean isOnePlusThree(List<EmployeeDepedentDetailsEntity> sumInsuredGroup, String rfqId,
                                  List<EmployeeDepedentDetailsEntity> golbalEmployeeDetailsBasedOnRfqId) {
        for (String employeeId : ageBindingData.getAllSetOfRelationOfOnePlusThree(golbalEmployeeDetailsBasedOnRfqId)) {
            if (sumInsuredGroup.stream().anyMatch(entity -> entity.getEmployeeId().equals(employeeId))) {
                return true;
            }
        }
        return false;
    }


    /*
     * This method returns all the count of Relationship with Gender i.e total count
     * of male or female with respect to relationship
     */
    public List<AgeBindingRatioAnalysisDto> getMaleAndFemaleRatios(
            List<EmployeeDepedentDetailsEntity> depedentDetailsEntities, double sumInsured, String rfqId,
            Set<String> employeeIds) {
        String selfRegExp = "(?i)emp?.+|(?i)Self?.+";
        String spouseRegExp = "(?i)Spouse?.+|(?i)Husb?.+|(?i)Wife?.+";
        String childRegExp = "(?i)child?.+|(?i)son?.+|(?i)daug?.+";
        String parentRegExp = "(?i)Father?.+|(?i)Mother?.+|(?i)Pare?.+";
        int employeeMaleCount = countRelationWise(depedentDetailsEntities, selfRegExp, sumInsured, rfqId, employeeIds);
        log.info("Total Male Employee Count From Ratio Table{} for Sum Insured:{}", employeeMaleCount, sumInsured);
        int spouseMaleCount = countRelationWise(depedentDetailsEntities, spouseRegExp, sumInsured, rfqId, employeeIds);
        log.info("Total Male Spouse Count From Ratio Table{} for Sum Insured:{}", spouseMaleCount, sumInsured);
        int childMaleCount = countRelationWise(depedentDetailsEntities, childRegExp, sumInsured, rfqId, employeeIds);
        log.info("Total Male Child Count From Ratio Table{} for Sum Insured:{}", childMaleCount, sumInsured);
        int parentMaleCount = countRelationWise(depedentDetailsEntities, parentRegExp, sumInsured, rfqId, employeeIds);
        log.info("Total Male Parents Count From Ratio Table{} for Sum Insured:{}", parentMaleCount, sumInsured);

        int totalEmployeeCount = depedentDetailsEntities.stream()
                .filter(entity -> entity.getRfqId().equalsIgnoreCase(rfqId))
                .filter(entity -> entity.getSumInsured() == sumInsured)
                .filter(entity -> employeeIds.contains(entity.getEmployeeId()))
                .filter(entity -> entity.getRelationship().trim().matches(selfRegExp)).toList()
                .size();

        int totalSpouseCount = depedentDetailsEntities.stream()
                .filter(entity -> entity.getSumInsured() == sumInsured && entity.getRfqId().equalsIgnoreCase(rfqId)
                        && employeeIds.contains(entity.getEmployeeId()))
                .filter(entity -> entity.getRelationship().trim().matches(spouseRegExp)).toList()
                .size();

        int totalChildCount = depedentDetailsEntities.stream()
                .filter(entity -> entity.getSumInsured() == sumInsured && entity.getRfqId().equalsIgnoreCase(rfqId)
                        && employeeIds.contains(entity.getEmployeeId()))
                .filter(entity -> entity.getRelationship().trim().matches(childRegExp)).toList()
                .size();

        int totalParentCount = depedentDetailsEntities.stream()
                .filter(entity -> entity.getSumInsured() == sumInsured && entity.getRfqId().equalsIgnoreCase(rfqId)
                        && employeeIds.contains(entity.getEmployeeId()))
                .filter(entity -> entity.getRelationship().trim().matches(parentRegExp)).toList()
                .size();

        List<AgeBindingRatioAnalysisDto> ratios = new ArrayList<>();

        ratios.add(new AgeBindingRatioAnalysisDto(String.valueOf(employeeMaleCount),
                String.valueOf(totalEmployeeCount - employeeMaleCount)));
        ratios.add(new AgeBindingRatioAnalysisDto(String.valueOf(spouseMaleCount),
                String.valueOf(totalSpouseCount - spouseMaleCount)));
        ratios.add(new AgeBindingRatioAnalysisDto(String.valueOf(childMaleCount),
                String.valueOf(totalChildCount - childMaleCount)));
        ratios.add(new AgeBindingRatioAnalysisDto(String.valueOf(parentMaleCount),
                String.valueOf(totalParentCount - parentMaleCount)));

        return ratios;
    }

    /*
     * This methods count all relationship age wrt to gender
     */
    private int countRelationWise(List<EmployeeDepedentDetailsEntity> entities, String regex, double sumInsured,
                                  String rfqId, Set<String> employeeIds) {
        String male = "(?i)^M.*$";
        List<EmployeeDepedentDetailsEntity> filteredEntities = entities.stream().distinct()
                .filter(entity -> entity.getRfqId().equalsIgnoreCase(rfqId))
                .filter(entity -> entity.getGender().trim().matches(male))
                .filter(entity -> entity.getSumInsured() == sumInsured)
                .filter(entity -> employeeIds.contains(entity.getEmployeeId()))
                .filter(entity -> entity.getRelationship().trim().matches(regex)).toList();
        return (int) filteredEntities.size();
    }

    /*
     * This method returns count of all Kids for Employees
     */
    public int countEmployeesWithChildrenCount(List<EmployeeDepedentDetailsEntity> entities, double minAge,
                                               double maxAge, int noOfKids, Set<String> employeeIds, Double sumInsured) {
        String children = "(?i)child?.+|(?i)son?.+|(?i)daug?.+";

        Map<String, List<EmployeeDepedentDetailsEntity>> employeeChildRelationships = entities.stream()
                .filter(data -> data.getAge() != null && data.getRelationship() != null).filter(data -> {
                    try {
                        double age = Double.parseDouble(data.getAge());
                        if (age < minAge || age > maxAge) {
                            return false;
                        }
                        return data.getRelationship().trim().matches(children)
                                && employeeIds.contains(data.getEmployeeId()) && data.getSumInsured() == sumInsured;
                    } catch (NumberFormatException e) {
                        log.error("Invalid age format for employee: {}", data.getEmployeeId());
                        return false;
                    }
                }).collect(Collectors.groupingBy(EmployeeDepedentDetailsEntity::getEmployeeId));

        long count = employeeChildRelationships.values().stream().filter(value -> value.size() == noOfKids).count();

        log.info("Count of Employee for Summary Table: {} , for no of Kids : {}", count, noOfKids);

        /*
         * Employee Id's for Kids with age
         */
        employeeChildRelationships.entrySet().stream().filter(entry -> entry.getValue().size() == noOfKids)
                .forEach(entry -> {
                    List<EmployeeDepedentDetailsEntity> childrenList = entry.getValue();
                    String employeeId = entry.getKey();
                    String age = childrenList.get(0).getAge();

                    log.info("Employee Id's for no of Kids {} : {} for age : {}", noOfKids, employeeId, age);
                });

        return (int) count;
    }

    /*
     * This method returns count of all parents for Employees
     */
    public int countEmployeesWithParentsCount(List<EmployeeDepedentDetailsEntity> entities, double minAge,
                                              double maxAge, int noOfParents, Set<String> employeeIds, Double sumInsured) {
        String parents = "(?i)Father?.+|(?i)Mother?.+|(?i)Pare?.+";

        Map<String, List<String>> employeeParentsRelationships = entities.stream()
                .filter(data -> data.getAge() != null && data.getRelationship() != null
                        && employeeIds.contains(data.getEmployeeId()) && data.getSumInsured() == sumInsured)
                .filter(data -> {
                    try {
                        double age = Double.parseDouble(data.getAge());
                        if (age < minAge || age > maxAge) {
                            return false;
                        }
                        return data.getRelationship().trim().matches(parents);
                    } catch (NumberFormatException e) {
                        log.error("Invalid age format for employee: {}", data.getEmployeeId());
                        return false;
                    }
                }).collect(Collectors.groupingBy(EmployeeDepedentDetailsEntity::getEmployeeId,
                        Collectors.mapping(EmployeeDepedentDetailsEntity::getRelationship, Collectors.toList())));

        long count = employeeParentsRelationships.values().stream().filter(value -> value.size() == noOfParents)
                .count();

        log.info("Count of Employee from Summary Table: {} , for no of Parents:{}", count, noOfParents);

        /*
         * EmployeeId's of Parents
         */
        employeeParentsRelationships.entrySet().stream().filter(entry -> entry.getValue().size() == noOfParents)
                .forEach(entry -> log.info("Employee Id's for no of Parents {} :{}", noOfParents, entry.getKey()));

        return (int) count;
    }

    /*
     * This method returns all single and married employee's count from the list
     */
    private AgeBindingSummaryDto getAllCountsOfEmployeesWithMartialStatus(List<EmployeeDepedentDetailsEntity> entities,
                                                                          Set<String> employeeIds, Double sumInsured, String rfqId) {
        String spouse = "(?i)husba?.+|(?i)wif?.+|(?i)Spou?.+";

        Set<String> marriedEmployeeIds = entities.stream()
                .filter(data -> data.getAge() != null && data.getRelationship() != null)
                .filter(data -> data.getRelationship().trim().matches(spouse)
                        && employeeIds.contains(data.getEmployeeId()) && data.getSumInsured() == sumInsured)
                .map(EmployeeDepedentDetailsEntity::getEmployeeId).collect(Collectors.toSet());

        Set<String> allEmployeeIds = entities.stream().filter(entity -> entity.getRfqId().equalsIgnoreCase(rfqId))
                .filter(entity -> entity.getSumInsured() == sumInsured)
                .filter(entity -> employeeIds.contains(entity.getEmployeeId()))
                .map(EmployeeDepedentDetailsEntity::getEmployeeId).collect(Collectors.toSet());

        log.info("Employees Who are Single are :{}", allEmployeeIds.size() - marriedEmployeeIds.size());
        log.info("Employees Who are Married are :{}", marriedEmployeeIds.size());

        return AgeBindingSummaryDto.builder().married(String.valueOf(marriedEmployeeIds.size()))
                .single(String.valueOf(allEmployeeIds.size() - marriedEmployeeIds.size())).build();
    }

    /*
     * This method returns Employee Age Who have 1 kid within the age specified
     */
    public int countOfEmployeeWithKidsCountByAge(List<EmployeeDepedentDetailsEntity> entities, double minAge,
                                                 double maxAge, int noOfKids, Set<String> employeeIds, Double sumInsured) {
        String employeePattern = "(?i)emp?.+|(?i)Self?.+";
        String childrenPattern = "(?i)child?.+|(?i)son?.+|(?i)daug?.+";

        long count = entities.stream()
                .filter(e -> e.getEmployeeId() != null && e.getRelationship() != null && e.getAge() != null
                        && e.getRelationship().trim().matches(employeePattern)
                        && Double.parseDouble(e.getAge()) >= minAge && Double.parseDouble(e.getAge()) <= maxAge
                        && employeeIds.contains(e.getEmployeeId()) && e.getSumInsured() == sumInsured)
                .filter(e -> {
                    long childrenCount = entities.stream()
                            .filter(child -> child.getEmployeeId().equals(e.getEmployeeId()))
                            .filter(child -> child.getRelationship() != null
                                    && child.getRelationship().trim().matches(childrenPattern))
                            .count();

                    log.info("Employee {} - Relationship contains children pattern: {}", e.getEmployeeId(),
                            childrenCount);

                    return childrenCount == noOfKids;
                }).peek(e -> log.info("Employee {} - countChildren: {}", e.getEmployeeId(), noOfKids)).count();

        log.info("Total count of employees with exactly {} child(ren): {}", noOfKids, count);

        return (int) count;
    }

    // Function to process Ratio Analysis
    private void processRatioAnalysis(List<EmployeeDepedentDetailsEntity> golbalEmployeeDetailsBasedOnRfqId,
                                      Double sumInsuredValue, String id, Document document, boolean excludeParents, Set<String> employeeIds)
            throws DocumentException, IOException {
        PdfPTable pdfPTable = new PdfPTable(4);
        pdfPTable.setWidthPercentage(100);
        pdfPTable.addCell(createCenteredCell("SNo"));
        pdfPTable.addCell(createCenteredCell("Relation"));
        pdfPTable.addCell(createCenteredCell("Count"));
        pdfPTable.addCell(createCenteredCell("Ratio"));
        document.add(pdfPTable);

        List<AgeBindingRatioAnalysisDto> maleAndFemaleRatios = getMaleAndFemaleRatios(golbalEmployeeDetailsBasedOnRfqId,
                sumInsuredValue, id, employeeIds);

        for (int i = 0; i < maleAndFemaleRatios.size(); i++) {
            AgeBindingRatioAnalysisDto ratioDto = maleAndFemaleRatios.get(i);

            // Exclude "Parents" relation if specified
            if (!excludeParents || i != 3) {
                PdfPTable ratioTable = new PdfPTable(4);
                ratioTable.setWidthPercentage(100);
                ratioTable.addCell(createCell(String.valueOf(i + 1)));

                String relation = "";
                if (i == 0) {
                    relation = "Employee";
                } else if (i == 1) {
                    relation = "Spouse";
                } else if (i == 2) {
                    relation = "Children";
                } else if (i == 3) {
                    relation = "Parents";
                }

                ratioTable.addCell(createCell(relation));

                int totalCount = Integer.parseInt(ratioDto.getMale()) + Integer.parseInt(ratioDto.getFemale());

                ratioTable.addCell(createCell("M:" + ratioDto.getMale() + " ," + "F:" + ratioDto.getFemale()));

                int maleCount = Integer.parseInt(ratioDto.getMale());
                int femaleCount = Integer.parseInt(ratioDto.getFemale());
                String ratioString = calculateRatio(maleCount, femaleCount);
                ratioTable.addCell(createCell(ratioString));

                document.add(ratioTable);
            }
        }
    }

}
