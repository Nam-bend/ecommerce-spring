package org.example.customer.controller;

import org.example.library.dto.ProductDto;
import org.example.library.entity.Category;
import org.example.library.service.CategoryService;
import org.example.library.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService,
                             CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // Trang shop: hiển thị tất cả sản phẩm với phân trang
    @GetMapping("/shop")
    public String shop(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "12") int size,
                       Model model) {
        Page<ProductDto> products = productService.getAllProducts(page, size);
        List<Category> categories = categoryService.findAll();

        model.addAttribute("products", products.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());

        return "shop";
    }

    // Tìm kiếm sản phẩm
    @GetMapping("/shop/search")
    public String search(@RequestParam("keyword") String keyword,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "12") int size,
                         Model model) {
        Page<ProductDto> products = productService.searchProducts(keyword, page, size);
        List<Category> categories = categoryService.findAll();

        model.addAttribute("products", products.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "shop";
    }

    // Xem chi tiết sản phẩm
    @GetMapping("/find-product/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        ProductDto product = productService.getById(id);
        if (product == null) {
            return "redirect:/shop"; // nếu không tìm thấy thì quay lại shop
        }

        // lấy thêm gợi ý sản phẩm cùng category
        List<ProductDto> relatedProducts = productService.findAll().stream()
                .filter(p -> p.getCategory().getId().equals(product.getCategory().getId())
                        && !p.getId().equals(product.getId()))
                .limit(4)
                .toList();

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);

        return "product-detail";
    }

    // Xem sản phẩm theo category
    @GetMapping("/shop/category/{id}")
    public String byCategory(@PathVariable("id") Long id,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "12") int size,
                             Model model) {
        List<Category> categories = categoryService.findAll();

        // tự viết thêm trong ProductRepository một query: findByCategoryId
        Page<ProductDto> products = productService.getProductsByCategory(id, page, size);

        Category category = categoryService.findById(id);

        model.addAttribute("products", products.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("category", category);

        return "shop";
    }
}
