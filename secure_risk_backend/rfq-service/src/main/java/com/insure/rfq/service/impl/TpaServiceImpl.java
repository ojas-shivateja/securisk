package com.insure.rfq.service.impl;

import com.insure.rfq.dto.ClaimsTPAHeadersDto;
import com.insure.rfq.dto.TpaDto;
import com.insure.rfq.entity.ClaimsTPAHeaders;
import com.insure.rfq.entity.Tpa;
import com.insure.rfq.exception.InvalidUser;
import com.insure.rfq.repository.TpaRepository;
import com.insure.rfq.service.TpaService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TpaServiceImpl implements TpaService {

    @Autowired
    private TpaRepository tpaRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
//	Transactional to ensure that a transaction is started and committed correctly. 
    public TpaDto createTpa(TpaDto tpa) {
        Tpa tpaRef = new Tpa();
        tpaRef.setLocation(tpa.getLocation());
        tpaRef.setTpaName(tpa.getTpaName());
        String active = "ACTIVE";
        tpaRef.setRecordStatus(active);
        tpaRef.setCreatedDate(new SimpleDateFormat().format(new Date()));
        List<ClaimsTPAHeaders> tpaHeaders = new ArrayList<>();
        List<ClaimsTPAHeadersDto> tpaDtoHeaders = tpa.getTpaHeaders();
        tpaDtoHeaders.stream().forEach(i -> {
            ClaimsTPAHeaders tpaHeader = new ClaimsTPAHeaders();
            tpaHeader.setHeaderName(i.getHeaderName());
            tpaHeader.setHeaderAliasName(i.getHeaderAliasName());
            tpaHeader.setRecordStatus(active);
            tpaHeader.setTpaList(tpaRef);
            tpaHeader.setCreatedDate(new SimpleDateFormat().format(new Date()));
            tpaHeader.setSheetName(i.getSheetName());
            tpaHeaders.add(tpaHeader);
        });
        tpaRef.setTpaHeaders(tpaHeaders);
        Tpa save = tpaRepository.save(tpaRef);
        return mapper.map(tpaRepository.save(save), TpaDto.class);
    }

    @Override
    public List<TpaDto> viewAllTpa() {
        return tpaRepository.findAll().stream().map(map -> mapper.map(map, TpaDto.class))
                .filter(tpa -> tpa.getRecordStatus().equals("ACTIVE")).toList();
    }

    @Override
    public TpaDto updateTpa(TpaDto tpaDto, Long tpaId) {
        Tpa tpa = tpaRepository.findById(tpaId).orElseThrow(() -> new InvalidUser("Id not found"));
        log.info("Tpa From Update Tpa", tpa);
        tpa.setTpaName(tpaDto.getTpaName());
        tpa.setLocation(tpaDto.getLocation());

        return mapper.map(tpaRepository.save(tpa), TpaDto.class);
    }

    @Override
    public Optional<TpaDto> getById(Long tpaId) {
        Optional<Tpa> tpa = tpaRepository.findById(tpaId);
        log.info("Tpa From GetTpaById", tpa);
        return tpa.map(tpaEntity -> mapper.map(tpaEntity, TpaDto.class));
    }

    @Override
    public String deleteTpa(Long tpaId) {
        Tpa tpa = tpaRepository.findById(tpaId).get();
        log.info("Tpa From Delete Tpa", tpa);
        tpa.setRecordStatus("INACTIVE");
        tpaRepository.save(tpa);
        return "Deleted Successfully";
    }

}
