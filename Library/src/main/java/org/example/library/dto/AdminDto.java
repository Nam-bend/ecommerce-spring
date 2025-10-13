    package org.example.library.dto;

    import jakarta.validation.constraints.Size;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class AdminDto {
        @Size(min=3,max=10 , message = "Invalid firstname name!(3-10 characters)")
        private String firstName;
        @Size(min=3,max=10 , message = "Invalid lastname name!(3-10 characters)")
        private String lastName;
        private String username;
        @Size(min=3,max=10 , message = "Invalid password !(5-15 characters)")
        private String password;
        private String repeatPassword;
    }
