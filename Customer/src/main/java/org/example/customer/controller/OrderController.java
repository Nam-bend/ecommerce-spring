package org.example.customer.controller;

import org.example.library.entity.Customer;
import org.example.library.entity.Order;
import org.example.library.entity.ShoppingCart;
import org.example.library.service.CustomerService;
import org.example.library.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {

    private final CustomerService customerService;
    private final OrderService orderService;

    public OrderController(CustomerService customerService, OrderService orderService) {
        this.customerService = customerService;
        this.orderService = orderService;
    }

    @GetMapping("/check-out")
    public String checkout(Model model, Principal principal) {
        // 1. Kiểm tra đăng nhập
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);

        // 2. Kiểm tra thông tin (SỬA LẠI ĐIỀU KIỆN)

        if (customer.getPhoneNumber() == null || customer.getPhoneNumber().trim().isEmpty()
                || customer.getAddress() == null || customer.getAddress().trim().isEmpty()
                || customer.getCity() == null || customer.getCity().trim().isEmpty()
                || customer.getCountry() == null || customer.getCountry().trim().isEmpty()) {
            model.addAttribute("customer", customer);
            return "redirect:/account"; // ← REDIRECT, không phải return "account"
        }
        // 3. Lấy giỏ hàng
        ShoppingCart cart = customer.getShoppingCart();
        // 4. Kiểm tra giỏ hàng có sản phẩm không
        if (cart == null || cart.getCartItem() == null || cart.getCartItem().isEmpty()) {
            return "redirect:/cart"; // ← Giỏ hàng trống
        }
        // 5. Truyền dữ liệu vào view
        model.addAttribute("customer", customer);
        model.addAttribute("cart", cart); // ← THÊM cart vào model
        // 6. Hiển thị trang checkout
        return "checkout";
    }

    // Submit đặt hàng
    @PostMapping("/order")
    public String placeOrder(@RequestParam("paymentMethod") String paymentMethod,
                             @RequestParam(value = "note", required = false) String note,
                             Principal principal) {
        if (principal == null) return "redirect:/login";

        Customer customer = customerService.findByUsername(principal.getName());
        ShoppingCart cart = customer.getShoppingCart();

        Order order = orderService.placeOrder(customer, cart, paymentMethod, note);

        // TODO: clear cart sau khi đặt hàng
        // shoppingCartService.clearCart(cart);

        return "redirect:/order-detail/" + order.getId();
    }

    // Danh sách đơn hàng của tôi
    @GetMapping("/orders")
    public String myOrders(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        Customer customer = customerService.findByUsername(principal.getName());
        List<Order> orders = orderService.getOrdersByCustomer(customer);

        model.addAttribute("orders", orders);
        return "orders"; // view: orders.html
    }

    // Chi tiết đơn hàng
    @GetMapping("/order-detail/{id}")
    public String orderDetail(@PathVariable("id") Long id, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        Order order = orderService.getOrderById(id);

        // ✅ Tính tổng số lượng sản phẩm trong đơn
        int totalQuantity = order.getOrderDetailList()
                .stream()
                .mapToInt(d -> (int) d.getQuantity())
                .sum();

        model.addAttribute("order", order);
        model.addAttribute("totalQuantity", totalQuantity);

        return "order-detail"; // view: order-detail.html
    }

    @PostMapping("/cancel-order/{id}")
    public String cancelOrder(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        Customer customer = customerService.findByUsername(principal.getName());
        Order order = orderService.getOrderById(id);


        if (!order.getCustomer().getId().equals(customer.getId())) {
            redirectAttributes.addFlashAttribute("error", "Bạn không được phép hủy đơn hàng này 😠");
            return "redirect:/orders";
        }

        // 🔄 Cập nhật trạng thái
        orderService.cancelOrder(order);

        // ✅ Gửi thông báo flash về trang /orders
        redirectAttributes.addFlashAttribute("success", "Đơn hàng #" + id + " đã được hủy 😏");

        return "redirect:/orders";
    }
}
