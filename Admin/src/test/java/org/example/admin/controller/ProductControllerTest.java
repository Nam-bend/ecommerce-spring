package org.example.admin.controller;

import org.example.library.dto.ProductDto;
import org.example.library.entity.Category;
import org.example.library.entity.Product;
import org.example.library.service.CategoryService;
import org.example.library.service.ProductService;
import org.example.library.utils.ImageUpload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ImageUpload imageUpload;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ProductController productController;

    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productDto = new ProductDto();
    }

    // Ảnh rỗng → thất bại
    @Test
    void testAddProduct_WhenImageEmpty() {
        MockMultipartFile image = new MockMultipartFile("file", new byte[0]);

        String result = productController.addProduct(productDto, image, 1L, redirectAttributes);

        assertEquals("redirect:/products", result);
        verify(redirectAttributes).addFlashAttribute("failed", "Vui lòng chọn hình ảnh!");
    }

    //⃣ Upload ảnh thất bại
    @Test
    void testAddProduct_WhenUploadFails() {
        MockMultipartFile image = new MockMultipartFile("imageProduct", "test.jpg", "image/jpeg", "abc".getBytes());
        Category category = new Category();
        when(categoryService.findById(1L)).thenReturn(category);
        when(imageUpload.uploadFile(any(), anyString())).thenReturn(false);

        String result = productController.addProduct(productDto, image, 1L, redirectAttributes);

        assertEquals("redirect:/products", result);
        verify(redirectAttributes).addFlashAttribute("failed", "Lỗi khi tải ảnh lên!");
    }

    //  Upload ảnh thành công + lưu thành công
    @Test
    void testAddProduct_Success() {
        MockMultipartFile image = new MockMultipartFile("imageProduct", "test.jpg", "image/jpeg", "abc".getBytes());
        Category category = new Category();
        when(categoryService.findById(1L)).thenReturn(category);
        when(imageUpload.uploadFile(any(), anyString())).thenReturn(true);
        when(productService.save(any(ProductDto.class))).thenReturn(new Product());

        String result = productController.addProduct(productDto, image, 1L, redirectAttributes);

        assertEquals("redirect:/products", result);
        verify(redirectAttributes).addFlashAttribute("success", "Thêm sản phẩm thành công!");
    }

    //  Upload ảnh thành công + lưu thất bại (trả về null)
    @Test
    void testAddProduct_SaveFails() {
        MockMultipartFile image = new MockMultipartFile("imageProduct", "test.jpg", "image/jpeg", "abc".getBytes());
        Category category = new Category();
        when(categoryService.findById(1L)).thenReturn(category);
        when(imageUpload.uploadFile(any(), anyString())).thenReturn(true);
        when(productService.save(any(ProductDto.class))).thenReturn(null);

        String result = productController.addProduct(productDto, image, 1L, redirectAttributes);

        assertEquals("redirect:/products", result);
        verify(redirectAttributes).addFlashAttribute("failed", "Thêm sản phẩm thất bại!");
    }

    //  Xử lý ngoại lệ
    @Test
    void testAddProduct_WhenExceptionThrown() {
        MockMultipartFile image = new MockMultipartFile("imageProduct", "test.jpg", "image/jpeg", "abc".getBytes());
        when(categoryService.findById(anyLong())).thenThrow(new RuntimeException("DB error"));

        String result = productController.addProduct(productDto, image, 1L, redirectAttributes);

        assertEquals("redirect:/products", result);
        verify(redirectAttributes).addFlashAttribute(startsWith("failed"), contains("Lỗi:"));
    }

    // Cập nhật sản phẩm - upload ảnh mới thành công
    @Test
    void testUpdateProduct_WithNewImageSuccess() {
        MockMultipartFile newImage = new MockMultipartFile("imageProduct", "new.jpg", "image/jpeg", "abc".getBytes());
        Category category = new Category();
        when(categoryService.findById(anyLong())).thenReturn(category);

        ProductDto currentProduct = new ProductDto();
        currentProduct.setImage("old.jpg");
        when(productService.getById(anyLong())).thenReturn(currentProduct);
        when(imageUpload.uploadFile(any(), anyString())).thenReturn(true);
        when(productService.update(any(ProductDto.class))).thenReturn(new Product());

        String result = productController.updateProduct(productDto, newImage, 1L, redirectAttributes);

        assertEquals("redirect:/products", result);
        verify(redirectAttributes).addFlashAttribute("success", "Cập nhật sản phẩm thành công!");
    }

    //  Cập nhật sản phẩm - upload ảnh mới thất bại
    @Test
    void testUpdateProduct_WhenUploadFails() {
        MockMultipartFile newImage = new MockMultipartFile("imageProduct", "new.jpg", "image/jpeg", "abc".getBytes());
        Category category = new Category();
        when(categoryService.findById(anyLong())).thenReturn(category);
        when(imageUpload.uploadFile(any(), anyString())).thenReturn(false);

        String result = productController.updateProduct(productDto, newImage, 1L, redirectAttributes);

        assertEquals("redirect:/products", result);
        verify(redirectAttributes).addFlashAttribute("failed", "Lỗi khi tải ảnh lên!");
    }

    //  Cập nhật sản phẩm - không upload ảnh mới, giữ ảnh cũ
    @Test
    void testUpdateProduct_KeepOldImage() {
        MockMultipartFile emptyImage = new MockMultipartFile("imageProduct", new byte[0]);
        Category category = new Category();
        when(categoryService.findById(anyLong())).thenReturn(category);

        ProductDto currentProduct = new ProductDto();
        currentProduct.setImage("old.jpg");
        when(productService.getById(anyLong())).thenReturn(currentProduct);
        when(productService.update(any(ProductDto.class))).thenReturn(new Product());

        String result = productController.updateProduct(productDto, emptyImage, 1L, redirectAttributes);

        assertEquals("redirect:/products", result);
        verify(redirectAttributes).addFlashAttribute("success", "Cập nhật sản phẩm thành công!");
    }

    //  Cập nhật sản phẩm - lỗi DB
    @Test
    void testUpdateProduct_WhenExceptionThrown() {
        MockMultipartFile newImage = new MockMultipartFile("imageProduct", "new.jpg", "image/jpeg", "abc".getBytes());
        when(categoryService.findById(anyLong())).thenThrow(new RuntimeException("DB error"));

        String result = productController.updateProduct(productDto, newImage, 1L, redirectAttributes);

        assertEquals("redirect:/products", result);
        verify(redirectAttributes).addFlashAttribute(startsWith("failed"), contains("Lỗi:"));
    }

    //  Cập nhật sản phẩm - update trả về null
    @Test
    void testUpdateProduct_WhenUpdateFails() {
        MockMultipartFile newImage = new MockMultipartFile("imageProduct", "new.jpg", "image/jpeg", "abc".getBytes());
        Category category = new Category();
        when(categoryService.findById(anyLong())).thenReturn(category);
        when(productService.update(any(ProductDto.class))).thenReturn(null);

        String result = productController.updateProduct(productDto, newImage, 1L, redirectAttributes);

        assertEquals("redirect:/products", result);
        verify(redirectAttributes).addFlashAttribute("failed", "Cập nhật sản phẩm thất bại!");
    }
}
