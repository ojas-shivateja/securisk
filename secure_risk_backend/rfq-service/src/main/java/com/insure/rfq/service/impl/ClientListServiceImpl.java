package com.insure.rfq.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.insure.rfq.dto.ClientListChildDto;
import com.insure.rfq.dto.ClientListDto;
import com.insure.rfq.dto.GetAllClientListDto;
import com.insure.rfq.dto.GetAllClientListUserByClientListIdDto;
import com.insure.rfq.dto.GetAllProductsByClientListIdDto;
import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.ClientProductAssociation;
import com.insure.rfq.entity.InsureList;
import com.insure.rfq.entity.Product;
import com.insure.rfq.entity.Tpa;
import com.insure.rfq.exception.InvalidClientList;
import com.insure.rfq.exception.InvalidUser;
import com.insure.rfq.login.entity.Location;
import com.insure.rfq.login.repository.LocationRepository;
import com.insure.rfq.repository.ClientListProductAssociationRepository;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.repository.ClientListUserRepository;
import com.insure.rfq.repository.InsureListRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.repository.TpaRepository;
import com.insure.rfq.service.ClientListService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClientListServiceImpl implements ClientListService {
	@Autowired
	private ClientListRepository clientListRepository;
	@Autowired
	private ProductRepository prodRepository;
	@Autowired
	private ModelMapper mapper;
	@Autowired
	private LocationRepository locationRepository;
	@Autowired
	private TpaRepository tpaRepository;
	@Autowired
	private InsureListRepository insureListRepository;
	@Autowired
	private ClientListUserRepository clientListUserRepository;
	@Autowired
	private ClientListProductAssociationRepository clientProductAssociationRepository;

	@Override
	public ClientListDto createClientList(ClientListDto clientListDto) {
		if (clientListDto != null) {
			// Create new ClientList entity
			ClientList clientListEntity = new ClientList();

			// Fetch related entities
			Optional<Location> location = locationRepository.findById(Long.parseLong(clientListDto.getLocationId()));
			log.info("Location From Create Client List: {}", location);

			Optional<Product> productOptional = prodRepository.findById(Long.parseLong(clientListDto.getProductId()));
			log.info("Product From Create Client List: {}", productOptional);

			Optional<Tpa> tpaOptional = tpaRepository.findById(Long.parseLong(clientListDto.getTpaId()));
			log.info("Tpa From Create Client List: {}", tpaOptional);

			Optional<InsureList> insurerOptional = insureListRepository.findById(clientListDto.getInsuranceCompanyId());
			log.info("Insurer From Create Client List: {}", insurerOptional);

			if (productOptional.isPresent() && location.isPresent()) {
				Product product = productOptional.get();
				Tpa tpa = tpaOptional.orElse(null);
				InsureList insurer = insurerOptional.orElse(null);

				// Set fields in ClientList entity
				clientListEntity.setClientName(clientListDto.getClientName());
				clientListEntity.setLocationId(location.get());
				clientListEntity.setCreatedDate(LocalDateTime.now());
				clientListEntity.setStatus("ACTIVE");

				// Save ClientList entity
				ClientList savedClientList = clientListRepository.save(clientListEntity);

				// Create and save ClientProductAssociation entity
				ClientProductAssociation association = new ClientProductAssociation();
				association.setClientList(savedClientList);
				association.setProduct(product);
				association.setPolicyType(clientListDto.getPolicyType());
				association.setTpa(tpa);
				association.setInsurer(insurer);
				clientProductAssociationRepository.save(association);

				// Prepare and return DTO
				ClientListDto listDto = new ClientListDto();
				listDto.setClientName(savedClientList.getClientName());
				listDto.setLocationId(String.valueOf(savedClientList.getLocationId().getLocationId()));
				listDto.setProductId(String.valueOf(product.getProductId()));
				listDto.setInsuranceCompanyId(insurer != null ? insurer.getInsurerName() : null);
				listDto.setPolicyType(clientListDto.getPolicyType());
				listDto.setTpaId(tpa != null ? tpa.getTpaName() : null);

				return listDto;
			} else {
				throw new IllegalArgumentException("Required Product or Location not found");
			}
		} else {
			return null;
		}
	}

    @Override
    public ClientListDto getClientById(long id) {
        ClientList clientList = clientListRepository.findById(id)
                .orElseThrow(() -> new InvalidUser(" invalid user Id"));
        return mapper.map(clientList, ClientListDto.class);
    }

    @Override
    public List<ClientListDto> getAllClients(int pageNo, int size, String sort) {
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(sort).descending());
        Page<ClientList> clientListPage = clientListRepository.findAll(pageable);
        return clientListPage.getContent().stream().filter(list -> list.getStatus().equalsIgnoreCase("ACTIVE"))
                .map(list -> mapper.map(list, ClientListDto.class)).toList();
    }

    @Override
    public ClientListChildDto updateClientList(ClientListChildDto clientListChildDto, Long clientListid) {
        if (clientListChildDto != null) {
            ClientList clientList = clientListRepository.findById(clientListid)
                    .orElseThrow(() -> new InvalidClientList("Id is not found"));
            log.info("ClientList From Update Client List", clientList);
            Optional<Location> location = locationRepository.findById(Long.parseLong(clientListChildDto.getLocation()));
            log.info("Location From Update Client List", location);
            clientListChildDto.setClientName(clientListChildDto.getClientName());
            clientListChildDto.setLocation(location.get().getLocationName());
            clientList.setClientName(clientListChildDto.getClientName());
            clientList.setLocationId(location.get());
            clientListRepository.save(clientList);
            return clientListChildDto;
        }
        return null;
    }

    @Override
    public String deleteClientById(Long clientId) {
        ClientList clientList = clientListRepository.findById(clientId).get();
        clientList.setStatus("INACTIVE");
        clientListRepository.save(clientList);
        return "Deleted Sucessfully";
    }

	@Override
	public List<GetAllClientListDto> getAllClientList() {
		List<GetAllClientListDto> list = clientListRepository.findAll().stream()
				.filter(clientList -> clientList.getStatus().equalsIgnoreCase("ACTIVE")).map(clientList -> {
					GetAllClientListDto allClientListDto = new GetAllClientListDto();
					allClientListDto.setClientName(clientList.getClientName());
					allClientListDto.setClientId(clientList.getCid());

					// Get users associated with the client list
					List<GetAllClientListUserByClientListIdDto> listOfUsers = clientListUserRepository
							.findByClientList(clientList.getCid()).stream()
							.filter(user -> user.getClientList() != null
									&& user.getClientList().getStatus().equalsIgnoreCase("ACTIVE")
									&& user.getStatus().equalsIgnoreCase("ACTIVE"))
							.map(user -> {
								GetAllClientListUserByClientListIdDto allClientListUserByClientListIdDto = new GetAllClientListUserByClientListIdDto();
								allClientListUserByClientListIdDto.setEmployeeId(user.getEmployeeId());
								allClientListUserByClientListIdDto.setEmailId(user.getMailId());
								allClientListUserByClientListIdDto.setName(user.getName());
								allClientListUserByClientListIdDto.setPhoneNumber(user.getPhoneNo());
								return allClientListUserByClientListIdDto;
							}).toList();

					// Get products associated with the client list using ClientProductAssociation
					List<GetAllProductsByClientListIdDto> listOfProducts = clientProductAssociationRepository
							.findByClientList(clientList).stream()
							.filter(association -> association.getProduct() != null
									&& association.getClientList().getStatus().equalsIgnoreCase("ACTIVE")
									&& association.getProduct().getStatus().equalsIgnoreCase("ACTIVE"))
							.map(association -> {
								GetAllProductsByClientListIdDto getProducts = new GetAllProductsByClientListIdDto();
								Product product = association.getProduct();

								getProducts.setProductId(String.valueOf(product.getProductId()));
								getProducts.setProductName(product.getProductName());
								getProducts.setPolicyType(association.getPolicyType());
								getProducts.setTpaId(
										association.getTpa() != null ? association.getTpa().getTpaName() : null);
								getProducts.setInsurerCompanyId(
										association.getInsurer() != null ? association.getInsurer().getInsurerName()
												: null);

								// Get product category name
								String categoryNameByProductId = prodRepository
										.findCategoryNameByProductId(product.getProductId());
								getProducts.setProductCategoryId(categoryNameByProductId);
								return getProducts;
							}).toList();

					allClientListDto.setListOfUsers(listOfUsers);
					allClientListDto.setListOfProducts(listOfProducts);

					if (clientList.getLocationId() != null) {
						allClientListDto.setLocation(clientList.getLocationId().getLocationName());
					} else {
						allClientListDto.setLocation(null);
					}

					allClientListDto.setStatus(clientList.getStatus());
					return allClientListDto;
				}).toList();

		int sno = 1; // Start sno from 1
		for (GetAllClientListDto dto : list) {
			dto.setSNO(sno);
			sno++; // Increment SNO for the next item
		}

		return list;
	}
}
