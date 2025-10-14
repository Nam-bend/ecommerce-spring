package org.example.library.service;

import org.example.library.entity.Customer;
import org.example.library.entity.Order;
import org.example.library.entity.ShoppingCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    // customer
    Order placeOrder(Customer customer, ShoppingCart cart, String paymentMethod, String note);
    List<Order> getOrdersByCustomer(Customer customer);
    Order getOrderById(Long id);
    void cancelOrder(Order order);

    //admin
    List<Order> getAllOrders();
    void save(Order order);
    void deleteOrderById(Long id);
    Page<Order> getAllOrdersPageable(Pageable pageable);
    public Page<Order> searchOrders(String keyword, int page, int size);
}
