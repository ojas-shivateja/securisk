package com.insure.rfq.generator;

import com.insure.rfq.entity.ClaimsDetails;
import com.insure.rfq.entity.CorporateDetailsEntity;
import com.insure.rfq.entity.CoverageDetailsEntity;
import com.insure.rfq.entity.ExpiryPolicyDetails;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class IrdaPdfGenerator {

	@Value("classpath:configfile/logo.png")
	Resource resource;
	@Value("classpath:configfile/signature.png")
	Resource resource1;

	public byte[] generateEmployeeDataReport() throws IOException {

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
			PdfPTable table = new PdfPTable(new float[] { 1, 1, 1.5f });
			table.setWidthPercentage(100);

			// Column 1: Image
			Image image = Image.getInstance(resource.toString()); // Replace with the path to your image

			image.scaleAbsolute(100, 100);
			PdfPCell cellImage = new PdfPCell(image, true);
			cellImage.setBorder(Rectangle.NO_BORDER);
			table.addCell(cellImage);

			// Column 2: Empty Cell
			PdfPCell emptyCell = new PdfPCell();
			emptyCell.setBorder(Rectangle.NO_BORDER);
			table.addCell(emptyCell);

			// Column 3: Wrapped Text
			PdfPCell cellText = new PdfPCell();
			cellText.setBorder(Rectangle.NO_BORDER); // Set no borders
			cellText.addElement(new Paragraph("InsuredName: USMBusinessSystems\nIRDAformat\nPolicyType:Fresh"));
			table.addCell(cellText);

			// Add the table to the document
			document.add(table);

			// Add some margin (space) after the table
			document.add(new Paragraph("\n")); // You can adjust the spacing as needed

			// Add a horizontal line
			LineSeparator line = new LineSeparator();
			line.setLineColor(BaseColor.BLACK);
			line.setLineWidth(2f);

			document.add(line);

			// Add some margin (space) after the table
			document.add(new Paragraph("\n\n\n")); // You can adjust the spacing as needed

			// Add a new table with 8 rows and 3 columns
			PdfPTable newTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			newTable.setWidthPercentage(100);

			// Add the title row
			PdfPCell titleCell = new PdfPCell(new Phrase("Insured Name"));
			titleCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			titleCell.setColspan(3); // Set colspan to 3
			titleCell.setPadding(10);
			newTable.addCell(titleCell);

			// Row - 1
			PdfPCell nameOfInsured = new PdfPCell(new Phrase("nameOfInsured"));
			nameOfInsured.setPadding(10);
			newTable.addCell(nameOfInsured);

			PdfPCell nameOfInsuredValue = new PdfPCell(new Phrase("nameOfInsuredValue"));
			nameOfInsuredValue.setColspan(2);
			nameOfInsuredValue.setPadding(10);
			newTable.addCell(nameOfInsuredValue);

			// Row - 2
			PdfPCell addressOfInsured = new PdfPCell(new Phrase("addressOfInsured"));
			addressOfInsured.setPadding(10);
			newTable.addCell(addressOfInsured);

			PdfPCell addressOfInsuredValue = new PdfPCell(new Phrase("addressOfInsuredValue"));
			addressOfInsuredValue.setColspan(2);
			addressOfInsuredValue.setPadding(10);
			newTable.addCell(addressOfInsuredValue);

			// Row - 3
			PdfPCell businessOfInsured = new PdfPCell(new Phrase("Business of Insured"));
			businessOfInsured.setPadding(10);
			newTable.addCell(businessOfInsured);

			PdfPCell businessOfInsuredValue = new PdfPCell(new Phrase("businessOfInsuredValue"));
			businessOfInsuredValue.setColspan(2);
			businessOfInsuredValue.setPadding(10);
			newTable.addCell(businessOfInsuredValue);

			// Row - 4
			PdfPCell contactPersonAtInsured = new PdfPCell(new Phrase("Contact Person at Insured"));
			contactPersonAtInsured.setPadding(10);
			newTable.addCell(contactPersonAtInsured);

			PdfPCell contactPersonAtInsuredValue = new PdfPCell(new Phrase("contactPersonAtInsured"));
			contactPersonAtInsuredValue.setColspan(2);
			contactPersonAtInsuredValue.setPadding(10);
			newTable.addCell(contactPersonAtInsuredValue);

			// Row - 5
			PdfPCell phoneAndEamil = new PdfPCell(new Phrase("phoneAndEamil"));
			phoneAndEamil.setPadding(10);
			newTable.addCell(phoneAndEamil);

			PdfPCell phoneValue = new PdfPCell(new Phrase("phoneValue"));
			phoneValue.setPadding(10);
			newTable.addCell(phoneValue);

			PdfPCell emailValue = new PdfPCell(new Phrase("emailValue"));
			emailValue.setPadding(10);
			newTable.addCell(emailValue);

			// Row - 6
			PdfPCell employerEmployeeRelationship = new PdfPCell(new Phrase("Employer-Employee relationship"));
			employerEmployeeRelationship.setPadding(10);
			newTable.addCell(employerEmployeeRelationship);

			PdfPCell employerEmployeeRelationshipValue = new PdfPCell(new Phrase("employerEmployeeRelationshipValue"));
			employerEmployeeRelationshipValue.setColspan(2);
			employerEmployeeRelationshipValue.setPadding(10); // Add padding
			newTable.addCell(employerEmployeeRelationshipValue);

			// Row - 7
			PdfPCell ifNoSpecifyRelationship = new PdfPCell(new Phrase("If No, specify relationship"));
			ifNoSpecifyRelationship.setPadding(10); // Add padding
			newTable.addCell(ifNoSpecifyRelationship);

			PdfPCell ifNoSpecifyRelationshipValue = new PdfPCell(new Phrase("ifNoSpecifyRelationshipValue"));
			ifNoSpecifyRelationshipValue.setColspan(2);
			ifNoSpecifyRelationshipValue.setPadding(10); // Add padding
			newTable.addCell(ifNoSpecifyRelationshipValue);

			// Add the new table to the document
			document.add(newTable);

			// Add a new table intermediaryDetailsTable
			PdfPTable intermediaryDetailsTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			intermediaryDetailsTable.setWidthPercentage(100);

			// Add the title row
			PdfPCell interTitle = new PdfPCell(new Phrase("Intermediary Details"));
			interTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
			interTitle.setColspan(3); // Set colspan to 3
			interTitle.setPadding(10);
			intermediaryDetailsTable.addCell(interTitle);

			//
			PdfPCell nameOfTheIntermediary = new PdfPCell(
					new Phrase("Name of the Intermediary (Existing & New if applicable)"));
			nameOfTheIntermediary.setPadding(10);
			intermediaryDetailsTable.addCell(nameOfTheIntermediary);

			PdfPCell nameOfTheIntermediaryValue = new PdfPCell(new Phrase("Securisk Insurance Brokers Pvt Ltd"));
			nameOfTheIntermediaryValue.setColspan(2);
			nameOfTheIntermediaryValue.setPadding(10);
			intermediaryDetailsTable.addCell(nameOfTheIntermediaryValue);

			// Add the new table to the document
			document.add(intermediaryDetailsTable);

			// Add a new table TPA DETAILS
			PdfPTable tpaTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			tpaTable.setWidthPercentage(100);

			// Add the title row
			PdfPCell tpaTitle = new PdfPCell(new Phrase("TPA Details"));
			tpaTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
			tpaTitle.setColspan(3); // Set colspan to 3

			// Add internal padding for the title row cell (10 points on all sides)
			tpaTitle.setPadding(10);

			tpaTable.addCell(tpaTitle);

			// Row - TAP 1
			PdfPCell tpaNameAndAddress = new PdfPCell(new Phrase("Name and Address"));

			// Add internal padding for the tpaNameAndAddress cell (10 points on all sides)
			tpaNameAndAddress.setPadding(10);

			tpaTable.addCell(tpaNameAndAddress);

			PdfPCell tpaNameAndAddressValue = new PdfPCell(new Phrase("Fresh value"));
			tpaNameAndAddressValue.setColspan(2);

			// Add internal padding for the tpaNameAndAddressValue cell (10 points on all
			// sides)
			tpaNameAndAddressValue.setPadding(10);

			tpaTable.addCell(tpaNameAndAddressValue);

			// Row - TAP 2
			PdfPCell tpaContactDetails = new PdfPCell(new Phrase("Contact Details"));

			// Add internal padding for the tpaContactDetails cell (10 points on all sides)
			tpaContactDetails.setPadding(10);

			tpaTable.addCell(tpaContactDetails);

			PdfPCell tpaContactDetailsValue = new PdfPCell(new Phrase("FRESH"));
			tpaContactDetailsValue.setColspan(2);

			// Add internal padding for the tpaContactDetailsValue cell (10 points on all
			// sides)
			tpaContactDetailsValue.setPadding(10);

			tpaTable.addCell(tpaContactDetailsValue);

			// Add the new table to the document
			document.add(tpaTable);

			// Expiring Policy Details table
			PdfPTable policyTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			policyTable.setWidthPercentage(100);

			// Add the policy title row
			PdfPCell policyTitle = new PdfPCell(new Phrase("Expiring Policy Details"));
			policyTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
			policyTitle.setColspan(3); // Set colspan to 3

			// Add internal padding for the policy title row cell (10 points on all sides)
			policyTitle.setPadding(10);

			policyTable.addCell(policyTitle);

			// Row - policy 1
			PdfPCell periodOfInsurance = new PdfPCell(
					new Phrase("Period of Insurance and Policy Number (Inception Date and Expiry Date)"));

			// Add internal padding for the periodOfInsurance cell (10 points on all sides)
			periodOfInsurance.setPadding(10);

			policyTable.addCell(periodOfInsurance);

			PdfPCell firstCol = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the firstCol cell (10 points on all sides)
			firstCol.setPadding(10);

			policyTable.addCell(firstCol);

			PdfPCell secondCol = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the secondCol cell (10 points on all sides)
			secondCol.setPadding(10);

			policyTable.addCell(secondCol);

			document.add(policyTable);

			document.newPage();

			// Policy copy with terms/ conditions including extensions is to be mandatorily
			// provided by the Proposer table
			PdfPTable policyCopyTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			policyCopyTable.setWidthPercentage(100);

			// Add the policy title row
			PdfPCell policyCopyTitle = new PdfPCell(new Phrase(
					"Policy copy with terms/ conditions including extensions is to be mandatorily provided by the Proposer"));
			policyCopyTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
			policyCopyTitle.setColspan(3); // Set colspan to 3

			// Add internal padding for the policyCopyTitle cell (10 points on all sides)
			policyCopyTitle.setPadding(10);

			policyCopyTable.addCell(policyCopyTitle);

			// Row - policy copy 1
			PdfPCell policyType = new PdfPCell(new Phrase("Policy type"));

			// Add internal padding for the policyType cell (10 points on all sides)
			policyType.setPadding(10);

			policyCopyTable.addCell(policyType);

			PdfPCell policyTypeValue = new PdfPCell(new Phrase("N/A"));
			policyTypeValue.setColspan(2);

			// Add internal padding for the policyTypeValue cell (10 points on all sides)
			policyTypeValue.setPadding(10);

			policyCopyTable.addCell(policyTypeValue);

			// Row - policy copy 2
			PdfPCell premiumPaid = new PdfPCell(new Phrase("Premium paid at inception (exclusive of Service Tax)"));

			// Add internal padding for the premiumPaid cell (10 points on all sides)
			premiumPaid.setPadding(10);

			policyCopyTable.addCell(premiumPaid);

			PdfPCell premiumPaidValue = new PdfPCell(new Phrase("N/A"));
			premiumPaidValue.setColspan(2);

			// Add internal padding for the premiumPaidValue cell (10 points on all sides)
			premiumPaidValue.setPadding(10);

			policyCopyTable.addCell(premiumPaidValue);

			// Row - policy copy 3
			PdfPCell premiumAddition = new PdfPCell(new Phrase("Premium addition during the year"));

			// Add internal padding for the premiumAddition cell (10 points on all sides)
			premiumAddition.setPadding(10);

			policyCopyTable.addCell(premiumAddition);

			PdfPCell premiumAdditionValue = new PdfPCell(new Phrase("N/A"));
			premiumAdditionValue.setColspan(2);

			// Add internal padding for the premiumAdditionValue cell (10 points on all
			// sides)
			premiumAdditionValue.setPadding(10);

			policyCopyTable.addCell(premiumAdditionValue);

			// Row - policy copy 4
			PdfPCell premiumDeletion = new PdfPCell(new Phrase("Premium deletion during the year"));

			// Add internal padding for the premiumDeletion cell (10 points on all sides)
			premiumDeletion.setPadding(10);

			policyCopyTable.addCell(premiumDeletion);

			PdfPCell premiumDeletionValue = new PdfPCell(new Phrase("N/A"));
			premiumDeletionValue.setColspan(2);

			// Add internal padding for the premiumDeletionValue cell (10 points on all
			// sides)
			premiumDeletionValue.setPadding(10);

			policyCopyTable.addCell(premiumDeletionValue);

			// Row - policy copy 5
			PdfPCell finalPremium = new PdfPCell(
					new Phrase("Final Premium collected (exclusive of Service Tax) as on date to be Specified."));

			// Add internal padding for the finalPremium cell (10 points on all sides)
			finalPremium.setPadding(10);

			policyCopyTable.addCell(finalPremium);

			PdfPCell finalPremiumValue = new PdfPCell(new Phrase("N/A"));
			finalPremiumValue.setColspan(2);

			// Add internal padding for the finalPremiumValue cell (10 points on all sides)
			finalPremiumValue.setPadding(10);

			policyCopyTable.addCell(finalPremiumValue);

			// Row - policy copy 6
			PdfPCell forHowManyYears = new PdfPCell(new Phrase("For how many years policy has been active"));

			// Add internal padding for the forHowManyYears cell (10 points on all sides)
			forHowManyYears.setPadding(10);

			policyCopyTable.addCell(forHowManyYears);

			PdfPCell forHowManyYearsValue = new PdfPCell(new Phrase("N/A"));
			forHowManyYearsValue.setColspan(2);

			// Add internal padding for the forHowManyYearsValue cell (10 points on all
			// sides)
			forHowManyYearsValue.setPadding(10);

			policyCopyTable.addCell(forHowManyYearsValue);

			document.add(policyCopyTable);

			// memberDetailsTable table
			PdfPTable memberDetailsTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			memberDetailsTable.setWidthPercentage(100);

			// Add the Member Details title row
			PdfPCell memberDetailsTitle = new PdfPCell(new Phrase("Member Details"));
			memberDetailsTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
			memberDetailsTitle.setColspan(3); // Set colspan to 3

			// Add internal padding for the memberDetailsTitle cell (10 points on all sides)
			memberDetailsTitle.setPadding(10);

			memberDetailsTable.addCell(memberDetailsTitle);

			// Row - Member Details 1
			PdfPCell expiringYear = new PdfPCell(new Phrase("Expiring Year"));

			// Add internal padding for the expiringYear cell (10 points on all sides)
			expiringYear.setPadding(10);

			memberDetailsTable.addCell(expiringYear);

			PdfPCell expiringYearValue = new PdfPCell(new Phrase(""));

			// Add internal padding for the expiringYearValue cell (10 points on all sides)
			expiringYearValue.setPadding(10);

			expiringYearValue.setColspan(2);
			memberDetailsTable.addCell(expiringYearValue);

			// Row - Member Details 2
			PdfPCell basisOfPremium = new PdfPCell(
					new Phrase("Basis of Premium Charging -per Family or per Member covered"));

			// Add internal padding for the basisOfPremium cell (10 points on all sides)
			basisOfPremium.setPadding(10);

			memberDetailsTable.addCell(basisOfPremium);

			PdfPCell basisOfPremiumValue = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the basisOfPremiumValue cell (10 points on all
			// sides)
			basisOfPremiumValue.setPadding(10);

			memberDetailsTable.addCell(basisOfPremiumValue);

			PdfPCell basisOfPremiumValueTwo = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the basisOfPremiumValueTwo cell (10 points on all
			// sides)
			basisOfPremiumValueTwo.setPadding(10);

			memberDetailsTable.addCell(basisOfPremiumValueTwo);

			// Row - Member Details 3
			PdfPCell membersAtInception = new PdfPCell(new Phrase("No. of Members at inception"));

			// Add internal padding for the membersAtInception cell (10 points on all sides)
			membersAtInception.setPadding(10);

			memberDetailsTable.addCell(membersAtInception);

			PdfPCell membersAtInceptionValue = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the membersAtInceptionValue cell (10 points on all
			// sides)
			membersAtInceptionValue.setPadding(10);

			memberDetailsTable.addCell(membersAtInceptionValue);

			PdfPCell membersAtInceptionValueTwo = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the membersAtInceptionValueTwo cell (10 points on
			// all sides)
			membersAtInceptionValueTwo.setPadding(10);

			memberDetailsTable.addCell(membersAtInceptionValueTwo);

			// Row - Member Details 4
			PdfPCell additionDuringTheYear = new PdfPCell(new Phrase("Addition during the year"));

			// Add internal padding for the additionDuringTheYear cell (10 points on all
			// sides)
			additionDuringTheYear.setPadding(10);

			memberDetailsTable.addCell(additionDuringTheYear);

			PdfPCell additionDuringTheYearValue = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the additionDuringTheYearValue cell (10 points on
			// all sides)
			additionDuringTheYearValue.setPadding(10);

			memberDetailsTable.addCell(additionDuringTheYearValue);

			PdfPCell additionDuringTheYearValueTwo = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the additionDuringTheYearValueTwo cell (10 points on
			// all sides)
			additionDuringTheYearValueTwo.setPadding(10);

			memberDetailsTable.addCell(additionDuringTheYearValueTwo);

			// Row - Member Details 5
			PdfPCell deletionDuringTheYear = new PdfPCell(new Phrase("Deletion during the year"));

			// Add internal padding for the deletionDuringTheYear cell (10 points on all
			// sides)
			deletionDuringTheYear.setPadding(10);

			memberDetailsTable.addCell(deletionDuringTheYear);

			PdfPCell deletionDuringTheYearValue = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the deletionDuringTheYearValue cell (10 points on
			// all sides)
			deletionDuringTheYearValue.setPadding(10);

			memberDetailsTable.addCell(deletionDuringTheYearValue);

			PdfPCell deletionDuringTheYearValueTwo = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the deletionDuringTheYearValueTwo cell (10 points on
			// all sides)
			deletionDuringTheYearValueTwo.setPadding(10);

			memberDetailsTable.addCell(deletionDuringTheYearValueTwo);

			// Row - Member Details 6
			PdfPCell membersAtExpiry = new PdfPCell(
					new Phrase("Final no. of Members at expiry (With complete enrollment date)"));

			// Add internal padding for the membersAtExpiry cell (10 points on all sides)
			membersAtExpiry.setPadding(10);

			memberDetailsTable.addCell(membersAtExpiry);

			PdfPCell membersAtExpiryValue = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the membersAtExpiryValue cell (10 points on all
			// sides)
			membersAtExpiryValue.setPadding(10);

			memberDetailsTable.addCell(membersAtExpiryValue);

			PdfPCell membersAtExpiryValueTwo = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the membersAtExpiryValueTwo cell (10 points on all
			// sides)
			membersAtExpiryValueTwo.setPadding(10);

			memberDetailsTable.addCell(membersAtExpiryValueTwo);

			document.add(memberDetailsTable);

			//

			// RenewalYear table
			PdfPTable renewalTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			renewalTable.setWidthPercentage(100);

			// Add the RenewalYear title row
			PdfPCell renewalTitle = new PdfPCell(new Phrase("RenewalYear"));
			renewalTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
			renewalTitle.setColspan(3); // Set colspan to 3

			// Add internal padding for the renewalTitle cell (10 points on all sides)
			renewalTitle.setPadding(10);

			renewalTable.addCell(renewalTitle);

			// Row - RenewalYear 1
			PdfPCell empty = new PdfPCell(new Phrase(""));

			// Add internal padding for the empty cell (10 points on all sides)
			empty.setPadding(10);

			renewalTable.addCell(empty);

			PdfPCell emptyValue = new PdfPCell(new Phrase("Employee"));

			// Add internal padding for the emptyValue cell (10 points on all sides)
			emptyValue.setPadding(10);

			renewalTable.addCell(emptyValue);

			PdfPCell emptyValueTwo = new PdfPCell(new Phrase("Dependent"));

			// Add internal padding for the emptyValueTwo cell (10 points on all sides)
			emptyValueTwo.setPadding(10);

			renewalTable.addCell(emptyValueTwo);

			// Row - RenewalYear 2
			PdfPCell noOfMembersToBeCovered = new PdfPCell(new Phrase("No of Members to be covered"));

			// Add internal padding for the noOfMembersToBeCovered cell (10 points on all
			// sides)
			noOfMembersToBeCovered.setPadding(10);

			renewalTable.addCell(noOfMembersToBeCovered);

			PdfPCell noOfMembersToBeCoveredValue = new PdfPCell(new Phrase("127"));

			// Add internal padding for the noOfMembersToBeCoveredValue cell (10 points on
			// all sides)
			noOfMembersToBeCoveredValue.setPadding(10);

			renewalTable.addCell(noOfMembersToBeCoveredValue);

			PdfPCell noOfMembersToBeCoveredValueValueTwo = new PdfPCell(new Phrase("135"));

			// Add internal padding for the noOfMembersToBeCoveredValueValueTwo cell (10
			// points on all sides)
			noOfMembersToBeCoveredValueValueTwo.setPadding(10);

			renewalTable.addCell(noOfMembersToBeCoveredValueValueTwo);

			// Row - RenewalYear 3
			PdfPCell pleaseSpecifySumInsuredRequired = new PdfPCell(new Phrase("Please Specify Sum Insured required"));

			// Add internal padding for the pleaseSpecifySumInsuredRequired cell (10 points
			// on all sides)
			pleaseSpecifySumInsuredRequired.setPadding(10);

			renewalTable.addCell(pleaseSpecifySumInsuredRequired);

			PdfPCell pleaseSpecifySumInsuredRequiredValue = new PdfPCell(new Phrase("Parents Only : 2000"));
			pleaseSpecifySumInsuredRequiredValue.setColspan(2);

			// Add internal padding for the pleaseSpecifySumInsuredRequiredValue cell (10
			// points on all sides)
			pleaseSpecifySumInsuredRequiredValue.setPadding(10);

			renewalTable.addCell(pleaseSpecifySumInsuredRequiredValue);

			// Row - RenewalYear 4
			PdfPCell familiesToBeCovered = new PdfPCell(
					new Phrase("If Family coverage then no of Families to be covered"));

			// Add internal padding for the familiesToBeCovered cell (10 points on all
			// sides)
			familiesToBeCovered.setPadding(10);

			renewalTable.addCell(familiesToBeCovered);

			PdfPCell familiesToBeCoveredValue = new PdfPCell(new Phrase("127"));

			// Add internal padding for the familiesToBeCoveredValue cell (10 points on all
			// sides)
			familiesToBeCoveredValue.setPadding(10);

			renewalTable.addCell(familiesToBeCoveredValue);

			PdfPCell familiesToBeCoveredValueTwo = new PdfPCell(new Phrase("135"));

			// Add internal padding for the familiesToBeCoveredValueTwo cell (10 points on
			// all sides)
			familiesToBeCoveredValueTwo.setPadding(10);

			renewalTable.addCell(familiesToBeCoveredValueTwo);

			// Row - RenewalYear 5
			PdfPCell familyFloaterSumInsured = new PdfPCell(new Phrase("Family/ Floater Sum Insured"));

			// Add internal padding for the familyFloaterSumInsured cell (10 points on all
			// sides)
			familyFloaterSumInsured.setPadding(10);

			renewalTable.addCell(familyFloaterSumInsured);

			PdfPCell familyFloaterSumInsuredValue = new PdfPCell(new Phrase("Parents Only : 2000"));
			familyFloaterSumInsuredValue.setColspan(2);

			// Add internal padding for the familyFloaterSumInsuredValue cell (10 points on
			// all sides)
			familyFloaterSumInsuredValue.setPadding(10);

			renewalTable.addCell(familyFloaterSumInsuredValue);

			document.add(renewalTable);

			document.newPage();

			// Claim Details as on under expiring table
			PdfPTable claimsDetailsTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			claimsDetailsTable.setWidthPercentage(100);

			// Add the Claim Details title row
			PdfPCell claimDetailsTitle = new PdfPCell(new Phrase("Claim Details as on under expiring policy:"));
			claimDetailsTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);

			// Add internal padding for the claimDetailsTitle cell (10 points on all sides)
			claimDetailsTitle.setPadding(10);

			claimsDetailsTable.addCell(claimDetailsTitle);

			// Add the Reimbursement title row
			PdfPCell cashlessTitle = new PdfPCell(new Phrase("Reimbursement:"));
			cashlessTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);

			// Add internal padding for the cashlessTitle cell (10 points on all sides)
			cashlessTitle.setPadding(10);

			claimsDetailsTable.addCell(cashlessTitle);

			// Add the Cashless title row
			PdfPCell reimbursementTitle = new PdfPCell(new Phrase("Cashless:"));
			reimbursementTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);

			// Add internal padding for the reimbursementTitle cell (10 points on all sides)
			reimbursementTitle.setPadding(10);

			claimsDetailsTable.addCell(reimbursementTitle);

			// Row - Claim Details 1
			PdfPCell claimsPaidasOnDate = new PdfPCell(new Phrase("Claims paid as on date"));

			// Add internal padding for the claimsPaidasOnDate cell (10 points on all sides)
			claimsPaidasOnDate.setPadding(10);

			claimsDetailsTable.addCell(claimsPaidasOnDate);

			PdfPCell claimsPaidasOnDateValue = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the claimsPaidasOnDateValue cell (10 points on all
			// sides)
			claimsPaidasOnDateValue.setPadding(10);

			claimsDetailsTable.addCell(claimsPaidasOnDateValue);

			PdfPCell claimsPaidasOnDateValueTwo = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the claimsPaidasOnDateValueTwo cell (10 points on
			// all sides)
			claimsPaidasOnDateValueTwo.setPadding(10);

			claimsDetailsTable.addCell(claimsPaidasOnDateValueTwo);

			// Row - Claim Details 2
			PdfPCell claimsOutstandingAsOnDate = new PdfPCell(new Phrase("Claims outstanding as on date"));

			// Add internal padding for the claimsOutstandingAsOnDate cell (10 points on all
			// sides)
			claimsOutstandingAsOnDate.setPadding(10);

			claimsDetailsTable.addCell(claimsOutstandingAsOnDate);

			PdfPCell claimsOutstandingAsOnDateValue = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the claimsOutstandingAsOnDateValue cell (10 points
			// on all sides)
			claimsOutstandingAsOnDateValue.setPadding(10);

			claimsDetailsTable.addCell(claimsOutstandingAsOnDateValue);

			PdfPCell claimsOutstandingAsOnDateValueTwo = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the claimsOutstandingAsOnDateValueTwo cell (10
			// points on all sides)
			claimsOutstandingAsOnDateValueTwo.setPadding(10);

			claimsDetailsTable.addCell(claimsOutstandingAsOnDateValueTwo);

			// Row - Claim Details 3
			PdfPCell ifOPDcoverGiveThenMentionOPDClaimsSeparately = new PdfPCell(
					new Phrase("If OPDcover given, then mention OPD claims separately"));

			// Add internal padding for the ifOPDcoverGiveThenMentionOPDClaimsSeparately
			// cell (10 points on all sides)
			ifOPDcoverGiveThenMentionOPDClaimsSeparately.setPadding(10);

			claimsDetailsTable.addCell(ifOPDcoverGiveThenMentionOPDClaimsSeparately);

			PdfPCell ifOPDcoverGiveThenMentionOPDClaimsSeparatelyValue = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the
			// ifOPDcoverGiveThenMentionOPDClaimsSeparatelyValue cell (10 points on all
			// sides)
			ifOPDcoverGiveThenMentionOPDClaimsSeparatelyValue.setPadding(10);

			claimsDetailsTable.addCell(ifOPDcoverGiveThenMentionOPDClaimsSeparatelyValue);

			PdfPCell ifOPDcoverGiveThenMentionOPDClaimsSeparatelyValueTwo = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the
			// ifOPDcoverGiveThenMentionOPDClaimsSeparatelyValueTwo cell (10 points on all
			// sides)
			ifOPDcoverGiveThenMentionOPDClaimsSeparatelyValueTwo.setPadding(10);

			claimsDetailsTable.addCell(ifOPDcoverGiveThenMentionOPDClaimsSeparatelyValueTwo);

			// Row - Claim Details 4
			PdfPCell detailsOfClaimsPaidUnderCorporate = new PdfPCell(
					new Phrase("Details of Claims paid under Corporate Buffer Facility as on ()"));

			// Add internal padding for the detailsOfClaimsPaidUnderCorporate cell (10
			// points on all sides)
			detailsOfClaimsPaidUnderCorporate.setPadding(10);

			claimsDetailsTable.addCell(detailsOfClaimsPaidUnderCorporate);

			PdfPCell detailsOfClaimsPaidUnderCorporateValue = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the detailsOfClaimsPaidUnderCorporateValue cell (10
			// points on all sides)
			detailsOfClaimsPaidUnderCorporateValue.setPadding(10);

			claimsDetailsTable.addCell(detailsOfClaimsPaidUnderCorporateValue);

			PdfPCell detailsOfClaimsPaidUnderCorporateValueTwo = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the detailsOfClaimsPaidUnderCorporateValueTwo cell
			// (10 points on all sides)
			detailsOfClaimsPaidUnderCorporateValueTwo.setPadding(10);

			claimsDetailsTable.addCell(detailsOfClaimsPaidUnderCorporateValueTwo);

			// Row - Claim Details 5
			PdfPCell claimsPaidAsOnDate = new PdfPCell(new Phrase("Claims Paid as on Date"));

			// Add internal padding for the claimsPaidAsOnDate cell (10 points on all sides)
			claimsPaidAsOnDate.setPadding(10);

			claimsDetailsTable.addCell(claimsPaidAsOnDate);

			PdfPCell claimsPaidAsOnDateValue = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the claimsPaidAsOnDateValue cell (10 points on all
			// sides)
			claimsPaidAsOnDateValue.setPadding(10);

			claimsDetailsTable.addCell(claimsPaidAsOnDateValue);

			PdfPCell claimsPaidAsOnDateValueTwo = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the claimsPaidAsOnDateValueTwo cell (10 points on
			// all sides)
			claimsPaidAsOnDateValueTwo.setPadding(10);

			claimsDetailsTable.addCell(claimsPaidAsOnDateValueTwo);

			// Row - Claim Details 6
			PdfPCell claimsOutstandingAsOnDateNew = new PdfPCell(new Phrase("Claims Outstanding as on date"));

			// Add internal padding for the claimsOutstandingAsOnDateNew cell (10 points on
			// all sides)
			claimsOutstandingAsOnDateNew.setPadding(10);

			claimsDetailsTable.addCell(claimsOutstandingAsOnDateNew);

			PdfPCell claimsOutstandingAsOnDateNewValue = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the claimsOutstandingAsOnDateNewValue cell (10
			// points on all sides)
			claimsOutstandingAsOnDateNewValue.setPadding(10);

			claimsDetailsTable.addCell(claimsOutstandingAsOnDateNewValue);

			PdfPCell claimsOutstandingAsOnDateNewValueTwo = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the claimsOutstandingAsOnDateNewValueTwo cell (10
			// points on all sides)
			claimsOutstandingAsOnDateNewValueTwo.setPadding(10);

			claimsDetailsTable.addCell(claimsOutstandingAsOnDateNewValueTwo);

			// Row - Claim Details 7
			PdfPCell totalClaimsPaid = new PdfPCell(new Phrase(
					"Total claims paid during the last two policy years immediately preceding the expiring year."));

			// Add internal padding for the totalClaimsPaid cell (10 points on all sides)
			totalClaimsPaid.setPadding(10);

			claimsDetailsTable.addCell(totalClaimsPaid);

			PdfPCell totalClaimsPaidValue = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the totalClaimsPaidValue cell (10 points on all
			// sides)
			totalClaimsPaidValue.setPadding(10);

			claimsDetailsTable.addCell(totalClaimsPaidValue);

			PdfPCell totalClaimsPaidValueTwo = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the totalClaimsPaidValueTwo cell (10 points on all
			// sides)
			totalClaimsPaidValueTwo.setPadding(10);

			claimsDetailsTable.addCell(totalClaimsPaidValueTwo);

			// Row - Claim Details 8
			PdfPCell totalClaimsPaidDuringTheLastThreeMonths = new PdfPCell(new Phrase(
					"Total claims paid during the last three months of two years of policy immediately preceding to the expiring year."));

			// Add internal padding for the totalClaimsPaidDuringTheLastThreeMonths cell (10
			// points on all sides)
			totalClaimsPaidDuringTheLastThreeMonths.setPadding(10);

			claimsDetailsTable.addCell(totalClaimsPaidDuringTheLastThreeMonths);

			PdfPCell totalClaimsPaidDuringTheLastThreeMonthsValue = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the totalClaimsPaidDuringTheLastThreeMonthsValue
			// cell (10 points on all sides)
			totalClaimsPaidDuringTheLastThreeMonthsValue.setPadding(10);

			claimsDetailsTable.addCell(totalClaimsPaidDuringTheLastThreeMonthsValue);

			PdfPCell totalClaimsPaidDuringTheLastThreeMonthsValueTwo = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the totalClaimsPaidDuringTheLastThreeMonthsValueTwo
			// cell (10 points on all sides)
			totalClaimsPaidDuringTheLastThreeMonthsValueTwo.setPadding(10);

			claimsDetailsTable.addCell(totalClaimsPaidDuringTheLastThreeMonthsValueTwo);

			document.add(claimsDetailsTable);

			// familyDetails table
			PdfPTable familyDetailsTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			familyDetailsTable.setWidthPercentage(100);

			// Add the familyDetails title row
			PdfPCell familyDetailsTitle = new PdfPCell(new Phrase("Family Details ( specify wherever applicable)"));
			familyDetailsTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
			familyDetailsTitle.setColspan(3);

			// Add internal padding for the familyDetailsTitle cell (10 points on all sides)
			familyDetailsTitle.setPadding(10);

			familyDetailsTable.addCell(familyDetailsTitle);

			// Row familyDetails 1
			PdfPCell familyDefinition = new PdfPCell(new Phrase(
					"Family Definition whether additional children covered, whether additional relationships covered, like brother sister etc."));

			// Add internal padding for the familyDefinition cell (10 points on all sides)
			familyDefinition.setPadding(10);

			familyDetailsTable.addCell(familyDefinition);

			PdfPCell familyDefinitionValue = new PdfPCell(new Phrase("N/A"));
			familyDefinitionValue.setColspan(2);

			// Add internal padding for the familyDefinitionValue cell (10 points on all
			// sides)
			familyDefinitionValue.setPadding(10);

			familyDetailsTable.addCell(familyDefinitionValue);

			// Row familyDetails 2
			PdfPCell anyRevisionRequiredInFamily = new PdfPCell(new Phrase(
					"Any revision required in Family definition under renewal policy - please specify if yes."));

			// Add internal padding for the anyRevisionRequiredInFamily cell (10 points on
			// all sides)
			anyRevisionRequiredInFamily.setPadding(10);

			familyDetailsTable.addCell(anyRevisionRequiredInFamily);

			PdfPCell anyRevisionRequiredInFamilyValue = new PdfPCell(new Phrase("N/A"));
			anyRevisionRequiredInFamilyValue.setColspan(2);

			// Add internal padding for the anyRevisionRequiredInFamilyValue cell (10 points
			// on all sides)
			anyRevisionRequiredInFamilyValue.setPadding(10);

			familyDetailsTable.addCell(anyRevisionRequiredInFamilyValue);

			document.add(familyDetailsTable);

			// Corporate Buffer Details table
			PdfPTable corporateBufferDetailsTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			corporateBufferDetailsTable.setWidthPercentage(100);

			// Add the Corporate Buffer Details title row
			PdfPCell corporateBufferTitle = new PdfPCell(
					new Phrase("Corporate Buffer Details required under RenewalPolicy"));
			corporateBufferTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
			corporateBufferTitle.setColspan(3);

			// Add internal padding for the corporateBufferTitle cell (10 points on all
			// sides)
			corporateBufferTitle.setPadding(10);

			corporateBufferDetailsTable.addCell(corporateBufferTitle);

			// Row Corporate Buffer Details 1
			PdfPCell perFamilyMaximumSI = new PdfPCell(new Phrase("Per Family Maximum SI for Corporate Buffer"));

			// Add internal padding for the perFamilyMaximumSI cell (10 points on all sides)
			perFamilyMaximumSI.setPadding(10);

			corporateBufferDetailsTable.addCell(perFamilyMaximumSI);

			PdfPCell perFamilyMaximumSIValue = new PdfPCell(new Phrase(""));

			// Add internal padding for the perFamilyMaximumSIValue cell (10 points on all
			// sides)
			perFamilyMaximumSIValue.setPadding(10);

			perFamilyMaximumSIValue.setColspan(2);
			corporateBufferDetailsTable.addCell(perFamilyMaximumSIValue);

			// Row Corporate Buffer Details 2
			PdfPCell maximumNumberOfcases = new PdfPCell(new Phrase(
					"Maximum Number of cases during the Policy period for Corporate Buffer if same is to be capped"));

			// Add internal padding for the maximumNumberOfcases cell (10 points on all
			// sides)
			maximumNumberOfcases.setPadding(10);

			corporateBufferDetailsTable.addCell(maximumNumberOfcases);

			PdfPCell maximumNumberOfcasesValue = new PdfPCell(new Phrase(""));

			// Add internal padding for the maximumNumberOfcasesValue cell (10 points on all
			// sides)
			maximumNumberOfcasesValue.setPadding(10);

			maximumNumberOfcasesValue.setColspan(2);
			corporateBufferDetailsTable.addCell(maximumNumberOfcasesValue);

			document.add(corporateBufferDetailsTable);

			document.newPage();

			// Create a two-column table
			PdfPTable contentTable = new PdfPTable(new float[] { 1.4f, 1, 1.2f });
			contentTable.setWidthPercentage(100);

			PdfPCell contentCell = new PdfPCell(new Phrase(
					"I/We hereby declare, on my behalf and on behalf of all persons proposed to be insured, that the above statements, answers and/or particulars given by me are true and complete in all respects to the best of my knowledge and that I/We am/are authorized to propose on behalf of these persons."));
			contentCell.setBorder(Rectangle.NO_BORDER);
			contentCell.setColspan(3);

			// Add internal padding for the palceAndDate cell (10 points on all sides)
			contentCell.setPadding(10);

			contentTable.addCell(contentCell);

			document.add(contentTable);

			document.add(new Paragraph("\n"));

			// Create a two-column table
			PdfPTable footerTable = new PdfPTable(new float[] { 1.4f, 1, 1.2f });
			footerTable.setWidthPercentage(100);

			PdfPCell palceAndDate = new PdfPCell(new Phrase("Place: Hyderabad\nDate: Tuesday, 01-Aug-2023"));
			palceAndDate.setBorder(Rectangle.NO_BORDER);

			// Add internal padding for the palceAndDate cell (10 points on all sides)
			palceAndDate.setPadding(10);

			footerTable.addCell(palceAndDate);

			// Column 2: Empty Cell
			PdfPCell footerEmptyCell = new PdfPCell();
			footerEmptyCell.setBorder(Rectangle.NO_BORDER);

			// Add internal padding for the footerEmptyCell cell (10 points on all sides)
			footerEmptyCell.setPadding(10);

			footerTable.addCell(footerEmptyCell);

			// Column 3: Wrapped Text
			PdfPCell signatureCell = new PdfPCell();
			signatureCell.setBorder(Rectangle.NO_BORDER); // Set no borders
			signatureCell.addElement(new Paragraph("Tuesday, 01-Aug-2023"));

			// Add internal padding for the signatureCell cell (10 points on all sides)
			signatureCell.setPadding(10);

			footerTable.addCell(signatureCell);

			document.add(footerTable);

			// Create a two-column table
			PdfPTable signatureTable = new PdfPTable(new float[] { 1.4f, 1, 1.2f });
			signatureTable.setWidthPercentage(100);

			PdfPCell signatureEmptyOne = new PdfPCell();
			signatureEmptyOne.setBorder(Rectangle.NO_BORDER);

			// Add internal padding for the signatureEmptyOne cell (10 points on all sides)
			signatureEmptyOne.setPadding(10);

			signatureTable.addCell(signatureEmptyOne);

			// Column 2: Empty Cell
			PdfPCell signatureEmptyTwo = new PdfPCell();
			signatureEmptyTwo.setBorder(Rectangle.NO_BORDER);

			// Add internal padding for the signatureEmptyTwo cell (10 points on all sides)
			signatureEmptyTwo.setPadding(10);

			signatureTable.addCell(signatureEmptyTwo);

			Image sigImage = Image.getInstance("C:\\Securisk\\securerisk_logo\\securisk.jpg"); // Replace with the path
			// to your image

			sigImage.scaleAbsolute(100, 100);
			PdfPCell signatureImage = new PdfPCell(sigImage, true);
			signatureImage.setBorder(Rectangle.NO_BORDER);

			// Add internal padding for the signatureImage cell (10 points on all sides)
			signatureImage.setPadding(10);

			signatureTable.addCell(signatureImage);

			document.add(signatureTable);

			// Create a two-column table
			PdfPTable stampTable = new PdfPTable(new float[] { 1.4f, 1, 1.2f });
			stampTable.setWidthPercentage(100);

			PdfPCell stampTitleEmpty = new PdfPCell();
			stampTitleEmpty.setBorder(Rectangle.NO_BORDER);

			// Add internal padding for the stampTitleEmpty cell (10 points on all sides)
			stampTitleEmpty.setPadding(10);

			stampTable.addCell(stampTitleEmpty);

			// Column 2: Empty Cell
			PdfPCell stampEmptyTwo = new PdfPCell();
			stampEmptyTwo.setBorder(Rectangle.NO_BORDER);

			// Add internal padding for the stampEmptyTwo cell (10 points on all sides)
			stampEmptyTwo.setPadding(10);

			stampTable.addCell(stampEmptyTwo);

			PdfPCell stamp = new PdfPCell(new Phrase("Signature of the Intermediary"));
			stamp.setBorder(Rectangle.NO_BORDER);

			// Add internal padding for the stamp cell (10 points on all sides)
			stamp.setPadding(5);

			stampTable.addCell(stamp);

			document.add(stampTable);

		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		document.close();

		return byteArrayOutputStream.toByteArray();

	}

	public byte[] generateEmployeeDataReport(CorporateDetailsEntity corporateDeails, ExpiryPolicyDetails expiry,
			ClaimsDetails claimsDetails, CoverageDetailsEntity coverageDetails, List<String> relation)
			throws IOException {

		List<String> filterValues = Arrays.asList("EMP", "Self", "Employee", "SELF", "employee", "emp", "self",
				"EMPLOYEE");

		// Create a Date object with the desired date
		Date date = new Date(); // August 1, 2023, 00:00:00 in milliseconds since epoch

		// Create a SimpleDateFormat object with the desired format
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd-MMM-yyyy", Locale.ENGLISH);

		// Format the date and print it
		String formattedDate = dateFormat.format(date);

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
			PdfPTable table = new PdfPTable(new float[] { 1, 1, 1.5f });
			table.setWidthPercentage(100);

			// Column 1: Image
			try (InputStream imageStream = resource.getInputStream()) {
				Image image = Image.getInstance(IOUtils.toByteArray(imageStream)); // Use IOUtils from Apache Commons IO
				image.scaleAbsolute(100, 100);
				PdfPCell cellImage = new PdfPCell(image, true);
				cellImage.setBorder(Rectangle.NO_BORDER);
				table.addCell(cellImage);
			} catch (Exception e) {
				e.printStackTrace(); // Handle exceptions appropriately
			}

			// Column 2: Empty Cell
			PdfPCell emptyCell = new PdfPCell();
			emptyCell.setBorder(Rectangle.NO_BORDER);
			table.addCell(emptyCell);

			// Column 3: Wrapped Text
			PdfPCell cellText = new PdfPCell();
			cellText.setBorder(Rectangle.NO_BORDER); // Set no borders
			cellText.addElement(new Paragraph("InsuredName : " + corporateDeails.getInsuredName()
					+ "\nIRDAformat\nPolicyType : " + corporateDeails.getPolicyType()));
			table.addCell(cellText);

			// Add the table to the document
			document.add(table);

			// Add some margin (space) after the table
			document.add(new Paragraph("\n")); // You can adjust the spacing as needed

			// Add a horizontal line
			LineSeparator line = new LineSeparator();
			line.setLineColor(BaseColor.BLACK);
			line.setLineWidth(2f);

			document.add(line);

			// Add some margin (space) after the table
			document.add(new Paragraph("\n\n\n")); // You can adjust the spacing as needed

			// Add a new table with 8 rows and 3 columns
			PdfPTable newTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			newTable.setWidthPercentage(100);

			// Add the title row
			PdfPCell titleCell = new PdfPCell(new Phrase("Insured Name"));
			titleCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			titleCell.setColspan(3); // Set colspan to 3
			titleCell.setPadding(10);
			newTable.addCell(titleCell);

			// Row - 1
			PdfPCell nameOfInsured = new PdfPCell(new Phrase("Name of Insured/ Proposer"));
			nameOfInsured.setPadding(10);
			newTable.addCell(nameOfInsured);

			PdfPCell nameOfInsuredValue = new PdfPCell(new Phrase(corporateDeails.getInsuredName()));
			nameOfInsuredValue.setColspan(2);
			nameOfInsuredValue.setPadding(10);
			newTable.addCell(nameOfInsuredValue);

			// Row - 2
			PdfPCell addressOfInsured = new PdfPCell(new Phrase("Address of Insured/ Proposer"));
			addressOfInsured.setPadding(10);
			newTable.addCell(addressOfInsured);

			PdfPCell addressOfInsuredValue = new PdfPCell(new Phrase(corporateDeails.getAddress()));
			addressOfInsuredValue.setColspan(2);
			addressOfInsuredValue.setPadding(10);
			newTable.addCell(addressOfInsuredValue);

			// Row - 3
			PdfPCell businessOfInsured = new PdfPCell(new Phrase("Business of Insured/ Proposer"));
			businessOfInsured.setPadding(10);
			newTable.addCell(businessOfInsured);

			String customNobValue = null;
			if (corporateDeails.getNob().equalsIgnoreCase("Custom")) {
				customNobValue = " Custom " + "(" + corporateDeails.getNobCustom() + ")";
			} else {
				customNobValue = corporateDeails.getNob();
			}

			PdfPCell businessOfInsuredValue = new PdfPCell(new Phrase(customNobValue));
			businessOfInsuredValue.setColspan(2);
			businessOfInsuredValue.setPadding(10);
			newTable.addCell(businessOfInsuredValue);

			// Row - 4
			PdfPCell contactPersonAtInsured = new PdfPCell(new Phrase("Contact Person at Insured"));
			contactPersonAtInsured.setPadding(10);
			newTable.addCell(contactPersonAtInsured);

			PdfPCell contactPersonAtInsuredValue = new PdfPCell(
					new Phrase(corporateDeails != null ? corporateDeails.getContactName() : "N/A"));
			contactPersonAtInsuredValue.setColspan(2);
			contactPersonAtInsuredValue.setPadding(10);
			newTable.addCell(contactPersonAtInsuredValue);

			// Row - 5
			PdfPCell phoneAndEamil = new PdfPCell(new Phrase("Phone no. and E-mail ID"));
			phoneAndEamil.setPadding(10);
			newTable.addCell(phoneAndEamil);

			PdfPCell phoneValue = new PdfPCell(new Phrase(corporateDeails.getPhNo()));
			phoneValue.setPadding(10);
			newTable.addCell(phoneValue);

			PdfPCell emailValue = new PdfPCell(new Phrase(corporateDeails.getEmail()));
			emailValue.setPadding(10);
			newTable.addCell(emailValue);

			// Row - 6
			PdfPCell employerEmployeeRelationship = new PdfPCell(new Phrase("Employer-Employee relationship"));
			employerEmployeeRelationship.setPadding(10);
			newTable.addCell(employerEmployeeRelationship);

			PdfPCell employerEmployeeRelationshipValue = new PdfPCell(new Phrase("N/A"));
			employerEmployeeRelationshipValue.setColspan(2);
			employerEmployeeRelationshipValue.setPadding(10); // Add padding
			newTable.addCell(employerEmployeeRelationshipValue);

			// Row - 7
			PdfPCell ifNoSpecifyRelationship = new PdfPCell(new Phrase("If No, specify relationship"));
			ifNoSpecifyRelationship.setPadding(10); // Add padding
			newTable.addCell(ifNoSpecifyRelationship);

			PdfPCell ifNoSpecifyRelationshipValue = new PdfPCell(new Phrase("N/A"));
			ifNoSpecifyRelationshipValue.setColspan(2);
			ifNoSpecifyRelationshipValue.setPadding(10); // Add padding
			newTable.addCell(ifNoSpecifyRelationshipValue);

			// Add the new table to the document
			document.add(newTable);

			// Add a new table intermediaryDetailsTable
			PdfPTable intermediaryDetailsTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			intermediaryDetailsTable.setWidthPercentage(100);

			// Add the title row
			PdfPCell interTitle = new PdfPCell(new Phrase("Intermediary Details"));
			interTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
			interTitle.setColspan(3); // Set colspan to 3
			interTitle.setPadding(10);
			intermediaryDetailsTable.addCell(interTitle);

			//
			PdfPCell nameOfTheIntermediary = new PdfPCell(
					new Phrase("Name of the Intermediary (Existing & New if applicable)"));
			nameOfTheIntermediary.setPadding(10);
			intermediaryDetailsTable.addCell(nameOfTheIntermediary);

			PdfPCell nameOfTheIntermediaryValue = new PdfPCell(new Phrase(corporateDeails.getIntermediaryName()));
			nameOfTheIntermediaryValue.setColspan(2);
			nameOfTheIntermediaryValue.setPadding(10);
			intermediaryDetailsTable.addCell(nameOfTheIntermediaryValue);

			// Add the new table to the document
			document.add(intermediaryDetailsTable);

			// Add a new table TPA DETAILS
			PdfPTable tpaTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			tpaTable.setWidthPercentage(100);

			// Add the title row
			PdfPCell tpaTitle = new PdfPCell(new Phrase("TPA Details"));
			tpaTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
			tpaTitle.setColspan(3); // Set colspan to 3

			// Add internal padding for the title row cell (10 points on all sides)
			tpaTitle.setPadding(10);

			tpaTable.addCell(tpaTitle);

			// Row - TAP 1
			PdfPCell tpaNameAndAddress = new PdfPCell(new Phrase("Tpa Name"));

			// Add internal padding for the tpaNameAndAddress cell (10 points on all sides)
			tpaNameAndAddress.setPadding(10);

			tpaTable.addCell(tpaNameAndAddress);

			PdfPCell tpaNameAndAddressValue = new PdfPCell(new Phrase((corporateDeails.getTpaName().equals("") ? "N/A"
					: corporateDeails.getTpaName() == null ? "N/A" : corporateDeails.getTpaName())));
//          + ""
//          + (corporateDeails.getTpaContactName().equals("") ? "N/A"
//                  : corporateDeails.getTpaContactName() == null ? "N/A"
//                          : ", "+corporateDeails.getTpaContactName())))
			tpaNameAndAddressValue.setColspan(2);

			// Add internal padding for the tpaNameAndAddressValue cell (10 points on all
			// sides)
			tpaNameAndAddressValue.setPadding(10);

			tpaTable.addCell(tpaNameAndAddressValue);

			// Row - TAP 2
			PdfPCell tpaContactDetails = new PdfPCell(new Phrase("Contact Details"));

			// Add internal padding for the tpaContactDetails cell (10 points on all sides)
			tpaContactDetails.setPadding(10);

			tpaTable.addCell(tpaContactDetails);

			PdfPCell tpaContactDetailsValue = new PdfPCell(new Phrase((corporateDeails.getTpaPhNo().equals("") ? ""
					: corporateDeails.getTpaPhNo() == null ? "" : corporateDeails.getTpaPhNo()) + " "
					+ (corporateDeails.getTpaEmail().equals("") ? "N/A"
							: corporateDeails.getTpaEmail() == null ? "N/A" : ", " + corporateDeails.getTpaEmail())));
			tpaContactDetailsValue.setColspan(2);

			// Add internal padding for the tpaContactDetailsValue cell (10 points on all
			// sides)
			tpaContactDetailsValue.setPadding(10);

			tpaTable.addCell(tpaContactDetailsValue);

			// Add the new table to the document
			document.add(tpaTable);

			// Expiring Policy Details table
			PdfPTable policyTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			policyTable.setWidthPercentage(100);

			// Add the policy title row
			PdfPCell policyTitle = new PdfPCell(new Phrase("Expiring Policy Details"));
			policyTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
			policyTitle.setColspan(3); // Set colspan to 3

			// Add internal padding for the policy title row cell (10 points on all sides)
			policyTitle.setPadding(10);

			policyTable.addCell(policyTitle);

			// Row - policy 1
			PdfPCell periodOfInsurance = new PdfPCell(
					new Phrase("Period of Insurance and Policy Number (Inception Date and Expiry Date)"));

			// Add internal padding for the periodOfInsurance cell (10 points on all sides)
			periodOfInsurance.setPadding(10);

			policyTable.addCell(periodOfInsurance);

			PdfPCell firstCol = new PdfPCell(
					new Phrase((expiry != null ? String.valueOf(expiry.getStartPeriod()).substring(0, 10) : "N/A")));

			// Add internal padding for the firstCol cell (10 points on all sides)
			firstCol.setPadding(10);

			policyTable.addCell(firstCol);

			PdfPCell secondCol = new PdfPCell(
					new Phrase((expiry != null ? String.valueOf(expiry.getEndPeriod()).substring(0, 10) : "N/A")));

			// Add internal padding for the secondCol cell (10 points on all sides)
			secondCol.setPadding(10);

			policyTable.addCell(secondCol);

			document.add(policyTable);

			document.newPage();

			// Policy copy with terms/ conditions including extensions is to be mandatorily
			// provided by the Proposer table
			PdfPTable policyCopyTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			policyCopyTable.setWidthPercentage(100);

			// Add the policy title row
			PdfPCell policyCopyTitle = new PdfPCell(new Phrase(
					"Policy copy with terms/ conditions including extensions is to be mandatorily provided by the Proposer"));
			policyCopyTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
			policyCopyTitle.setColspan(3); // Set colspan to 3

			// Add internal padding for the policyCopyTitle cell (10 points on all sides)
			policyCopyTitle.setPadding(10);

			policyCopyTable.addCell(policyCopyTitle);

			// Row - policy copy 1
			PdfPCell policyType = new PdfPCell(new Phrase("Policy type"));

			// Add internal padding for the policyType cell (10 points on all sides)
			policyType.setPadding(10);

			policyCopyTable.addCell(policyType);

			PdfPCell policyTypeValue = new PdfPCell(new Phrase(corporateDeails.getPolicyType()));
			policyTypeValue.setColspan(2);

			// Add internal padding for the policyTypeValue cell (10 points on all sides)
			policyTypeValue.setPadding(10);

			policyCopyTable.addCell(policyTypeValue);

			// Row - policy copy 2
			PdfPCell premiumPaid = new PdfPCell(new Phrase("Premium paid at inception (exclusive of Service Tax)"));
			premiumPaid.setPadding(10);
			policyCopyTable.addCell(premiumPaid);

			// Convert premium paid value to integer
			String premiumPaidInt = String.valueOf(expiry != null ? expiry.getPremiumPaidInception() : 0);
			PdfPCell premiumPaidValue = new PdfPCell(
					new Phrase(corporateDeails.getPolicyType().equalsIgnoreCase("Fresh") ? "N/A"
							: String.valueOf(premiumPaidInt)));
			premiumPaidValue.setColspan(2);
			premiumPaidValue.setPadding(10);
			policyCopyTable.addCell(premiumPaidValue);

			// Row - policy copy 3
			PdfPCell premiumAddition = new PdfPCell(new Phrase("Premium addition during the year"));
			premiumAddition.setPadding(10);
			policyCopyTable.addCell(premiumAddition);

			// Convert premium addition value to integer
			String premiumAdditionInt = expiry != null ? expiry.getAdditionPremium() : " ";
			PdfPCell premiumAdditionValue = new PdfPCell(
					new Phrase(corporateDeails.getPolicyType().equalsIgnoreCase("Fresh") ? "N/A"
							: String.valueOf(premiumAdditionInt)));
			premiumAdditionValue.setColspan(2);
			premiumAdditionValue.setPadding(10);
			policyCopyTable.addCell(premiumAdditionValue);

			// Row - policy copy 4
			PdfPCell premiumDeletion = new PdfPCell(new Phrase("Premium deletion during the year"));
			premiumDeletion.setPadding(10);
			policyCopyTable.addCell(premiumDeletion);

			// Convert premium deletion value to integer
			String premiumDeletionInt = expiry != null ? expiry.getDeletionPremium() : " ";
			PdfPCell premiumDeletionValue = new PdfPCell(
					new Phrase(corporateDeails.getPolicyType().equalsIgnoreCase("Fresh") ? "N/A"
							: String.valueOf(premiumDeletionInt)));
			premiumDeletionValue.setColspan(2);
			premiumDeletionValue.setPadding(10);
			policyCopyTable.addCell(premiumDeletionValue);

			// Row - policy copy 5
			PdfPCell finalPremium = new PdfPCell(
					new Phrase("Final Premium collected (exclusive of Service Tax) as on date to be Specified."));
			finalPremium.setPadding(10);
			policyCopyTable.addCell(finalPremium);

			PdfPCell finalPremiumValue = new PdfPCell(new Phrase("N/A"));
			finalPremiumValue.setColspan(2);
			finalPremiumValue.setPadding(10);
			policyCopyTable.addCell(finalPremiumValue);

			// Row - policy copy 6
			PdfPCell forHowManyYears = new PdfPCell(new Phrase("For how many years policy has been active"));
			forHowManyYears.setPadding(10);
			policyCopyTable.addCell(forHowManyYears);

			// Convert active years value to integer
			String activeYearsInt = expiry != null ? expiry.getActiveYears() : " ";
			PdfPCell forHowManyYearsValue = new PdfPCell(
					new Phrase(corporateDeails.getPolicyType().equalsIgnoreCase("Fresh") ? "N/A"
							: String.valueOf(activeYearsInt)));
			forHowManyYearsValue.setColspan(2);
			forHowManyYearsValue.setPadding(10);
			policyCopyTable.addCell(forHowManyYearsValue);

			document.add(policyCopyTable);

			// memberDetailsTable table
			PdfPTable memberDetailsTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			memberDetailsTable.setWidthPercentage(100);

			// Add the Member Details title row
			PdfPCell memberDetailsTitle = new PdfPCell(new Phrase("Member Details"));
			memberDetailsTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
			memberDetailsTitle.setColspan(3); // Set colspan to 3

			// Add internal padding for the memberDetailsTitle cell (10 points on all sides)
			memberDetailsTitle.setPadding(10);

			memberDetailsTable.addCell(memberDetailsTitle);

			// ---------------------------
			PdfPCell expiringYearValue = new PdfPCell(new Phrase("Expiring Details"));

			// Add internal padding for the expiringYearValue cell (10 points on all sides)
			expiringYearValue.setPadding(10);

			memberDetailsTable.addCell(expiringYearValue);
			PdfPCell emptyValue1 = new PdfPCell(new Phrase("Employee"));

			// Add internal padding for the emptyValue cell (10 points on all sides)
			emptyValue1.setPadding(10);

			memberDetailsTable.addCell(emptyValue1);

			PdfPCell emptyValueTwo1 = new PdfPCell(new Phrase("Dependent"));

			// Add internal padding for the emptyValueTwo cell (10 points on all sides)
			emptyValueTwo1.setPadding(10);

			memberDetailsTable.addCell(emptyValueTwo1);

			// Row - Member Details 2
			PdfPCell basisOfPremium = new PdfPCell(
					new Phrase("Basis of Premium Charging -per Family or per Member covered"));

			// Add internal padding for the basisOfPremium cell (10 points on all sides)
			basisOfPremium.setPadding(10);

			memberDetailsTable.addCell(basisOfPremium);

			PdfPCell basisOfPremiumValue = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the basisOfPremiumValue cell (10 points on all
			// sides)
			basisOfPremiumValue.setPadding(10);

			memberDetailsTable.addCell(basisOfPremiumValue);

			PdfPCell basisOfPremiumValueTwo = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the basisOfPremiumValueTwo cell (10 points on all
			// sides)
			basisOfPremiumValueTwo.setPadding(10);

			memberDetailsTable.addCell(basisOfPremiumValueTwo);

			// Row - Member Details 3
			PdfPCell membersAtInception = new PdfPCell(new Phrase("No. of Members at inception"));

			// Add internal padding for the membersAtInception cell (10 points on all sides)
			membersAtInception.setPadding(10);

			memberDetailsTable.addCell(membersAtInception);

			String membersNoInception = "0";
			String additionPremium = "0";
			String deletionPremium = "0";
			String totalMembers = "0";
			String membersNoInceptionDependents = "0";
			String additionPremiumDependents = "0";
			String deletionPremiumDependents = "0";
			String totalMembersDependents = "0";
			if (expiry != null) {
				membersNoInception = expiry.getMembersNoInception();
				additionPremium = expiry.getAdditions();
				deletionPremium = expiry.getDeletions();
				totalMembers = expiry.getTotalMembers();
				membersNoInceptionDependents = expiry.getMembersNoInceptionForDependents();
				additionPremiumDependents = expiry.getAdditionsForDependents();
				deletionPremiumDependents = expiry.getDeletionsForDependents();
				totalMembersDependents = expiry.getTotalMembersForDependents();
			}

			PdfPCell membersAtInceptionValue = new PdfPCell(new Phrase(
					corporateDeails.getPolicyType().equalsIgnoreCase("Fresh") ? "Fresh" : membersNoInception));

			// Add internal padding for the membersAtInceptionValue cell (10 points on all
			// sides)
			membersAtInceptionValue.setPadding(10);

			memberDetailsTable.addCell(membersAtInceptionValue);

			PdfPCell membersAtInceptionValueTwo = new PdfPCell(
					new Phrase(corporateDeails.getPolicyType().equalsIgnoreCase("Fresh") ? "Fresh"
							: membersNoInceptionDependents));

			// Add internal padding for the membersAtInceptionValueTwo cell (10 points on
			// all sides)
			membersAtInceptionValueTwo.setPadding(10);

			memberDetailsTable.addCell(membersAtInceptionValueTwo);

			// Row - Member Details 4
			PdfPCell additionDuringTheYear = new PdfPCell(new Phrase("Addition during the year"));

			// Add internal padding for the additionDuringTheYear cell (10 points on all
			// sides)
			additionDuringTheYear.setPadding(10);

			memberDetailsTable.addCell(additionDuringTheYear);

			PdfPCell additionDuringTheYearValue = new PdfPCell(new Phrase(
					new Phrase(corporateDeails.getPolicyType().equalsIgnoreCase("Fresh") ? "Fresh" : additionPremium)));

			// Add internal padding for the additionDuringTheYearValue cell (10 points on
			// all sides)
			additionDuringTheYearValue.setPadding(10);

			memberDetailsTable.addCell(additionDuringTheYearValue);

			PdfPCell additionDuringTheYearValueTwo = new PdfPCell(new Phrase(new Phrase(
					corporateDeails.getPolicyType().equalsIgnoreCase("Fresh") ? "Fresh" : additionPremiumDependents)));

			// Add internal padding for the additionDuringTheYearValueTwo cell (10 points on
			// all sides)
			additionDuringTheYearValueTwo.setPadding(10);

			memberDetailsTable.addCell(additionDuringTheYearValueTwo);

			// Row - Member Details 5
			PdfPCell deletionDuringTheYear = new PdfPCell(new Phrase("Deletion during the year"));

			// Add internal padding for the deletionDuringTheYear cell (10 points on all
			// sides)
			deletionDuringTheYear.setPadding(10);

			memberDetailsTable.addCell(deletionDuringTheYear);

			PdfPCell deletionDuringTheYearValue = new PdfPCell(new Phrase(
					new Phrase(corporateDeails.getPolicyType().equalsIgnoreCase("Fresh") ? "Fresh" : deletionPremium)));

			// Add internal padding for the deletionDuringTheYearValue cell (10 points on
			// all sides)
			deletionDuringTheYearValue.setPadding(10);

			memberDetailsTable.addCell(deletionDuringTheYearValue);

			PdfPCell deletionDuringTheYearValueTwo = new PdfPCell(new Phrase(new Phrase(
					corporateDeails.getPolicyType().equalsIgnoreCase("Fresh") ? "Fresh" : deletionPremiumDependents)));

			// Add internal padding for the deletionDuringTheYearValueTwo cell (10 points on
			// all sides)
			deletionDuringTheYearValueTwo.setPadding(10);

			memberDetailsTable.addCell(deletionDuringTheYearValueTwo);

			// Row - Member Details 6
			PdfPCell membersAtExpiry = new PdfPCell(
					new Phrase("Final no. of Members at expiry (With complete enrollment date)"));

			// Add internal padding for the membersAtExpiry cell (10 points on all sides)
			membersAtExpiry.setPadding(10);

			memberDetailsTable.addCell(membersAtExpiry);

			PdfPCell membersAtExpiryValue = new PdfPCell(new Phrase(
					new Phrase(corporateDeails.getPolicyType().equalsIgnoreCase("Fresh") ? "Fresh" : totalMembers)));

			// Add internal padding for the membersAtExpiryValue cell (10 points on all
			// sides)
			membersAtExpiryValue.setPadding(10);

			memberDetailsTable.addCell(membersAtExpiryValue);

			PdfPCell membersAtExpiryValueTwo = new PdfPCell(new Phrase(
					corporateDeails.getPolicyType().equalsIgnoreCase("Fresh") ? "Fresh" : totalMembersDependents));

			// Add internal padding for the membersAtExpiryValueTwo cell (10 points on all
			// sides)
			membersAtExpiryValueTwo.setPadding(10);

			memberDetailsTable.addCell(membersAtExpiryValueTwo);

			document.add(memberDetailsTable);

			//

			// RenewalYear table
			PdfPTable renewalTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			renewalTable.setWidthPercentage(100);

			// Add the RenewalYear title row
			PdfPCell renewalTitle = new PdfPCell(new Phrase("Renewal Year"));
			renewalTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
			renewalTitle.setColspan(3); // Set colspan to 3

			// Add internal padding for the renewalTitle cell (10 points on all sides)
			renewalTitle.setPadding(10);

			renewalTable.addCell(renewalTitle);

			// Row - RenewalYear 1
			PdfPCell empty = new PdfPCell(new Phrase(""));

			// Add internal padding for the empty cell (10 points on all sides)
			empty.setPadding(10);

			renewalTable.addCell(empty);

			PdfPCell emptyValue = new PdfPCell(new Phrase("Employee"));

			// Add internal padding for the emptyValue cell (10 points on all sides)
			emptyValue.setPadding(10);

			renewalTable.addCell(emptyValue);

			PdfPCell emptyValueTwo = new PdfPCell(new Phrase("Dependent"));

			// Add internal padding for the emptyValueTwo cell (10 points on all sides)
			emptyValueTwo.setPadding(10);

			renewalTable.addCell(emptyValueTwo);

			// Row - RenewalYear 2
			PdfPCell noOfMembersToBeCovered = new PdfPCell(new Phrase("No of Members to be covered"));

			// Add internal padding for the noOfMembersToBeCovered cell (10 points on all
			// sides)
			noOfMembersToBeCovered.setPadding(10);

			renewalTable.addCell(noOfMembersToBeCovered);

			long employeeSum =0;
			if (!relation.isEmpty()) {
			    employeeSum = relation.stream()
			        .filter(str -> filterValues.stream().anyMatch(str::equalsIgnoreCase))
			        .count();
			} 

			PdfPCell noOfMembersToBeCoveredValue = new PdfPCell(new Phrase(Long.toString(employeeSum)));

			// Add internal padding for the noOfMembersToBeCoveredValue cell (10 points on
			// all sides)
			noOfMembersToBeCoveredValue.setPadding(10);

			renewalTable.addCell(noOfMembersToBeCoveredValue);

			long totalLength = relation.size() - employeeSum;

			PdfPCell noOfMembersToBeCoveredValueValueTwo = new PdfPCell(new Phrase(String.valueOf(totalLength)));

			// Add internal padding for the noOfMembersToBeCoveredValueValueTwo cell (10
			// points on all sides)
			noOfMembersToBeCoveredValueValueTwo.setPadding(10);

			renewalTable.addCell(noOfMembersToBeCoveredValueValueTwo);

			// Row - RenewalYear 3
			PdfPCell pleaseSpecifySumInsuredRequired = new PdfPCell(new Phrase("Please Specify Sum Insured required"));
			pleaseSpecifySumInsuredRequired.setPadding(10);
			renewalTable.addCell(pleaseSpecifySumInsuredRequired);

			// Declare and initialize familyDefication list
			List<Double> familyDefication = new ArrayList<>();

			// Add values from coverageDetails lists to familyDefication
			familyDefication.addAll(coverageDetails.getFamilyDefication13Amount());
			familyDefication.addAll(coverageDetails.getFamilyDefication15Amount());
			familyDefication.addAll(coverageDetails.getFamilyDeficationParentsAmount());

			List<Integer> familyDeficationInt = new ArrayList<>(); // List to store integer values

			// Convert doubles to integers and add to the list
			for (Double value : familyDefication) {
				familyDeficationInt.add(value.intValue());
			}

			// Convert integer list to string for display
			String resultString = familyDeficationInt.stream().map(String::valueOf).collect(Collectors.joining(", "));

			PdfPCell pleaseSpecifySumInsuredRequiredValue = new PdfPCell(new Phrase(resultString));
			pleaseSpecifySumInsuredRequiredValue.setColspan(2);
			pleaseSpecifySumInsuredRequiredValue.setPadding(10);
			renewalTable.addCell(pleaseSpecifySumInsuredRequiredValue);

			// Row - RenewalYear 4
			PdfPCell familiesToBeCovered = new PdfPCell(
					new Phrase(String.valueOf("If Family coverage then no of Families to be covered")));

			// Add internal padding for the familiesToBeCovered cell (10 points on all
			// sides)
			familiesToBeCovered.setPadding(10);

			renewalTable.addCell(familiesToBeCovered);

			PdfPCell familiesToBeCoveredValue = new PdfPCell(new Phrase(new Phrase(String.valueOf(employeeSum))));

			// Add internal padding for the familiesToBeCoveredValue cell (10 points on all
			// sides)
			familiesToBeCoveredValue.setPadding(10);

			renewalTable.addCell(familiesToBeCoveredValue);

			PdfPCell familiesToBeCoveredValueTwo = new PdfPCell(new Phrase(new Phrase(String.valueOf(totalLength))));

			// Add internal padding for the familiesToBeCoveredValueTwo cell (10 points on
			// all sides)
			familiesToBeCoveredValueTwo.setPadding(10);

			renewalTable.addCell(familiesToBeCoveredValueTwo);
			// Row - RenewalYear 5
			PdfPCell familyFloaterSumInsured = new PdfPCell(new Phrase("Family/ Floater Sum Insured"));

			// Add internal padding for the familyFloaterSumInsured cell (10 points on all
			// sides)
			familyFloaterSumInsured.setPadding(10);

			renewalTable.addCell(familyFloaterSumInsured);

			PdfPCell familyFloaterSumInsuredValue = new PdfPCell(new Phrase((coverageDetails.isFamilyDefication13()
					? "1+3 : " + coverageDetails.getFamilyDefication13Amount().stream().map(Double::intValue).toList()
					: "")
					+ ""
					+ (coverageDetails.isFamilyDefication15()
							? ", 1+5 : " + coverageDetails.getFamilyDefication15Amount().stream().map(Double::intValue)
									.toList()
							: "")
					+ ""
					+ (coverageDetails.isFamilyDeficationParents()
							? ", Parents : " + coverageDetails.getFamilyDeficationParentsAmount().stream()
									.map(Double::intValue).toList()
							: "")));
			familyFloaterSumInsuredValue.setColspan(2);

			// Add internal padding for the familyFloaterSumInsuredValue cell (10 points on
			// all sides)
			familyFloaterSumInsuredValue.setPadding(10);

			renewalTable.addCell(familyFloaterSumInsuredValue);

			document.add(renewalTable);

			document.newPage();

			// Claim Details as on under expiring table
			PdfPTable claimsDetailsTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			claimsDetailsTable.setWidthPercentage(100);

			// Add the Claim Details title row
			PdfPCell claimDetailsTitle = new PdfPCell(new Phrase("Claim Details as on under expiring policy:"));
			claimDetailsTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);

			// Add internal padding for the claimDetailsTitle cell (10 points on all sides)
			claimDetailsTitle.setPadding(10);

			claimsDetailsTable.addCell(claimDetailsTitle);

			// Add the Reimbursement title row
			PdfPCell cashlessTitle = new PdfPCell(new Phrase("Reimbursement:"));
			cashlessTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);

			// Add internal padding for the cashlessTitle cell (10 points on all sides)
			cashlessTitle.setPadding(10);

			claimsDetailsTable.addCell(cashlessTitle);

			// Add the Cashless title row
			PdfPCell reimbursementTitle = new PdfPCell(new Phrase("Cashless:"));
			reimbursementTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);

			// Add internal padding for the reimbursementTitle cell (10 points on all sides)
			reimbursementTitle.setPadding(10);

			claimsDetailsTable.addCell(reimbursementTitle);

			// Row - Claim Details 1
			PdfPCell claimsPaidasOnDate = new PdfPCell(new Phrase("Claims paid as on date"));

			// Add internal padding for the claimsPaidasOnDate cell (10 points on all sides)
			claimsPaidasOnDate.setPadding(10);

			claimsDetailsTable.addCell(claimsPaidasOnDate);

			PdfPCell claimsPaidasOnDateValue = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the claimsPaidasOnDateValue cell (10 points on all
			// sides)
			claimsPaidasOnDateValue.setPadding(10);

			claimsDetailsTable.addCell(claimsPaidasOnDateValue);

			PdfPCell claimsPaidasOnDateValueTwo = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the claimsPaidasOnDateValueTwo cell (10 points on
			// all sides)
			claimsPaidasOnDateValueTwo.setPadding(10);

			claimsDetailsTable.addCell(claimsPaidasOnDateValueTwo);

			// Row - Claim Details 2
			PdfPCell claimsOutstandingAsOnDate = new PdfPCell(new Phrase("Claims outstanding as on date"));

			// Add internal padding for the claimsOutstandingAsOnDate cell (10 points on all
			// sides)
			claimsOutstandingAsOnDate.setPadding(10);

			claimsDetailsTable.addCell(claimsOutstandingAsOnDate);

			PdfPCell claimsOutstandingAsOnDateValue = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the claimsOutstandingAsOnDateValue cell (10 points
			// on all sides)
			claimsOutstandingAsOnDateValue.setPadding(10);

			claimsDetailsTable.addCell(claimsOutstandingAsOnDateValue);

			PdfPCell claimsOutstandingAsOnDateValueTwo = new PdfPCell(new Phrase("Fresh"));

			// Add internal padding for the claimsOutstandingAsOnDateValueTwo cell (10
			// points on all sides)
			claimsOutstandingAsOnDateValueTwo.setPadding(10);

			claimsDetailsTable.addCell(claimsOutstandingAsOnDateValueTwo);

			// Row - Claim Details 3
			PdfPCell ifOPDcoverGiveThenMentionOPDClaimsSeparately = new PdfPCell(
					new Phrase("If OPDcover given, then mention OPD claims separately"));

			// Add internal padding for the ifOPDcoverGiveThenMentionOPDClaimsSeparately
			// cell (10 points on all sides)
			ifOPDcoverGiveThenMentionOPDClaimsSeparately.setPadding(10);

			claimsDetailsTable.addCell(ifOPDcoverGiveThenMentionOPDClaimsSeparately);

			PdfPCell ifOPDcoverGiveThenMentionOPDClaimsSeparatelyValue = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the
			// ifOPDcoverGiveThenMentionOPDClaimsSeparatelyValue cell (10 points on all
			// sides)
			ifOPDcoverGiveThenMentionOPDClaimsSeparatelyValue.setPadding(10);

			claimsDetailsTable.addCell(ifOPDcoverGiveThenMentionOPDClaimsSeparatelyValue);

			PdfPCell ifOPDcoverGiveThenMentionOPDClaimsSeparatelyValueTwo = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the
			// ifOPDcoverGiveThenMentionOPDClaimsSeparatelyValueTwo cell (10 points on all
			// sides)
			ifOPDcoverGiveThenMentionOPDClaimsSeparatelyValueTwo.setPadding(10);

			claimsDetailsTable.addCell(ifOPDcoverGiveThenMentionOPDClaimsSeparatelyValueTwo);

			// Row - Claim Details 4
			PdfPCell detailsOfClaimsPaidUnderCorporate = new PdfPCell(
					new Phrase("Details of Claims paid under Corporate Buffer Facility as on ()"));

			// Add internal padding for the detailsOfClaimsPaidUnderCorporate cell (10
			// points on all sides)
			detailsOfClaimsPaidUnderCorporate.setPadding(10);

			claimsDetailsTable.addCell(detailsOfClaimsPaidUnderCorporate);

			PdfPCell detailsOfClaimsPaidUnderCorporateValue = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the detailsOfClaimsPaidUnderCorporateValue cell (10
			// points on all sides)
			detailsOfClaimsPaidUnderCorporateValue.setPadding(10);

			claimsDetailsTable.addCell(detailsOfClaimsPaidUnderCorporateValue);

			PdfPCell detailsOfClaimsPaidUnderCorporateValueTwo = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the detailsOfClaimsPaidUnderCorporateValueTwo cell
			// (10 points on all sides)
			detailsOfClaimsPaidUnderCorporateValueTwo.setPadding(10);

			claimsDetailsTable.addCell(detailsOfClaimsPaidUnderCorporateValueTwo);

			// Row - Claim Details 5
			PdfPCell claimsPaidAsOnDate = new PdfPCell(new Phrase("Claims Paid as on Date"));

			// Add internal padding for the claimsPaidAsOnDate cell (10 points on all sides)
			claimsPaidAsOnDate.setPadding(10);

			claimsDetailsTable.addCell(claimsPaidAsOnDate);

			PdfPCell claimsPaidAsOnDateValue = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the claimsPaidAsOnDateValue cell (10 points on all
			// sides)
			claimsPaidAsOnDateValue.setPadding(10);

			claimsDetailsTable.addCell(claimsPaidAsOnDateValue);

			PdfPCell claimsPaidAsOnDateValueTwo = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the claimsPaidAsOnDateValueTwo cell (10 points on
			// all sides)
			claimsPaidAsOnDateValueTwo.setPadding(10);

			claimsDetailsTable.addCell(claimsPaidAsOnDateValueTwo);

			// Row - Claim Details 6
			PdfPCell claimsOutstandingAsOnDateNew = new PdfPCell(new Phrase("Claims Outstanding as on date"));

			// Add internal padding for the claimsOutstandingAsOnDateNew cell (10 points on
			// all sides)
			claimsOutstandingAsOnDateNew.setPadding(10);

			claimsDetailsTable.addCell(claimsOutstandingAsOnDateNew);

			PdfPCell claimsOutstandingAsOnDateNewValue = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the claimsOutstandingAsOnDateNewValue cell (10
			// points on all sides)
			claimsOutstandingAsOnDateNewValue.setPadding(10);

			claimsDetailsTable.addCell(claimsOutstandingAsOnDateNewValue);

			PdfPCell claimsOutstandingAsOnDateNewValueTwo = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the claimsOutstandingAsOnDateNewValueTwo cell (10
			// points on all sides)
			claimsOutstandingAsOnDateNewValueTwo.setPadding(10);

			claimsDetailsTable.addCell(claimsOutstandingAsOnDateNewValueTwo);

			// Row - Claim Details 7
			PdfPCell totalClaimsPaid = new PdfPCell(new Phrase(
					"Total claims paid during the last two policy years immediately preceding the expiring year."));

			// Add internal padding for the totalClaimsPaid cell (10 points on all sides)
			totalClaimsPaid.setPadding(10);

			claimsDetailsTable.addCell(totalClaimsPaid);

			PdfPCell totalClaimsPaidValue = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the totalClaimsPaidValue cell (10 points on all
			// sides)
			totalClaimsPaidValue.setPadding(10);

			claimsDetailsTable.addCell(totalClaimsPaidValue);

			PdfPCell totalClaimsPaidValueTwo = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the totalClaimsPaidValueTwo cell (10 points on all
			// sides)
			totalClaimsPaidValueTwo.setPadding(10);

			claimsDetailsTable.addCell(totalClaimsPaidValueTwo);

			// Row - Claim Details 8
			PdfPCell totalClaimsPaidDuringTheLastThreeMonths = new PdfPCell(new Phrase(
					"Total claims paid during the last three months of two years of policy immediately preceding to the expiring year."));

			// Add internal padding for the totalClaimsPaidDuringTheLastThreeMonths cell (10
			// points on all sides)
			totalClaimsPaidDuringTheLastThreeMonths.setPadding(10);

			claimsDetailsTable.addCell(totalClaimsPaidDuringTheLastThreeMonths);

			PdfPCell totalClaimsPaidDuringTheLastThreeMonthsValue = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the totalClaimsPaidDuringTheLastThreeMonthsValue
			// cell (10 points on all sides)
			totalClaimsPaidDuringTheLastThreeMonthsValue.setPadding(10);

			claimsDetailsTable.addCell(totalClaimsPaidDuringTheLastThreeMonthsValue);

			PdfPCell totalClaimsPaidDuringTheLastThreeMonthsValueTwo = new PdfPCell(new Phrase("N/A"));

			// Add internal padding for the totalClaimsPaidDuringTheLastThreeMonthsValueTwo
			// cell (10 points on all sides)
			totalClaimsPaidDuringTheLastThreeMonthsValueTwo.setPadding(10);

			claimsDetailsTable.addCell(totalClaimsPaidDuringTheLastThreeMonthsValueTwo);

			document.add(claimsDetailsTable);

			// familyDetails table
			PdfPTable familyDetailsTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			familyDetailsTable.setWidthPercentage(100);

			// Add the familyDetails title row
			PdfPCell familyDetailsTitle = new PdfPCell(new Phrase("Family Details ( specify wherever applicable)"));
			familyDetailsTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
			familyDetailsTitle.setColspan(3);

			// Add internal padding for the familyDetailsTitle cell (10 points on all sides)
			familyDetailsTitle.setPadding(10);

			familyDetailsTable.addCell(familyDetailsTitle);

			// Row familyDetails 1
			PdfPCell familyDefinition = new PdfPCell(new Phrase(
					"Family Definition whether additional children covered, whether additional relationships covered, like brother sister etc."));

			// Add internal padding for the familyDefinition cell (10 points on all sides)
			familyDefinition.setPadding(10);

			familyDetailsTable.addCell(familyDefinition);

			PdfPCell familyDefinitionValue = new PdfPCell(new Phrase("N/A"));
			familyDefinitionValue.setColspan(2);

			// Add internal padding for the familyDefinitionValue cell (10 points on all
			// sides)
			familyDefinitionValue.setPadding(10);

			familyDetailsTable.addCell(familyDefinitionValue);

			// Row familyDetails 2
			PdfPCell anyRevisionRequiredInFamily = new PdfPCell(new Phrase(
					"Any revision required in Family definition under renewal policy - please specify if yes."));

			// Add internal padding for the anyRevisionRequiredInFamily cell (10 points on
			// all sides)
			anyRevisionRequiredInFamily.setPadding(10);

			familyDetailsTable.addCell(anyRevisionRequiredInFamily);

			PdfPCell anyRevisionRequiredInFamilyValue = new PdfPCell(new Phrase("N/A"));
			anyRevisionRequiredInFamilyValue.setColspan(2);

			// Add internal padding for the anyRevisionRequiredInFamilyValue cell (10 points
			// on all sides)
			anyRevisionRequiredInFamilyValue.setPadding(10);

			familyDetailsTable.addCell(anyRevisionRequiredInFamilyValue);

			document.add(familyDetailsTable);

			// Corporate Buffer Details table
			PdfPTable corporateBufferDetailsTable = new PdfPTable(new float[] { 1.4f, 1, 1 });
			corporateBufferDetailsTable.setWidthPercentage(100);

			// Add the Corporate Buffer Details title row
			PdfPCell corporateBufferTitle = new PdfPCell(
					new Phrase("Corporate Buffer Details required under RenewalPolicy"));
			corporateBufferTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
			corporateBufferTitle.setColspan(3);

			// Add internal padding for the corporateBufferTitle cell (10 points on all
			// sides)
			corporateBufferTitle.setPadding(10);

			corporateBufferDetailsTable.addCell(corporateBufferTitle);

			// Row Corporate Buffer Details 1
			PdfPCell perFamilyMaximumSI = new PdfPCell(new Phrase("Per Family Maximum SI for Corporate Buffer"));

			// Add internal padding for the perFamilyMaximumSI cell (10 points on all sides)
			perFamilyMaximumSI.setPadding(10);

			corporateBufferDetailsTable.addCell(perFamilyMaximumSI);

			String maxSumInsured = "0";
			String maxCasesNo = "0";
			if (claimsDetails != null) {
				maxSumInsured = claimsDetails.getMaxSumInsured();
				maxCasesNo = claimsDetails.getMaxCasesNo();
			}
			PdfPCell perFamilyMaximumSIValue = new PdfPCell(
					new Phrase(corporateDeails.getPolicyType().equalsIgnoreCase("Fresh") ? "N/A" : maxSumInsured));

			// Add internal padding for the perFamilyMaximumSIValue cell (10 points on all
			// sides)
			perFamilyMaximumSIValue.setPadding(10);

			perFamilyMaximumSIValue.setColspan(2);
			corporateBufferDetailsTable.addCell(perFamilyMaximumSIValue);

			// Row Corporate Buffer Details 2
			PdfPCell maximumNumberOfcases = new PdfPCell(new Phrase(
					"Maximum Number of cases during the Policy period for Corporate Buffer if same is to be capped"));

			// Add internal padding for the maximumNumberOfcases cell (10 points on all
			// sides)
			maximumNumberOfcases.setPadding(10);

			corporateBufferDetailsTable.addCell(maximumNumberOfcases);

			PdfPCell maximumNumberOfcasesValue = new PdfPCell(
					new Phrase(corporateDeails.getPolicyType().equalsIgnoreCase("Fresh") ? "N/A" : maxCasesNo));

			// Add internal padding for the maximumNumberOfcasesValue cell (10 points on all
			// sides)
			maximumNumberOfcasesValue.setPadding(10);

			maximumNumberOfcasesValue.setColspan(2);
			corporateBufferDetailsTable.addCell(maximumNumberOfcasesValue);

			document.add(corporateBufferDetailsTable);

			document.newPage();

			// Create a two-column table
			PdfPTable contentTable = new PdfPTable(new float[] { 1.4f, 1, 1.2f });
			contentTable.setWidthPercentage(100);

			PdfPCell contentCell = new PdfPCell(new Phrase(
					"I/We hereby declare, on my behalf and on behalf of all persons proposed to be insured, that the above statements, answers and/or particulars given by me are true and complete in all respects to the best of my knowledge and that I/We am/are authorized to propose on behalf of these persons."));
			contentCell.setBorder(Rectangle.NO_BORDER);
			contentCell.setColspan(3);

			// Add internal padding for the palceAndDate cell (10 points on all sides)
			contentCell.setPadding(10);

			contentTable.addCell(contentCell);

			document.add(contentTable);

			// document.add(new Paragraph(
			// "I/We hereby declare, on my behalf and on behalf of all persons proposed to
			// be insured, that the above statements, answers and/or particulars given by me
			// are true and complete in all respects to the best of my knowledge and that

			document.add(new Paragraph("\n"));

			// Create a two-column table
			PdfPTable footerTable = new PdfPTable(new float[] { 1.4f, 1, 1.2f });
			footerTable.setWidthPercentage(100);

			PdfPCell palceAndDate = new PdfPCell(
					new Phrase("Place: " + corporateDeails.getAddress() + "\nDate: " + formattedDate));
			palceAndDate.setBorder(Rectangle.NO_BORDER);

			// Add internal padding for the palceAndDate cell (10 points on all sides)
			palceAndDate.setPadding(10);

			footerTable.addCell(palceAndDate);

			// Column 2: Empty Cell
			PdfPCell footerEmptyCell = new PdfPCell();
			footerEmptyCell.setBorder(Rectangle.NO_BORDER);

			// Add internal padding for the footerEmptyCell cell (10 points on all sides)
			footerEmptyCell.setPadding(10);

			footerTable.addCell(footerEmptyCell);

			// Column 3: Wrapped Text
			PdfPCell signatureCell = new PdfPCell();
			signatureCell.setBorder(Rectangle.NO_BORDER); // Set no borders
			signatureCell.addElement(new Paragraph(formattedDate));

			// Add internal padding for the signatureCell cell (10 points on all sides)
			signatureCell.setPadding(10);

			footerTable.addCell(signatureCell);

			document.add(footerTable);

			// Create a two-column table
			PdfPTable signatureTable = new PdfPTable(new float[] { 1.4f, 1, 1.2f });
			signatureTable.setWidthPercentage(100);

			PdfPCell signatureEmptyOne = new PdfPCell();
			signatureEmptyOne.setBorder(Rectangle.NO_BORDER);

			// Add internal padding for the signatureEmptyOne cell (10 points on all sides)
			signatureEmptyOne.setPadding(10);

			signatureTable.addCell(signatureEmptyOne);

			// Column 2: Empty Cell
			PdfPCell signatureEmptyTwo = new PdfPCell();
			signatureEmptyTwo.setBorder(Rectangle.NO_BORDER);

			// Add internal padding for the signatureEmptyTwo cell (10 points on all sides)
			signatureEmptyTwo.setPadding(10);

			signatureTable.addCell(signatureEmptyTwo);

			InputStream imageStreamSignature = resource1.getInputStream();
			Image sigImage = Image.getInstance(IOUtils.toByteArray(imageStreamSignature)); // Replace with the path
			// to your image
			sigImage.scaleAbsolute(100, 100);
			PdfPCell signatureImage = new PdfPCell(sigImage, true);
			signatureImage.setBorder(Rectangle.NO_BORDER);

			// Add internal padding for the signatureImage cell (10 points on all sides)
			signatureImage.setPadding(10);

			signatureTable.addCell(signatureImage);

			document.add(signatureTable);

			// Create a two-column table
			PdfPTable stampTable = new PdfPTable(new float[] { 1.4f, 1, 1.2f });
			stampTable.setWidthPercentage(100);

			PdfPCell stampTitleEmpty = new PdfPCell();
			stampTitleEmpty.setBorder(Rectangle.NO_BORDER);

			// Add internal padding for the stampTitleEmpty cell (10 points on all sides)
			stampTitleEmpty.setPadding(10);

			stampTable.addCell(stampTitleEmpty);

			// Column 2: Empty Cell
			PdfPCell stampEmptyTwo = new PdfPCell();
			stampEmptyTwo.setBorder(Rectangle.NO_BORDER);

			// Add internal padding for the stampEmptyTwo cell (10 points on all sides)
			stampEmptyTwo.setPadding(10);

			stampTable.addCell(stampEmptyTwo);

			PdfPCell stamp = new PdfPCell(new Phrase("Signature of the Intermediary"));
			stamp.setBorder(Rectangle.NO_BORDER);

			// Add internal padding for the stamp cell (10 points on all sides)
			stamp.setPadding(5);

			stampTable.addCell(stamp);

			document.add(stampTable);

		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.close();

		return byteArrayOutputStream.toByteArray();
	}
}