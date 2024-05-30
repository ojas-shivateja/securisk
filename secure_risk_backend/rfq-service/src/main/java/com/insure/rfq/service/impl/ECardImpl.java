package com.insure.rfq.service.impl;

import com.insure.rfq.dto.DisplayAllECardDto;
import com.insure.rfq.dto.ECardDto;
import com.insure.rfq.dto.EndorsementDto;
import com.insure.rfq.dto.UpdateECardDto;
import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.ECardEntity;
import com.insure.rfq.entity.Product;
import com.insure.rfq.exception.EntityNotFoundException;
import com.insure.rfq.exception.InvaildEndorsementException;
import com.insure.rfq.exception.InvalidClientList;
import com.insure.rfq.exception.InvalidProduct;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.repository.ECardRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.ECardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class ECardImpl implements ECardService {
    @Autowired
    private ECardRepository eCardRepository;

    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private ClientListRepository clientListRepository;
    @Value("${file.path.coverageMain}")
    private String mainpath;


    @Override
    public ECardDto create(ECardDto eCardDto, Long clientListId, Long productId) {
        ECardEntity entity = new ECardEntity();




        String filepath = retunFilePath(eCardDto.getFileName());
        Path path = Paths.get(filepath);
        String fileName = filepath.substring(filepath.indexOf("_") + 1, filepath.lastIndexOf("."));
        entity.setFileName(filepath); // in case you still need the full file path in the ECardDto object
        entity.setEmployeeId(fileName);


        if (clientListId != null) {
            ClientList clientList = clientListRepository.findById(clientListId).orElseThrow(() -> new InvalidClientList("ClientList Not Found"));
            entity.setRfqId(clientList.getRfqId());
        }

        if (clientListId != null) {
            try {
                ClientList clientList = clientListRepository.findById(clientListId).orElseThrow(() -> new InvalidClientList("ClientList is not Found"));
                entity.setClientList(clientList);
            } catch (InvalidClientList e) {
                throw new InvalidClientList("ClientList is not Found");
            }
        }
        if (productId != null) {
            try {
                Product product = productRepository.findById(productId).orElseThrow(() -> new InvalidProduct("Product is not Found"));
                entity.setProduct(product);
            } catch (InvalidProduct e) {
                throw new InvalidProduct("Product is not Found");
            }
        }
        entity.setCreateDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
        entity.setRecordStatus("ACTIVE");

        ECardEntity entity1 = eCardRepository.save(entity);
        eCardDto.setECardId(entity1.getECardId());
        return eCardDto;
    }


    public String retunFilePath(MultipartFile file) {
        try {
            // Generate a unique file name
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Create the directory if it doesn't exist
            Path directory = Paths.get(mainpath);

            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            // Save the file to the local machine
            Path filePath = Paths.get(mainpath, fileName);
            Files.copy(file.getInputStream(), filePath);

            // Return the path of the saved file
            return filePath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception
            return null;
        }
    }

    public String addFileToDatabaseServer(ZipInputStream zipInputStream, String filename) {
       try {
            // Generate a unique file name
            String fileName = UUID.randomUUID().toString() + "_" + filename;

            // Create the directory if it doesn't exist
            Path directory = Paths.get(mainpath);

            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            // Save the file to the local machine
            Path filePath = Paths.get(mainpath, fileName);

            if (!Files.exists(filePath.getParent())) {
              Files.createDirectories(filePath.getParent());
              
            }
            try (FileOutputStream outputStream = new FileOutputStream(filePath.toFile())) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = zipInputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }

            // Return the path of the saved file
            return filePath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception
            return null;
        }
    }

    @Override
    public List<DisplayAllECardDto> getAllECard(Long clientlistId, Long productId) {

        List<DisplayAllECardDto> result = eCardRepository.findAll().stream().filter(i -> i.getRecordStatus().equalsIgnoreCase("ACTIVE"))
                .filter(i -> clientlistId != null && i.getClientList().getCid() == clientlistId).
                filter(i -> productId != null && i.getProduct().getProductId().equals(productId))
                .map(i -> {
                    DisplayAllECardDto displayAllEndorsementDto = new DisplayAllECardDto();

                    displayAllEndorsementDto.setECardId(i.getECardId());
                    displayAllEndorsementDto.setFileName(i.getFileName());
                    String[] fileNameParts = i.getFileName().split("_");
                    if (fileNameParts.length > 1) {

                        String remainingFileName = String.join("_", Arrays.copyOfRange(fileNameParts, 1, fileNameParts.length));
                        displayAllEndorsementDto.setFileName(remainingFileName);
                    } else {
                        // If there is no underscore, set the full fileName
                        displayAllEndorsementDto.setFileName(i.getFileName());
                    }

                    displayAllEndorsementDto.setECardId(i.getECardId());
                    return displayAllEndorsementDto;
                })
                .toList();

        if (result.isEmpty()) {
            String errorMessage = "No endorsement data found for clientlistId=" + clientlistId + " and productId=" + productId;
            throw new InvalidClientList(errorMessage);
        }

        return result;
    }

    @Override
    public DisplayAllECardDto getById(Long eCardId) {

        ECardEntity e = eCardRepository.findById(eCardId).orElseThrow(() -> new InvaildEndorsementException("Invalid Endorsement"));

        DisplayAllECardDto displayAllEndorsementDto = new DisplayAllECardDto();
        displayAllEndorsementDto.setECardId(e.getECardId());
        displayAllEndorsementDto.setFileName(e.getFileName());

        String[] fileNameParts = e.getFileName().split("_");
        if (fileNameParts.length > 1) {
            // Joining the remaining parts after the first underscore
            String remainingFileName = String.join("_", Arrays.copyOfRange(fileNameParts, 1, fileNameParts.length));
            displayAllEndorsementDto.setFileName(remainingFileName);
        } else {
            // If there is no underscore, set the full fileName
            displayAllEndorsementDto.setFileName(e.getFileName());
        }

        return displayAllEndorsementDto;

    }


    @Override
    public String deleteECardById(Long id) {


        ECardEntity entity = eCardRepository.findById(id).orElseThrow(() -> new InvaildEndorsementException("Not Found"));
        entity.setRecordStatus("IN ACTIVE");

        ECardEntity endorsementEntity = eCardRepository.save(entity);
        String message = "";
        if (endorsementEntity != null) {
            message = "delete successful";
        }

        return message;
    }


    public String updateECardById(UpdateECardDto dto, Long id) {
        ECardEntity entity = eCardRepository.findById(id).orElseThrow(() -> new InvaildEndorsementException("not found"));

        String filePath = retunFilePath(dto.getFileName());
        entity.setFileName(filePath);

        entity.setUpdateDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
        ECardEntity endorsementEntity = eCardRepository.save(entity);
        String message = "";
        if (endorsementEntity != null) {
            message = "updated successful";
        }
        return message;
    }

    @Override
    public String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filePath.substring(lastDotIndex + 1);
        }
        return null;
    }

    @Override
    public byte[] downloadClientDetialsDocumentByClientDetialsId(Long eCardId) throws IOException {
        try {
            if (eCardId != null) {
                ECardEntity endorsement = eCardRepository.findById(eCardId)
                        .orElseThrow(() -> new InvaildEndorsementException("Invalid Endorsement"));
                Path path = Paths.get(endorsement.getFileName());
                return Files.readAllBytes(path);
            }
        } catch (IOException e) {
            // Handle IOException appropriately, e.g., log the error
            e.printStackTrace();
            throw new InvaildEndorsementException("Error reading file");
        }

        return null;
    }


    @Override
    public List<ECardEntity> getAllECardDownload(Long clientListId, Long productId) {

        List<ECardEntity> result = eCardRepository.findAll().stream()
                .filter(i -> i.getRecordStatus().equalsIgnoreCase("ACTIVE"))
                .filter(i -> clientListId != null && i.getClientList().getCid() == (clientListId))
                .filter(i -> productId != null && i.getProduct().getProductId().equals(productId))
                .toList();

        if (result.isEmpty()) {
            String errorMessage = "No endorsement data found for clientListId=" + clientListId + " and productId=" + productId;
            throw new InvalidClientList(errorMessage);
        }

        return result;
    }


    @Override
    public byte[] getFileDataById(Long eCardId) {
        ECardEntity endorsement = eCardRepository.findById(eCardId)
                .orElseThrow(() -> new EntityNotFoundException("Endorsement not found with id: " + eCardId));
        EndorsementDto endorsementDto = new EndorsementDto();
        String fileDataAsString = endorsement.getFileName();

        byte[] bytes = convertStringToByteArray(fileDataAsString);

        endorsementDto.setFileData(bytes);

        return endorsementDto.getFileData();

        // Assuming the file data is stored directly in the entity
        // Replace this with your actual method or field
    }

    @Override
    public List<DisplayAllECardDto> getAllECardbyId(Long clientlistId, Long productId, String employeeId) {
        List<DisplayAllECardDto> result = eCardRepository.findAll().stream().filter(i -> i.getRecordStatus().equalsIgnoreCase("ACTIVE"))
                .filter(i -> clientlistId != null && i.getClientList().getCid() == clientlistId).
                filter(i -> productId != null && i.getProduct().getProductId().equals(productId)).

                filter(i -> employeeId != null && i.getEmployeeId().equals(employeeId))

                .map(i -> {
                    DisplayAllECardDto displayAllEndorsementDto = new DisplayAllECardDto();

                    displayAllEndorsementDto.setECardId(i.getECardId());
                    displayAllEndorsementDto.setFileName(i.getFileName());
                    String[] fileNameParts = i.getFileName().split("_");
                    if (fileNameParts.length > 1) {

                        String remainingFileName = String.join("_", Arrays.copyOfRange(fileNameParts, 1, fileNameParts.length));
                        displayAllEndorsementDto.setFileName(remainingFileName);
                    } else {
                        // If there is no underscore, set the full fileName
                        displayAllEndorsementDto.setFileName(i.getFileName());
                    }

                    displayAllEndorsementDto.setECardId(i.getECardId());
                    return displayAllEndorsementDto;
                })
                .toList();

        if (result.isEmpty()) {
            String errorMessage = "No endorsement data found for clientlistId=" + clientlistId + " and productId=" + productId;
            throw new InvalidClientList(errorMessage);
        }

        return result;
    }

    @Override
    public byte[] downloadClientDetialsDocumentByEmployeeId(String employeeId) throws IOException {
        try {
            if (employeeId != null) {
                ECardEntity endorsement = eCardRepository.findByEmployeeId(employeeId)
                        .orElseThrow(() -> new InvaildEndorsementException("Invalid Endorsement"));
                Path path = Paths.get(endorsement.getFileName());
                return Files.readAllBytes(path);
            }
        } catch (IOException e) {
            // Handle IOException appropriately, e.g., log the error
            e.printStackTrace();
            throw new InvaildEndorsementException("Error reading file");
        }

        return null;
    }


    @Override
    public void uploadEcardsDocument(ECardDto eCardDto, Long clientListId, Long productId) throws IOException {
        ZipInputStream zipInputStream = new ZipInputStream(eCardDto.getFileName().getInputStream());
        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (!zipEntry.isDirectory() && zipEntry.getName().endsWith(".pdf")) {
                String uploadedFileName = addFileToDatabaseServer(zipInputStream, zipEntry.getName());
                if (uploadedFileName == null) {
                    continue;
                }
                // creating card entity
                ECardEntity eCardEntity = new ECardEntity();
                eCardEntity.setFileName(uploadedFileName);

                String[] parts = uploadedFileName.split("/");
                String[] lastPart = parts[parts.length - 1].replace(".pdf", "").split("\\\\");
                String employeeId=lastPart[lastPart.length-1];
                eCardEntity.setEmployeeId(employeeId);

                if (clientListId != null) {
                    ClientList clientList = clientListRepository.findById(clientListId).orElseThrow(() -> new InvalidClientList("ClientList Not Found"));
                    eCardEntity.setRfqId(clientList.getRfqId());
                }

                if (clientListId != null) {
                    try {
                        ClientList clientList = clientListRepository.findById(clientListId).orElseThrow(() -> new InvalidClientList("ClientList is not Found"));
                        eCardEntity.setClientList(clientList);
                    } catch (InvalidClientList e) {
                        throw new InvalidClientList("ClientList is not Found");
                    }
                }
                if (productId != null) {
                    try {
                        Product product = productRepository.findById(productId).orElseThrow(() -> new InvalidProduct("Product is not Found"));
                        eCardEntity.setProduct(product);
                    } catch (InvalidProduct e) {
                        throw new InvalidProduct("Product is not Found");
                    }
                }
                eCardEntity.setCreateDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
                eCardEntity.setRecordStatus("ACTIVE");


                eCardRepository.save(eCardEntity);
            }

        }
        zipInputStream.close();


    }



    private byte[] convertStringToByteArray(String data) {
        // Implement the logic to convert the string to a byte array
        // This could be based on encoding or other requirements of your application
        return data.getBytes(StandardCharsets.UTF_8); // Change the charset based on your needs
    }

}




