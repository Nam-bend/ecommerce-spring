package org.example.library.service.impl;



import org.example.library.dto.CustomerDto;
import org.example.library.entity.Customer;
import org.example.library.entity.Role;
import org.example.library.repository.CustomerRepository;
import org.example.library.repository.RoleRepository;
import org.example.library.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setUsername(customerDto.getUsername());
        customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));

        roleRepository.findByName("Customer")
                .ifPresent(role -> customer.setRoles(List.of(role)));

        Customer savedCustomer = customerRepository.save(customer);
        return mapperDTO(savedCustomer);
    }

    @Override
    public Customer findByUsername(String username) {
        return customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    public boolean existsByUsername(String username) {
        return customerRepository.existsByUsername(username);
    }

    @Override
    public Customer saveInfor(Customer customer) {
      return   customerRepository.save(customer);

    }

    private CustomerDto mapperDTO(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setUsername(customer.getUsername());
        // Không trả password để bảo mật
        return dto;
    }
}

