package org.example.library.service;

import org.example.library.entity.Customer;
import org.example.library.entity.Order;
import org.example.library.entity.ShoppingCart;

import java.util.List;

public interface OrderService {
    Order placeOrder(Customer customer, ShoppingCart cart, String paymentMethod, String note);
    List<Order> getOrdersByCustomer(Customer customer);
    Order getOrderById(Long id);
    void cancelOrder(Order order);

}
