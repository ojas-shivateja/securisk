package com.insure.rfq.service.impl;

import com.insure.rfq.dto.ProductClientDto;
import com.insure.rfq.entity.ClientList;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.ProductClientService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductClientServiceImp implements ProductClientService {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ClientListRepository clientListRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ProductClientDto> getProductsBasedonClientId(Long clientId) {
        ClientList clientList = clientListRepository.findById(clientId).get();
        return clientList.getProduct().stream().map(client -> {
            ProductClientDto clientDto = new ProductClientDto();
            clientDto.setPid(client.getProductId());
            clientDto.setProductType(client.getProductName());
            clientDto.setPolicyType(client.getPolicyType());
            return clientDto;
        }).toList();
    }
}
