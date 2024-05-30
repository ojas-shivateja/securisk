package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Declarationandclaim_Submission_ImportantNotesDto {

    private  String proof_id_Type;
    private  String id_proof;           // select the proof NAME WE HAVE TO GIVE
    private  MultipartFile id_proofUpload;//  we have to upload the proof
    private  String user_detailsId;

}
