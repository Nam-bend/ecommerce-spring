package org.example.library.service;

import org.example.library.dto.AdminDto;
import org.example.library.entity.Admin;

public interface AdminService {
    Admin findByUserName(String username);
    Admin save(AdminDto adminDto);
    boolean existsByUsername(String username);
}
