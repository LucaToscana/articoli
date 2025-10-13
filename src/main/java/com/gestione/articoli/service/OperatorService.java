package com.gestione.articoli.service;

import com.gestione.articoli.dto.InactiveUsersDto;
import com.gestione.articoli.dto.OperatorDto;

import java.util.List;

public interface OperatorService {
    List<OperatorDto> getAllOperators();
    OperatorDto getOperatorById(Long id);
    void deactivateOperator(Long id);
    void deleteOperator(Long id);
    OperatorDto updateOperator(Long id, OperatorDto operatorDto);
}
