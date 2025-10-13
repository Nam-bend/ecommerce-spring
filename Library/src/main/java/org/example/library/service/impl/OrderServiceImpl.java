package org.example.library.service.impl;

import jakarta.transaction.Transactional;
import org.example.library.entity.*;
import org.example.library.repository.OrderRepository;
import org.example.library.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order placeOrder(Customer customer, ShoppingCart cart, String paymentMethod, String note) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(new Date());
        order.setStatus("Pending");
        order.setNote(note);
        order.setDeliveryDate(null);   // giao hàng set sau
        order.setShippingFee(0.0);     // ví dụ miễn phí ship
        order.setPaymentMethod(paymentMethod);
        double totalPrice = 0;
        List<OrderDetail> details = new ArrayList<>();

        // ✅ Dùng for-each thường thay vì lambda
        for (CartItem item : cart.getCartItem()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getProduct().getSalePrice());
            detail.setTotalPrice(item.getQuantity() * item.getProduct().getSalePrice());

            details.add(detail);
            totalPrice += detail.getTotalPrice();
        }

        order.setTotalPrice(totalPrice);
        order.setOrderDetailList(details);

        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByCustomer(Customer customer) {
        return orderRepository.findByCustomer(customer);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng id = " + id));
    }

    @Override
    public void cancelOrder(Order order) {
        order.setStatus("Cancelled"); // cập nhật trạng thái
        orderRepository.save(order);  // lưu lại vào DB
    }

}
