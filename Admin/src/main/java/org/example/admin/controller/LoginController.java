package org.example.admin.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.library.dto.AdminDto;
import org.example.library.service.AdminService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final AdminService adminService;
    private final BCryptPasswordEncoder passwordEncoder;
    public LoginController(AdminService adminService, BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.adminService = adminService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login"; // trả về view login.html
    }

    @RequestMapping("/index")
    public String home() {
        return "index";
    }

    @GetMapping("/register")
    public String registerForm(Model model, HttpSession session) {
        model.addAttribute("adminDto", new AdminDto());

        // Lấy message từ session nếu có
        Object message = session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message");
            session.removeAttribute("successMessage");
        }

        return "register"; // trả về view register.html
    }


    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password"; // trả về view forgot-password.html
    }

    @PostMapping("/register-new")
    public String addNewAdmin(@Valid @ModelAttribute("adminDto") AdminDto adminDto,
                              BindingResult bindingResult,
                              Model model,
                              HttpSession session) {
        try {
            // 1. Validate form
            if (bindingResult.hasErrors()) {
                model.addAttribute("adminDto", adminDto);
                return "register";
            }

            // 2. Kiểm tra username/email đã tồn tại
            if (adminService.existsByUsername(adminDto.getUsername())) {
                session.setAttribute("message", "Email hoặc username đã tồn tại!");
                return "redirect:/register";
            }

            // 3. Kiểm tra password và repeatPassword khớp
            if (adminDto.getPassword() == null || !adminDto.getPassword().equals(adminDto.getRepeatPassword())) {
                session.setAttribute("message", "Mật khẩu không khớp!");
                return "redirect:/register";
            }
//           adminDto.setPassword(passwordEncoder.encode(adminDto.getPassword()));

            // 5 Lưu admin mới
            adminService.save(adminDto);

            session.setAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập.");

            return "redirect:/register";

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", "Lỗi hệ thống, vui lòng thử lại sau!");
            return "redirect:/register";
        }
    }
}
