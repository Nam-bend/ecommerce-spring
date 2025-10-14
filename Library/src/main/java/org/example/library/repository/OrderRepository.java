package org.example.library.repository;



import org.example.library.entity.Customer;
import org.example.library.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(Customer customer);
    @Query("SELECT o FROM Order o WHERE CAST(o.id AS string) LIKE %:keyword% " +
            "OR CONCAT(o.customer.firstName, ' ', o.customer.lastName) LIKE %:keyword%")
    Page<Order> findByIdContainingOrCustomerNameContaining(@Param("keyword") String keyword, Pageable pageable);
}
