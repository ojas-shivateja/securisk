package com.insure.rfq.service;

import java.util.List;
import java.util.Optional;

import com.insure.rfq.dto.TpaDto;

public interface TpaService {

	TpaDto createTpa(TpaDto tpa);

	List<TpaDto> viewAllTpa();

	TpaDto updateTpa(TpaDto tpa, Long tpaId);
	
	Optional<TpaDto> getById(Long tpaId);
	
	String deleteTpa(Long tpaId);

}
