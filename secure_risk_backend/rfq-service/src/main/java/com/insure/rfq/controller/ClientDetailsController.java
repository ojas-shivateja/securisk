package com.insure.rfq.controller;


import com.insure.rfq.dto.ClientDetailsDto;
import com.insure.rfq.dto.DisplayAllClientDetailsDto;
import com.insure.rfq.dto.DisplayAllEndorsementDto;
import com.insure.rfq.dto.UpdateClientDetailsDto;
import com.insure.rfq.entity.ClientDetailsEntity;
import com.insure.rfq.entity.EndorsementEntity;
import com.insure.rfq.repository.ClientDetailsRepository;
import com.insure.rfq.service.ClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/clientlist/ClientDetails")
@CrossOrigin(origins = "*")
public class ClientDetailsController {


    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired

private ClientDetailsRepository clientDetailsRepository;
    @PostMapping("/createClientDetails")
    public ResponseEntity<?> createClientDetails(@ModelAttribute ClientDetailsDto clientDetailsDto, @RequestParam("clientListId") Long clientListId, @RequestParam("productId") Long productId) {

            ClientDetailsDto clientDetails = clientDetailsService.createClientDetails(clientDetailsDto, clientListId, productId);
            return new ResponseEntity<>(clientDetails, HttpStatus.CREATED);

    }

    @GetMapping("/getAllClientDetails")
    @ResponseStatus(HttpStatus.OK)
    public List<DisplayAllClientDetailsDto> getAllClientDetails(@RequestParam Long clientlistId, @RequestParam Long productId) {
        return clientDetailsService.getAllClientDetails(clientlistId, productId);
    }

    @PutMapping("/updateClientDetails/{clientDetailsId}")
    @ResponseStatus(HttpStatus.OK)
    public String updateClientDetails(@ModelAttribute UpdateClientDetailsDto dto, @PathVariable Long clientDetailsId) {

        return clientDetailsService.updateClientDetails(dto, clientDetailsId);

    }

    @DeleteMapping("/deleteClientDetails/{clientDetailsId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteClientDetails(@PathVariable Long clientDetailsId) {
        return clientDetailsService.deleteClientDetailsById(clientDetailsId);
    }

    @GetMapping("/getByIdClientDetails")
    @ResponseStatus(HttpStatus.OK)
    public DisplayAllClientDetailsDto getById(@RequestParam Long clientDetailsId) {
        return clientDetailsService.getById(clientDetailsId);
    }

    @GetMapping("/downloadFileByClientDetailsId")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> downloadFile(@RequestParam Long clientDetailsId) {
        try {
            // Retrieve the endorsement entity
            ClientDetailsEntity endorsement=clientDetailsRepository.findById(clientDetailsId).get();

            // Validate the endorsement
            if (endorsement == null) {
                return ResponseEntity.notFound().build();
            }

            // Read file content
            byte[] fileContent = clientDetailsService.downloadClientDetialsDocumentByClientDetialsId(clientDetailsId);

            // Create a ByteArrayResource from file content
            ByteArrayResource resource = new ByteArrayResource(fileContent);

            // Get the file extension from the fileName using service method
            String fileExtension = clientDetailsService.getFileExtension(endorsement.getFileName());

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/downloadAll")
    public ResponseEntity<byte[]> downloadAllEndorsements(@RequestParam Long clientListId,@RequestParam Long productId) {
        try {
            List<ClientDetailsEntity> endorsements = clientDetailsService.getAllClientDetailsDownload(clientListId,productId);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                for (ClientDetailsEntity endorsement : endorsements) {
                    // Check the status of the endorsement
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
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ClientDetails.zip");

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


    private byte[] getFileDataFromDatabase(Long clientDetailsId) {
        // Replace this with your actual implementation to retrieve file data by endorsementId
        return clientDetailsService.getFileDataById(clientDetailsId);
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
    @DeleteMapping("/cleareAllClientDetails")
	public ResponseEntity<?> clearAllClientDetails() {

		return ResponseEntity.ok(clientDetailsService.cleareAllClientDetails());
	}
}
