package org.example.customer.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.library.dto.CustomerDto;
import org.example.library.service.CustomerService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CustomerController {

    private final CustomerService customerService;
    private final BCryptPasswordEncoder passwordEncoder;

    public CustomerController(CustomerService customerService,
                              BCryptPasswordEncoder passwordEncoder) {
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
    }

    // GET /login
    @GetMapping("/login")
    public String loginForm() {
        return "login"; // trả về login.html
    }

    // GET /register
    @GetMapping("/register")
    public String registerForm(Model model, HttpSession session) {
        model.addAttribute("customerDto", new CustomerDto());

        // Lấy message từ session nếu có
        Object message = session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message");
        }

        return "register"; // trả về register.html
    }

    // POST /register-new
    @PostMapping("/register-new")
    public String registerCustomer(@Valid @ModelAttribute("customerDto") CustomerDto customerDto,
                                   BindingResult bindingResult,
                                   Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (customerService.existsByUsername(customerDto.getUsername())) {
            model.addAttribute("message", "Email đã tồn tại, vui lòng chọn email khác!");
            return "register";
        }

        if (!customerDto.getPassword().equals(customerDto.getRepeatPassword())) {
            model.addAttribute("message", "Mật khẩu nhập lại không khớp!");
            return "register";
        }
        customerService.save(customerDto);
        model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "login";
    }

}
