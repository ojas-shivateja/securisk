package com.insure.rfq.entity;

public enum RequiredDocument {


    ///////Cataract fields////////
    FILLED_AND_SIGNED_CLAIM_FORM("Filled and signed claim form"),
    ID_PROOF("Copy of cancelled cheque, Pan Card, Aadhar, or any Govt issued photo id proof of the patient and the employee"),
    BARCODE_LENS_STICKER_AND_INVOICE("Barcode Lens sticker and the invoice is mandatory"),
    DISCHARGE_SUMMARY("Original detailed discharge/daycare summary"),
    HOSPITAL_BILL("Original hospital main bill with complete breakup of the expenses incurred"),
    CASH_PAID_RECEIPTS("Original cash paid receipts, please note amount receipt on the letterhead is not accepted"),
    INVESTIGATION_REPORTS("Supporting investigation report proving the diagnosis (A scan report) and all the lab investigations reports with the prescription"),
    PRESCRIPTIONS("Supporting Prescriptions for all Lab investigations and pharmacy if any"),
    LAB_REPORTS("The lab reports has to have signature of the MD pathologist only (Supreme court instruction)"),
    REASON_FOR_NON_CASHLESS("Reason for not availing cashless in network hospital if the admission happened in our network hospital"),
    HOSPITAL_REGISTRATION_CERTIFICATE("Hospital registration certificate in case of non network of hospital"),


    ///////_MATERNItY////
    FILLED_AND_SIGNED_CLAIM_FORM_MATERNITY("Filled and signed claim form"),
    CANCELLED_CHEQUE_AND_ID_PROOF_MATERNITY("Copy of cancelled cheque of the primary insurer(employee) and any Govt issued photo ID Proof of the patient and the employee"),
    DISCHARGE_SUMMARY_WITH_GPLA_STATUS_MATERNITY("Original detailed discharge summary with GPLA status"),
    HOSPITAL_BILL_WITH_BREAKUP_MATERNITY("Original hospital main bill/final bill with complete breakup of all the expenses"),
    CASH_PAID_RECEIPTS_MATERNITY("Original pre-numbered cash paid receipts, please note amount receipt on the letterhead is not accepted under insurance claim settlement"),
    LAB_INVESTIGATION_REPORTS_MATERNITY("Lab investigations reports with Prescription are mandatory, all the reports have to have the signature of the MD pathologist only (Supreme Court order)"),
    EXTERNAL_MEDICINES_PRESCRIPTION_MATERNITY("Any Medicines purchased externally have to have clear Prescription attached to the purchases"),
    EMPLOYEE_PAN_CARD_MATERNITY("Pan card is mandatory of the employee wherever the claim amount is more than 1 Lakh"),
    REASON_FOR_NOT_AVAILING_CASHLESS_MATERNITY("Reason for not availing cashless in network hospital if the admission happened in our network hospital"),
    HOSPITAL_REGISTRATION_CERTIFICATE_MATERNITY("Hospital registration certificate has to be submitted in case the hospital is not in our network"),
    MATERNITY_AND_BABY_CLAIMS_SEPARATE_MATERNITY("Maternity and Baby claims have to be filed separately only"),
    CLAIMS_NOT_CLUBBED_TOGETHER_MATERNITY("In case of claims clubbed together will not be processed"),

    //////DIALYSIS fields////////////////////////////////

    FILLED_AND_SIGNED_CLAIM_FORM_DIALYSIS("Filled and signed claim form"),
    CANCELLED_CHEQUE_AND_ID_PROOF_DIALYSIS("Copy of cancelled cheque of the primary insured person (employee) and any govt issued photo ID Proof of the patient and the employee"),
    DISCHARGE_SUMMARY_WITH_TREATMENT_DIALYSIS("Original detailed discharge summary/daycare summary with complete treatment"),
    HOSPITAL_MAIN_BILL_WITH_BREAKUP_DIALYSIS("Original hospital main bill with complete breakup of all the expenses"),
    CASH_PAID_RECEIPTS_DIALYSIS("Original cash paid receipts, please note amount receipt on the letterhead is not accepted"),
    LAB_INVESTIGATION_REPORTS_WITH_PRESCRIPTION_DIALYSIS("Lab investigations reports with Prescription are mandatory, all the reports have to have signature of the MD pathologist only (Supreme Court order)"),
    EXTERNAL_MEDICINES_PRESCRIPTION_DIALYSIS("Any Medicines purchased externally has to have clear Prescription attached to the purchases"),
///////// Other General Claims fields////////

    FILLED_AND_SIGNED_CLAIM_FORM_GENERAL("Filled and signed claim form"),
    CANCELLED_CHEQUE_AND_ID_PROOF_GENERAL("Copy of cancelled cheque of the primary insured person (employee) and any govt issued photo ID Proof of the patient and the employee"),
    DISCHARGE_SUMMARY_WITH_TREATMENT_GENERAL("Original detailed discharge summary/daycare summary with complete treatment"),
    HOSPITAL_MAIN_BILL_WITH_BREAKUP_GENERAL("Original hospital main bill with complete breakup of all the expenses"),
    CASH_PAID_RECEIPTS_GENERAL("Original cash paid receipts, please note amount receipt on the letterhead is not accepted"),
    LAB_INVESTIGATION_REPORTS_WITH_PRESCRIPTION_GENERAL("Lab investigations reports with Prescription are mandatory, all the reports have to have signature of the MD pathologist only (Supreme Court order)"),
    EXTERNAL_MEDICINES_PRESCRIPTION_GENERAL("Any Medicines purchased externally has to have clear Prescription attached to the purchases"),
    SUPPORTING_PRESCRIPTION_FOR_LAB_INVESTIGATIONS_GENERAL("Supporting Prescription for all Lab investigations if done any"),
    PROOF_OF_DIAGNOSIS_AND_PROCEDURES_GENERAL("Proof of diagnosis, Investigation and X-RAY, CT or MRI, scopy procedures"),
    MLC_OR_FIR_FOR_ACCIDENT_CASES_GENERAL("In case of accident Cases MLC or FIR is mandatory (Medico Legal certificate at hospital), FIR-first information required with police station"),
    IMPLANT_STICKERS_AND_INVOICE_GENERAL("Implant stickers and the invoice wherever is used for surgeries, like Angioplasty, Joint replacement, and any ortho surgical cases"),
    EMPLOYEE_PAN_CARD_GENERAL("Pan card is mandatory of the employee wherever the claim amount is more than 1 Lakh"),
    INDORE_CASE_PAPERS_AND_BILLING_TARIFFS_GENERAL("Indore case papers (non-network and network) and the hospital billing tariffs (non-network)"),
    REASON_FOR_NOT_AVAILING_CASHLESS_GENERAL("Reason for not availing cashless in network hospital if the admission happened in our network hospital"),
    REASON_FOR_DELAYED_SUBMISSION_GENERAL("If you are submitting a claim for more than 30 days from the Discharge date, please share the reason for delay submission as it has to be referred to insurer and claim will be processed based on the advice from insurer"),
    MANDATORY_PRESCRIPTIONS_FOR_PRE_AND_POST_CLAIMS_GENERAL("Pre and post claims Every bill that is paid has to have mandatory prescriptions and it should be relevant to the admission claim"),
    ANY_OTHER_GENERAL("Any Other (Please Specify)");




    private final String description;

    RequiredDocument(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
