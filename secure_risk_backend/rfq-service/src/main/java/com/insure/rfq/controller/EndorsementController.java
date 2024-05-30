package com.insure.rfq.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.dto.DisplayAllEndorsementDto;
import com.insure.rfq.dto.EndorsementDto;
import com.insure.rfq.dto.UpdateEndorsementDetailsDto;
import com.insure.rfq.entity.EndorsementEntity;
import com.insure.rfq.repository.EndorsementRepository;
import com.insure.rfq.service.EndorsementService;

@RestController
@RequestMapping("/clientlist/endorsement")
@CrossOrigin(origins = "*")
public class EndorsementController {
    @Autowired
    private EndorsementService endorsementService;

    @Autowired
    private EndorsementRepository endorsementRepository;

    @PostMapping("/createEndorsement")
    public ResponseEntity<?> createEndorsement(@ModelAttribute EndorsementDto endorsementDto, @RequestParam("clientListId") Long clientListId, @RequestParam("productId") Long productId) {
        EndorsementDto endorsement = endorsementService.createEndorsement(endorsementDto, clientListId, productId);
        return new ResponseEntity<>(endorsement, HttpStatus.CREATED);
    }

    @GetMapping("/getAllEndorsement")
    @ResponseStatus(HttpStatus.OK)
    public List<DisplayAllEndorsementDto> getAllEndorsement(@RequestParam Long clientListId,
                                                            @RequestParam Long productId) {
        return endorsementService.getAllEndorsement(clientListId, productId);
    }

    @GetMapping("/getByIdEndorsement")
    @ResponseStatus(HttpStatus.OK)
    public DisplayAllEndorsementDto getById(@RequestParam Long endorsementId) {
        return endorsementService.getById(endorsementId);
    }

    @PutMapping("/updateEndorsement/{endorsmentId}")
    @ResponseStatus(HttpStatus.OK)
    public String updateEndorsement(@ModelAttribute UpdateEndorsementDetailsDto dto, @PathVariable Long endorsmentId) {
        System.out.println(endorsmentId);
        System.out.println("Endorsement Details : " + dto);
        return endorsementService.updateEndorsementById(dto, endorsmentId);
    }

    @DeleteMapping("/deleteEndorsement/{endorsmentId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteEndorsement(@PathVariable Long endorsmentId) {
        return endorsementService.deleteEndorsementById(endorsmentId);
    }

    @GetMapping("/downloadFileByEndorsementId")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> downloadFile(@RequestParam Long endorsementId) {
        try {
            // Retrieve the endorsement entity
            EndorsementEntity endorsement = endorsementRepository.findById(endorsementId).get();

            // Validate the endorsement
            if (endorsement == null) {
                return ResponseEntity.notFound().build();
            }

            // Read file content
            byte[] fileContent = endorsementService.downloadEndrosementDocumentByEndrosementId(endorsementId);

            // Create a ByteArrayResource from file content
            ByteArrayResource resource = new ByteArrayResource(fileContent);

            // Get the file extension from the fileName using service method
            String fileExtension = endorsementService.getFileExtension(endorsement.getFileName());

            // Set content type based on file extension
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;


            if (fileExtension != null) {
                mediaType = MediaType.valueOf("application/" + fileExtension.toLowerCase());
            }

            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", endorsement.getFileName() + "." + fileExtension);
            headers.setContentType(mediaType);
            headers.setContentLength(fileContent.length);

            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (IOException e) {
            // Handle exception appropriately (e.g., log, return error response)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/downloadAll")
    public ResponseEntity<byte[]> downloadAllEndorsements(@RequestParam Long clientlistId,@RequestParam Long productId) {
        try {
            List<EndorsementEntity> endorsements = endorsementService.getAllEndorsements(clientlistId,productId);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                for (EndorsementEntity endorsement : endorsements) {

                    if ("active".equalsIgnoreCase(endorsement.getRecordStatus())) {
                        String fileName1 = endorsement.getFileName();


                        File file = new File(fileName1);
                        if (file.exists()) {
                            // Logging for debugging
                            System.out.println("Processing file: " + fileName1);

                            // Create a ZipEntry using the file name
                            ZipEntry entry = new ZipEntry(file.getName());
                            zos.putNextEntry(entry);

                            // Set the correct content type based on file extension
                            HttpHeaders headers = new HttpHeaders();
                            String contentType = getContentType(fileName1);

                            // Add the content type to the response header
                            headers.add(HttpHeaders.CONTENT_TYPE, contentType);

                            // Write the file data to the zip stream
                            try (InputStream is = new FileInputStream(file)) {
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = is.read(buffer)) > 0) {
                                    zos.write(buffer, 0, bytesRead);
                                }
                            }
                            zos.closeEntry();
                        } else {
                            // Log or handle the case where the file does not exist
                            System.out.println("File not found: " + fileName1);
                        }
                    }
                }
            }

            byte[] zipBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=allEndorsements.zip");

            // Set the overall content type for the zip file
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(zipBytes.length)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipBytes);

        } catch (IOException e) {
            e.printStackTrace(); // Log the exception for further investigation
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    private byte[] getFileDataFromDatabase(Long endorsementId) {
        // Replace this with your actual implementation to retrieve file data by endorsementId
        return endorsementService.getFileDataById(endorsementId);
    }

    private String getContentType(String filename) {
        if (filename.endsWith(".pdf")) {
            return "application/pdf";
        } else if (filename.endsWith(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else {
            return "application/octet-stream"; // Default to binary data if type is unknown
        }
    }
    
    @DeleteMapping("/clearAllEndorsements")
    public ResponseEntity<?> clearAllEndorsements() {
        return ResponseEntity.ok(endorsementService.clearAllEndorsements());
    }


}
