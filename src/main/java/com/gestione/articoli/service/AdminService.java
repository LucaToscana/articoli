package com.gestione.articoli.service;

import java.util.List;

import com.gestione.articoli.dto.AdminDto;

public interface AdminService {
    String createAdmin(String username, String password);
	List<AdminDto> getAdmins();
    void deleteAdmin(Long id);
	AdminDto getAdminById(Long id);
}
