package com.insure.rfq.login.service;

import java.util.List;
import java.util.Map;

import com.insure.rfq.login.dto.OperationTableDto;

public interface OperationService {

	OperationTableDto createOperation(OperationTableDto operationTableDto);

	Map<String, List<String>> getPermissonBasedOnDeptId(Long id);

	List<OperationTableDto> getAllOperation();

}
