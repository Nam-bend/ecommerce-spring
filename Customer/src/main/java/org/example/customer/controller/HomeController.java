package org.example.customer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.example.library.dto.ProductDto;
import org.example.library.entity.Category;
import org.example.library.service.CategoryService;
import org.example.library.service.ProductService;

import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor  // Lombok tạo constructor cho các field final
public class HomeController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping({"/", })
    public String home() {
        return "home";
    }

    @GetMapping("/index")
    public String index(Model model) {
        List<Category> categories = categoryService.findAll();
        List<ProductDto> productDtos = productService.  findAll();

        model.addAttribute("categories", categories);
        model.addAttribute("products", productDtos);

        return "index";
    }
}

