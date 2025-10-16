package com.gestione.articoli.service;

import java.util.List;

import com.gestione.articoli.dto.MachineDto;

public interface MachineService {
    String createMachine(String name, String password, List<Long> list);
	List<MachineDto> getMachines();
    MachineDto getMachineById(Long id);
    void deleteMachine(Long id);
	MachineDto updateMachine(Long id, MachineDto machineDto);
	MachineDto getMyActivity();
	void updateMachinePassword(Long id, String newPassword);
}
