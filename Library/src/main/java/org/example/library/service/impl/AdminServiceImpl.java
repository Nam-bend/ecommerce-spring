package org.example.library.service.impl;

import org.example.library.dto.AdminDto;
import org.example.library.entity.Admin;
import org.example.library.repository.AdminRepository;
import org.example.library.repository.RoleRepository;
import org.example.library.service.AdminService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final    BCryptPasswordEncoder passwordEncoder;

    public AdminServiceImpl(AdminRepository adminRepository,
                            RoleRepository roleRepository,
                            BCryptPasswordEncoder passwordEncoder
                           ) {
        this.adminRepository = adminRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        ;
    }


    @Override
    public Admin findByUserName(String username) {
        return adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    public Admin save(AdminDto adminDto) {
        Admin admin = new Admin();
        admin.setFirstName(adminDto.getFirstName());
        admin.setLastName(adminDto.getLastName());
        admin.setUsername(adminDto.getUsername());
        admin.setPassword(passwordEncoder.encode(adminDto.getPassword()));
        roleRepository.findByName("ADMIN")
                .ifPresent(role -> admin.setRoles(List.of(role)));

        return adminRepository.save(admin);
    }

    @Override
    public boolean existsByUsername(String username) {
        return adminRepository.existsByUsername(username);
    }
}
