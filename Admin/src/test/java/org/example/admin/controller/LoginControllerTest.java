package org.example.admin.controller;

import jakarta.servlet.http.HttpSession;
import org.example.library.dto.AdminDto;
import org.example.library.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    @Mock
    private AdminService adminService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @InjectMocks
    private LoginController loginController;

    private AdminDto adminDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminDto = new AdminDto();
    }

    // ️ Form có lỗi validate
    @Test
    void testAddNewAdmin_WithValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = loginController.addNewAdmin(adminDto, bindingResult, model, session);

        assertEquals("register", result);
        verify(model, times(1)).addAttribute("adminDto", adminDto);
    }

    // Username đã tồn tại
    @Test
    void testAddNewAdmin_WhenUsernameExists() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(adminService.existsByUsername(anyString())).thenReturn(true);

        adminDto.setUsername("admin@gmail.com");

        String result = loginController.addNewAdmin(adminDto, bindingResult, model, session);

        assertEquals("redirect:/register", result);
        verify(session, times(1)).setAttribute("message", "Email hoặc username đã tồn tại!");
    }

    //  Mật khẩu và repeatPassword không khớp
    @Test
    void testAddNewAdmin_WhenPasswordsNotMatch() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(adminService.existsByUsername(anyString())).thenReturn(false);

        adminDto.setUsername("admin@gmail.com");
        adminDto.setPassword("123");
        adminDto.setRepeatPassword("456");

        String result = loginController.addNewAdmin(adminDto, bindingResult, model, session);

        assertEquals("redirect:/register", result);
        verify(session).setAttribute("message", "Mật khẩu không khớp!");
    }

    // Đăng ký thành công
    @Test
    void testAddNewAdmin_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(adminService.existsByUsername(anyString())).thenReturn(false);

        adminDto.setUsername("admin@gmail.com");
        adminDto.setPassword("123456");
        adminDto.setRepeatPassword("123456");

        String result = loginController.addNewAdmin(adminDto, bindingResult, model, session);

        assertEquals("redirect:/register", result);
        verify(adminService, times(1)).save(adminDto);
        verify(session, times(1)).setAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập.");
    }

    //  Lỗi ngoại lệ hệ thống
    @Test
    void testAddNewAdmin_WhenExceptionThrown() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(adminService.existsByUsername(anyString())).thenThrow(new RuntimeException("DB error"));

        adminDto.setUsername("admin@gmail.com");
        adminDto.setPassword("123");
        adminDto.setRepeatPassword("123");

        String result = loginController.addNewAdmin(adminDto, bindingResult, model, session);

        assertEquals("redirect:/register", result);
        verify(session).setAttribute("message", "Lỗi hệ thống, vui lòng thử lại sau!");
    }

    //  Username bị null
    @Test
    void testAddNewAdmin_WhenUsernameIsNull() {
        when(bindingResult.hasErrors()).thenReturn(false);
        adminDto.setUsername(null);
        adminDto.setPassword("123");
        adminDto.setRepeatPassword("123");

        String result = loginController.addNewAdmin(adminDto, bindingResult, model, session);

        assertEquals("redirect:/register", result);
        verify(adminService).existsByUsername(null);
    }

    //  Password null
    @Test
    void testAddNewAdmin_WhenPasswordIsNull() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(adminService.existsByUsername(anyString())).thenReturn(false);

        adminDto.setUsername("admin@gmail.com");
        adminDto.setPassword(null);
        adminDto.setRepeatPassword(null);

        String result = loginController.addNewAdmin(adminDto, bindingResult, model, session);

        assertEquals("redirect:/register", result);
        verify(session).setAttribute("message", "Mật khẩu không khớp!");
    }

    // repeatPassword bị null
    @Test
    void testAddNewAdmin_WhenRepeatPasswordMissing() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(adminService.existsByUsername(anyString())).thenReturn(false);

        adminDto.setUsername("admin@gmail.com");
        adminDto.setPassword("12345");
        adminDto.setRepeatPassword(null);

        String result = loginController.addNewAdmin(adminDto, bindingResult, model, session);

        assertEquals("redirect:/register", result);
        verify(session).setAttribute("message", "Mật khẩu không khớp!");
    }

    // Password quá ngắn (giả lập validation lỗi)
    @Test
    void testAddNewAdmin_WhenPasswordTooShort() {
        when(bindingResult.hasErrors()).thenReturn(true);

        adminDto.setPassword("12"); // ngắn hơn yêu cầu

        String result = loginController.addNewAdmin(adminDto, bindingResult, model, session);

        assertEquals("register", result);
        verify(model).addAttribute("adminDto", adminDto);
    }

    //Lỗi khi save xuống DB
    @Test
    void testAddNewAdmin_WhenSaveThrowsException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(adminService.existsByUsername(anyString())).thenReturn(false);
        doThrow(new RuntimeException("DB error")).when(adminService).save(any(AdminDto.class));

        adminDto.setUsername("admin@gmail.com");
        adminDto.setPassword("123456");
        adminDto.setRepeatPassword("123456");

        String result = loginController.addNewAdmin(adminDto, bindingResult, model, session);

        assertEquals("redirect:/register", result);
        verify(session).setAttribute("message", "Lỗi hệ thống, vui lòng thử lại sau!");
    }

    //  Username có khoảng trắng thừa
    @Test
    void testAddNewAdmin_TrimWhitespaceInUsername() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(adminService.existsByUsername(anyString())).thenReturn(false);

        adminDto.setUsername("  admin@gmail.com  ");
        adminDto.setPassword("123456");
        adminDto.setRepeatPassword("123456");

        String result = loginController.addNewAdmin(adminDto, bindingResult, model, session);

        assertEquals("redirect:/register", result);
        verify(adminService, times(1)).save(adminDto);
    }

    //  Session bị null (vẫn không crash) trường hợp này ko thể sảy ra nhưng vẫn được test
    @Test
    void testAddNewAdmin_WhenSessionNull() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(adminService.existsByUsername(anyString())).thenReturn(false);

        adminDto.setUsername("admin@gmail.com");
        adminDto.setPassword("123");
        adminDto.setRepeatPassword("123");

        String result = loginController.addNewAdmin(adminDto, bindingResult, model, null);

        assertEquals("redirect:/register", result);
    }
}
