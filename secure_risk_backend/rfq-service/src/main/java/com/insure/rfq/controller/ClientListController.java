package com.insure.rfq.controller;

import com.insure.rfq.dto.ClientListChildDto;
import com.insure.rfq.dto.ClientListDto;
import com.insure.rfq.dto.GetAllClientListDto;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.service.impl.ClientListServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/rfq/clientList")
@RestController
@CrossOrigin(origins = { "*" })
public class ClientListController {

	@Autowired
	private ClientListServiceImpl clientListServiceImpl;
	@Autowired
	private ClientListRepository clientListRepository;

	@PostMapping("/add")
	public ResponseEntity<ClientListDto> createClient(@RequestBody ClientListDto clientList) {
		ClientListDto createClientList = clientListServiceImpl.createClientList(clientList);
		return new ResponseEntity<>(createClientList, HttpStatus.CREATED);
	}

	@GetMapping("/getClient/{id}")
	public ResponseEntity<ClientListDto> getClientDetailsById(@PathVariable Long id) {
		ClientListDto clientById = clientListServiceImpl.getClientById(id);
		return ResponseEntity.ok(clientById);
	}

//  @GetMapping("/getAll")
//  public ResponseEntity<List<ClientListDto>> getAllClients(
//        @RequestParam(required = false, defaultValue = "0", name = "pageNo") int pageNo,
//        @RequestParam(required = false, defaultValue = "createdDate", name = "sort") String sort,
//        @RequestParam(required = false, defaultValue = "5", name = "size") int size) {
//     List<ClientListDto> clientListDtos = clientListServiceImpl.getAllClients(pageNo, size, sort);
//
//     // Set the sno field based on the index of each element in the list
//     for (int i = 0; i < clientListDtos.size(); i++) {
//        clientListDtos.get(i).setSNo(i + 1);
//     }
//
//     return new ResponseEntity<>(clientListDtos, HttpStatus.OK);
//  }

	@PutMapping("/updateClient/{id}")
	public ResponseEntity<ClientListChildDto> updateClient(@RequestBody ClientListChildDto clientListDto,
														   @PathVariable Long id) {
		ClientListChildDto updateClientList = clientListServiceImpl.updateClientList(clientListDto, id);
		return new ResponseEntity<>(updateClientList, HttpStatus.OK);
	}

	@DeleteMapping("/deleteClientByClientListId/{id}")
	public ResponseEntity<String> deleteClientByClientListId(@PathVariable Long id) {
		String deleteClientById = clientListServiceImpl.deleteClientById(id);
		return new ResponseEntity<>(deleteClientById, HttpStatus.OK);
	}

	@GetMapping("/getAllClientList")
	public ResponseEntity<List<GetAllClientListDto>> getAllClientList() {
		List<GetAllClientListDto> allClientList = clientListServiceImpl.getAllClientList();
		return ResponseEntity.ok(allClientList);
	}


	@GetMapping("/getdashboardClientCount")
	public List<Object[]> getRfqCounts() {
		List<Object[]> results = clientListRepository.countApplicationsByStatus();
		return results;
	}
}