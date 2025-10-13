package org.example.customer.controller;

import lombok.RequiredArgsConstructor;
import org.example.library.entity.Customer;
import org.example.library.service.CityService;
import org.example.library.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final CustomerService customerService;
    private final CityService cityService;

//    @GetMapping("/check-out")
//    public String checkout(Model model, Principal principal) {
//        if (principal == null) {
//            return "redirect:/login";
//        }
//
//        String username = principal.getName();
//        Customer customer = customerService.findByUsername(username);
//
//        // kiểm tra thông tin bắt buộc
//        if (isNullOrEmpty(customer.getPhoneNumber()) ||
//                isNullOrEmpty(customer.getAddress()) ||
//                isNullOrEmpty(customer.getCity()) ||
//                isNullOrEmpty(customer.getCountry())) {
//
//            return "account"; // nếu thiếu thông tin thì quay lại trang account để update
//        }
//
//
//        return "checkout";
//    }
@GetMapping("/account")
public String accountHome(Model model, Principal principal) {
    if (principal == null) {
        return "redirect:/login";
    }

    String username = principal.getName();
    Customer customer = customerService.findByUsername(username);

    // Truyền dữ liệu vào view
    model.addAttribute("customer", customer);
    model.addAttribute("cities", cityService.getAll()); // ← QUAN TRỌNG!

    return "account"; // ← Trả về view account.html
}


    @PostMapping("/update-infor")
    public String updateCustomer(
            @ModelAttribute("customer") Customer customer,
            RedirectAttributes redirectAttributes,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        // Lấy user hiện tại từ DB theo tài khoản đang đăng nhập
        Customer existing = customerService.findByUsername(principal.getName());

        // Chỉ update những thông tin cho phép
        existing.setFirstName(customer.getFirstName());
        existing.setLastName(customer.getLastName());
        existing.setPhoneNumber(customer.getPhoneNumber());
        existing.setAddress(customer.getAddress());
        existing.setCity(customer.getCity());
        existing.setCountry(customer.getCountry());

        Customer saved = customerService.saveInfor(existing);

        redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");

        return "redirect:/account"; // redirect về trang account thay vì check-out
    }


    private boolean isNullOrEmpty(String str) {
        return (str == null || str.trim().isEmpty());
    }
}
