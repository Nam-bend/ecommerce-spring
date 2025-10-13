package org.example.customer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class CustomerConfiguration {

    private final CustomerServiceConfig customerServiceConfig;

    public CustomerConfiguration(CustomerServiceConfig customerServiceConfig) {
        this.customerServiceConfig = customerServiceConfig;
    }

    @Bean
    public SecurityFilterChain customerSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/css/**", "/js/**", "/vendor/**", "/img/**", "/images/**"
                        ).permitAll()

                        // Trang public
                        .requestMatchers("/", "/index", "/login", "/do-login", "/register", "/register-new").permitAll()
                        .requestMatchers("/about", "/contact").permitAll()

                        // Shop & sản phẩm
                        .requestMatchers("/shop/**", "/find-product/**").permitAll()

                        // Trang dành cho user login (CUSTOMER)
                        .requestMatchers(
                                "/account/**",
                                "/update-infor/**",
                                "/cart/**",
                                "/add-to-cart/**",
                                "/update-cart/**",
                                "/check-out/**",
                                "/order/**",
                                "/orders/**",
                                "/order-detail/**",
                                "/cancel-order/**"
                        ).hasAuthority("CUSTOMER")

                        // Các request khác thì vẫn yêu cầu login
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")                  // trang login custom
                        .loginProcessingUrl("/do-login")      // url xử lý login
                        .defaultSuccessUrl("/index", true)    // login thành công thì về trang chủ
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                );

        return http.build();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return customerServiceConfig; // service implements UserDetailsService
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
