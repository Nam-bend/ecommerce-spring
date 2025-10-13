package org.example.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    @NotBlank(message = "Vui lòng nhập họ")
    @Size(min = 3, max = 15, message = "Họ phải từ 3 - 15 ký tự")
    private String firstName;

    @NotBlank(message = "Vui lòng nhập tên")
    @Size(min = 3, max = 15, message = "Tên phải từ 3 - 15 ký tự")
    private String lastName;

    @NotBlank(message = "Vui lòng nhập email")
    @Email(message = "Email không hợp lệ")
    private String username;

    @NotBlank(message = "Vui lòng nhập mật khẩu")
    @Size(min = 5, max = 20, message = "Mật khẩu phải từ 5 - 20 ký tự")
    private String password;

    @NotBlank(message = "Vui lòng nhập lại mật khẩu")
    private String repeatPassword;
}
