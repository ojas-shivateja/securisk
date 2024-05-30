package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ImportantNotesDisplayDto {
    private  Long id;
    private String user_detailsId;
    private String proof_id_Type;
    private String id_proof;           // select the proof NAME WE HAVE TO GIVE
    private String id_proofUpload;//  we have to upload the proof
}
