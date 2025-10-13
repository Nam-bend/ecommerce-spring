package org.example.admin.controller;

import org.example.library.dto.ProductDto;
import org.example.library.entity.Category;
import org.example.library.entity.Product;
import org.example.library.service.CategoryService;
import org.example.library.service.ProductService;
import org.example.library.utils.ImageUpload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ImageUpload imageUpload;

    public ProductController(ProductService productService, CategoryService categoryService, ImageUpload imageUpload) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.imageUpload = imageUpload;
    }
    @GetMapping("/products")
    public String products(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        Page<ProductDto> productPage;

        if (keyword != null && !keyword.isEmpty()) {
            productPage = productService.searchProducts(keyword, page, size);
            model.addAttribute("keyword", keyword);
        } else {
            productPage = productService.getAllProducts(page, size);
        }

        List<Category> categories = categoryService.findAll();

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());

        // ✅ FIX: Đếm số sản phẩm TRONG TRANG HIỆN TẠI
        model.addAttribute("size", productPage.getContent().size());

        model.addAttribute("categories", categories);
        model.addAttribute("productNew", new ProductDto());
        model.addAttribute("title", "Quản Lý Sản Phẩm");

        return "products";
    }
//    @GetMapping("/products")
//    public String products(Model model) {
//        List<ProductDto> products = productService.findAll();
//        List<Category> categories = categoryService.findAll(); // Lấy tất cả categories
//
//        model.addAttribute("products", products);
//        model.addAttribute("categories", categories);
//        model.addAttribute("size", products.size());
//        model.addAttribute("productNew", new ProductDto());
//        model.addAttribute("title", "Quản Lý Sản Phẩm");
//
//        return "products";
//    }

    @PostMapping("/add-product")
    public String addProduct(@ModelAttribute("productNew") ProductDto productDto,
                             @RequestParam("imageProduct") MultipartFile imageProduct,
                             @RequestParam("category") Long categoryId,
                             RedirectAttributes redirectAttributes) {
        try {
            if (imageProduct.isEmpty()) {
                redirectAttributes.addFlashAttribute("failed", "Vui lòng chọn hình ảnh!");
                return "redirect:/products";
            }

            Category category = categoryService.findById(categoryId);
            productDto.setCategory(category);

            // Tạo tên file duy nhất
            String originalFilename = imageProduct.getOriginalFilename();
            String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;

            // Upload file vào static/img/image-product
            if (imageUpload.uploadFile(imageProduct, uniqueFilename)) {
                productDto.setImage( uniqueFilename); // Lưu đường dẫn vào DB
            } else {
                redirectAttributes.addFlashAttribute("failed", "Lỗi khi tải ảnh lên!");
                return "redirect:/products";
            }

            Product savedProduct = productService.save(productDto);

            if (savedProduct != null) {
                redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("failed", "Thêm sản phẩm thất bại!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("failed", "Lỗi: " + e.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/update-product")
    public String updateProductForm(@RequestParam("id") Long id, Model model) {
        ProductDto product = productService.getById(id);
        List<Category> categories = categoryService.findAll();

        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        model.addAttribute("title", "Cập Nhật Sản Phẩm");

        return "update-product";
    }

    @PostMapping("/update-product")
    public String updateProduct(@ModelAttribute("product") ProductDto productDto,
                                @RequestParam(value = "imageProduct", required = false) MultipartFile imageProduct,
                                @RequestParam("category") Long categoryId,
                                RedirectAttributes redirectAttributes) {
        try {
            // 1) Gắn category
            Category category = categoryService.findById(categoryId);
            productDto.setCategory(category);

            // 2) Lấy sản phẩm hiện tại từ DB để có đường dẫn ảnh cũ (không phụ thuộc hidden field)
            ProductDto current = productService.getById(productDto.getId());
            String oldImagePath = (current != null) ? current.getImage() : null;

            // 3) Nếu có upload ảnh mới
            if (imageProduct != null && !imageProduct.isEmpty()) {
                String originalFilename = imageProduct.getOriginalFilename();
                String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;

                // 3.1) Upload ảnh mới TRƯỚC
                boolean uploaded = imageUpload.uploadFile(imageProduct, uniqueFilename);
                if (!uploaded) {
                    redirectAttributes.addFlashAttribute("failed", "Lỗi khi tải ảnh lên!");
                    return "redirect:/products";
                }

                // 3.2) Set đường dẫn ảnh mới vào DTO để service update
                productDto.setImage(uniqueFilename);

                // 3.3) Xoá ảnh cũ SAU khi upload mới thành công
                if (oldImagePath != null && !oldImagePath.isEmpty()) {
                    File oldFile = new File(imageUpload.getFilePath(oldImagePath));

                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }
            } else {
                // Không up ảnh mới -> giữ ảnh cũ (service chỉ update image khi DTO có giá trị)
                productDto.setImage(oldImagePath);
            }

            // 4) Cập nhật
            Product updatedProduct = productService.update(productDto);
            if (updatedProduct != null) {
                redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("failed", "Cập nhật sản phẩm thất bại!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("failed", "Lỗi: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/delete-product")
    public String deleteProduct(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            ProductDto product = productService.getById(id);
            if (product != null && product.getImage() != null) {
                File oldFile = new File(imageUpload.getFilePath(product.getImage()));

                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }
            productService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("failed", "Xóa sản phẩm thất bại: " + e.getMessage());
        }
        return "redirect:/products";
    }
}