package com.insure.rfq.service;

import java.util.List;

import com.insure.rfq.dto.Declarationandclaim_Submission_ImportantNotesDto;
import com.insure.rfq.dto.ImportantNotesDisplayDto;

public interface ClientListSubmission_ImportantNotesService {

     String sbmitClaim_Declarationandclaim_Submission_ImportantNotesCreation(Declarationandclaim_Submission_ImportantNotesDto  dto,

                                                                                 Long ClientId,Long ProductId, Long employeeId);
   List  <ImportantNotesDisplayDto> getAllImportantNotesDisplayDto();
}
