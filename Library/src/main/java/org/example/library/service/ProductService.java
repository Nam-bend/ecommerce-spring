package org.example.library.service;

import org.example.library.dto.ProductDto;
import org.example.library.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    //admin
    Page<ProductDto> getAllProducts(int page, int size);

    Page<ProductDto> searchProducts(String keyword, int page, int size);

    List<ProductDto> findAll();

    Product save(ProductDto productDto);

    Product update(ProductDto productDto);

    void deleteById(Long id);


    ProductDto getById(Long id);

    //customer

    Page<ProductDto> getProductsByCategory(Long categoryId, int page, int size);

    Product getProductById(Long id);

}
