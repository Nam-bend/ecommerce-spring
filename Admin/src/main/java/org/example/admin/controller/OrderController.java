package org.example.admin.controller;

import org.example.library.entity.Order;
import org.example.library.entity.OrderDetail;
import org.example.library.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Hiển thị danh sách đơn hàng với phân trang và tìm kiếm
    @GetMapping("/orders")
    public String listOrders(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        Page<Order> orderPage;

        if (keyword != null && !keyword.isEmpty()) {
            orderPage = orderService.searchOrders(keyword, page, size);
            model.addAttribute("keyword", keyword);
        } else {
            orderPage = orderService.getAllOrdersPageable(PageRequest.of(page, size));
        }

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalItems", orderPage.getTotalElements());
        model.addAttribute("size", orderPage.getContent().size());
        model.addAttribute("title", "Quản Lý Đơn Hàng");

        return "orders";
    }

    // Xem chi tiết đơn hàng
    @GetMapping("/orders/view")
    public String orderDetail(
            @RequestParam("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Order order = orderService.getOrderById(id);
            // Sử dụng mapToDouble để xử lý quantity là double
            double totalQuantity = order.getOrderDetailList().stream()
                    .mapToDouble(OrderDetail::getQuantity)
                    .sum();
            model.addAttribute("order", order);
            model.addAttribute("totalQuantity", totalQuantity);
            model.addAttribute("title", "Chi Tiết Đơn Hàng #" + id);
            return "order-details";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("failed", "Không tìm thấy đơn hàng #" + id);
            return "redirect:/orders";
        }
    }

    // Cập nhật trạng thái đơn hàng
    @PostMapping("/orders/update-status")
    public String updateStatus(
            @RequestParam("id") Long id,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Order order = orderService.getOrderById(id);
            if (order.getStatus().equals("Completed") || order.getStatus().equals("Cancelled")) {
                redirectAttributes.addFlashAttribute("failed", "Không thể cập nhật trạng thái đơn hàng đã hoàn thành hoặc đã hủy!");
            } else {
                order.setStatus(status);
                orderService.save(order);
                redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng #" + id + " thành công!");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("failed", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        return "redirect:/orders/view?id=" + id;
    }

    // Xóa đơn hàng
    @GetMapping("/orders/delete")
    public String deleteOrder(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            orderService.deleteOrderById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa đơn hàng #" + id + " thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failed", "Xóa đơn hàng thất bại: " + e.getMessage());
        }
        return "redirect:/orders";
    }
}