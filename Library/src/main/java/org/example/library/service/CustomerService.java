package org.example.library.service;

import org.example.library.dto.CustomerDto;
import org.example.library.entity.Customer;

public interface CustomerService {
 CustomerDto save(CustomerDto customerDto);
 Customer  findByUsername(String username);
    boolean existsByUsername(String username);
    Customer saveInfor(Customer customer);

}
